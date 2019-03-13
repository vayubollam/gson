package suncor.com.android.mfp;

import android.content.Intent;

import com.worklight.nativeandroid.common.WLUtils;
import com.worklight.wlclient.HttpClientManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.zip.GZIPInputStream;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSource;
import suncor.com.android.SuncorApplication;
import suncor.com.android.ui.home.HomeActivity;

public class MFPRequestInterceptor implements Interceptor {
    public static void attachInterceptor(HttpClientManager instance) {
        try {
            Field okHttpBuilderField = HttpClientManager.class.getDeclaredField("builder");
            Field okHttpField = HttpClientManager.class.getDeclaredField("httpClient");
            okHttpField.setAccessible(true);
            okHttpBuilderField.setAccessible(true);
            OkHttpClient.Builder builder = (OkHttpClient.Builder) okHttpBuilderField.get(instance);
            builder.addNetworkInterceptor(new MFPRequestInterceptor());
            okHttpField.set(instance, builder.build());
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        if (response.code() == 403) {
            BufferedSource source = response.body().source();
            source.request(Long.MAX_VALUE);
            Buffer buffer = source.buffer().clone();
            byte[] bytes;
            if (response.header("Content-Encoding") != null && response.header("Content-Encoding").equalsIgnoreCase("gzip")) {
                GZIPInputStream gzipStream = new GZIPInputStream(buffer.inputStream());
                bytes = WLUtils.readStreamToByteArray(gzipStream);
            } else {
                bytes = buffer.readByteArray();
            }
            String body = new String(bytes, Charset.forName("UTF-8"));
            try {
                JSONObject object = new JSONObject(body);
                if (object.has("errorCode")) {
                    if (ErrorCodes.OTHER_SESSION_STARTED.equalsIgnoreCase(object.getString("errorCode"))) {
                        SessionManager.getInstance().logout();
                        Intent intent = new Intent(SuncorApplication.getInstance(), HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        SuncorApplication.getInstance().startActivity(intent);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return response;
    }
}

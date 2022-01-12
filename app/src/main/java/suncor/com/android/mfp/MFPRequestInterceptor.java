package suncor.com.android.mfp;

import android.content.Intent;
import android.os.Handler;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.worklight.nativeandroid.common.WLUtils;
import com.worklight.wlclient.HttpClientManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.zip.GZIPInputStream;

import javax.inject.Inject;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSource;
import suncor.com.android.SuncorApplication;
import suncor.com.android.model.Resource;
import suncor.com.android.ui.main.MainActivity;
import suncor.com.android.utilities.FingerprintManager;
import suncor.com.android.utilities.Timber;

import static suncor.com.android.utilities.Constants.ERROR_CODE;

public class MFPRequestInterceptor implements Interceptor {

    @Inject
    SessionManager sessionManager;
    @Inject
    SuncorApplication application;
    @Inject
    FingerprintManager fingerprintManager;

    @Inject
    MFPRequestInterceptor() {
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
            Timber.v("Response Intercepted:\nRequest URI:%s\ncode :%d\nbody:%s", response.request().url().toString(), response.code(), body);
            try {
                JSONObject object = new JSONObject(body);
                if (object.has(ERROR_CODE)) {
                    if (ErrorCodes.ERR_CONFLICTING_LOGINS.equalsIgnoreCase(object.getString(ERROR_CODE))) {
                        Handler mainHandler = new Handler(application.getMainLooper());
                        mainHandler.post(() -> sessionManager.logout().observeForever((result) -> {
                            //The livedata from logout is short lived, so observing it forever won't leak memories
                            if (result.status == Resource.Status.SUCCESS) {
                                LocalBroadcastManager.getInstance(application).sendBroadcast(new Intent(MainActivity.LOGGED_OUT_DUE_CONFLICTING_LOGIN));
                            }
                        }));
                    } else if (ErrorCodes.ERR_PASSWORD_CHANGE_REQUIRES_RE_LOGIN.equalsIgnoreCase(object.getString(ERROR_CODE))) {
                        Handler mainHandler = new Handler(application.getMainLooper());
                        mainHandler.post(() -> {
                            sessionManager.logout().observeForever(result -> {
                                if (result.status == Resource.Status.SUCCESS) {
                                    fingerprintManager.deactivateAutoLogin();
                                    fingerprintManager.deactivateFingerprint();
                                    LocalBroadcastManager.getInstance(application).sendBroadcast(new Intent(MainActivity.LOGGED_OUT_DUE_PASSWORD_CHANGE));
                                }
                            });
                        });
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return response;
    }

    public static void attachRequestInterceptor(MFPRequestInterceptor requestInterceptor, HttpClientManager instance) {
        try {
            Field okHttpBuilderField = HttpClientManager.class.getDeclaredField("builder");
            Field okHttpField = HttpClientManager.class.getDeclaredField("httpClient");
            okHttpField.setAccessible(true);
            okHttpBuilderField.setAccessible(true);
            OkHttpClient.Builder builder = (OkHttpClient.Builder) okHttpBuilderField.get(instance);
            builder.addNetworkInterceptor(requestInterceptor);
            okHttpField.set(instance, builder.build());
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}


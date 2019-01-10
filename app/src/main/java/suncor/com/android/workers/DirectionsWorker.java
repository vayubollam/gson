package suncor.com.android.workers;

import android.content.Context;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class DirectionsWorker extends Worker {
    private static final String ORIGIN_LAT = "origin_lat";
    private static final String ORIGIN_LNG = "origin_lng";
    private static final String DEST_LAT = "dest_lat";
    private static final String DEST_LNG = "dest_lng";
    public static final String KEY_RESULT = "result";


    public DirectionsWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }


    @NonNull
    @Override
    public Result doWork() {
        double  origin_lat = getInputData().getDouble(ORIGIN_LAT, 0);
        double origin_lng = getInputData().getDouble(ORIGIN_LNG, 0);
        double dest_lat = getInputData().getDouble(DEST_LAT, 0);
        double dest_lng = getInputData().getDouble(DEST_LNG, 0);
        HashMap<String,String> info;
       info = getDistanceDuration(new LatLng(origin_lat,origin_lng),new LatLng(dest_lat,dest_lng));

     if(info!=null) {
         Data output = new Data.Builder()
                 .putString("distance", info.get("distance"))
                 .putString("duration", info.get("duration"))
                 .build();
         return Result.success(output);
     }else
     {
        return Result.retry();
     }

    }


    private HashMap<String,String> getDistanceDuration(LatLng origin, LatLng dest){
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String mode = "mode = driving";
        String mapKey="key=AIzaSyAtC2AuQA0e-jJYbMrteC06unYKysCa1tA";
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&"+ mode+ "&"+ mapKey;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
         HashMap<String,String> info=new HashMap<>();
        try {
            String response=downloadUrl(url);

                JSONObject jsonObj = new JSONObject(response);

                JSONArray legs=jsonObj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");
                    JSONObject jo=legs.getJSONObject(0);
                    String distance=jo.getJSONObject("distance").getString("text");
                    String duration=jo.getJSONObject("duration").getString("text");

                   info.put("distance",distance);
            info.put("duration",duration);
    return info;




        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }

    }





    private String downloadUrl(String strUrl) throws IOException {
        String data = "";

                InputStream iStream = null;
                HttpURLConnection urlConnection = null;
                try {
                    URL url = new URL(strUrl);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.connect();
                    iStream = urlConnection.getInputStream();

                    BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                    StringBuilder sb = new StringBuilder();

                    String line = "";
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }

                    data = sb.toString();

                    br.close();

                } catch (Exception e) {
                    Log.d("Exception_from", e.toString());
                } finally {
                    try {
                        Objects.requireNonNull(iStream).close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    urlConnection.disconnect();
                }

        return data;
    }




}

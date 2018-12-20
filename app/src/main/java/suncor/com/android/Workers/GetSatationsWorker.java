package suncor.com.android.Workers;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.worklight.wlclient.api.WLFailResponse;
import com.worklight.wlclient.api.WLResourceRequest;
import com.worklight.wlclient.api.WLResponse;
import com.worklight.wlclient.api.WLResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.Operation;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import suncor.com.android.dataObjects.Station;
import suncor.com.android.ui.MainActivity;

public class GetSatationsWorker  extends Worker {
    List<Station> stations;
    private String result="ALL_STATIONS";
    private String jsonText;
    private boolean jobCompleted=false;
    private Data stationsOutPut;

    public GetSatationsWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @Override
    public Result doWork() {

        URI adapterPath = null;


        try {
            adapterPath = new URI("/adapters/suncor/v1/locations?southWestLat=0&southWestLong=0&northEastLat=0&northEastLong=0&amenities=PayAtPump;ULTRA94;PAYPASS,PAYWAVE");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        WLResourceRequest request = new WLResourceRequest(adapterPath, WLResourceRequest.GET);
        request.send(new WLResponseListener() {
            @Override
            public void onSuccess(WLResponse wlResponse) {
                 jsonText = wlResponse.getResponseText();
                Log.d("worker_result","job completed");
                 stationsOutPut =new Data.Builder()
                        .putString(result,jsonText)
                        .build();
                jobCompleted=true;

            }

            @Override
            public void onFailure(WLFailResponse wlFailResponse) {
                Log.d("mfp_error",wlFailResponse.getErrorMsg());

                jobCompleted=false;
            }
        });
        return  jobCompleted ? Result.success(stationsOutPut) : Result.failure();

    }
}

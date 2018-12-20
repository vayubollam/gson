package suncor.com.android.Workers;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.worklight.wlclient.api.WLFailResponse;
import com.worklight.wlclient.api.WLResourceRequest;
import com.worklight.wlclient.api.WLResponse;
import com.worklight.wlclient.api.WLResponseListener;

import java.net.URI;
import java.net.URISyntaxException;

import androidx.annotation.Nullable;
import androidx.core.app.JobIntentService;
import androidx.work.Data;
import suncor.com.android.constants.GeneralConstants;

public class GetStationsService extends IntentService {
     private String jsonText;
     private boolean jobCompleted=false;
    public static final String PARAM_OUT_MSG = "omsg";
    public GetStationsService(String name) {
        super(name);
    }

    public GetStationsService() {
        super("get stations");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

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
                Log.d("worker_result","job completed from intent");

                jobCompleted=true;

                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(GeneralConstants.ACTION_RESP);
                broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                broadcastIntent.putExtra(PARAM_OUT_MSG, jsonText);
                sendBroadcast(broadcastIntent);

            }

            @Override
            public void onFailure(WLFailResponse wlFailResponse) {
                Log.d("mfp_error",wlFailResponse.getErrorMsg());

                jobCompleted=false;
            }
        });

    }
}

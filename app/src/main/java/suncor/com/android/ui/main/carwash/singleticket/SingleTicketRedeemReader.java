package suncor.com.android.ui.main.carwash.singleticket;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;

import javax.inject.Inject;

import suncor.com.android.R;
import suncor.com.android.SuncorApplication;
import suncor.com.android.model.singleticket.SingleTicketRedeem;

public class SingleTicketRedeemReader {
    private final SuncorApplication application;
    private final Gson gson;

    @Inject
    public SingleTicketRedeemReader(SuncorApplication application, Gson gson) {
        this.application = application;
        this.gson = gson;
    }

    public SingleTicketRedeem[] getSingleTicketRedeemsList() {
        InputStream jsonFile = application.getResources().openRawResource(R.raw.single_ticket_redeem);
        SingleTicketRedeem[] rewards = gson.fromJson(new InputStreamReader(jsonFile), SingleTicketRedeem[].class);
        return rewards;
    }
}

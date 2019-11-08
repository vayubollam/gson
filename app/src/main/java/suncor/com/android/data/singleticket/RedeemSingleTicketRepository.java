package suncor.com.android.data.singleticket;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import suncor.com.android.R;
import suncor.com.android.model.singleticket.SingleTicketRedeem;

@Singleton
public class RedeemSingleTicketRepository {
    ArrayList<SingleTicketRedeem> singleTicketRedeemList;


//    @Inject
//    public RedeemSingleTicketRepository() {
//        InputStream jsonStream = context.getResources().openRawResource(R.raw.single_ticket_redeem);
//        JSONObject jsonObject = new JSONObject(Strings.convertStreamToString(jsonStream));
//        JSONArray jsonContries = jsonObject.getJSONArray("countries");
//        List<CountryVO> countries = new ArrayList<CountryVO>();
//        for (int i = 0, m = countries.length(); i < m; i++) {
//            JSONObject jsonCountry = countries.getJSONObject(i);
//            CountryVO country = new CountryVO();
//            country.setCountryName(jsonCountry.getString("country"));
//            String co = jsonCountry.getString("countryCode");
//            country.setCountryCode(co);
//            try {
//                Class<?> drawableClass = com.example.R.drawable.class; // replace package
//                Field drawableField = drawableClass.getField(co);
//                int drawableId = (Integer)drawableField.get(null);
//                Drawable drawable = getResources().getDrawable(drawableId);
//                country.setCountryFlag(drawable);
//            } catch (Exception e) {
//                // report exception
//            }
//            countries.add(country);
//        }
//    }
}

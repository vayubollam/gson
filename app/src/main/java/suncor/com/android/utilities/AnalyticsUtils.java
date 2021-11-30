package suncor.com.android.utilities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Pair;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import suncor.com.android.model.cards.CardType;

public class AnalyticsUtils {

    public enum Event {
        viewItem("view_item"),
        selectContent("select_content"),
        videoStart("video_start"),
        videoThreshold25("video_threshold_25"),
        videoThreshold50("video_threshold_50"),
        videoThreshold75("video_threshold_75"),
        videoComplete("video_complete"),
        formStart("form_start"),
        formStep("form_step"),
        formComplete("form_complete"),
        formError("form_error"),
        navigation("navigation"),
        buttonTap("button_tap"),
        alert("alert"),
        alertInteraction("alert_interaction"),
        error("error_log"),
        paymentPreauthorize("payment_preauthorize"),
        paymentComplete("payment_complete"),
        intersite("intersite"),
        infoTap("info_tap"),
        menuTap("menu_tap");


        private final String name;

        Event(final String name){
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public enum Param {
        itemId("item_id"),
        itemName("item_name"),
        creativeName("creative_name"),
        creativeSlot("creative_slot"),
        contentType("content_type"),
        promotions("promotions"),
        videoTitle("videoTitle"),
        formName("formName"),
        stepName("stepName"),
        formSelection("formSelection"),
        actionBarTap("actionBarTap"),
        buttonText("buttonText"),
        alertTitle("alertTitle"),
        alertSelection("alertSelection"),
        cardType("cardType"),
        errorMessage("errorMessage"),
        detailMessage("detailErrorMessage"),
        paymentMethod("paymentMethod"),
        fuelAmountSelection("fuelAmountSelection"),
        intersiteURL("intersiteURL"),
        infoText("infoText"),
        menuSelection("menuSelection");

        private final String name;

        Param(final String name){
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static volatile String userID;
    public static CardType currentCardType;
    public static int buildNumber;

    public static void setUserId(String userId){
         userID = userId;
    }

    @SafeVarargs
    public static void logEvent(Context context, String eventName, Pair<String, String>... variables) {
        Bundle bundle = new Bundle();
        for (Pair<String, String> variable : variables) {
            bundle.putString(variable.first, variable.second);
        }
        if (userID != null) {
            bundle.putString("user_id", userID);
        }
        if (buildNumber != 0) {
            bundle.putString("BuildNumber", String.valueOf(buildNumber));
        }
        FirebaseAnalytics.getInstance(context).logEvent(eventName, bundle);
    }

    public static void setUserProperty(Context context, String userID, boolean rbcLinked ){
        FirebaseAnalytics.getInstance(context).setUserId(userID);
        FirebaseAnalytics.getInstance(context).setUserProperty("userID", userID);
        FirebaseAnalytics.getInstance(context).setUserProperty("is_linked_rbc", rbcLinked ? "true" : "false");
    }

    @SafeVarargs
    public static void logEvent(Context context, Event event, Pair<Param,String>... parameters) {
        ArrayList<Pair<String, String>> params = new ArrayList();
        for (Pair<Param, String> param: parameters) {
            params.add(new Pair<>(param.first.toString(), param.second));
        }
        logEvent(context, event.toString(), params.toArray(new Pair[params.size()]));
    }

    public static void logPromotionEvent(Context context, Event event, String itemId, String itemName, String creativeName, String creativeSlot, String contentType) {
        Bundle promotion = new Bundle();
        promotion.putString(Param.itemId.toString(), itemId);
        promotion.putString(Param.itemName.toString(), itemName);
        promotion.putString(Param.creativeName.toString(), creativeName);
        promotion.putString(Param.creativeSlot.toString(), creativeSlot);
        ArrayList promotions = new ArrayList();
        promotions.add(promotion);

        Bundle ecommerceBundle = new Bundle();
        ecommerceBundle.putParcelableArrayList(Param.promotions.toString(), promotions);
        if (event.equals(Event.selectContent)){
            ecommerceBundle.putString(Param.contentType.toString(), contentType);
            ecommerceBundle.putString(Param.itemId.toString(), itemId);
        }

        FirebaseAnalytics.getInstance(context).logEvent(event.toString(), ecommerceBundle);
    }

    public static void logPromotionEvent(Context context, Event event, String itemId, String itemName, String creativeName, String creativeSlot){
        logPromotionEvent(context, event, itemId, itemName, creativeName, creativeSlot, "Internal Promotions");
    }

    public static void logCarwashActivationEvent(Context context, Event event, String stepName, CardType cardType) {
        AnalyticsUtils.currentCardType = cardType;
        logCarwashActivationEvent(context, event, stepName);
    }

    public static void logCarwashActivationEvent(Context context, Event event, String stepName) {
        if (currentCardType == CardType.WAG) {
            logEvent(context, event,
                    new Pair<>(Param.formName, "Activate Wash by Wash & Go card"),
                    new Pair<>(Param.stepName, stepName)
            );
        } else if (currentCardType == CardType.SP) {
            logEvent(context, event,
                    new Pair<>(Param.formName, "Activate Wash by Season Pass card"),
                    new Pair<>(Param.stepName, stepName)
            );
        }
    }

    public static String getCardFormName() {
        if (currentCardType == CardType.WAG) {
            return "Activate Wash by Wash & Go card";
        } else if (currentCardType == CardType.SP) {
            return "Activate Wash by Season Pass card";
        }
        return "None";
    }

    public static void setCurrentScreenName(Activity activity, String screenName) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, activity.getComponentName().getClassName());
       // bundle.putString(MyAppAnalyticsConstants.Param.TOPIC, topic);
        FirebaseAnalytics.getInstance(activity).logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);
    }

    public enum ErrorMessages {

        backendError("SUNCORXXXX");

        private final String name;

        ErrorMessages(final String name){
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

    }


}

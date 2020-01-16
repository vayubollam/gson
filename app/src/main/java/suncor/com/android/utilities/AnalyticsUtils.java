package suncor.com.android.utilities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Pair;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Map;

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
        navigation("navigation")
        ;

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
        actionBarTap("actionBarTap")
        ;

        private final String name;

        Param(final String name){
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static String userID;

    @SafeVarargs
    public static void logEvent(Context context, String eventName, Pair<String, String>... variables) {
        Bundle bundle = new Bundle();
        for (Pair<String, String> variable : variables) {
            bundle.putString(variable.first, variable.second);
        }
        if (userID != null) {
            bundle.putString("user_id", userID);
        }
        FirebaseAnalytics.getInstance(context).logEvent(eventName, bundle);
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

    public static void setCurrentScreenName(Activity activity, String screenName) {
        FirebaseAnalytics.getInstance(activity).setCurrentScreen(activity, screenName, activity.getClass().getSimpleName());
    }
}

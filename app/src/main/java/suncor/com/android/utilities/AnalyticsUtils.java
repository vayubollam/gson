package suncor.com.android.utilities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Pair;

import com.google.firebase.analytics.FirebaseAnalytics;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import suncor.com.android.model.cards.CardType;

import static suncor.com.android.utilities.Constants.*;

public class AnalyticsUtils {

    public enum Event {
        VIEWITEM(VIEW_ITEM),
        SELECTCONTENT(SELECT_CONTENT),
        VIDEOSTART(VIDEO_START),
        VIDEOTHRESHOLD25(VIDEO_THRESHOLD_25),
        VIDEOTHRESHOLD50(VIDEO_THRESHOLD_50),
        VIDEOTHRESHOLD75(VIDEO_THRESHOLD_75),
        VIDEOCOMPLETE(VIDEO_COMPLETE),
        FORMSTART(FORM_START),
        FORMSTEP(FORM_STEP),
        FORMCOMPLETE(FORM_COMPLETE),
        FORMERROR(FORM_ERROR),
        _NAVIGATION(NAVIGATION),
        BUTTONTAP(BUTTON_TAP),
        _ALERT(ALERT),
        alertInteraction(ALERT_INTERACTION),
        error(ERROR_LOG),
        paymentPreauthorize(PAYMENT_PREAUTHORIZE),
        paymentComplete(PAYMENT_COMPLETE),
        intersite(INTERSITE),
        infoTap(INFO_TAP),
        menuTap(MENU_TAP);


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
        ITEMID(ITEM_ID),
        ITEMNAME(ITEM_NAME),
        CREATIVENAME(CREATIVE_NAME),
        CREATIVESLOT(CREATIVE_SLOT),
        CONTENTTYPE(CONTENT_TYPE),
        PROMOTIONS_ENUM(PROMOTIONS),
        VIDEOTITLE(VIDEO_TITLE),
        FORMNAME(FORM_NAME),
        STEPNAME(STEP_NAME),
        FORMSELECTION(FORM_SELECTION),
        ACTIONBARTAP(ACTIONBAR_TAP),
        buttonText(BUTTON_TEXT),
        alertTitle(ALERT_TITLE),
        alertSelection(ALERT_SELECTION),
        cardType(CARD_TYPE),
        errorMessage(ERROR_MESSAGE),
        detailMessage(DETAIL_ERROR_MESSAGE),
        paymentMethod(PAYMENT_METHOD),
        pointsRedeemed(POINTS_REDEEMED),
        redeemedPoints(REDEEMED_POINTS),
        fuelAmountSelection(FUEL_AMOUNT_SELECTION),
        checkBoxInput(CHECKBOX_INPUT),
        intersiteURL(INTERSITE_URL),
        infoText(INFO_TEXT),
        menuSelection(MENU_SELECTION);

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

    public static int getBuildNumber() {
        return buildNumber;
    }

    public static void setBuildNumber(int buildNumber) {
        buildNumber = buildNumber;
    }

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
            bundle.putString(USER_ID, userID);
        }
        if (buildNumber != 0) {
            bundle.putString(BUILD_NUMBER, String.valueOf(buildNumber));
        }
        FirebaseAnalytics.getInstance(context).logEvent(eventName, bundle);
    }

    public static void setUserProperty(Context context, String userID, boolean rbcLinked){
        FirebaseAnalytics.getInstance(context).setUserId(userID);
        FirebaseAnalytics.getInstance(context).setUserProperty(USER_ID_1, userID);
        FirebaseAnalytics.getInstance(context).setUserProperty(IS_RBC_LINKED, rbcLinked ? TRUE : FALSE);
    }

    public static void setPetroPointsProperty(Context context,int pointsBalance){
        FirebaseAnalytics.getInstance(context).setUserProperty("petro_points", String.valueOf(pointsBalance));
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
        promotion.putString(Param.ITEMID.toString(), itemId);
        promotion.putString(Param.ITEMNAME.toString(), itemName);
        promotion.putString(Param.CREATIVENAME.toString(), creativeName);
        promotion.putString(Param.CREATIVESLOT.toString(), creativeSlot);
        ArrayList promotions = new ArrayList();
        promotions.add(promotion);

        Bundle ecommerceBundle = new Bundle();
        ecommerceBundle.putParcelableArrayList(Param.PROMOTIONS_ENUM.toString(), promotions);
        if (event.equals(Event.SELECTCONTENT)){
            ecommerceBundle.putString(Param.CONTENTTYPE.toString(), contentType);
            ecommerceBundle.putString(Param.ITEMID.toString(), itemId);
        }

        FirebaseAnalytics.getInstance(context).logEvent(event.toString(), ecommerceBundle);
    }

    public static void logPromotionEvent(Context context, Event event, String itemId, String itemName, String creativeName, String creativeSlot){
        logPromotionEvent(context, event, itemId, itemName, creativeName, creativeSlot, INTERNAL_PROMOTIONS);
    }

    public static void logCarwashActivationEvent(Context context, Event event, String stepName, CardType cardType) {
        AnalyticsUtils.currentCardType = cardType;
        logCarwashActivationEvent(context, event, stepName);
    }

    public static void logCarwashActivationEvent(Context context, Event event, String stepName) {
        if (currentCardType == CardType.WAG) {
            logEvent(context, event,
                    new Pair<>(Param.FORMNAME, ACTIVATE_WNG),
                    new Pair<>(Param.STEPNAME, stepName)
            );
        } else if (currentCardType == CardType.SP) {
            logEvent(context, event,
                    new Pair<>(Param.FORMNAME, ACTIVATE_SP),
                    new Pair<>(Param.STEPNAME, stepName)
            );
        }
    }

    public static String getCardFormName() {
        if (currentCardType == CardType.WAG) {
            return ACTIVATE_WNG;
        } else if (currentCardType == CardType.SP) {
            return ACTIVATE_SP;
        }
        return NONE;
    }

    public static void setCurrentScreenName(@NotNull Activity activity, String screenName) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, activity.getComponentName().getClassName());
       // bundle.putString(MyAppAnalyticsConstants.Param.TOPIC, topic);
        FirebaseAnalytics.getInstance(activity).logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);
    }

    public enum ErrorMessages {

        backendError(DEFAULT_ERROR_SUNCORXXXX);

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

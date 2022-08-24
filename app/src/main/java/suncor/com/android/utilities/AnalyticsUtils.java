package suncor.com.android.utilities;

import static suncor.com.android.analytics.BaseEvents.ALERT_INTERACTION;
import static suncor.com.android.analytics.BaseEvents.BUTTON_TAP;
import static suncor.com.android.analytics.BaseEvents.FORM_ERROR;
import static suncor.com.android.analytics.BaseEvents.NAVIGATION;
import static suncor.com.android.utilities.Constants.ACTIVATE_SP;
import static suncor.com.android.utilities.Constants.ACTIVATE_WNG;
import static suncor.com.android.utilities.Constants.ALERT;
import static suncor.com.android.utilities.Constants.ALERT_SELECTION;
import static suncor.com.android.utilities.Constants.ALERT_TITLE;
import static suncor.com.android.utilities.Constants.BUILD_NUMBER;
import static suncor.com.android.utilities.Constants.BUTTON_TEXT;
import static suncor.com.android.utilities.Constants.CARD_TYPE;
import static suncor.com.android.utilities.Constants.CONTENT_TYPE;
import static suncor.com.android.utilities.Constants.CREATIVE_NAME;
import static suncor.com.android.utilities.Constants.CREATIVE_SLOT;
import static suncor.com.android.utilities.Constants.DEFAULT_ERROR_SUNCORXXXX;
import static suncor.com.android.utilities.Constants.DETAIL_ERROR_MESSAGE;
import static suncor.com.android.utilities.Constants.ERROR_LOG;
import static suncor.com.android.utilities.Constants.ERROR_MESSAGE;
import static suncor.com.android.utilities.Constants.FALSE;
import static suncor.com.android.utilities.Constants.FORM_COMPLETE;
import static suncor.com.android.utilities.Constants.FORM_NAME;
import static suncor.com.android.utilities.Constants.FORM_SELECTION;
import static suncor.com.android.utilities.Constants.FORM_START;
import static suncor.com.android.utilities.Constants.FORM_STEP;
import static suncor.com.android.utilities.Constants.INFO_TAP;
import static suncor.com.android.utilities.Constants.INFO_TEXT;
import static suncor.com.android.utilities.Constants.INTERNAL_PROMOTIONS;
import static suncor.com.android.utilities.Constants.INTERSITE;
import static suncor.com.android.utilities.Constants.INTERSITE_URL;
import static suncor.com.android.utilities.Constants.IS_RBC_LINKED;
import static suncor.com.android.utilities.Constants.ITEM_ID;
import static suncor.com.android.utilities.Constants.ITEM_NAME;
import static suncor.com.android.utilities.Constants.MENU_SELECTION;
import static suncor.com.android.utilities.Constants.MENU_TAP;
import static suncor.com.android.utilities.Constants.NONE;
import static suncor.com.android.utilities.Constants.PAYMENT_COMPLETE;
import static suncor.com.android.utilities.Constants.PAYMENT_METHOD;
import static suncor.com.android.utilities.Constants.PROMOTIONS;
import static suncor.com.android.utilities.Constants.SELECT_CONTENT;
import static suncor.com.android.utilities.Constants.TRUE;
import static suncor.com.android.utilities.Constants.USER_ID;
import static suncor.com.android.utilities.Constants.USER_ID_1;
import static suncor.com.android.utilities.Constants.VIDEO_COMPLETE;
import static suncor.com.android.utilities.Constants.VIDEO_START;
import static suncor.com.android.utilities.Constants.VIDEO_THRESHOLD_25;
import static suncor.com.android.utilities.Constants.VIDEO_THRESHOLD_50;
import static suncor.com.android.utilities.Constants.VIDEO_THRESHOLD_75;
import static suncor.com.android.utilities.Constants.VIDEO_TITLE;
import static suncor.com.android.utilities.Constants.VIEW_ITEM;
import static suncor.com.android.utilities.Constants.STEP_NAME;

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
        menuTap(MENU_TAP),
        activateCarwashSuccess(ACTIVATE_CAR_WASH_SUCCESS),
        cwConfirmPin(CW_CONFIRM_PIN),
        activateCarWashClick(ACTIVATE_CAR_WASH_CLICK),
        ADDPPTSTOWALLET(ADD_PPTS_TO_WALLET),
        ADDPPTSTOWALLETERROR(ADD_PPTS_TO_WALLET_ERROR),
        APPRATINGPROMPT(APP_RATING_PROMPT);


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
        fuelAmountSelection(FUEL_AMOUNT_SELECTION),
        intersiteURL(INTERSITE_URL),
        infoText(INFO_TEXT),
        menuSelection(MENU_SELECTION),
        carWashCardType(CAR_WASH_CARD_TYPE),
        WALLETTYPE(WALLET_TYPE);

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

    public static void setUserProperty(Context context, String userID, boolean rbcLinked ){
        FirebaseAnalytics.getInstance(context).setUserId(userID);
        FirebaseAnalytics.getInstance(context).setUserProperty(USER_ID_1, userID);
        FirebaseAnalytics.getInstance(context).setUserProperty(IS_RBC_LINKED, rbcLinked ? TRUE : FALSE);
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

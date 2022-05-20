package suncor.com.android.ui.main.home;

import android.graphics.drawable.Drawable;

import javax.annotation.Nullable;

public class OfferCard {
    private String text;
    private Drawable image;
    private OfferButton leftButton;
    private OfferButton rightButton;
    private boolean isImageLogoVisible;
    private String bannerTag;

    public OfferCard(String text, Drawable image, OfferButton leftButton, OfferButton rightButton, boolean isImageLogoVisible, String bannerTag) {
        this.text = text;
        this.image = image;
        this.leftButton = leftButton;
        this.rightButton = rightButton;
        this.isImageLogoVisible = isImageLogoVisible;
        this.bannerTag = bannerTag;
    }

    public OfferCard(String text, Drawable image, OfferButton button, boolean isImageLogoVisible, String bannerTag) {
        this.text = text;
        this.image = image;
        this.leftButton = button;
        this.isImageLogoVisible = isImageLogoVisible;
        this.bannerTag = bannerTag;
    }

    public boolean hasRightButton() {
        return rightButton != null;
    }

    public String getText() {
        return text;
    }

    public Drawable getImage() {
        return image;
    }

    public OfferButton getLeftButton() {
        return leftButton;
    }

    public OfferButton getRightButton() {
        return rightButton;
    }

    public boolean isImageLogoVisible() {
        return isImageLogoVisible;
    }

    public String getBannerTag() {
        return bannerTag;
    }

    public void setBannerTag(String bannerTag) {
        this.bannerTag = bannerTag;
    }

    public void setImageLogoVisible(boolean imageLogoVisible) {
        isImageLogoVisible = imageLogoVisible;
    }

    public static class OfferButton {
        private String text;
        private Drawable rightDrawable;
        private Runnable action;

        public OfferButton(String text, @Nullable Drawable rightDrawable, Runnable action) {
            this.text = text;
            this.rightDrawable = rightDrawable;
            this.action = action;
        }

        public OfferButton(String text, Runnable action) {
            this.text = text;
            this.action = action;
        }


        public String getText() {
            return text;
        }

        public Drawable getRightDrawable() {
            return rightDrawable;
        }

        public Runnable getAction() {
            return action;
        }
    }
}

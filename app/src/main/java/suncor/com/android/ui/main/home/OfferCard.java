package suncor.com.android.ui.main.home;

import android.graphics.drawable.Drawable;

import javax.annotation.Nullable;

public class OfferCard {
    private String text;
    private Drawable image;
    private OfferButton leftButton;
    private OfferButton rightButton;

    public OfferCard(String text, Drawable image, OfferButton leftButton, OfferButton rightButton) {
        this.text = text;
        this.image = image;
        this.leftButton = leftButton;
        this.rightButton = rightButton;
    }

    public OfferCard(String text, Drawable image, OfferButton button) {
        this.text = text;
        this.image = image;
        this.leftButton = button;
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

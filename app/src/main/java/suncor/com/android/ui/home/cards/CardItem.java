package suncor.com.android.ui.home.cards;

public class CardItem {
    private String title;
    private String balance;

    public String getTitle() {
        return title;
    }

    public String getBalance() {
        return balance;
    }

    public CardItem(String title, String balance) {
        this.title = title;
        this.balance = balance;
    }
}

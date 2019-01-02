package suncor.com.android.dataObjects;

public class Hour {
    private String close;
    private String open;

    public Hour(String close, String open) {
        this.close = close;
        this.open = open;
    }

    public void setClose(String close) {
        this.close = close;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getClose() {
        return close;
    }

    public String getOpen() {
        return open;
    }
}

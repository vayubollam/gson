package suncor.com.android.model.account;

import androidx.annotation.Nullable;

public class Province {
    private String id;
    private String name;
    private String firstCharacter;

    public Province(String id, String name, String firstCharacter) {
        this.id = id;
        this.name = name;
        this.firstCharacter = firstCharacter;
    }

    public String getFirstCharacter() {
        return firstCharacter;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || !(obj instanceof Province)) {
            return false;
        }
        return ((Province) obj).id.equals(this.id);
    }
}

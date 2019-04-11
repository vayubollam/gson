package suncor.com.android.model.account;

import java.util.ArrayList;

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

    public static Province findProvince(ArrayList<Province> provincesList, String id) {
        for (Province province : provincesList) {
            if (province.id.equals(id)) {
                return province;
            }
        }
        return null;
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

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || !(obj instanceof Province)) {
            return false;
        }
        return ((Province) obj).id.equals(this.id);
    }
}

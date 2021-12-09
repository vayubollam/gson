package suncor.com.android.uicomponents.dropdown;

import androidx.recyclerview.widget.RecyclerView;

public abstract class  DropDownAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public abstract String getSelectedValue();
    public abstract String getSelectedSubValue();
    public abstract void setListener(ChildViewListener listener);
    public abstract void showUpdatePreAuthPopup();
}

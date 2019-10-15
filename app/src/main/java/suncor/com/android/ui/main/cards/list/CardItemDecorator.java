package suncor.com.android.ui.main.cards.list;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class CardItemDecorator extends RecyclerView.ItemDecoration {
    private final int mSpace;

    public CardItemDecorator(int space) {
        this.mSpace = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        if (position != 0)
            outRect.top = mSpace;
    }
}

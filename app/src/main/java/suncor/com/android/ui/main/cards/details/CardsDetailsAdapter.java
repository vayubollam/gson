package suncor.com.android.ui.main.cards.details;

import android.content.Context;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import suncor.com.android.R;
import suncor.com.android.databinding.PetroCanadaExpandedCardItemBinding;
import suncor.com.android.utilities.Consumer;

public class CardsDetailsAdapter extends RecyclerView.Adapter<CardsDetailsAdapter.CardsDetailHolder> {
    private ArrayList<ExpandedCardItem> cardItems = new ArrayList<>();
    private Consumer<ExpandedCardItem> callBack;
    private View.OnClickListener activeWashListener;
    private GestureDetector mGestureDetector;
    private MyGestureDetector myGestureDetector;
    private Context context;
    private float x1, x2;
    private float y1, y2;
    private long t1, t2;

    public CardsDetailsAdapter(Context context, Consumer<ExpandedCardItem> callBack, View.OnClickListener activeWashListener) {
        this.callBack = callBack;
        this.activeWashListener = activeWashListener;
        this.context = context;
        myGestureDetector = new MyGestureDetector();
        mGestureDetector = new GestureDetector(context, myGestureDetector);
    }

    @NonNull
    @Override
    public CardsDetailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PetroCanadaExpandedCardItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.petro_canada_expanded_card_item, parent, false);
        return new CardsDetailHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CardsDetailHolder holder, int position) {
        holder.binding.setCard(cardItems.get(position));
        holder.binding.moreButton.setOnClickListener(v -> callBack.accept(cardItems.get(position)));
    }

    public void removeCard(ExpandedCardItem expandedCardItem) {
        int position = cardItems.indexOf(expandedCardItem);
        cardItems.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, cardItems.size());
    }

    @Override
    public int getItemCount() {
        return cardItems.size();
    }

    public ArrayList<ExpandedCardItem> getCardItems() {
        return cardItems;
    }

    public void setCardItems(ArrayList<ExpandedCardItem> cardItems) {
        this.cardItems = cardItems;
    }

    public class CardsDetailHolder extends RecyclerView.ViewHolder {
        PetroCanadaExpandedCardItemBinding binding;

        public CardsDetailHolder(@NonNull PetroCanadaExpandedCardItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.activeWashButton.setOnClickListener(activeWashListener);
            binding.flBarcode.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        x1 = event.getX();
                        y1 = event.getY();
                        t1 = System.currentTimeMillis();
                        return true;
                    } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                        x2 = event.getX();
                        y2 = event.getY();
                        t2 = System.currentTimeMillis();

                        float diffTime = Math.abs(t2 - t1);
                        float diffX = x2 - x1;

                        if (diffTime < 500) {
                            if (diffX < 200) {
                                if (cardItems.get(getAdapterPosition()).isPdfShown()) {
                                    binding.barcodeImage.setImageDrawable(cardItems.get(getAdapterPosition()).getBarCode());
                                    cardItems.get(getAdapterPosition()).setPdfShown(false);
                                } else {
                                    binding.barcodeImage.setImageDrawable(cardItems.get(getAdapterPosition()).getBarCodePDF());
                                    cardItems.get(getAdapterPosition()).setPdfShown(true);
                                }
                            }
                        }
                        return true;
                    }
                    return true;
                }
            });
        }
    }

    public class MyGestureDetector implements GestureDetector.OnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return true;
        }
    }
}

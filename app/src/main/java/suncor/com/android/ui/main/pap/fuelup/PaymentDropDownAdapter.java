package suncor.com.android.ui.main.pap.fuelup;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import suncor.com.android.HomeNavigationDirections;
import suncor.com.android.R;
import suncor.com.android.databinding.AddPaymentDropDownItemBinding;
import suncor.com.android.databinding.GooglePayDropDownItemBinding;
import suncor.com.android.databinding.PaymentDropDownItemBinding;
import suncor.com.android.ui.main.wallet.payments.list.PaymentListItem;
import suncor.com.android.uicomponents.dropdown.ChildViewListener;
import suncor.com.android.uicomponents.dropdown.DropDownAdapter;
import suncor.com.android.utilities.AnalyticsUtils;
import suncor.com.android.utilities.Timber;


public class PaymentDropDownAdapter extends DropDownAdapter {

    private static final String TAG = PaymentDropDownAdapter.class.getSimpleName();

    public static final String PAYMENT_TYPE_GOOGLE_PAY = "google_pay";

    private static final int DROP_DOWN_LAYOUT = 1;
    private static final int ADD_DROP_DOWN_LAYOUT = 2;
    private static final int GOOGLE_PAY_LAYOUT = 3;

    private ArrayList<PaymentListItem> payments = new ArrayList<>();
    private int selectedPos = -1;
    private ChildViewListener listener;
    private PaymentDropDownCallbacks callbacks;
    private final Context mContext;
    private boolean showGooglePayOption = false;


    public PaymentDropDownAdapter(final Context context, PaymentDropDownCallbacks callbacks) {
        this.mContext = context;
        this.callbacks = callbacks;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == ADD_DROP_DOWN_LAYOUT){
            return new AddPaymentViewHolder(AddPaymentDropDownItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        } else if(viewType == GOOGLE_PAY_LAYOUT){
            return new GooglePayViewHolder(GooglePayDropDownItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        } else {
            return new ChildDropDownViewHolder(PaymentDropDownItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(position < payments.size()){
            ((ChildDropDownViewHolder)holder).setDataOnView(payments.get(position));
        } else if(position == payments.size() && showGooglePayOption){
            ((GooglePayViewHolder)holder).setDataOnView();
        } else {
            ((AddPaymentViewHolder)holder).setDataOnView();
        }
    }

    @Override
    public int getItemCount() {
        return payments.size() +  (showGooglePayOption ? 2 : 1);
    }

    @Override
    public int getItemViewType(int position) {
        return position < payments.size()  ? DROP_DOWN_LAYOUT : (position  == payments.size() && showGooglePayOption) ? GOOGLE_PAY_LAYOUT : ADD_DROP_DOWN_LAYOUT;
    }

    @Override
    public String getSelectedValue(){
        if (selectedPos == -1) return null;
        if(selectedPos > payments.size() - 1 && showGooglePayOption){
            return mContext.getString(R.string.google_pay);
        }
        PaymentListItem payment = payments.get(selectedPos);
        return payment.getCardInfo();
    }

    @Override
    public String getSelectedSubValue() {
        if (selectedPos == -1) return null;
        if(selectedPos > payments.size() - 1 && showGooglePayOption){
            return null;
        }
        PaymentListItem payment = payments.get(selectedPos);
        return payment.getExp();
    }

    public void addPayment(PaymentListItem paymentListItem, boolean setSelected) {
        payments.add(paymentListItem);

        if (setSelected) {
            setSelectedPos(paymentListItem.getPaymentDetail().getId());
        }

        notifyDataSetChanged();
    }

    public void addPayments(List<PaymentListItem> paymentListItem) {
        payments.addAll(paymentListItem);
        notifyDataSetChanged();
    }

    public void addGooglePayOption(String userSelectedSourceId){
        showGooglePayOption = true;
        notifyDataSetChanged();
        setSelectedPos(userSelectedSourceId);
    }

    public void setSelectedPos(String userPaymentSourceId) {
        int i = 0;
        boolean found = false;
        for (PaymentListItem payment : payments) {
            if (payment.getPaymentDetail().getId().equals(userPaymentSourceId)) {
                found = true;
                break;
            }
            i++;
        }

        if (found) {
            selectedPos = i;

            if(Objects.nonNull(listener)) {
                listener.onSelectValue(getSelectedValue(), getSelectedSubValue());
            }

            notifyDataSetChanged();
        } else if(showGooglePayOption){
            selectedPos = payments.size();
            listener.onSelectGooglePay(getSelectedValue());
            callbacks.onPaymentChanged(PAYMENT_TYPE_GOOGLE_PAY);
        }
    }

    @Override
    public void setListener(ChildViewListener listener) {
        this.listener = listener;
    }

    //fixed limit listing
     class ChildDropDownViewHolder extends RecyclerView.ViewHolder {
            PaymentDropDownItemBinding binding;

            ChildDropDownViewHolder(@NonNull PaymentDropDownItemBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }

            public void setDataOnView(PaymentListItem value){
                try {
                    binding.header.setText(value.getCardInfo());
                    binding.subheader.setText(value.getExp());
                }catch (NullPointerException ex){
                    Timber.e(TAG,  "Error on inflating data , " + ex.getMessage());
                }
                binding.container.setSelected(selectedPos == getAdapterPosition());

                if (selectedPos == getAdapterPosition()) {
                    if(Objects.nonNull(listener)) {
                        listener.onSelectValue(value.getCardInfo(), value.getExp());
                    }
                }

                binding.container.setOnClickListener(v -> {
                    notifyItemChanged(selectedPos);
                    selectedPos = getAdapterPosition();
                    notifyItemChanged(selectedPos);

                    if(Objects.nonNull(listener)) {
                        listener.onSelectValue(value.getCardInfo(), value.getExp());
                        listener.expandCollapse();
                    }

                    callbacks.onPaymentChanged(value.getPaymentDetail().getId());
                });

            }
        }

	//Manual limit
     class GooglePayViewHolder extends RecyclerView.ViewHolder {
        GooglePayDropDownItemBinding binding;

        GooglePayViewHolder(@NonNull GooglePayDropDownItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setDataOnView(){
            binding.container.setSelected(selectedPos == getAdapterPosition());

            binding.container.setOnClickListener(v -> {
                notifyItemChanged(selectedPos);
                selectedPos = getAdapterPosition();
                notifyItemChanged(selectedPos);

                if(Objects.nonNull(listener)) {
                    listener.onSelectGooglePay(binding.header.getText().toString());
                    listener.expandCollapse();
                }

                callbacks.onPaymentChanged(PAYMENT_TYPE_GOOGLE_PAY);
            });
        }
    }

	//Manual limit
     class AddPaymentViewHolder extends RecyclerView.ViewHolder {
        AddPaymentDropDownItemBinding binding;

        AddPaymentViewHolder(@NonNull AddPaymentDropDownItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setDataOnView(){
            binding.container.setSelected(selectedPos == getAdapterPosition());

            binding.container.setOnClickListener(v -> {
                AnalyticsUtils.logEvent(mContext, AnalyticsUtils.Event.buttonTap,
                        new Pair<>(AnalyticsUtils.Param.buttonText, mContext.getString(R.string.add_new_payment).toLowerCase()));
                AnalyticsUtils.setCurrentScreenName((Activity) mContext, "pay-at-pump-add-credit-card");
                Navigation.findNavController((Activity) mContext, R.id.nav_host_fragment).navigate(R.id.action_fuel_up_to_addPaymentFragment);
            });
        }
    }
}


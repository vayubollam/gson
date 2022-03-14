package suncor.com.android.ui.main.pap.selectpump;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import suncor.com.android.R;
import suncor.com.android.analytics.pap.SelectPumpAnalytics;
import suncor.com.android.databinding.TutorialSelectPumpBinding;
import suncor.com.android.ui.common.SuncorButton;
import suncor.com.android.uicomponents.pagerindicator.CircleIndicator;

public class SelectPumpHelpDialogFragment extends DialogFragment {


    public SelectPumpHelpDialogFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_pump_help, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String uriPrefix = "android.resource://" + getActivity().getPackageName() + "/";
        RecyclerView tutorialRecycler = getView().findViewById(R.id.tutorial_recycler);
        CircleIndicator pageIndicator = getView().findViewById(R.id.page_indicator);
        SuncorButton buttonClose = getView().findViewById(R.id.button_close);

        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(tutorialRecycler);

        TutorialAdapter adapter = new TutorialAdapter(setUpImageSlides());
        tutorialRecycler.setAdapter(adapter);

        pageIndicator.attachToRecyclerView(tutorialRecycler, pagerSnapHelper);
        adapter.registerAdapterDataObserver(pageIndicator.getAdapterDataObserver());
        buttonClose.setOnClickListener(v -> dismissAllowingStateLoss());
    }

    private List<Drawable> setUpImageSlides() {
        List<Drawable> images = new ArrayList<>();
        images.add(getContext().getDrawable(R.drawable.pump_tooltip_1));
        images.add(getContext().getDrawable(R.drawable.pump_tooltip_2));
        return images;
    }

    class TutorialAdapter extends RecyclerView.Adapter<TutorialViewHolder> {

        private List<Drawable> images;

        TutorialAdapter(List<Drawable> images) {
            this.images = images;
        }

        @NonNull
        @Override
        public TutorialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TutorialSelectPumpBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.tutorial_select_pump, parent, false);
            return new TutorialViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull TutorialViewHolder holder, int position) {
            holder.binding.imageView.setImageDrawable(images.get(position));
        }

        @Override
        public int getItemCount() {
            return images.size();
        }
    }

    class TutorialViewHolder extends RecyclerView.ViewHolder {
        TutorialSelectPumpBinding binding;

        TutorialViewHolder(@NonNull TutorialSelectPumpBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        SelectPumpAnalytics.logSelectPumpHelpScreenName(requireActivity());

        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

}

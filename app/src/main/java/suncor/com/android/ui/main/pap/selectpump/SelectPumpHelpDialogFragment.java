package suncor.com.android.ui.main.pap.selectpump;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import suncor.com.android.R;
import suncor.com.android.databinding.TutorialScreenListitemBinding;
import suncor.com.android.ui.common.SuncorButton;
import suncor.com.android.ui.tutorial.TutorialContent;
import suncor.com.android.uicomponents.pagerindicator.CircleIndicator;
import suncor.com.android.utilities.AnalyticsUtils;

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

        TutorialAdapter adapter = new TutorialAdapter(setUpTutorialSlides(uriPrefix));
        tutorialRecycler.setAdapter(adapter);
        tutorialRecycler.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            VideoView video = v.findViewById(R.id.tutorial_video);
            video.start();
        });

        pageIndicator.attachToRecyclerView(tutorialRecycler, pagerSnapHelper);
        adapter.registerAdapterDataObserver(pageIndicator.getAdapterDataObserver());
        buttonClose.setOnClickListener(v -> dismissAllowingStateLoss());
    }

    private List<TutorialContent> setUpTutorialSlides(String uriPrefix) {
        List<TutorialContent> tutorialContents = new ArrayList<>();
        tutorialContents.add(new TutorialContent("", uriPrefix + R.raw.tutorial_1));
        tutorialContents.add(new TutorialContent("", uriPrefix + R.raw.tutorial_2));
        return tutorialContents;
    }

    class TutorialAdapter extends RecyclerView.Adapter<TutorialViewHolder> {

        private List<TutorialContent> tutorials;

        TutorialAdapter(List<TutorialContent> tutorials) {
            this.tutorials = tutorials;
        }

        @NonNull
        @Override
        public TutorialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TutorialScreenListitemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.tutorial_screen_listitem, parent, false);
            return new TutorialViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull TutorialViewHolder holder, int position) {
            holder.binding.tutorialHeader.setText(tutorials.get(position).getHeader());
            holder.binding.tutorialVideo.setVideoURI(tutorials.get(position).getVideoUri());
            holder.binding.tutorialVideo.setZOrderOnTop(true);
            holder.binding.tutorialVideo.setOnPreparedListener(mp -> mp.setLooping(true));
            holder.binding.tutorialVideo.start();
        }

        @Override
        public int getItemCount() {
            return tutorials.size();
        }
    }

    class TutorialViewHolder extends RecyclerView.ViewHolder {
        TutorialScreenListitemBinding binding;

        TutorialViewHolder(@NonNull TutorialScreenListitemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        AnalyticsUtils.setCurrentScreenName(getActivity(), "select-pump-help");
    }

}

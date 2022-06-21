package suncor.com.android.ui.tutorial;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import suncor.com.android.R;
import suncor.com.android.databinding.FragmentTutorialBinding;
import suncor.com.android.databinding.TutorialScreenListitemBinding;
import suncor.com.android.ui.SplashActivity;
import suncor.com.android.utilities.AnalyticsUtils;

public class TutorialFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentTutorialBinding binding = FragmentTutorialBinding.inflate(inflater, container, false);
        if (getActivity() != null) {
            String uriPrefix = "android.resource://" + getActivity().getPackageName() + "/";
            getActivity().getWindow().setStatusBarColor(Color.WHITE);
            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
            binding.tutorialRecycler.setLayoutManager(linearLayoutManager);
            PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
            pagerSnapHelper.attachToRecyclerView(binding.tutorialRecycler);
            TutorialAdapter adapter = new TutorialAdapter(setUpTutorialSlides(uriPrefix));
            binding.tutorialRecycler.setAdapter(adapter);
            binding.tutorialRecycler.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                VideoView video = v.findViewById(R.id.tutorial_video);
                video.start();
            });
            binding.pageIndicator.attachToRecyclerView(binding.tutorialRecycler, pagerSnapHelper);
            adapter.registerAdapterDataObserver(binding.pageIndicator.getAdapterDataObserver());
            binding.buttonClose.setOnClickListener(v -> {
                if (getActivity() instanceof SplashActivity) {
                    ((SplashActivity) getActivity()).openMainActivity(false);
                } else {
                    getActivity().onBackPressed();
                }
            });
        }
        return binding.getRoot();
    }

    private List<TutorialContent> setUpTutorialSlides(String uriPrefix) {
        List<TutorialContent> tutorialContents = new ArrayList<>();
        tutorialContents.add(new TutorialContent(getString(R.string.tutorial_silde_page4_header), uriPrefix + R.raw.dollar_off_tutorial));
      //  tutorialContents.add(new TutorialContent(getString(R.string.tutorial_silde_page1_header), uriPrefix + R.raw.tutorial_1));
      //  tutorialContents.add(new TutorialContent(getString(R.string.tutorial_silde_page2_header), uriPrefix + R.raw.tutorial_2));
       // tutorialContents.add(new TutorialContent(getString(R.string.tutorial_silde_page3_header), uriPrefix + R.raw.tutorial_3));
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
        AnalyticsUtils.setCurrentScreenName(getActivity(),"onboarding-tutorial-carousel" );
    }
}

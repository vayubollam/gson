package suncor.com.android.ui.tutorial;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class TutorialFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getActivity() != null) {
            getActivity().getWindow().setStatusBarColor(Color.WHITE);
            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        FragmentTutorialBinding binding = FragmentTutorialBinding.inflate(inflater, container, false);
        TutorialContent slide1 = new TutorialContent(getString(R.string.tutorial_silde_page1_header), getResources().getDrawable(R.drawable.feature_img_1, null));
        TutorialContent slide2 = new TutorialContent(getString(R.string.tutorial_silde_page2_header), getResources().getDrawable(R.drawable.feature_img_2, null));
        TutorialContent slide3 = new TutorialContent(getString(R.string.tutorial_silde_page3_header), getResources().getDrawable(R.drawable.feature_img_3, null));
        List<TutorialContent> tutorialContents = new ArrayList<>();
        tutorialContents.add(slide1);
        tutorialContents.add(slide2);
        tutorialContents.add(slide3);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);

        binding.tutorialRecycler.setLayoutManager(linearLayoutManager);
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(binding.tutorialRecycler);
        TutorialAdapter adapter = new TutorialAdapter(tutorialContents);
        binding.tutorialRecycler.setAdapter(adapter);
        binding.pageIndicator.attachToRecyclerView(binding.tutorialRecycler, pagerSnapHelper);
        adapter.registerAdapterDataObserver(binding.pageIndicator.getAdapterDataObserver());
        binding.buttonClose.setOnClickListener(v -> ((SplashActivity) getActivity()).openMainActivity(false));

        return binding.getRoot();
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
            holder.binding.tutorialImage.setImageDrawable(tutorials.get(position).getImage());
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
}

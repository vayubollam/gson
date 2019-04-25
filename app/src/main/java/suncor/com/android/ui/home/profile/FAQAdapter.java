package suncor.com.android.ui.home.profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import suncor.com.android.R;
import suncor.com.android.databinding.QuestionItemBinding;
import suncor.com.android.databinding.TopQuestionFooterBinding;
import suncor.com.android.model.account.Question;

public class FAQAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private QuestionItemBinding binding;
    private TopQuestionFooterBinding footerBinding;
    private ArrayList<Question> questions;
    private Context context;
    private static final int FOOTER_VIEW = 1;
    private static final int HEADER_VIEW = 2;

    public FAQAdapter(ArrayList<Question> questions, Context ctx) {
        this.questions = questions;
        context = ctx;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == FOOTER_VIEW) {
            footerBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.top_question_footer, parent, false);
            return new FooterViewHolder(footerBinding.getRoot());
        }
        if (viewType == HEADER_VIEW) {
            return new HeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.top_question_header, parent, false));
        }
        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.question_item, parent, false);
        return new NormalViewHolder(binding.getRoot());


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof FooterViewHolder) {
            } else if (holder instanceof NormalViewHolder) {
                NormalViewHolder nvh = (NormalViewHolder) holder;
                binding.questionTxt.setText(questions.get(position - 1).getQuestion());
            }
    }

    @Override
    public int getItemCount() {
        if (questions == null)
            return 0;
        if (questions.size() == 0)
            return 1;
        return questions.size() + 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADER_VIEW;
        }

        if (position == questions.size() + 1) {
            return FOOTER_VIEW;
        }
        return super.getItemViewType(position);

    }

    class NormalViewHolder extends ViewHolder {

        public NormalViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    class FooterViewHolder extends ViewHolder {

        public FooterViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    class HeaderViewHolder extends ViewHolder {

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}

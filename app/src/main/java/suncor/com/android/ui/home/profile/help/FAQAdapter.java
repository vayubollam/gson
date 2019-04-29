package suncor.com.android.ui.home.profile.help;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import suncor.com.android.utilities.Consumer;

public class FAQAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Question> questions;
    private Context context;
    private static final int FOOTER_VIEW = 1;
    private static final int HEADER_VIEW = 2;
    private Consumer<Question> callback;

    public FAQAdapter(ArrayList<Question> questions, Context ctx, Consumer<Question> callback) {
        this.questions = questions;
        context = ctx;
        this.callback = callback;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == FOOTER_VIEW) {
            TopQuestionFooterBinding footerBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.top_question_footer, parent, false);
            return new FooterViewHolder(footerBinding);
        }
        if (viewType == HEADER_VIEW) {
            return new HeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.top_question_header, parent, false));
        }
        QuestionItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.question_item, parent, false);
        return new NormalViewHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FooterViewHolder) {
            FooterViewHolder fvh = (FooterViewHolder) holder;
            fvh.binding.callUsButton.setOnClickListener(v ->
            {
                String phone = context.getResources().getString(R.string.customer_support_number);
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                context.startActivity(intent);
            });
            fvh.binding.emailButton.setOnClickListener(v ->
            {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", context.getResources().getString(R.string.customer_support_email), null));
                context.startActivity(Intent.createChooser(emailIntent, null));
            });
        } else if (holder instanceof NormalViewHolder) {
            NormalViewHolder nvh = (NormalViewHolder) holder;
            nvh.binding.questionTxt.setText(questions.get(position - 1).getQuestion());
            nvh.binding.getRoot().setOnClickListener(v ->
                    callback.accept(questions.get(position - 1)));
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
        QuestionItemBinding binding;

        public NormalViewHolder(QuestionItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    class FooterViewHolder extends ViewHolder {
        TopQuestionFooterBinding binding;

        public FooterViewHolder(TopQuestionFooterBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
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

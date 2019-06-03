package suncor.com.android.ui.main.profile.help;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import suncor.com.android.R;
import suncor.com.android.databinding.QuestionItemBinding;
import suncor.com.android.model.account.Question;
import suncor.com.android.utilities.Consumer;

public class FAQAdapter extends RecyclerView.Adapter<FAQAdapter.NormalViewHolder> {
    private ArrayList<Question> questions;
    private Consumer<Question> callback;

    public FAQAdapter(ArrayList<Question> questions, Context ctx, Consumer<Question> callback) {
        this.questions = questions;
        this.callback = callback;
    }

    @NonNull
    @Override
    public NormalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        QuestionItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.question_item, parent, false);
        return new NormalViewHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull NormalViewHolder holder, int position) {
        holder.binding.questionTxt.setText(questions.get(position).getQuestion());
        holder.binding.getRoot().setOnClickListener(v ->
                callback.accept(questions.get(position)));

    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    class NormalViewHolder extends RecyclerView.ViewHolder {
        QuestionItemBinding binding;

        public NormalViewHolder(QuestionItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

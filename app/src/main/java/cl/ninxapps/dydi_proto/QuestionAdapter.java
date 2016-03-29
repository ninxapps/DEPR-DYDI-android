package cl.ninxapps.dydi_proto;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

/**
 * Created by jose on 25/3/16.
 */
public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {

    private List<Question> questionList;

    public QuestionAdapter(List<Question> questionList) {
        this.questionList = questionList;
    }


    @Override
    public int getItemCount() {
        return questionList.size();
    }

    @Override
    public void onBindViewHolder(QuestionViewHolder contactViewHolder, int i) {
        Question q = questionList.get(i);
        contactViewHolder.vText.setText(q.text);
        contactViewHolder.vComment.setText("" + q.comments);
        contactViewHolder.vVote.setText("" + (q.yesCount + q.noCount));
        contactViewHolder.vPercent.setText(q.yesCount + "%");

        contactViewHolder.tUp.setOnClickListener(new VoteListener(q, contactViewHolder, i, 1));
        contactViewHolder.tDown.setOnClickListener(new VoteListener(q, contactViewHolder, i, -1));

        if (q.answered) {
            if (q.answer == -1){
                contactViewHolder.answer.setBackgroundColor(Color.rgb(255, 0, 0));
            } else {
                contactViewHolder.answer.setBackgroundColor(Color.rgb(0, 255, 0));
            }
            contactViewHolder.circle.setColorFilter(q.color);
            contactViewHolder.circle.setRotation(q.yesCount*-180/100);

            contactViewHolder.options.setVisibility(View.GONE);
            contactViewHolder.results.setVisibility(View.VISIBLE);
            contactViewHolder.answer.setVisibility(View.VISIBLE);

        } else {
            contactViewHolder.options.setVisibility(View.VISIBLE);
            contactViewHolder.results.setVisibility(View.GONE);
            contactViewHolder.answer.setVisibility(View.GONE);
        }
    }

    private class VoteListener implements View.OnClickListener{

        private int position;
        private QuestionViewHolder contactViewHolder;
        Question q;
        int vote;

        public VoteListener(Question q, QuestionViewHolder contactViewHolder,int position, int vote) {
            this.q = q;
            this.contactViewHolder = contactViewHolder;
            this.position = position;
            this.vote = vote;
        }

        public Question vote(QuestionViewHolder v, Question q) {
            // do it

            FrameLayout options = v.options;
            FrameLayout results = v.results;

            ImageView circle = v.circle;
            TextView percentView = v.vPercent;

            ImageView answer = v.answer;

            Random ran = new Random();
            q.yesCount = ran.nextInt(100);
            q.noCount = 100 - q.yesCount;

            percentView.setText(q.yesCount + "%");

            if (q.yesCount < 50) {
                q.color = Color.rgb(255, 255/50*(q.yesCount), 0);
            } else {
                q.color = Color.rgb(255-(255/50*(q.yesCount-50)), 255, 0);
            }


            if (vote == -1){
                answer.setBackgroundColor(Color.rgb(255, 0, 0));
            } else {
                answer.setBackgroundColor(Color.rgb(0, 255, 0));
            }


            q.answer = vote;
            q.answered = true;

            circle.setColorFilter(q.color);
            circle.setRotation(0);

            options.setVisibility(View.GONE);
            results.setVisibility(View.VISIBLE);
            answer.setVisibility(View.VISIBLE);

            circle.animate().rotationBy(q.yesCount*-180/100).start();

            return q;

        }

        @Override
        public void onClick(View v) {
            q = vote(contactViewHolder, q);
            if (q != null) {
                questionList.set(position, q);
            }
        }
    }

    @Override
    public QuestionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.question_item_card, viewGroup, false);
        QuestionViewHolder holder = new QuestionViewHolder(itemView, i);

        return holder;
    }

    public class QuestionViewHolder extends RecyclerView.ViewHolder {

        protected TextView vText;
        protected TextView vPercent;
        protected TextView vCategory;
        protected TextView vDate;
        protected TextView vNsfw;
        protected TextView vComment;
        protected TextView vVote;
        protected ImageButton tUp;
        protected ImageButton tDown;
        protected FrameLayout options;
        protected FrameLayout results;
        protected ImageView answer;
        protected ImageView circle;
        protected Question q;

        public QuestionViewHolder(View v, int position) {
            super(v);
            final View questionView = v;
            final QuestionViewHolder self = this;
            q = questionList.get(position);

            vText =  (TextView) v.findViewById(R.id.text);
            vPercent =  (TextView) v.findViewById(R.id.percent);
            vComment =  (TextView) v.findViewById(R.id.comment);
            vVote =  (TextView) v.findViewById(R.id.vote);

            options =  (FrameLayout) v.findViewById(R.id.options);
            results =  (FrameLayout) v.findViewById(R.id.results);
            answer =  (ImageView) v.findViewById(R.id.answer);

            circle = (ImageView)v.findViewById(R.id.circle);


            tUp =  (ImageButton) v.findViewById(R.id.thumb_up);
            tDown =  (ImageButton) v.findViewById(R.id.thumb_down);

            Log.i("QVH", "Position: " + position);

            /*tUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // do something
                    q = self.vote(questionView, q, 1);
                    if (q != null) {
                        questionList.set(self.getAdapterPosition(), q);
                    }
                }
            });
*/


        }
    }

}

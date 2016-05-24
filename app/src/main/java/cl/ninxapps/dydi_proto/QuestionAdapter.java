package cl.ninxapps.dydi_proto;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Random;

/**
 * Created by jose on 25/3/16.
 */
public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {

    private List<Question> questionList;
    private Context mContext;
    private int[] colors;
    private int visibleThreshold = 5;
    private int firstVisibleItem, lastVisibleItem, height;
    private boolean loading;
    private int originalHeight;

    public QuestionAdapter(List<Question> questionList, Context mContext, RecyclerView recyclerView) {
        this.questionList = questionList;
        this.mContext = mContext;
        colors = new int[8];
        colors[0] = Color.parseColor("#666699");
        colors[1] = Color.parseColor("#336699");
        colors[2] = Color.parseColor("#CC6666");
        colors[3] = Color.parseColor("#6699FF");
        colors[4] = Color.parseColor("#666666");
        colors[5] = Color.parseColor("#9966CC");
        colors[6] = Color.parseColor("#669966");
        colors[7] = Color.parseColor("#CC66CC");

        height = recyclerView.getMeasuredHeight();
        originalHeight = -1;

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int state = RecyclerView.SCROLL_STATE_IDLE;
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                state = RecyclerView.SCROLL_STATE_SETTLING;
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState){

                if (newState == recyclerView.SCROLL_STATE_IDLE && state != newState){
                    state = newState;
                    final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();

                    if (lastVisibleItem != firstVisibleItem){

                        Rect firstRect = new Rect();
                        Rect lastRect = new Rect();

                        View firstView = linearLayoutManager.getChildAt(0);
                        View lastView = linearLayoutManager.getChildAt(1);

                        if (firstView != null && lastView != null){
                            firstView.getGlobalVisibleRect(firstRect);
                            lastView.getGlobalVisibleRect(lastRect);

                            if (firstRect.height() > lastRect.height()){
                                recyclerView.smoothScrollToPosition(firstVisibleItem);
                            } else {
                                recyclerView.smoothScrollToPosition(lastVisibleItem);
                            }

                        }


                    }

                }

            }
        });
    }


    @Override
    public int getItemCount() {
        return questionList.size();
    }

    @Override
    public void onBindViewHolder(QuestionViewHolder questionVH, int i) {
        Question q = questionList.get(i);
        questionVH.vText.setText(q.text);

        questionVH.tUp.setOnClickListener(new VoteListener(q, questionVH, i, 1));
        questionVH.tDown.setOnClickListener(new VoteListener(q, questionVH, i, -1));

        questionVH.vContent.setBackgroundColor(colors[i % colors.length]);

        int height = originalHeight;

        if (q.answered) {
            /*if (q.answer == -1){
                questionVH.answer.setBackgroundColor(Color.rgb(255, 0, 0));
            } else {
                questionVH.answer.setBackgroundColor(Color.rgb(0, 255, 0));
            }*/

            ViewGroup.LayoutParams layoutParams = questionVH.vContent.getLayoutParams();
            layoutParams.height = (int)(height*0.8);
            questionVH.vContent.setLayoutParams(layoutParams);

            questionVH.vOptions.setVisibility(View.INVISIBLE);
            questionVH.vResults.setVisibility(View.VISIBLE);

        } else {
            if (height > 0){
                ViewGroup.LayoutParams layoutParams = questionVH.vContent.getLayoutParams();
                layoutParams.height = height;
                questionVH.vContent.setLayoutParams(layoutParams);
            }
            questionVH.vResults.setVisibility(View.INVISIBLE);
            questionVH.vOptions.setVisibility(View.VISIBLE);

        }

        if (q.nsfw){
            questionVH.vNsfw.setVisibility(View.VISIBLE);
        } else {
            questionVH.vNsfw.setVisibility(View.INVISIBLE);
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

            if (originalHeight == -1){
                originalHeight = v.vContent.getMeasuredHeight();
            }

            Random ran = new Random();
            q.yesCount = ran.nextInt(100);
            q.noCount = 100 - q.yesCount;

            if (q.yesCount < 50) {
                q.color = Color.rgb(255, 255/50*(q.yesCount), 0);
            } else {
                q.color = Color.rgb(255-(255/50*(q.yesCount-50)), 255, 0);
            }

            q.answer = vote;
            q.answered = true;

            if (vote == -1){
                v.vResultText.setText(q.noCount + " people out of " + (q.noCount + q.yesCount) + " voted like you");
            } else {
                v.vResultText.setText(q.yesCount + " people out of " + (q.noCount + q.yesCount) + " voted like you");
            }

            final QuestionViewHolder fVH = v;
            int height = originalHeight;
            Log.i("HEIGHT", height+"");

            v.vResults.setVisibility(View.VISIBLE);
            ValueAnimator anim = ValueAnimator.ofInt(height, (int)(height*0.8));
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int val = (Integer) valueAnimator.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = fVH.vContent.getLayoutParams();
                    layoutParams.height = val;
                    fVH.vContent.setLayoutParams(layoutParams);
                }
            });
            anim.setDuration(300);
            v.vOptions.animate().alpha(0f).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    fVH.vOptions.setVisibility(View.INVISIBLE);
                    fVH.vOptions.setAlpha(1f);
                }
            });
            anim.start();


            /*String bgColor = "voteScale" + (int)Math.floor(q.yesCount*0.08);
            Log.i("COLOR", bgColor);
            try {
                int id = R.color.class.getField(bgColor).getInt(null);
                v.vBack.setBackgroundColor(ContextCompat.getColor(mContext, id));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }*/

            //v.vCard.startAnimation(flipAnimation);

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

        protected RelativeLayout vCard;
        protected RelativeLayout vContent;
        protected TextView vText;
        protected TextView vPercent;
        protected TextView vCategory;
        protected TextView vDate;
        protected TextView vNsfw;
        protected TextView vStats;
        protected TextView vResultText;
        protected ImageButton tUp;
        protected ImageButton tDown;
        protected FrameLayout vOptions;
        protected RelativeLayout vResults;
        protected Question q;


        public QuestionViewHolder(View v, int position) {
            super(v);
            final View questionView = v;
            final QuestionViewHolder self = this;
            q = questionList.get(position);

            vCard =  (RelativeLayout) v.findViewById(R.id.card_view);
            vContent =  (RelativeLayout) v.findViewById(R.id.content);
            // vBack =  (RelativeLayout) v.findViewById(R.id.back);

            vText =  (TextView) v.findViewById(R.id.text);

            vCategory =  (TextView) v.findViewById(R.id.category);
            vNsfw =  (TextView) v.findViewById(R.id.nsfw);

            vStats =  (TextView) v.findViewById(R.id.stats);

            vOptions =  (FrameLayout) v.findViewById(R.id.options);
            vResults =  (RelativeLayout) v.findViewById(R.id.results);
            vResultText = (TextView) v.findViewById(R.id.results_text);

            tUp =  (ImageButton) v.findViewById(R.id.thumb_up);
            tDown =  (ImageButton) v.findViewById(R.id.thumb_down);

        }
    }

}

package cl.ninxapps.dydi_proto;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import kotlin.Pair;

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
    private int bottomPadding;

    public QuestionAdapter(List<Question> questionList, Context mContext, RecyclerView recyclerView, int padding) {
        this.bottomPadding = padding;
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

    public void setBottomPadding(int padding){
        this.bottomPadding = padding;
    }


    @Override
    public int getItemCount() {
        return questionList.size();
    }

    @Override
    public void onBindViewHolder(QuestionViewHolder questionVH, int i) {
        Question q = questionList.get(i);
        questionVH.vText.setText(q.text);
        questionVH.vCategory.setText(q.category);

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

            /*if (q.answer == 1) {
                questionVH.vResultText.setBackgroundColor(ContextCompat.getColor(questionVH.vCard.getContext(), R.color.voteScale8));
            }
            else {
                questionVH.vResultText.setBackgroundColor(ContextCompat.getColor(questionVH.vCard.getContext(), R.color.voteScale0));
            }*/

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


        //Called when a person votes through hand icons
        public Question vote(QuestionViewHolder v, Question q) {
            // do it


            final int fQ = q.id;

            List<Pair<String, String>> params = new ArrayList<Pair<String, String>>() {{
                add(new Pair<>("answer[question_id]", fQ+""));
                add(new Pair<>("answer[choice]", vote+""));
            }};

            Fuel.post(GlobalConstants.API+"/answers", params).responseString(new Handler<String>() {
                @Override
                public void failure(Request request, Response response, FuelError error) {
                    //do something when it is failure
                    CharSequence text = "Question Faaaaaail: " + error.toString();
                    Log.e("FUEL", error.toString());
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(contactViewHolder.tDown.getContext(), text, duration);
                    toast.show();
                }

                @Override
                public void success(Request request, Response response, String data) {
                    //do something when it is successful
                    CharSequence text = "Answer ok";
                    Log.i("FUEL", "Answer ok");
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(contactViewHolder.tDown.getContext(), text, duration);
                    toast.show();
                }
            });

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

            /*
            if (vote == -1){
                v.vResultText.setText(q.noCount + " people out of " + (q.noCount + q.yesCount) + " voted like you");
            } else {
                v.vResultText.setText(q.yesCount + " people out of " + (q.noCount + q.yesCount) + " voted like you");
            }
            */

            int percent = (int) Math.round(q.yesCount*1.0/(q.noCount+q.yesCount)*100.0);

            if(percent <= 100 && percent > 75){
                v.vResultIcon.setImageResource(R.drawable.very_normal);
                v.vResultIconText.setText("Normal");
            } else if (percent <= 75 && percent > 50){
                v.vResultIcon.setImageResource(R.drawable.kinda_normal);
                v.vResultIconText.setText("Kinda normal");
            } else if (percent <= 50 && percent > 40){
                v.vResultIcon.setImageResource(R.drawable.not_normal);
                v.vResultIconText.setText("Not normal");
            } else if (percent <= 40 && percent > 20){
                v.vResultIcon.setImageResource(R.drawable.weird);
                v.vResultIconText.setText("Weird");
            } else if (percent <= 20){
                v.vResultIcon.setImageResource(R.drawable.very_weird);
                v.vResultIconText.setText("Very weird");
            }
            v.vResultText.setText(percent+"% think this is normal");

            final QuestionViewHolder fVH = v;
            int height = originalHeight;
            int newHeight;
            newHeight = height-(v.vResultIcon.getHeight()+v.vResultIconText.getHeight()+v.vResultText.getHeight()+10);

            v.vResults.setVisibility(View.VISIBLE);
            //ValueAnimator anim = ValueAnimator.ofInt(height, (int)(height*0.75));
            ValueAnimator anim = ValueAnimator.ofInt(height, newHeight);
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
        QuestionViewHolder holder = new QuestionViewHolder(itemView, i, this.bottomPadding);

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
        protected TextView vResultIconText;
        protected ImageButton tUp;
        protected ImageButton tDown;
        protected FrameLayout vOptions;
        protected RelativeLayout vResults;
        protected Question q;
        protected ImageView vResultIcon;


        public QuestionViewHolder(View v, int position, int bottomPadding) {
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
            vResultIcon = (ImageView) v.findViewById(R.id.results_icon);
            vResultText = (TextView) v.findViewById(R.id.results_text);
            vResultIconText = (TextView) v.findViewById(R.id.results_icon_text);

            tUp =  (ImageButton) v.findViewById(R.id.thumb_up);
            tDown =  (ImageButton) v.findViewById(R.id.thumb_down);

        }
    }

}

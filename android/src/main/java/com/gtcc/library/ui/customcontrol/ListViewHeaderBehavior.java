package com.gtcc.library.ui.customcontrol;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.*;
import com.gtcc.library.R;
import com.gtcc.library.util.LogUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ShenCV on 8/19/13.
 */
public class ListViewHeaderBehavior extends ListViewBehaviorBase {
    private final static int RELEASE_To_REFRESH = 0;
    private final static int PULL_To_REFRESH = 1;
    private final static int REFRESHING = 2;
    private final static int DONE = 3;

    private RefreshableListView listView;

    private LinearLayout headView; // header

    private TextView tipsTextview;//pull to refresh
    private TextView lastUpdatedTextView;//last update
    private ImageView arrowImageView;//arrow image
    private ProgressBar progressBar;//progress bar

    private RotateAnimation animation;
    private RotateAnimation reverseAnimation;

    // to ensure the value of startY been loaded only once in a touch event
    private boolean isRecord;

    private int headContentWidth;
    private int headContentHeight;

    private int startY;//start height, use to record the height to header
    private int firstItemIndex;

    private int state;

    private boolean isBack;

    private final static String TAG = LogUtils
            .makeLogTag(ListViewHeaderBehavior.class);

    public ListViewHeaderBehavior(RefreshableListView view){
        listView = view;
        // this animation can rotate the arrow image
        animation = new RotateAnimation(0, -180,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(250);
        animation.setFillAfter(true);

        reverseAnimation = new RotateAnimation(-180, 0,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        reverseAnimation.setInterpolator(new LinearInterpolator());
        reverseAnimation.setDuration(250);
        reverseAnimation.setFillAfter(true);
    }

    @Override
    public LinearLayout init(Context context){
        // get contents from header view
        LayoutInflater inflater = LayoutInflater.from(context);

        headView = (LinearLayout) inflater.inflate(R.layout.refreshable_listview_header, null);//add header to ListView

        arrowImageView = (ImageView) headView.findViewById(R.id.head_arrowImageView);
        arrowImageView.setMinimumWidth(50);
        arrowImageView.setMinimumHeight(50);
        progressBar = (ProgressBar) headView
                .findViewById(R.id.head_progressBar);
        tipsTextview = (TextView) headView.findViewById(R.id.head_tipsTextView);
        lastUpdatedTextView = (TextView) headView
                .findViewById(R.id.head_lastUpdatedTextView);

        measureView(headView);
        headContentHeight = headView.getMeasuredHeight();
        headContentWidth = headView.getMeasuredWidth();

        headView.setPadding(0, -1 * headContentHeight, 0, 0);//setPadding(int left, int top, int right, int bottom)
        headView.invalidate();//Invalidate the whole view

        Log.v("size", "width:" + headContentWidth + " height:"
                + headContentHeight);

        return headView;
    }

    @Override
    public void onScroll(int firstVisibleItem, int visibleItemCount, int totalItemCount){
        firstItemIndex = firstVisibleItem;
    }

    @Override
    public void onTouch(MotionEvent event){
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (firstItemIndex == 0 && !isRecord) {
                    // if first item index is 0，and startY has not been recorded, then set isRecord = true;
                    startY = (int) event.getY();
                    isRecord = true;

                    Log.v(TAG, "record current position when pull down‘");
                }
                break;

            case MotionEvent.ACTION_UP:

                if (state != REFRESHING) {
                    if (state == PULL_To_REFRESH) {//pull down
                        state = DONE;
                        setViewByState();

                        Log.v(TAG, "pull down");
                    }
                    else if (state == RELEASE_To_REFRESH) {//release
                        state = REFRESHING;
                        setViewByState();
                        // the real refresh method
                        listView.onRefreshHeader();

                        Log.v(TAG, "release");
                    }
                }

                isRecord = false;
                isBack = false;

                break;

            case MotionEvent.ACTION_MOVE:
                int tempY = (int) event.getY();
                int heightDiff = tempY - startY;

                if (!isRecord && firstItemIndex == 0) {
                    Log.v(TAG, "record current position when move");
                    isRecord = true;
                    startY = tempY;
                }
                if (state != REFRESHING && isRecord) {//if not refreshing，but startY has value：tempY is the variable height，startY is start height
                    switch (state)
                    {
                        case RELEASE_To_REFRESH:
                            if ((heightDiff < headContentHeight)//current height - start height < header height
                                    && (heightDiff) > 0) {
                                state = PULL_To_REFRESH;
                                setViewByState();

                                Log.v(TAG, "release to pull");
                            }
                            else if (heightDiff <= 0) {//current height <= start height
                                state = DONE;
                                setViewByState();

                                Log.v(TAG, "release to done");
                            }

                            headView.setPadding(0, heightDiff > headContentHeight ? headContentHeight : heightDiff - headContentHeight,
                                    0, 0);
                            headView.invalidate();
                            break;
                        case PULL_To_REFRESH:
                            if (tempY - startY >= headContentHeight) {
                                state = RELEASE_To_REFRESH;
                                isBack = true;
                                setViewByState();

                                Log.v(TAG, "pull to release");
                            }
                            else if (tempY - startY <= 0) {
                                state = DONE;
                                setViewByState();

                                Log.v(TAG, "pull to done");
                            }

                            headView.setPadding(0, -1 * headContentHeight
                                    + heightDiff > headContentHeight ? headContentHeight : heightDiff, 0, 0);
                            headView.invalidate();
                            break;
                        case DONE:
                            if (tempY - startY > 0) {
                                state = PULL_To_REFRESH;
                                setViewByState();
                            }
                            break;
                    }
                }
                break;
        }
    }

    @Override
    protected void setViewByState() {
        switch (state) {
            case RELEASE_To_REFRESH:
                progressBar.setVisibility(View.GONE);

                arrowImageView.clearAnimation();
                arrowImageView.startAnimation(animation);

                tipsTextview.setText(R.string.release_to_refresh);
                break;
            case PULL_To_REFRESH:
                progressBar.setVisibility(View.GONE);
                arrowImageView.clearAnimation();

                if (isBack) {
                    isBack = false;
                    arrowImageView.clearAnimation();
                    arrowImageView.startAnimation(reverseAnimation);

                    tipsTextview.setText(R.string.pull_to_refresh);
                } else {
                    tipsTextview.setText(R.string.pull_to_refresh);
                }

                break;

            case REFRESHING:

                headView.setPadding(0, 0, 0, 0);
                headView.invalidate();

                progressBar.setVisibility(View.VISIBLE);
                arrowImageView.clearAnimation();
                arrowImageView.setVisibility(View.GONE);
                tipsTextview.setText(R.string.refreshing);

                break;
            case DONE:
                headView.setPadding(0, -1 * headContentHeight, 0, 0);
                headView.invalidate();

                progressBar.setVisibility(View.GONE);
                arrowImageView.clearAnimation();
                arrowImageView.setImageResource(R.drawable.ic_pulltorefresh_arrow);
                tipsTextview.setText(R.string.pull_to_refresh);

                break;
        }
    }

    @Override
    public void onRefreshComplete(boolean isSuccess){
        state = DONE;
        String msg;
        if (isSuccess)
        {
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            msg = String.format("%1$s: %2$s", listView.getResources().getString(R.string.last_updated), date);
        }
        else
            msg = listView.getResources().getString(R.string.update_failed);
        lastUpdatedTextView.setText(msg);
        setViewByState();
    }
}

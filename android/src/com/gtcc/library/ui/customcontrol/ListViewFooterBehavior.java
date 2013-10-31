package com.gtcc.library.ui.customcontrol;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.gtcc.library.R;
import com.gtcc.library.util.LogUtils;

/**
 * Created by ShenCV on 8/19/13.
 */
public class ListViewFooterBehavior extends ListViewBehaviorBase {
    private final static int NO_MORE_DATA = 0;
    private final static int REFRESHING = 1;
    private final static int DONE = 2;
    private final static String TAG = LogUtils.makeLogTag(ListViewFooterBehavior.class);

    private RefreshableListView listView;

    private LinearLayout footView;

    private ProgressBar progressBar;
    private TextView textView;

    private boolean isLoading;

    private int footContentWidth;
    private int footContentHeight;

    private int state;

    public ListViewFooterBehavior(RefreshableListView view){
        this.listView = view;
    }

    @Override
    public LinearLayout init(Context context){
        LayoutInflater inflater = LayoutInflater.from(context);

        footView = (LinearLayout)inflater.inflate(R.layout.refreshable_listview_footer, null);
        progressBar = (ProgressBar) footView
                .findViewById(R.id.foot_progressBar);
        textView = (TextView) footView.findViewById(R.id.foot_lastUpdatedTextView);

        measureView(footView);
        footContentWidth = footView.getMeasuredWidth();
        footContentHeight = footView.getMeasuredHeight();

        state = DONE;
        isLoading = false;
        setViewByState();

        Log.v("size", "width:" + footContentWidth + " height:"
                + footContentHeight);

        return footView;
    }

    @Override
    public void onScroll(int firstVisibleItem, int visibleItemCount, int totalItemCount){
        int lastVisibleItem = firstVisibleItem + visibleItemCount;
        if (lastVisibleItem == totalItemCount && totalItemCount > 1 && !isLoading){ //item count always >=1, because there is footer
            isLoading = true;
            state = REFRESHING;
            setViewByState();
            listView.onRefreshFooter();
        }
    }

    @Override
    public void onRefreshComplete(boolean isSuccess){
        state = isSuccess ? DONE : NO_MORE_DATA;
        isLoading = false;
        setViewByState();
    }

    @Override
    protected void setViewByState(){
        if (state == DONE){
            footView.setPadding(0, 0, 0, -1 * footContentHeight); //done, hide foot
            progressBar.setVisibility(View.GONE);
        }
        else if (state == REFRESHING) {
            footView.setPadding(0, 0, 0, 0); //refreshing
            progressBar.setVisibility(View.VISIBLE);
        }
        else{
            // no more to show
            footView.setPadding(0, 0, 0, 0);
            textView.setText(R.string.foot_no_more_data);
            progressBar.setVisibility(View.GONE);
        }
        footView.invalidate();
    }
}

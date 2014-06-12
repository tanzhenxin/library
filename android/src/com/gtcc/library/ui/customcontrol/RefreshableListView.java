package com.gtcc.library.ui.customcontrol;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.gtcc.library.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.gtcc.library.util.LogUtils;

/**
 *
 * Customized ListView
 *
 */

public class RefreshableListView extends ListView {
//    private ListViewBehaviorBase headerBehavior;
    private ListViewBehaviorBase footerBehavior;

    public OnRefreshListener refreshListener;
    private OnScrollListener scrollListener;

    public RefreshableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        //headerBehavior = new ListViewHeaderBehavior(this);
        //super.addHeaderView(headerBehavior.init(context), null, false);

        footerBehavior = new ListViewFooterBehavior(this);
        super.addFooterView(footerBehavior.init(context), null, false);
    }

    @Override
    public void setOnScrollListener(OnScrollListener l){
        scrollListener = l;
        super.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                scrollListener.onScrollStateChanged(absListView, i);
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //headerBehavior.onScroll(firstVisibleItem, visibleItemCount, totalItemCount);
                footerBehavior.onScroll(firstVisibleItem, visibleItemCount, totalItemCount);
                scrollListener.onScroll(absListView, firstVisibleItem, visibleItemCount, totalItemCount);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //headerBehavior.onTouch(event);
        return super.onTouchEvent(event);
    }

    public void setOnRefreshListener(OnRefreshListener refreshListener) {
        this.refreshListener = refreshListener;
    }

    public interface OnRefreshListener {
        public void onRefreshHeader();
        public void onRefreshFooter();
    }

    public void onRefreshComplete(boolean isSuccess) {
        // refresh complete, update both header and footer
        //headerBehavior.onRefreshComplete(isSuccess);
        footerBehavior.onRefreshComplete(isSuccess);
    }

    public void onRefreshHeader() {
        if (refreshListener != null) {
            refreshListener.onRefreshHeader();
        }
    }

    public void onRefreshFooter(){
        if (refreshListener != null){
            refreshListener.onRefreshFooter();
        }
    }
}

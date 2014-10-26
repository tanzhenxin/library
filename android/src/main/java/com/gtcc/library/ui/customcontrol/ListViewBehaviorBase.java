package com.gtcc.library.ui.customcontrol;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by ShenCV on 8/28/13.
 */
public abstract class ListViewBehaviorBase {
    public abstract LinearLayout init(Context context);

    public abstract void onScroll(int firstVisibleItem, int visibleItemCount, int totalItemCount);

    public abstract void onRefreshComplete(boolean isSuccess);

    public void onTouch(MotionEvent event){}

    protected abstract void setViewByState();

    protected void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(lpHeight,
                    View.MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }
}

package com.gtcc.library.util;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

public class TypefaceUtils {
	
	private static Typeface regularFont;
	private static Typeface thinFont;

    public static Typeface getTypeface(final Context context, Typeface typeface) {
    	if (typeface == null) 
    		return getThinFont(context);
    	
    	int style = typeface.getStyle();
    	if (style == Typeface.BOLD) {
    		return getNormalFont(context);
    	}
    	else if (style == Typeface.NORMAL){
    		return getThinFont(context);
    	}
    	
    	return typeface;
    }
    
    private static Typeface getNormalFont(final Context context) {
		if (regularFont == null) {
			regularFont = getTypeface(context, "fz-regular.ttf");
		}
		return regularFont;
    }
    
    private static Typeface getThinFont(final Context context) {
        if (thinFont == null)
        	thinFont = getTypeface(context, "fz-thin.ttf");
        return thinFont;
    }

    public static void setTypeface(final TextView... textViews) {
        if (textViews == null || textViews.length == 0)
            return;
        ;
        Typeface typeface = getTypeface(textViews[0].getContext(), textViews[0].getTypeface());
        for (TextView textView : textViews)
            textView.setTypeface(typeface);
    }


    public static Typeface getTypeface(final Context context, final String name) {
        return Typeface.createFromAsset(context.getAssets(), name);
    }
}
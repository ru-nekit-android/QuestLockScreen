package ru.nekit.android.qls.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.devspark.robototextview.RobotoTypefaces;

import ru.nekit.android.qls.R;

public class RobotoTypefaceUtil {

    public static void setUpTypeface(@NonNull Context context, @NonNull TextView textView, int styleResId) {
        TypedArray ta = context.obtainStyledAttributes(styleResId, R.styleable.TextStyle);
        int fontFamily = ta.getInt(R.styleable.TextStyle_fontFamily, RobotoTypefaces.FONT_FAMILY_ROBOTO);
        int textWeight = ta.getInt(R.styleable.TextStyle_textWeight, RobotoTypefaces.TEXT_WEIGHT_NORMAL);
        int textStyle = ta.getInt(R.styleable.TextStyle_textStyle, RobotoTypefaces.TEXT_STYLE_NORMAL);
        ta.recycle();
        RobotoTypefaces.setUpTypeface(textView, fontFamily, textWeight, textStyle);
    }
}

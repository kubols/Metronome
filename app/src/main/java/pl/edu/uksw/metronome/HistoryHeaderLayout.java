package pl.edu.uksw.metronome;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Jakub on 23/02/16.
 */
public class HistoryHeaderLayout extends LinearLayout {

    private final static int SIZE_SUBHEAD = 20;
    private final static int SIZE_CAPTION = 16;
    private final static String SUBHEAD = "subhead";

    public HistoryHeaderLayout(Context context, String title, String caption) {
        super(context);
        setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 72, getResources().getDisplayMetrics())
        ));
        setVerticalGravity(Gravity.CENTER);
        setOrientation(VERTICAL);
        addTextView(title, SUBHEAD, SIZE_SUBHEAD, ContextCompat.getColor(context, R.color.textPrimary));
        addTextView(caption, SIZE_CAPTION, ContextCompat.getColor(context, R.color.textSecondary));
        //setDivider();
    }

    // add text view for header with tag to find it in view
    private void addTextView(String string, String tag, int size, int color){
        TextView textView = new TextView(getContext());
        textView.setTag(tag);
        textView.setText(string);
        textView.setTextColor(color);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        this.addView(textView);
    }

    // add text view for second text
    private void addTextView(String string, int size, int color){
        TextView textView = new TextView(getContext());
        textView.setText(string);
        textView.setTextColor(color);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        this.addView(textView);
    }

    // add line divider
    private void setDivider(){
        View line = new View(getContext());
        line.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 1));
        line.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.textPrimary));
        addView(line);
    }

}

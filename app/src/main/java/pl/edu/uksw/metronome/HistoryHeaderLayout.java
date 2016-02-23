package pl.edu.uksw.metronome;

import android.content.Context;
import android.graphics.Color;
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

    private final static int SIZE_SUBHEAD = 16;
    private final static int SIZE_CAPTION = 12;

    public HistoryHeaderLayout(Context context, String title, String caption) {
        super(context);
        setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 72, getResources().getDisplayMetrics())
        ));
        setVerticalGravity(Gravity.CENTER);
        setOrientation(VERTICAL);
        addTextView(title, SIZE_SUBHEAD, R.color.textPrimary);
        addTextView(caption, SIZE_CAPTION, R.color.textSecondary);
        setDivider();
    }

    private void addTextView(String string, int size, int color){
        TextView textView = new TextView(getContext());
        textView.setText(string);
        textView.setTextColor(color);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        this.addView(textView);
    }

    private void setDivider(){
        View line = new View(getContext());
        line.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 1));
        line.setBackgroundColor(Color.rgb(51, 51, 51));
        addView(line);
    }

}

package pl.rzeszow.swiktor.tabuteokratyczne;

import android.content.Context;

public class NarzedziaWspolne {

    public static float pxToDp(float px, Context context) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public interface TitleChangeListener {
        public void onTitleSet(String msg);
    }
}
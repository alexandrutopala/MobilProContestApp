package ro.infotop.journeytoself.listener;

import android.view.View;

public interface OnClickListener {
    void click(View v, int i);

    static OnClickListener empty() {
        return (v, i) -> {};
    }
}

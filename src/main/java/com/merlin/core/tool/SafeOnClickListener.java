package com.merlin.core.tool;

import android.view.View;

import com.merlin.core.util.MLog;

/**
 * @author merlin
 */

public abstract class SafeOnClickListener implements View.OnClickListener {

    private IClick iClick;

    public SafeOnClickListener(IClick iClick) {
        this.iClick = iClick;
    }

    @Override
    public void onClick(View view) {
        if (view != null && isCanClick()) {
            iClick.onClickView(view);
        } else {
            MLog.e("view is null");
        }
    }

    protected boolean isCanClick() {
        return true;
    }

}

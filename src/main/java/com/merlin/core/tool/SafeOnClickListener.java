package com.merlin.core.tool;

import android.view.View;

import com.merlin.core.util.MLog;

/**
 * Created by ncm on 16/11/30.
 */

public abstract class SafeOnClickListener implements View.OnClickListener {

    @Override
    public void onClick(View view) {
        if (view != null && isCanClick()) {
            onClickView(view);
        } else {
            MLog.e("view is null");
        }
    }

    protected abstract void onClickView(View view);

    protected boolean isCanClick() {
        return true;
    }

}

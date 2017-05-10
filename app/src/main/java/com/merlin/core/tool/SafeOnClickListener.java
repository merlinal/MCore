package com.merlin.core.tool;

import android.view.View;

import com.merlin.core.util.LogUtil;

/**
 * Created by ncm on 16/11/30.
 */

public abstract class SafeOnClickListener implements View.OnClickListener {

    @Override
    public void onClick(View view) {
        if (view != null) {
            onClickView(view);
        } else {
            LogUtil.e("view is null");
        }
    }

    protected abstract void onClickView(View view);

}

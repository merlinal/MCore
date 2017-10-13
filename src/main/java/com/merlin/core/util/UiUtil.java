package com.merlin.core.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.merlin.core.R;
import com.merlin.core.context.MContext;

/**
 * Created by ncm on 16/11/7.
 */

public class UiUtil {

    public static Dialog showDialogBottom(Context context, View view) {
        return showDialog(context, view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT,
                0, 0, 0, R.style.anim_from_bottom, Gravity.BOTTOM, true);
    }

    public static Dialog showDialog(Context context, View view, int width, int height, int x, int y, int style, int anim, int gravity, boolean cancelable) {
        Dialog dialog = getDialog(context, view, width, height, x, y, style, anim, gravity, cancelable);
        dialog.show();
        return dialog;
    }

    public static Dialog getDialog(Context context, View view, int width, int height, int x, int y, int style, int anim, int gravity, boolean cancelable) {
        Dialog dialog = new Dialog(context, style != 0 ? style : R.style.dialog);
        dialog.setContentView(view);
        Window window = dialog.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        dialog.setCancelable(cancelable);
        dialog.setCanceledOnTouchOutside(cancelable);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.x = x;
        params.y = y;
        params.width = (width != 0 ? width : WindowManager.LayoutParams.WRAP_CONTENT);
        params.height = (height != 0 ? height : WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(0x00000000));
        dialog.getWindow().setAttributes(params);
        if (gravity != 0) {
            window.setGravity(gravity);  //此处可以设置dialog显示的位置
        }
        if (anim != 0) {
            window.setWindowAnimations(anim);  //添加动画
        }
        return dialog;
    }

    public static void toast(String content) {
        Toast.makeText(MContext.app(), content, Toast.LENGTH_SHORT).show();
    }

}

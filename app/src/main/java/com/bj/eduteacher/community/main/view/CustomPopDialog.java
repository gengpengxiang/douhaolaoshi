package com.bj.eduteacher.community.main.view;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.bj.eduteacher.R;

public class CustomPopDialog extends Dialog {
    public CustomPopDialog(Context context) {
        super(context);
    }

    public CustomPopDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Builder {
        private Context context;
        private View view;

        public Builder(Context context) {
            this.context = context;
        }

        public View getView() {
            return view;
        }

        public void setView(View view) {
            this.view = view;
        }

        public CustomPopDialog create(int LayoutId) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final CustomPopDialog dialog = new CustomPopDialog(context, R.style.Dialog);
            View layout = inflater.inflate(LayoutId, null);
            dialog.addContentView(layout, new LayoutParams(
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                    , android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
            dialog.setContentView(layout);
            //add
            Window dialogWindow = dialog.getWindow();
            LayoutParams p = dialogWindow.getAttributes();
            int width =getScreenWidth(context);
            int height = getScreenHeight(context);
            p.height = (int) (height * 0.25); // 高度设置为屏幕的0.6，根据实际情况调整
            p.width = (int) (width * 0.7); // 宽度设置为屏幕的0.65，根据实际情况调整
            dialogWindow.setAttributes(p);

            return dialog;
        }


        public CustomPopDialog create2(int LayoutId) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final CustomPopDialog dialog = new CustomPopDialog(context, R.style.Dialog);
            View layout = inflater.inflate(LayoutId, null);
            dialog.addContentView(layout, new LayoutParams(
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                    , android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
            dialog.setContentView(layout);
            //add
            Window dialogWindow = dialog.getWindow();
            LayoutParams p = dialogWindow.getAttributes();
            int width =getScreenWidth(context);
            int height = getScreenHeight(context);
            //p.height = (int) (height * 0.25); // 高度设置为屏幕的0.6，根据实际情况调整
            p.width = (int) (width * 0.75); // 宽度设置为屏幕的0.65，根据实际情况调整
            dialogWindow.setAttributes(p);

            return dialog;
        }

        public CustomPopDialog create(int LayoutId,double h,double w) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final CustomPopDialog dialog = new CustomPopDialog(context, R.style.Dialog);
            View layout = inflater.inflate(LayoutId, null);
            dialog.addContentView(layout, new LayoutParams(
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                    , android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
            dialog.setContentView(layout);
            //add
            Window dialogWindow = dialog.getWindow();
            LayoutParams p = dialogWindow.getAttributes();
            int width =getScreenWidth(context);
            int height = getScreenHeight(context);
            p.height = (int) (height * h); // 高度设置为屏幕的0.6，根据实际情况调整
            p.width = (int) (width * w); // 宽度设置为屏幕的0.65，根据实际情况调整
            dialogWindow.setAttributes(p);

            return dialog;
        }

        public CustomPopDialog create(int LayoutId,double h,double w,int gravity) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final CustomPopDialog dialog = new CustomPopDialog(context, R.style.Dialog);
            View layout = inflater.inflate(LayoutId, null);
            dialog.addContentView(layout, new LayoutParams(
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                    , android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
            dialog.setContentView(layout);
            //add
            Window dialogWindow = dialog.getWindow();
            LayoutParams p = dialogWindow.getAttributes();
            int width =getScreenWidth(context);
            int height = getScreenHeight(context);
            p.height = (int) (height * h); // 高度设置为屏幕的0.6，根据实际情况调整
            p.width = (int) (width * w); // 宽度设置为屏幕的0.65，根据实际情况调整
            dialogWindow.setAttributes(p);
            dialogWindow.setGravity(gravity);

            return dialog;
        }
    }


    public static int getScreenWidth(Context context)
    {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }
    public static int getScreenHeight(Context context)
    {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }
}

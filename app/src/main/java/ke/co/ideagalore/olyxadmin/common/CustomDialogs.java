package ke.co.ideagalore.olyxadmin.common;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import ke.co.ideagalore.olyxadmin.R;

public class CustomDialogs {

    Dialog myDialog;

    public void showProgressDialog(Context context, String message) {
        myDialog = new Dialog(context);
        myDialog.setContentView(R.layout.progress_dialog);
        myDialog.setCanceledOnTouchOutside(false);
        myDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        myDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView textView = myDialog.findViewById(R.id.tv_message);
        textView.setText(message);
        myDialog.show();
    }


    public void dismissProgressDialog(){
        myDialog.dismiss();
    }

    public void showSnackBar(Activity activity, String message) {

        RelativeLayout view = (activity).findViewById(R.id.layout);
        final Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(activity.getResources().getColor(R.color.accent));

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackBarView.getLayoutParams();
        params.gravity = Gravity.TOP;
        params.topMargin=100;
        params.bottomMargin = 100;
        params.leftMargin = 20;
        params.rightMargin = 20;
        snackBarView.setLayoutParams(params);
        snackbar.show();

    }
}

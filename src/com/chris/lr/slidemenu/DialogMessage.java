package com.chris.lr.slidemenu;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

public class DialogMessage {

	private Context context;
	private Dialog dialog;

	public DialogMessage(Context context) {
		this.context = context;
	}

	public Dialog mypDialog;
	//private ImageView proView;
	private TextView cententView;
	public final int MESSAGE_SWIPE = 1;
	public final int MESSAGE_INPUTPIN = 2;
	public final int MESSAGE_LOADING = 3;
	public final int MESSAGE_PRINT = 4;

	public void showLoginDialog(int type, String amount) {
		View customFrame = null;
		customFrame = LayoutInflater.from(context).inflate(
				R.layout.my_loading_layout, null);
		cententView = (TextView) customFrame.findViewById(R.id.customFrameMsg);
		//TextView amountView  = (TextView) customFrame.findViewById(R.id.text_load_amount);
//		if (amount!= null) {
//			amountView.setVisibility(View.VISIBLE);
//			amountView.setText(amount);
//		}
		//proView = (ImageView) customFrame.findViewById(R.id.customFrameLoadImg);
		//proView.setBackgroundResource(R.drawable.loading_icon);
		Animation rotateAnimation = null;
		rotateAnimation = AnimationUtils.loadAnimation(context,
				R.anim.my_rotate);
		LinearInterpolator lir = new LinearInterpolator();
		rotateAnimation.setInterpolator(lir);
		rotateAnimation.setFillAfter(true);
		//proView.startAnimation(rotateAnimation);
		//ImageView showView = (ImageView) customFrame.findViewById(R.id.customFrameShowImg);
		if (type == MESSAGE_SWIPE) {
		} else if (type == MESSAGE_INPUTPIN) {
		} else if (type == 4) {
		} else {

		}
		android.view.ViewGroup.LayoutParams layoutParams = new LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		mypDialog = new Dialog(context, R.style.My_Theme_Dialog_Alert2);
		mypDialog.addContentView(customFrame, layoutParams);
		mypDialog.setCancelable(false);
		mypDialog.setCanceledOnTouchOutside(false);
		try {
			mypDialog.show();
		} catch (Exception e) {
			Log.e("dialog_show", e + "");
		}

	}

	public void noticeDialog(String msg, int type, String amount) {
		showLoginDialog(type, amount);
		cententView.setText(msg);
		return;
	}

	public void dialogDismiss() {
		if (mypDialog != null) {
			mypDialog.dismiss();
			mypDialog = null;
		}
		if (dialog != null) {

			dialog.dismiss();
			dialog = null;
			Log.e("dialog", "dialog dismiss");
		} else {
			Log.e("dialog", "dialog is null");
		}

	}

}

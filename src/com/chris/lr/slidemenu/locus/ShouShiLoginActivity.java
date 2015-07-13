package com.chris.lr.slidemenu.locus;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.chris.lr.slidemenu.R;
import com.chris.lr.slidemenu.locus.LocusPassWordView.OnCompleteListener;


public class ShouShiLoginActivity extends Activity {
	private LocusPassWordView lpwv;
	private Toast toast;
	private TextView tv;
	private void showToast(CharSequence message) {
		if (null == toast) {
			toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
		} else {
			toast.setText(message);
		}

		toast.show();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		lpwv = (LocusPassWordView) this.findViewById(R.id.mLocusPassWordView);
		tv=(TextView) findViewById(R.id.tv_login_check);
		tv.setOnClickListener(new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
//				Intent intent =new Intent();
//				
//				finish();
				
			}
		});
		lpwv.setOnCompleteListener(new OnCompleteListener() {
			@Override
			public void onComplete(String mPassword) {
				// 如果密码正确,则进入主页面。
				if (lpwv.verifyPassword(mPassword)) {
					showToast("旧密码输入正确!");
					Intent intent = new Intent(ShouShiLoginActivity.this,
							SetPasswordActivity.class);
					intent.putExtra("NEED", "1");
					// 打开新的Activity
					startActivity(intent);
					overridePendingTransition(R.anim.in_from_right, R.anim.out_of_left);
					finish();
				} else {
					showToast("旧密码输入错误,请重新输入");
					lpwv.markError(2000);
					//lpwv.clearPassword();
				}
			}
		});

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			finish();
			overridePendingTransition(R.anim.in_from_left, R.anim.out_of_right);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onStart() {
		super.onStart();
		// 如果密码为空,则进入设置密码的界面
		SharedPreferences sp=getSharedPreferences("data",0);
		boolean havePWD=sp.getBoolean("HAVE_PWD", false);
		if (!havePWD) {
			lpwv.setVisibility(View.GONE);
			Intent intent = new Intent(ShouShiLoginActivity.this,
					SetPasswordActivity.class);
			// 打开新的Activity
			intent.putExtra("NEED", "0");
			startActivity(intent);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_of_left);
			finish();
		} else {
			lpwv.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

}

package com.chris.lr.slidemenu.locus;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.centerm.mpos.util.Log;
import com.chris.lr.slidemenu.R;
import com.chris.lr.slidemenu.locus.LocusPassWordView.OnCompleteListener;


public class SetPasswordActivity extends Activity {
	private LocusPassWordView lpwv;
	private String password;
	private String password2;
	private boolean needverify = true;
	private Toast toast;
	private String needState;
	private ImageView iv_return;
	private int i=0;
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
		setContentView(R.layout.setpassword_activity);
		lpwv = (LocusPassWordView) this.findViewById(R.id.mLocusPassWordView);
		iv_return=(ImageView) findViewById(R.id.shoushisetreturn);
		password="";
		password2="";
		iv_return.setOnClickListener(new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(R.anim.in_from_left,R.anim.out_of_right);
			}
		});
		Intent intent=getIntent();
		needState=intent.getStringExtra("NEED");
		lpwv.setOnCompleteListener(new OnCompleteListener() {
			@Override
			public void onComplete(String mPassword) {
				
//				if (needverify) {
//					if (lpwv.verifyPassword(mPassword)) {
//						showToast("旧密码或超级密码输入正确,请输入新密码!");
//						lpwv.clearPassword();
//						needverify = false;
//					} else {
//						showToast("错误的超级密码或旧密码,请重新输入!");
//						//lpwv.clearPassword();
//						lpwv.markError(2000);
//						password = "";
//					}
//				}
				i++;
				//Toast.makeText(getApplicationContext(), i+">>>>>手势密码:"+mPassword, Toast.LENGTH_SHORT).show();
				if(i<=1){
					password = mPassword;
					showToast("请再次绘制解锁图案");
					lpwv.clearPassword();
				}else{
					password2 = mPassword;
					Log.i("xys","PWD1:"+password+";"+"PWD2:"+password2);
					if(!password2.equals(password)){
						showToast("两次密码输入不一致,请重新输入");
						lpwv.markError(2000);
						i=0;
					}else{
						lpwv.resetPassWord(password);
						lpwv.clearPassword();
						i=0;
						doSavePassWord();
						
					}
					
				}
				
			}
		});

//		OnClickListener mOnClickListener = new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				switch (v.getId()) {
//				case R.id.tvSave:
//					if (StringUtil.isNotEmpty(password)) {
//						lpwv.resetPassWord(password);
//						lpwv.clearPassword();
//						showToast("密码修改成功,请记住密码.");
//						doSavePassWord();
//						finish();
//						overridePendingTransition(R.anim.in_from_left,R.anim.out_of_right);
////						startActivity(new Intent(SetPasswordActivity.this,
////								ShouShiLoginActivity.class));
//						
//					} else {
//						lpwv.clearPassword();
//						showToast("密码不能为空,请输入密码.");
//					}
//					break;
//				case R.id.tvReset:
//					lpwv.clearPassword();
//					break;
//				}
//			}
//		};
//		Button buttonSave = (Button) this.findViewById(R.id.tvSave);
//		buttonSave.setOnClickListener(mOnClickListener);
//		Button tvReset = (Button) this.findViewById(R.id.tvReset);
//		tvReset.setOnClickListener(mOnClickListener);
		// 如果密码为空,直接输入密码
		if (lpwv.isPasswordEmpty()) {
			this.needverify = false;
			showToast("请输入密码");
		}
	}

	protected void doSavePassWord() {
		showToast("密码设置成功,请记住密码");
		SharedPreferences.Editor sharedata = getSharedPreferences(
				"data", 0).edit();
		sharedata.putBoolean("HAVE_PWD", true);
		sharedata.commit();
		try {
			Thread.sleep(1000);
			finish();
			overridePendingTransition(R.anim.in_from_left,R.anim.out_of_right);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

}

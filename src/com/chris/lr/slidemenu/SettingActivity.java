package com.chris.lr.slidemenu;

import com.chris.lr.slidemenu.locus.SetPasswordActivity;
import com.chris.lr.slidemenu.locus.ShouShiLoginActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SettingActivity extends Activity {
private ImageView back;
private ImageView ivMima;
private LinearLayout hideView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_setting);
		back=(ImageView) findViewById(R.id.return_back);
		ivMima=(ImageView)findViewById(R.id.ivmima);
		hideView=(LinearLayout)findViewById(R.id.hideview);
		hideView.setOnClickListener(new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				goToShouShi();
			}
		});
		
		ivMima.setOnClickListener(new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(hideView.getVisibility()==View.GONE){
					ivMima.setBackgroundResource(R.drawable.switchok);
					ivMima.invalidate();
					hideView.setVisibility(View.VISIBLE);
				}else if(hideView.getVisibility()==View.VISIBLE){
					ivMima.setBackgroundResource(R.drawable.switchno);
					ivMima.invalidate();
					hideView.setVisibility(View.GONE);
				}
				
			}
		});
		back.setOnClickListener(new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(R.anim.in_from_left, R.anim.out_of_right); 
			}
		});
	}
	private void goToShouShi(){
		
		Intent intent=new Intent();
		intent.setClass(this, ShouShiLoginActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.in_from_right, R.anim.out_of_left);
		
	}
	
}

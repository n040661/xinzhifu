package com.chris.lr.slidemenu;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;


public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_start);

        new AsyLogin().execute();


    }


    class AsyLogin extends AsyncTask<String,String,String>{
        @Override
        protected String doInBackground(String... strings) {
            try {
                Thread.sleep(3000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            goToWhatActivity();
            
        }
    }


	public void goToWhatActivity() {
		
		SharedPreferences sp=getSharedPreferences("data",0);
		boolean havePWD=sp.getBoolean("HAVE_PWD", false);
		if(!havePWD){
			Intent intent=new Intent();
	        intent.setClass(getApplicationContext(),StartLoginActivity.class);
	        startActivity(intent);
	        overridePendingTransition(R.anim.in_from_right,R.anim.out_of_left);
	        finish();
		}else{
			Intent intent=new Intent();
			intent.setClass(this, ShouShiLoginActivity_main.class);
			startActivity(intent);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_of_left);
			finish();
		}
		
	}

}

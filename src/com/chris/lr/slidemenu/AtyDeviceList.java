package com.chris.lr.slidemenu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.centerm.mpos.bluetooth.BluetoothController;
import com.centerm.mpos.bluetooth.BluetoothController.BluetoothSearchListener;
import com.centerm.mpos.bluetooth.BluetoothController.BluetoothStateListener;
import com.centerm.mpos.bluetooth.MposDevice;

public class AtyDeviceList extends Activity implements
		android.view.View.OnClickListener {
	private ListView devicesList;

	private LinearLayout search;

	private ProgressDialog progress;
	private AlertDialog alertDialog;
	private DialogMessage dialogMessage;
	private ArrayAdapter<String> mDevicesArrayAdapter;
	private ProgressBar btPb;
	private ImageView iv;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case BluetoothController.STATE_CONNECTED:
				if (alertDialog != null && alertDialog.isShowing()) {
					alertDialog.dismiss();
				}
				if (alertDialog != null && alertDialog.isShowing()) {
					alertDialog.dismiss();
				}
				dialogMessage.dialogDismiss();
				setResult(RESULT_OK);
				finish();
				overridePendingTransition(R.anim.in_from_left, R.anim.out_of_right); 
				break;

			case BluetoothController.STATE_DISCONNECT:
				dialogMessage.dialogDismiss();
				if (progress != null && progress.isShowing())
					progress.dismiss();
				if (alertDialog != null && alertDialog.isShowing()) {
					alertDialog.dismiss();
				}
				break;
			case 1:
				search.setClickable(true);
				btPb.setVisibility(View.GONE);
				search.setVisibility(View.VISIBLE);
				break;

			}
		};
	};

	class searchListener implements BluetoothSearchListener {

		@Override
		public void onSearchDevice(MposDevice device) {
			mDevicesArrayAdapter.add(device.getName() + "\n"
					+ device.getAddress());
			devicesList.setSelection(mDevicesArrayAdapter.getCount() - 1);

		}

		@Override
		public void onStopSearch() {
			Log.d("", "stop");
			handler.sendEmptyMessage(1);
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.bt_search);
iv=(ImageView) findViewById(R.id.return_to);
iv.setOnClickListener(new android.view.View.OnClickListener(){

	@Override
	public void onClick(View arg0) {
		setResult(RESULT_CANCELED);
		finish();
		overridePendingTransition(R.anim.in_from_left, R.anim.out_of_right); 
	}
	
});
		mDevicesArrayAdapter = new ArrayAdapter<String>(this,
				R.layout.device_name);
		dialogMessage = new DialogMessage(this);
		initView();
		search.setClickable(false);
		search.setVisibility(View.GONE);
		MainActivity.controller.discovery(12, new searchListener());

	}

	private void initView() {

        btPb=(ProgressBar) findViewById(R.id.bt_pb);
		search = (LinearLayout) findViewById(R.id.search);
		search.setOnClickListener(this);

		devicesList = (ListView) findViewById(R.id.list_view1);
		devicesList.setAdapter(mDevicesArrayAdapter);

		devicesList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				try {
					MainActivity.controller.stopDiscovery();
					search.setVisibility(View.VISIBLE);
					search.setClickable(true);
					btPb.setVisibility(View.INVISIBLE);

					String info = ((TextView) view).getText().toString();
					String name = info.substring(0, info.length() - 18);
					cur_name = name;
					String address = info.substring(info.length() - 17);
					cur_address = address;
					showChooseDialog();
				} catch (Exception e) {
				}

			}
		});
	}

	public static String cur_address;
	String cur_name;

	class MyBluetoothListener implements BluetoothStateListener {

		@Override
		public void onStateChange(int state) {
			Message msg = new Message();
			switch (state) {
			case BluetoothController.STATE_CONNECTED:
				msg.what = BluetoothController.STATE_CONNECTED;
				break;
			case BluetoothController.STATE_CONNECTING:
				msg.what = BluetoothController.STATE_CONNECTING;
				break;
			case BluetoothController.STATE_DISCONNECT:
				msg.what = BluetoothController.STATE_DISCONNECT;
				break;
			}

			handler.sendMessage(msg);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.search:
			
				search.setClickable(false);
				search.setVisibility(View.GONE);
				mDevicesArrayAdapter.clear();
				MainActivity.controller.discovery(12, new searchListener());
				btPb.setVisibility(View.VISIBLE);
			

			break;

		}
	}

	private Dialog mypDialog;
	private TextView title_text;
	private TextView title_msg;
	private Button left;
	private Button right;

	public void showChooseDialog() {
		View customFrame = null;
		customFrame = LayoutInflater.from(this).inflate(
				R.layout.device_conform_layout, null);
		title_text = (TextView) customFrame.findViewById(R.id.title_text);
		title_msg = (TextView) customFrame.findViewById(R.id.title_msg);
		title_text.setText("您选择连接的设备");
		title_msg.setText(cur_name);
		left = (Button) customFrame.findViewById(R.id.device_right_btn);
		right = (Button) customFrame.findViewById(R.id.device_left_btn);
		left.setOnClickListener(new LeftListener());
		right.setOnClickListener(new RightListener());
		android.view.ViewGroup.LayoutParams layoutParams = new LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		mypDialog = new Dialog(this, R.style.My_Theme_Dialog_Alert2);
		mypDialog.addContentView(customFrame, layoutParams);
		mypDialog.setCancelable(false);
		mypDialog.setCanceledOnTouchOutside(false);
		try {
			mypDialog.show();
		} catch (Exception e) {
			Log.e("dialog_show", e + "");
		}

	}

	class LeftListener implements android.view.View.OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			mypDialog.dismiss();
		}

	}

	class RightListener implements android.view.View.OnClickListener {

		@Override
		public void onClick(View v) {
			dialogMessage.noticeDialog("正在连接" + cur_name, 3, null);
			MainActivity.controller.connect(cur_address,
					new MyBluetoothListener());
			mypDialog.dismiss();
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MainActivity.controller.stopDiscovery();

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		 if (keyCode == KeyEvent.KEYCODE_BACK) {
			 finish();
				overridePendingTransition(R.anim.in_from_left, R.anim.out_of_right); 
	        }
		return super.onKeyDown(keyCode, event);
		
	}
	

}

package com.chris.lr.slidemenu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.centerm.command.MPOSVersion;
import com.centerm.ctmpos.MPOSController;
import com.centerm.ctmpos.MPOSControllerCallback;
import com.centerm.mpos.util.HexUtil;
import com.centerm.mpos.util.StringUtil;
import com.chris.lr.slidemenu.LayoutRelative.OnScrollListener;

public class MainActivity extends Activity implements OnGestureListener, OnTouchListener {

	private static final String TAG = "ChrisSlideMenu";
	private RelativeLayout mainLayout;
	private RelativeLayout leftLayout;
	private RelativeLayout rightLayout;
	private LayoutRelative layoutSlideMenu;
	private MyDialog dialog;
	private ImageView ivMore;
	private ImageView ivSettings;
	private ImageView settings;
	private ImageView ivSignin;
    private ImageView ivQuit;
    private ImageView ivBill;
	private ImageView ivShouKuan;
	private GestureDetector mGestureDetector;
	private LinearLayout changeUser;
	private static final int SPEED = 30;
	private boolean bIsScrolling = false;
	private int iLimited = 0;
	private int mScroll = 0;
	private View mClickedView = null;
	
	private TextView tv_lianxiren;
	private TextView tv_shanghubianhao;
	private TextView tv_yinhangkahao;
	private TextView tv_kaihuhang;
	private TextView tv_shoujihaoma;
	private TextView tv_youxiang;
	public static final int REQUEST_ENABLE_BT = 3;
	public static final int REQUEST_DEVICE_LIST = 1002;
	public static MPOSController controller;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
	}
	private void gotoDeviceList() {
		Intent intent = new Intent(this, AtyDeviceList.class);
		startActivityForResult(intent, REQUEST_DEVICE_LIST);
		overridePendingTransition(R.anim.in_from_right, R.anim.out_of_left); 
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_ENABLE_BT:
			if (resultCode == RESULT_OK) {
				gotoDeviceList();
			} else {
				finish();
				overridePendingTransition(R.anim.in_from_left, R.anim.out_of_right); 
			}
			break;
		case REQUEST_DEVICE_LIST:
			if (resultCode == RESULT_OK) {
                dialog.show();
                controller.getMPOSVerion(new MPOSControllerCallback() {
        			@Override
        			public void onGetMPOSVersionSuc(MPOSVersion mVer) {
        				Log.i("xys",

        						"sn:" + mVer.getCMPOSSN() + "\nvendorID:"
        								+ mVer.getVendorID() + "\nhardwareVer:"
        								+ mVer.getHardWarever() + "\nsoftwareVer:"
        								+ mVer.getSoftwareVer() + "\nKSN:"
        								+ HexUtil.toString(mVer.getKSN())
        								+ "\nMPOSModel:" + mVer.getMPOSModel()
        								+ "\nmodulesStatus:" + mVer.getModulesStatus()
        								+ "\nisInit:" + mVer.isInit() + "\nproductId:"
        								+ StringUtil.byte2HexStr(mVer.getProductId())
        						);
        				new AsySignin().execute(mVer.getCMPOSSN());
        			}

        			@Override
        			public void onError(int errId, String errMsg) {
        				Log.i("xys", errId + ":" + errMsg);
        			}
        		});


			} 
			break;

		default:
			break;
		}
	}

	
	private CheckBox ck;
	private Button left;
	private Button right;
	private Dialog myDialog;
    private void showSureQUitDialog(){
        View customFrame = null;
        customFrame = LayoutInflater.from(this).inflate(
                R.layout.sure_quit_dialog, null);
        ck = (CheckBox) customFrame.findViewById(R.id.ck);
//        title_msg = (TextView) customFrame.findViewById(R.id.title_msg);
//        title_text.setText("您选择连接的设备");
//        title_msg.setText(cur_name);
        left = (Button) customFrame.findViewById(R.id.device_left_btn);
        right = (Button) customFrame.findViewById(R.id.device_right_btn);
        left.setOnClickListener(new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				SharedPreferences.Editor sharedata = getSharedPreferences(
						"data", 0).edit();
				if(ck.isChecked()){
					sharedata.putBoolean("HAVE_CHECKED", true);
					sharedata.commit();
				}else{
					sharedata.putBoolean("HAVE_CHECKED",false);
					sharedata.commit();
				}
				finish();
				overridePendingTransition(R.anim.in_from_left, R.anim.out_of_right);
			}
		});
        right.setOnClickListener(new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				myDialog.cancel();
			}
		});
        android.view.ViewGroup.LayoutParams layoutParams = new WindowManager.LayoutParams(
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        myDialog = new Dialog(this, R.style.My_Theme_Dialog_Alert2);
        myDialog.addContentView(customFrame, layoutParams);
        myDialog.setCancelable(true);
        myDialog.setCanceledOnTouchOutside(true);
        try {
            myDialog.show();
        } catch (Exception e) {
            Log.e("dialog_show", e + "");
        }
    }
    
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		setData();
	}
	private void setData(){
		tv_lianxiren.setText(Const.xingming);
		tv_shanghubianhao.setText(Const.shanghuhao);
		 tv_yinhangkahao.setText(Const.cardno);
		 tv_kaihuhang.setText(Const.bank);
		 tv_shoujihaoma.setText(Const.shoujihao);
		 tv_youxiang.setText(Const.mail);
	}
	private void initView(){
	    dialog=new MyDialog(this, R.style.mydialog);
        dialog.setCanceledOnTouchOutside(false);
        controller = MPOSController.getInstance(MainActivity.this.getApplicationContext());

        
        tv_lianxiren=(TextView) findViewById(R.id.lianxiren);
        tv_shanghubianhao=(TextView) findViewById(R.id.shanghubianhao);
        tv_yinhangkahao=(TextView) findViewById(R.id.yinhangkahao);
        tv_kaihuhang=(TextView) findViewById(R.id.kaihuhang);
        tv_shoujihaoma=(TextView) findViewById(R.id.shoujihaoma);
        tv_youxiang=(TextView) findViewById(R.id.youxiang);
        setData();
        
        
		mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);
		leftLayout = (RelativeLayout) findViewById(R.id.leftLayout);
		rightLayout = (RelativeLayout) findViewById(R.id.rightLayout);
		changeUser =(LinearLayout) findViewById(R.id.qiehuanzhanghao);
		changeUser.setOnClickListener(new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent=new Intent();
				intent.setClass(MainActivity.this, LoginActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_of_left); 
			}
		});
		mainLayout.setOnTouchListener(this);
		leftLayout.setOnTouchListener(this);
		rightLayout.setOnTouchListener(this);
		
		layoutSlideMenu = (LayoutRelative) findViewById(R.id.layoutSlideMenu);
		layoutSlideMenu.setOnScrollListener(new OnScrollListener(){
			@Override
			public void doOnScroll(MotionEvent e1, MotionEvent e2,
					float distanceX, float distanceY) {
				onScroll(distanceX);
			}
			
			@Override
			public void doOnRelease(){
				onRelease();
			}
		});
		
		ivMore = (ImageView) findViewById(R.id.ivMore);
		ivSettings = (ImageView) findViewById(R.id.ivSettings);
		settings = (ImageView) findViewById(R.id.settings);
		ivSignin=(ImageView)findViewById(R.id.signin);
        ivQuit=(ImageView)findViewById(R.id.quit);
        ivBill=(ImageView)findViewById(R.id.bill);
        ivQuit.setOnClickListener(new android.view.View.OnClickListener(){
            @Override
            public void onClick(View view) {
            	SharedPreferences sp=getSharedPreferences("data",0);
				boolean haveChecked=sp.getBoolean("HAVE_CHECKED", false);
				if(!haveChecked){
					 showSureQUitDialog();
				}else{
					finish();
					overridePendingTransition(R.anim.in_from_left, R.anim.out_of_right);
				}
           
            }
        });
		ivShouKuan=(ImageView)findViewById(R.id.ivshoukuan);
		ivShouKuan.setOnClickListener(new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 判断是否签到
				if(Const.signin){
					Intent intent=new Intent();
					intent.setClass(MainActivity.this, BuyActivity.class);
					startActivity(intent);
					overridePendingTransition(R.anim.in_from_right, R.anim.out_of_left); 
				}else{
					 AlertDialog.Builder builder =
		                     new AlertDialog.Builder(MainActivity.this);
		             builder.setTitle("提示")
		                     .setMessage("还没签到,请先签到!")
		                     .setCancelable(true)
		                     .setPositiveButton("立刻签到", new DialogInterface.OnClickListener() {
		                         // 点击确认按钮触发的事件
		                         public void onClick(DialogInterface dialogIn, int id) {
		                        	 dialogIn.dismiss();
		                         }
		                     })
		                     .setNegativeButton("取消",
		                             new DialogInterface.OnClickListener() {
		                                 // 点击取消按钮触发的事件
		                                 public void onClick(DialogInterface dialogIn, int id) {
		                                	 dialogIn.dismiss();
		                                 }
		                             });
		             builder.create().show();
				}
				
			}
		});
		ivMore.setOnTouchListener(this);
		ivSettings.setOnTouchListener(this);
		settings.setOnClickListener(new android.view.View.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(MainActivity.this, SettingActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_of_left); 
			}});
		ivSignin.setOnClickListener(new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Signin();
			}

		});
		
		mGestureDetector = new GestureDetector(this);
		mGestureDetector.setIsLongpressEnabled(false);
		
		resizeLayout();
	}

    @Override
    protected void onResume() {
        super.onResume();

        RelativeLayout.LayoutParams lp = (LayoutParams) mainLayout.getLayoutParams();

        if (lp.leftMargin != 0) {
            if (lp.leftMargin > 0) {
                new SlideMenu().execute(leftLayout.getLayoutParams().width, -SPEED);
            } else if (lp.leftMargin < 0) {
                new SlideMenu().execute(rightLayout.getLayoutParams().width, SPEED);
            }
        }
    }
    private void Signin() {



        gotoDeviceList();



        
	}
	class AsySignin extends AsyncTask<String, String, String>{

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			Log.i("xys", "开始获取设备号......");
			String result=null;
			List<BasicNameValuePair> data=new ArrayList<BasicNameValuePair>();
			data.add(new BasicNameValuePair("http", "http://192.168.1.196:8080/XysAppWeb/AppMain.action"));
			data.add(new BasicNameValuePair("msg", "0102"));
            Log.i("xys",params[0]);
			data.add(new BasicNameValuePair("sn",params[0]));
			try {
				result=Utils.HttpPostData(data);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} 
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(result==null){
				Log.i("xys", ">>>>发生异常");
				caozuoMYDialog(null);
			}else{
				Log.i("xys", "通信正常>>>>>"+result);
				JSONObject resultJson=null;
				String data=null;
                try {
                    resultJson = new JSONObject(result);
                    data=(String) resultJson.get("data");
                    if(data!=null){
                        Const.terminal=data;
                        new AsySignin2().execute(Const.terminal);
                        Log.i("xys", "terminal:"+data);
                    }
                    Log.i("xys", "result："+result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

			}
			
		}
		
	}
    class AsySignin2 extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            Log.i("xys", "开始签到......");
            String result=null;
            List<BasicNameValuePair> data=new ArrayList<BasicNameValuePair>();
            data.add(new BasicNameValuePair("http", "http://192.168.1.196:8080/XysAppWeb/AppMain.action"));
            data.add(new BasicNameValuePair("msg", "0800"));
		data.add(new BasicNameValuePair("serialNo","000001"));
			data.add(new BasicNameValuePair("terminalNo",params[0]));
			//data.add(new BasicNameValuePair("terminalNo","94873661"));
            //data.add(new BasicNameValuePair("merchantNo","820148155052920"));
//            Const.terminal="94873661";
//            Const.shanghuhao="820148155052920";
            data.add(new BasicNameValuePair("merchantNo",Const.shanghuhao));
            Log.i("xys",data.toString());

            try {
                result=Utils.HttpPostData(data);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result==null){
                Log.i("xys", ">>>>发生异常");
                caozuoMYDialog(null);
            }else{
                Log.i("xys", "通信正常>>>>>"+result);
                JSONObject resultJson=null;
                String msg=null;
                    try {
					resultJson = new JSONObject(result);
					 msg=(String) resultJson.get("state");
					 if(msg.equals("ok")){
						 Const.qiandaoPINkey = (String) resultJson.get("pinKey");
						 Const.qiandaoMACkey = (String) resultJson.get("macKey");
						 Const.qiandaoPINkeyCV=(String) resultJson.get("pkcv");
						 Const.qiandaoMACkeyCV=(String) resultJson.get("mkcv");
						 Const.asMacKey=(String) resultJson.get("asmacKey");
						 Const.asMacKeyCV=(String) resultJson.get("asmkcv");
						 Const.batchnum=(String)resultJson.get("batchnum");
						 Log.i("xys", "pinKey:"+Const.qiandaoPINkey+";"+"macKey:"+Const.qiandaoMACkey);
					 }
				    Log.i("xys", "result："+result);
				} catch (JSONException e) {
					e.printStackTrace();
				}
                finally{
                    caozuoMYDialog(msg);
                }
            }

        }

    }
	public void caozuoMYDialog(String string) {
		dialog.cancel();
		if(string==null){
			Toast.makeText(this, ">>>>发生异常", Toast.LENGTH_SHORT).show();
		}else if(!string.equals("ok")){
			Toast.makeText(this, "签到失败>>>>no", Toast.LENGTH_SHORT).show();
		}else{
			Const.signin=true;
			Toast.makeText(this, "签到成功>>>>>ok", Toast.LENGTH_SHORT).show();
		}
		
	}
	 /**
     * 自定义的对话框类
     * 实现对话框的自定义样式
     */
    class MyDialog extends Dialog{
    	ProgressBar pb;
        /**
         * 自定义构造器初始化参数
         * @param context XZHL1210_ClerkDisplayActivity的上下文
         * @param theme 自定义dialog的样式


         */
        public MyDialog(MainActivity context, int theme) {
            super(context, theme);
        }
        /**
         * 重写的加载自定义dialog视图的方法
         * @param savedInstanceState 保存的实例化状态
         */
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.setContentView(R.layout.my_dialogview);
            // 初始化控件
            setupDialogView();
        }
        /**
         * 初始化控件
         */
        private void setupDialogView() {
        	pb=(ProgressBar)findViewById(R.id.progressBar);
        }
    }
	/*
	 * 使用leftMargin及rightMargin防止layout被挤压变形
	 * Math.abs(leftMargin - rightMargin) = layout.width
	 */
	private void resizeLayout(){
		DisplayMetrics dm = getResources().getDisplayMetrics();
		
		// 固定 main layout, 防止被左、右挤压变形
		RelativeLayout.LayoutParams lp = (LayoutParams) mainLayout.getLayoutParams();
		lp.width = dm.widthPixels;
		mainLayout.setLayoutParams(lp);
		
		// 将左layout调整至main layout左边
		lp = (LayoutParams) leftLayout.getLayoutParams();
		lp.leftMargin = -lp.width;
		leftLayout.setLayoutParams(lp);
		Log.d(TAG, "left l.margin = " + lp.leftMargin);
		
		// 将左layout调整至main layout右边
		lp = (LayoutParams) rightLayout.getLayoutParams();
		lp.leftMargin = dm.widthPixels;
		lp.rightMargin = -lp.width;
		rightLayout.setLayoutParams(lp);
		Log.d(TAG, "right l.margin = " + lp.leftMargin);
	}
	
	

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
			RelativeLayout.LayoutParams lp = (LayoutParams) mainLayout.getLayoutParams();
			
			if(lp.leftMargin != 0){
				if(lp.leftMargin > 0){
					new SlideMenu().execute(leftLayout.getLayoutParams().width, -SPEED);
				}else if(lp.leftMargin < 0){
					new SlideMenu().execute(rightLayout.getLayoutParams().width, SPEED);
				}
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void rollLayout(int margin){
		RelativeLayout.LayoutParams lp = (LayoutParams) mainLayout.getLayoutParams();
		lp.leftMargin += margin;
		lp.rightMargin -= margin;
		mainLayout.setLayoutParams(lp);
		lp = (LayoutParams) leftLayout.getLayoutParams();
		lp.leftMargin += margin;
		leftLayout.setLayoutParams(lp);
		lp = (LayoutParams) rightLayout.getLayoutParams();
		lp.leftMargin += margin;
		lp.rightMargin -= margin;
		rightLayout.setLayoutParams(lp);
	}

	private void onScroll(float distanceX){
		bIsScrolling = true;
		mScroll += distanceX;  // 向左为正
		Log.d(TAG, "mScroll = " + mScroll + ", distanceX = " + distanceX);
		
		RelativeLayout.LayoutParams lp = (LayoutParams) mainLayout.getLayoutParams();
		RelativeLayout.LayoutParams lpLeft = (LayoutParams) leftLayout.getLayoutParams();
		RelativeLayout.LayoutParams lpRight = (LayoutParams) rightLayout.getLayoutParams();
		Log.d(TAG, "lp.leftMargin = " + lp.leftMargin);
		
		int distance = 0;
		if(mScroll > 0){ // 向左移动
			if(lp.leftMargin <= 0){ // 打开右导航菜单
				if(iLimited > 0){
					return;
				}
				distance = lpRight.width - Math.abs(lp.leftMargin);
			}else if(lp.leftMargin > 0){ // 关闭左导航菜单
				distance = lp.leftMargin;
			}
			if(mScroll >= distance){
				mScroll = distance;
			}
		}else if(mScroll < 0){  // 向右移动
			if(lp.leftMargin >= 0){ // 打开左导航菜单
				if(iLimited < 0){
					return;
				}
				distance = lpLeft.width - Math.abs(lp.leftMargin);
			}else if(lp.leftMargin < 0){ // 关闭右导航菜单
				distance = Math.abs(lp.leftMargin);
			}
			if(mScroll <= -distance){
				mScroll = -distance;
			}
		}

		Log.d(TAG, "mScroll = " + mScroll);
		if(mScroll != 0){
			rollLayout(-mScroll);
		}
	}
	
	private void onRelease(){
		RelativeLayout.LayoutParams lp = (LayoutParams) mainLayout.getLayoutParams();
		if(lp.leftMargin < 0){ // 左移
			/*
			 * 	左移大于右导航宽度一半，则自动展开,否则自动缩回去
			 */
			if(Math.abs(lp.leftMargin) > rightLayout.getLayoutParams().width/2){
				new SlideMenu().execute(rightLayout.getLayoutParams().width - Math.abs(lp.leftMargin), -SPEED);
			}else{
				new SlideMenu().execute(Math.abs(lp.leftMargin), SPEED);
			}
		}else if(lp.leftMargin > 0){
			/*
			 * 	右移大于左导航宽度一半，则自动展开,否则自动缩回去
			 */
			if(Math.abs(lp.leftMargin) > leftLayout.getLayoutParams().width/2){
				new SlideMenu().execute(leftLayout.getLayoutParams().width - Math.abs(lp.leftMargin), SPEED);
			}else{
				new SlideMenu().execute(Math.abs(lp.leftMargin), -SPEED);
			}
		}
	}

	///////////////////// ListView.onItemClick ///////////////////////
//	@Override
//	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//		Toast.makeText(this, title[arg2], Toast.LENGTH_SHORT).show();
//	}
	
	////////////////////////////// onTouch ///////////////////////////////
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		mClickedView = v;
		
		if(MotionEvent.ACTION_UP == event.getAction() && bIsScrolling){
			onRelease();
		}
		
		return mGestureDetector.onTouchEvent(event);
	}
	
	/////////////////// GestureDetector Override Begin ///////////////////
	@Override
	public boolean onDown(MotionEvent e) {
		
		bIsScrolling = false;
		mScroll = 0;
		iLimited = 0;
		RelativeLayout.LayoutParams lp = (LayoutParams) mainLayout.getLayoutParams();
		if(lp.leftMargin > 0){
			iLimited = 1;
		}else if(lp.leftMargin < 0){
			iLimited = -1;
		}
		
		return true;
	}

	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		onScroll(distanceX);
		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		/*
		 * 	正常情况下，mainLayout的leftMargin为0,
		 * 	当左/右菜单为打开中，此时就不为0，需要判断
		 */
		if(mClickedView != null){
			RelativeLayout.LayoutParams lp = (LayoutParams) mainLayout.getLayoutParams();
			
			if(mClickedView == ivMore){
				Log.d(TAG, "[onSingleTapUp] ivMore clicked! leftMargin = " + lp.leftMargin);
				
				if(lp.leftMargin == 0){
					new SlideMenu().execute(leftLayout.getLayoutParams().width, SPEED);
				}else{
					new SlideMenu().execute(leftLayout.getLayoutParams().width, -SPEED);
				}
			}else if(mClickedView == ivSettings){
				Log.d(TAG, "[onSingleTapUp] ivSettings clicked! leftMargin = " + lp.leftMargin);
				
				if(lp.leftMargin == 0){
					new SlideMenu().execute(rightLayout.getLayoutParams().width, -SPEED);
				}else{
					new SlideMenu().execute(rightLayout.getLayoutParams().width, SPEED);
				}
			}else if(mClickedView == mainLayout){
				Log.d(TAG, "[onSingleTapUp] mainLayout clicked!");
			}
		}
		return true;
	}
	/////////////////// GestureDetector Override End ///////////////////
	
	/**
	 * 
	 * @author cheng.yang
	 *
	 *	左、右菜单滑出
	 *
	 *	params[0]: 滑动距离
	 *	params[1]: 滑动速度,带方向
	 */
	public class SlideMenu extends AsyncTask<Integer, Integer, Void>{
		@Override
		protected Void doInBackground(Integer... params) {
			if(params.length != 2){
				Log.e(TAG, "error, params must have 2!");
			}

			int times = params[0] / Math.abs(params[1]);
			if(params[0] % Math.abs(params[1]) != 0){
				times ++;
			}
			
			for(int i = 0; i < times; i++){
				this.publishProgress(params[0], params[1], i+1);
			}
			
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			if(values.length != 3){
				Log.e(TAG, "error, values must have 3!");
			}

			int distance = Math.abs(values[1]) * values[2];
			int delta = values[0] - distance;

			int leftMargin = 0;
			if(values[1] < 0){ // 左移
				leftMargin = (delta > 0 ? values[1] : -(Math.abs(values[1]) - Math.abs(delta)));
			}else{
				leftMargin = (delta > 0 ? values[1] : (Math.abs(values[1]) - Math.abs(delta)));
			}
			
			rollLayout(leftMargin);
		}
	}
}

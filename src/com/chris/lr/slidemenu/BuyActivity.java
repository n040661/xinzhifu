package com.chris.lr.slidemenu;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.centerm.ctmpos.MPOSControllerCallback;
import com.centerm.mpos.util.HexUtil;
import com.centerm.mpos.util.StringUtil;
import com.centerm.mpos.util.TlvUtil;

public class BuyActivity extends Activity {
private ImageView ivReturnBack;
private RelativeLayout shuaKa;
private EditText etShuru;
String strOne[];
String etShuruStr;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_buy);
		ivReturnBack=(ImageView) findViewById(R.id.buy_return_back);
		shuaKa=(RelativeLayout) findViewById(R.id.tvshuaka);
		etShuru=(EditText)findViewById(R.id.shurujine);
		
		
		ivReturnBack.setOnClickListener(new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(R.anim.in_from_left, R.anim.out_of_right); 
			}
		});
		shuaKa.setOnClickListener(new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				shuaKa.setClickable(false);
				SharedPreferences sp=getSharedPreferences("data",0);
				boolean haveGuan=sp.getBoolean("HAVE_GUAN", false);
				if(!haveGuan){
					guanZhuang();
				}else{
					String str=etShuru.getText()+"";
					startSwipe(str.replaceAll(",", ""));
				}
			}
		});
		strOne=new String[2];
        etShuruStr="";
		
		new Thread() {
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            etShuruStr=etShuru.getText()+"";
                           // if(!etShuruStr.equals(globaltemp1)){
                                
                            etShuruStr=etShuruStr.replaceAll(",", "");
                            if(isDecimal(etShuruStr)){
                            	strOne[0]=etShuruStr.substring(0, etShuruStr.indexOf('.'));
                                strOne[1]=etShuruStr.substring(etShuruStr.indexOf('.')+1,etShuruStr.length());
                                
                                String temp="";
                                int count=0;
                                for(int i=strOne[0].length()-1;i>=0;i--){
                                    temp+=(strOne[0].charAt(i));
                                    count++;
                                    if(count%3==0 && count<strOne[0].length()){
                                        temp+=",";
                                    }
                                }
                                String temp2="";
                                for(int i=temp.length()-1;i>=0;i--){
                                	temp2+=temp.charAt(i);
                                }
                                String temp1="";
                                if(strOne[1].length()>2){
                                    if(Integer.parseInt(strOne[1].charAt(2)+"")>=5){
                                        temp1+=(strOne[1].charAt(0));
                                        temp1+=(Integer.parseInt(strOne[1].charAt(1)+"")+1);
                                    }else{
                                        temp1+=strOne[1].charAt(0);
                                        temp1+=strOne[1].charAt(1);
                                    }
                                }else{
                                    for(int i=0;i<strOne[1].length();i++){
                                        temp1+=strOne[1].charAt(i);
                                    }
                                }
                                String format=temp2+"."+temp1;
                                int position=etShuru.getSelectionStart();
                                int a=0;
                                int b=0;
                                for(int i=0;i<etShuru.getText().length();i++){
                                	if(etShuru.getText().charAt(i)==','){
                                		a++;
                                	}
                                }
                                for(int i=0;i<format.length();i++){
                                	if(format.charAt(i)==','){
                                		b++;
                                	}
                                }
                                etShuru.setText(format);
                                //globaltemp1=format;
                                if(position>format.length()){
                                	etShuru.setSelection(position-1);
                                }else if(b>a){
                                	etShuru.setSelection(position+(b-a));
                                }else{
                                	etShuru.setSelection(position);
                                }
                            }else{
                            	String temp="";
                                int count=0;
                                for(int i=etShuruStr.length()-1;i>=0;i--){
                                    temp+=(etShuruStr.charAt(i));
                                    count++;
                                    if(count%3==0 && count<etShuruStr.length()){
                                        temp+=",";
                                    }
                                }
                                String temp2="";
                                for(int i=temp.length()-1;i>=0;i--){
                                	temp2+=temp.charAt(i);
                                }
                                
                                int position=etShuru.getSelectionStart();
                                int a=0;
                                int b=0;
                                for(int i=0;i<etShuru.getText().length();i++){
                                	if(etShuru.getText().charAt(i)==','){
                                		a++;
                                	}
                                }
                                for(int i=0;i<temp2.length();i++){
                                	if(temp2.charAt(i)==','){
                                		b++;
                                	}
                                }
                                
                                etShuru.setText(temp2);
                                //globaltemp1=temp2;
                                if(position>temp2.length()){
                                	etShuru.setSelection(position-1);
                                }else if(b>a){
                                	etShuru.setSelection(position+(b-a));
                                }else{
                                	etShuru.setSelection(position);
                                }
                                
                            }
                        }
                    });
                }
            };
        }.start();
	}
	
	 private boolean isDecimal(String etPriceStr) {
	        // TODO Auto-generated method stub
	        for(int i=0;i<etPriceStr.length();i++){
	            if(etPriceStr.charAt(i)=='.'){
	                return true;
	            }
	        }
	        return false;
	    }
	private void guanZhuang(){
		
		// 灌装自定义主密钥,索引为1
				byte[] mKey2= StringUtil.hexStr2Bytes("32323232323232323232323232323232");
				MainActivity.controller.disperseTMK(0, mKey2,
						new MPOSControllerCallback() {
							@Override
							public void onDisperseTMKSuc() {
								//showToast("灌装0主密钥成功", null);
								//Const.haveMainKey = true;
								Log.i("xys", "灌装0主密钥成功");
								byte[] tdk = null;
								byte[] mak = StringUtil.hexStr2Bytes(Const.qiandaoMACkey+Const.qiandaoMACkeyCV);
								byte[] pik = StringUtil.hexStr2Bytes(Const.qiandaoPINkey+Const.qiandaoPINkeyCV);
								MainActivity.controller.disperseWKey(0, tdk, mak, pik,
										new MPOSControllerCallback() {
											@Override
											public void onDisperseWKSuc() {
												//showToast("灌装0工作密钥成功", null);
												Log.i("xys", "灌装0工作密钥成功");
												byte[] mKey = StringUtil.hexStr2Bytes("33333333333333333333333333333333");
												MainActivity.controller.disperseTMK(1, mKey,
														new MPOSControllerCallback() {
															@Override
															public void onDisperseTMKSuc() {
																//showToast("灌装1主密钥成功", null);
																Log.i("xys", "灌装1主密钥成功");
																byte[] tdk = null;
																byte[] mak = StringUtil.hexStr2Bytes(Const.asMacKey+Const.asMacKeyCV);
																byte[] pik = null;
																MainActivity.controller.disperseWKey(1, tdk, mak, pik,
																		new MPOSControllerCallback() {
																			@Override
																			public void onDisperseWKSuc() {
																				//showToast("灌装1工作密钥成功", null);
																				Log.i("xys", "灌装1工作密钥成功");
																				//Const.haveGuanKey=true;
																				SharedPreferences.Editor sharedata = getSharedPreferences(
																						"data", 0).edit();
																				sharedata.putBoolean("HAVE_GUAN", true);
																				sharedata.commit();
																				
																				startSwipe(etShuru.getText()+"");
																			}
																			@Override
																			public void onError(int errId, String errMsg) {
																				showToast(null, errId + ":" + errMsg);
																				shuaKa.setClickable(true);
																			}
																		});
															}
															@Override
															public void onError(int errId, String errMsg) {
																showToast(null, errId + ":" + errMsg);
																shuaKa.setClickable(true);
															}
														});
											}
											@Override
											public void onError(int errId, String errMsg) {
												showToast(null, errId + ":" + errMsg);
												shuaKa.setClickable(true);
											}
										});
							}
							@Override
							public void onError(int errId, String errMsg) {
								showToast(null, errId + ":" + errMsg);
								shuaKa.setClickable(true);
							}
						});
	}
	/*
	 * 开启读卡器
	 */
	private void startSwipe(final String shuru) {
		Log.i("xys", shuru);
		String temp0="";
		String temp1="";
		if(shuru.contains(".")){
			String[] jine=new String[2];
				jine[0]=shuru.substring(0,shuru.indexOf("."));
				jine[1]=shuru.substring(shuru.indexOf(".")+1,shuru.length());
			Log.i("xys", jine.toString());
			if(jine[0].length()<10){
				for(int i=0;i<10-jine[0].length();i++){
					temp0+="0";
				}
			}else if(jine[0].length()==10){
				temp0=jine[0];
			}else{
				temp0=jine[0].substring(0,10);
			}
			temp0=temp0+jine[0];
			if(jine[1].length()<2){
				temp1=jine[1]+"0";
			}else if(jine[1].length()==2){
				temp1=jine[1];
			}else{
				temp1=jine[1].substring(0, 2);
			}
			
		}else{
			if(shuru.length()<10){
				for(int i=0;i<10-shuru.length();i++){
					temp0+="0";
				}
			}
			temp0=temp0+shuru;
			temp1=temp1+"00";
		}
		final String amt = temp0+temp1;
		Log.i("xys", "amt>>>>>"+amt);
		int tmo = 20;// 超时时间
		int rmode = 3;// 读卡模式 1代表只读磁条卡
		MainActivity.controller.startSwipe(rmode, 0x00, tmo, 0x02, null, amt,
				new MPOSControllerCallback() {
					@Override
					public void onCardSwipeDetected(int eventId) {
						Log.i("xys", "id:" + eventId);// Log.d("刷卡事件：" + eventId);
						if (eventId == 1) {
							startPBOC(amt,shuru);
						}
						if (eventId == 2) {
							showToast("mpos终端取消刷卡", null);
							shuaKa.setClickable(true);
						}
						if (eventId == 3) {
							showToast("刷卡超时", null);
							shuaKa.setClickable(true);
						}
					}

					// 读取卡片信息成功，eventId==0
					@Override
					public void onCardSwipeSuc(String pan, String expireDT,
							String oneTD, String twoTD, String threeTD) {
						//showToast(pan, null);
						Log.i("xys", "pan:"+pan+"twoTD："+twoTD);
						Const.pan = pan;
						Const.expireDT = expireDT;
						Const.oneTD = oneTD;
						Const.twoTD = twoTD;
						Const.threeTD = threeTD;
						
						// 3>>>> 在开启输密之前灌装自定义主密钥
						// 4>>>>> 请求POS开启输密
						startInputShowPan(pan, expireDT, oneTD, twoTD, threeTD,amt,shuru);
					}
					@Override
					public void onError(int errId, String errMsg) {
						showToast(null, errId + ":" + errMsg);
						shuaKa.setClickable(true);
					}
				});

	}
	private void startPBOC(final String amt,final String shuru) {
		byte[] tlvData = null;
		Map<String, String> map = new HashMap<String, String>();
		map.put("9F02", amt);// 授权金额
		map.put("9F03", "000000000000");// 其他金额
		map.put("9C", "30");// 交易类型
		map.put("DF7C", "01");//
		map.put("DF71", "06");//
		map.put("DF72", "01");// 是否强制联机：
		map.put("DF73", "00");//
		try {
			tlvData = TlvUtil.mapToTlv(map);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		MainActivity.controller.startPBOC(tlvData, new MPOSControllerCallback() {
			@Override
			public void onStartPbocSuc(byte[] reslult, String pan,
					String twoTD, String expireDT, boolean isFallback) {

				if (isFallback) {
					showToast(null, "读卡失败，请重试");
					return;
				}
						Log.i("xys", "55域:"+HexUtil.toString(reslult));
						startInputShowPan2(pan, expireDT,twoTD,amt,HexUtil.toString(reslult),shuru);
						Log.i("", "PBOC标准流程结果：" + HexUtil.toString(reslult)
								+ "isFallback:" + isFallback);

						Log.d("", "pan:" + pan + "\ntwoTD:" + twoTD + "\nexpireDT"
								+ expireDT);
			}

			@Override
			public void onError(int errId, String errMsg) {
				showToast(null, errId + ":" + errMsg);
				shuaKa.setClickable(true);
			}
		});
	}
	private void startInputShowPan2(final String pan, final String expireDT,
			 final String twoTD, final String amt,final String info,final String shuru) {
		
						final String fmtPan = twoTD.substring(0,19);
						String transTp = "消费交易";
						String amt_1 = amt;
						Log.i("xys", "IC卡PAN："+pan+"fmtPan："+fmtPan+"info:"+info);
						String showPan = fmtPan.substring(0, 6)+"****"+fmtPan.substring(fmtPan.length()-4,fmtPan.length());
						//int mKeyId = 1; // 主密钥索引
						MainActivity.controller.startInputPIN(6, 4, 0, 20, fmtPan, transTp,
								amt_1, showPan, new MPOSControllerCallback() {
									@Override
									public void onPinEnterDetected(int eventId) {
										if (eventId == 2) {
											showToast("mpos终端取消输密", null);
											shuaKa.setClickable(true);
										}
										if (eventId == 3) {
											showToast("输密超时", null);
											shuaKa.setClickable(true);
										}
									}
									// 输密成功,获取返回的pinBlock
									@Override
									public void onPinInputSuc(int pinLen, final byte[] pinBlock) {
										Log.i("xys",String.format("回调输密结果\npinBlock:%s",HexUtil.toString(pinBlock)));
										Const.pinBlock = HexUtil.toString(pinBlock);
										showToast("输密成功", null);
										Log.i("xys", "输密成功"+Const.pinBlock);
										Const.jine=shuru;
										Intent intent=new Intent();
										String[] strs=new String[]{pan,fmtPan, expireDT,"",twoTD, "",HexUtil.toString(pinBlock),info,amt};
										intent.putExtra("data", strs);
										intent.setClass(BuyActivity.this, CommadTestActivity.class);
										startActivity(intent);
										overridePendingTransition(R.anim.in_from_right, R.anim.out_of_left); 
										finish();
										//overridePendingTransition(R.anim.in_from_left, R.anim.out_of_right); 
													}
													@Override
													public void onError(int errId, String errMsg) {
														showToast(null, errId + ":TTTTT" + errMsg);
														shuaKa.setClickable(true);
													}
												});
	}
	
	   // 开启输密
		private void startInputShowPan(final String pan, final String expireDT,
				final String oneTD, final String twoTD, final String threeTD,final String amt,final String shuru) {
			
							String fmtPan = pan;
							String transTp = "消费交易";
							String amt_1 = amt;
							String showPan = pan.substring(0, 6)+"****"+pan.substring(pan.length()-4,pan.length());
							//int mKeyId = 1; // 主密钥索引
							MainActivity.controller.startInputPIN(6, 4, 0, 20, fmtPan, transTp,
									amt_1, showPan, new MPOSControllerCallback() {
										@Override
										public void onPinEnterDetected(int eventId) {
											if (eventId == 2) {
												showToast("mpos终端取消输密", null);
												shuaKa.setClickable(true);
											}
											if (eventId == 3) {
												showToast("输密超时", null);
												shuaKa.setClickable(true);
											}
										}
										// 输密成功,获取返回的pinBlock
										@Override
										public void onPinInputSuc(int pinLen, final byte[] pinBlock) {
											Log.i("xys",String.format("回调输密结果\npinBlock:%s",HexUtil.toString(pinBlock)));
											Const.pinBlock = HexUtil.toString(pinBlock);
											showToast("输密成功", null);
											Log.i("xys", "输密成功"+Const.pinBlock);
											Const.jine=shuru;
											Intent intent=new Intent();
											String[] strs=new String[]{"",pan, expireDT,oneTD, twoTD, threeTD,HexUtil.toString(pinBlock),"",amt};
											intent.putExtra("data", strs);
											intent.setClass(BuyActivity.this, CommadTestActivity.class);
											startActivity(intent);
											overridePendingTransition(R.anim.in_from_right, R.anim.out_of_left); 
											finish();
											//overridePendingTransition(R.anim.in_from_left, R.anim.out_of_right); 
														}
														@Override
														public void onError(int errId, String errMsg) {
															showToast(null, errId + ":TTTTT" + errMsg);
															shuaKa.setClickable(true);
														}
													});
		}
		
	private void showToast(String sucMsg, String errMsg) {

		if (sucMsg != null) {
			Toast.makeText(BuyActivity.this, "SUC:" + sucMsg,
					Toast.LENGTH_SHORT).show();
		}
		if (errMsg != null) {
			Toast.makeText(BuyActivity.this, "FAIL:" + errMsg,
					Toast.LENGTH_SHORT).show();
		}

	}
	
}

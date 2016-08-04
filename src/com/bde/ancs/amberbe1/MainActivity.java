package com.bde.ancs.amberbe1;

import gz.lifesense.ancs.aidl.DeviceInfo;
import gz.lifesense.ancs.aidl.RemoteBlueTooth;
import gz.lifesense.ancs.server.ShareManager;
import gz.lifesense.ancs.util.Consts;
import gz.lifesense.ancs.util.DialogUtil;
import gz.lifesense.ancs.util.RLog;

import com.bde.ancs.amberbe1.R;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


@SuppressLint("NewApi")
public class MainActivity extends BaseActivity implements OnClickListener
{

	private RelativeLayout layout_device_item;
	BluetoothAdapter mBluetoothAdapter;
	private final int REQUEST_ENABLE_BT = 0;
	private BluetoothStateReceiver btStateReceiver = new BluetoothStateReceiver();
//	private TextView device_name;
	private String TAG = "MainActivity";

	private ShareManager shareManager;
	private BluetoothManager bluetoothManager;
	
	private LinearLayout bound_device_llt;
	private TextView device_name_tv;
	private Button btn_sacn;
	
	RemoteBlueTooth mRemoteBlueTooth=null;
	DeviceInfo beauty = null;
	boolean isBinded=false;
	ExternServiceConnection mConnection = new ExternServiceConnection();
	private static final int  REQUEST_DISCOVERABLE_BLUETOOTH = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		RLog.i(TAG, "onCreate()");
		super.onCreate(savedInstanceState);
		setCenterView(R.layout.activity_main);

		initView();
		initReceiver();
		shareManager 	= new ShareManager(MainActivity.this);
		
		Intent intentServer = new Intent(MainActivity.this, BlueToothService.class);  
        startService(intentServer); 
		
		Intent intent  = new Intent(Consts.actionName);
		intent.setPackage(getPackageName());
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	private void initView() 
	{
		btn_sacn=(Button)findViewById(R.id.btn_sacn);
		btn_sacn.setOnClickListener(this);
		bound_device_llt=(LinearLayout)findViewById(R.id.bound_device_llt);
		device_name_tv=(TextView)findViewById(R.id.device_name_tv);
		layout_device_item=(RelativeLayout)findViewById(R.id.layout_device_item);
		layout_device_item.setOnClickListener(this);
		
		initBindDialog(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				Intent mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); 
	            startActivityForResult(mIntent, 1); 
	            dismissBindDialog();
			}
		});
	}
	
	@Override
	public void onClick(View v) 
	{
		// TODO Auto-generated method stub
		switch (v.getId())
		{
			case R.id.btn_sacn:
				Intent intent=new Intent(MainActivity.this, SearchActivity.class);
				startActivity(intent);
				break;
			case R.id.layout_device_item:
				Intent intent1=new Intent(MainActivity.this, DeviceDetailActivity.class);
				startActivity(intent1);
				break;
				default:
					break;
		}
	}

	
	private void initReceiver()
	{
		IntentFilter filter = new IntentFilter();
		//filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		filter.addAction("android.bluetooth.device.action.PAIRING_REQUEST");
		filter.addAction("android.intent.action.BOOT_COMPLETED");// �?�?
		filter.addAction("DEVICE_CONNECTED");
		filter.addAction("DEVICE_DISCONNECT");
		filter.addAction("REPLAY_CALL");
		filter.addAction("REPLAY_REMOVE");
		filter.addAction("HARDWARE_VERSION");
		filter.addAction("NOT_FIND_FCCO_SERVICE");
		filter.addAction("START_CONNECT");
		// filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
		registerReceiver(btStateReceiver, filter);
	}

	private class ExternServiceConnection implements ServiceConnection
	{
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) 
		{
			Log.i(TAG, "onServiceConnected...");
			mRemoteBlueTooth = RemoteBlueTooth.Stub.asInterface(service);
			if(mRemoteBlueTooth==null)
			{
				Log.e(TAG, "mRemoteBlueTooth is null...");
				return;
			}
			try
			{
				isBinded=true;
				beauty 	= mRemoteBlueTooth.getDeviceInfo();
			} 
			catch (RemoteException e) 
			{
				e.printStackTrace();
			}
			
			try 
			{ 
				if(mRemoteBlueTooth.isBluetoothOpen()==false)
				{
					Log.i(TAG, "onServiceConnected bt no open...");
						bound_device_llt.setVisibility(View.GONE);
				}
				else
				{
					if(mRemoteBlueTooth.isDeviceConnected()==true)
					{
						bound_device_llt.setVisibility(View.VISIBLE);
						//if(shareManager.getDeviceName().equals(""))
						//	device_name_tv.setText(getResources().getString(R.string.prename)+"PE841M");
						//else
							device_name_tv.setText(shareManager.getDeviceName());
					}
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/*
			if(mRemoteBlueTooth != null)
			{
				try {
					if(mRemoteBlueTooth.getDiscoverDuration())
					{
					    Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
					    discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
					    startActivityForResult(discoverableIntent, REQUEST_DISCOVERABLE_BLUETOOTH);
					    mRemoteBlueTooth.setDiscoverDuration(false);
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			*/
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.i(TAG, "onServiceDisconnected...");
		}

	}//
	
	@Override
	protected void onStart() 
	{
		super.onStart();
	}
	
	@Override
	protected void onResume() 
	{
		super.onResume();

		initANCSManager();
		DialogUtil.getInstance().DismissPopupWindow();

		if (!shareManager.getDeviceAddress().equals("")) 
		{
				try 
				{
					if(mRemoteBlueTooth!=null &&mRemoteBlueTooth.isDeviceConnected())
					{
						bound_device_llt.setVisibility(View.VISIBLE);
						//if(shareManager.getDeviceName().equals(""))
						//	device_name_tv.setText(getResources().getString(R.string.prename)+"PE841M");
						//else
							device_name_tv.setText(shareManager.getDeviceName());
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.e(TAG, "==================");
		}
		else
		{
			bound_device_llt.setVisibility(View.GONE);
		}
		
		try {
			
			if(mRemoteBlueTooth!=null && mRemoteBlueTooth.isBluetoothOpen()==false)
			{
				Log.i(TAG, "onResume bt no open...");
				bound_device_llt.setVisibility(View.GONE);
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private class BluetoothStateReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent) 
		{
			int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
			Log.i("TAG", "state=" + state + ",,intent.getAction()=" + intent.getAction());
			DialogUtil.getInstance().DismissPopupWindow();

			String action = intent.getAction();
			if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) 
			{
				switch (state) 
				{
					case BluetoothAdapter.STATE_TURNING_ON://on
						break;
						
					case BluetoothAdapter.STATE_ON: //last on
						if(!shareManager.getDeviceAddress().equals(""))
						{
							initANCSManager();
							try 
							{
								if(mRemoteBlueTooth!=null)
									mRemoteBlueTooth.connectLast();
							} catch (RemoteException e) 
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						break;
						
					case BluetoothAdapter.STATE_TURNING_OFF://off
						break;
						
					case BluetoothAdapter.STATE_OFF:// last off
						showBindDialog(getResources().getString(R.string.req_open_bt));
						break;
					default:
						break;
				}
			} 
			else if (action.equals("android.bluetooth.device.action.PAIRING_REQUEST")) {// 系统配对请求
			} 
			else if (action.equals("START_CONNECT")) 
			{
			} 
			else if (action.equals("DEVICE_CONNECTED")) 
			{
				Log.d(TAG, "收到广播，蓝牙连接成�?");
				bound_device_llt.setVisibility(View.VISIBLE);
				device_name_tv.setText(shareManager.getDeviceName());
			}
			else if (action.equals("DEVICE_DISCONNECT"))
			{
				bound_device_llt.setVisibility(View.GONE);
			}
		}
	}

	
	private void initANCSManager() 
	{
		Log.i(TAG, "initANCSManager");
		bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
	}


	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		RLog.i(TAG, "onDestroy()");
		// unregisterReceiver(btStateReceiver) 

		try 
		{
			unregisterReceiver(btStateReceiver);
		}
		catch (Exception e) 
		{
		}
		unbindService(mConnection);	
	}

	@Override
	protected void initHeader()
	{
		setHeader_Title(getResources().getString(R.string.title));
	}

}


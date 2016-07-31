package com.bde.ancs.androidplugin;

import gz.lifesense.ancs.aidl.DeviceInfo;
import gz.lifesense.ancs.aidl.RemoteBlueTooth;
import gz.lifesense.ancs.device.SearchListAdapter;
import gz.lifesense.ancs.server.ShareManager;
import gz.lifesense.ancs.util.Consts;
import gz.lifesense.ancs.util.DialogUtil;

import java.util.ArrayList;

import com.bde.ancs.androidplugin.R;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

@SuppressLint("NewApi")
public class SearchActivity extends BaseActivity implements OnItemClickListener 
{
	private ListView searchListView;
	private SearchListAdapter searchListAdapter;
	private ArrayList<DeviceInfo> deviceInfos;
	private ArrayList<BluetoothDevice> deviceNameList;
	private static final String TAG = "SearchActivity";

	private BluetoothAdapter mBluetoothAdapter;
	//private DeviceManager mDeviceManager;
	private static String macHolder,nameHolder;
	private BluetoothStateReceiver btStateReceiver;
	private BluetoothManager bluetoothManager;
	
	private final int SHOW_LOADING_DIALOG=2444;
	private final int SHOW_CONNECTED_CONNECTED=2333;
	private final int RETURN_TO_MAIN=2555;
	private RotateAnimation rotateAnimation;
	
	RemoteBlueTooth mRemoteBlueTooth=null;
	DeviceInfo beauty = null;
	boolean isBinded=false;
	ExternServiceConnection mConnection = new ExternServiceConnection();
	
	Handler mHandler = new Handler() 
	{
		@Override
		public void handleMessage(Message msg) 
		{
			super.handleMessage(msg);

			switch (msg.what) 
			{
				case SHOW_LOADING_DIALOG:
					break;
					
				case SHOW_CONNECTED_CONNECTED:
					ShareManager shareManager=new ShareManager(SearchActivity.this);
					shareManager.setDeviceAddress(macHolder);
					shareManager.setDeviceName(nameHolder);
					showConnectdDialog();
					sendEmptyMessageDelayed(RETURN_TO_MAIN, 3000);
					break;
					
				case RETURN_TO_MAIN:
					dismissConnectedDialog();
					finish();
					break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setCenterView(R.layout.activity_search);
		
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		filter.addAction("android.bluetooth.device.action.PAIRING_REQUEST");
		filter.addAction("android.intent.action.BOOT_COMPLETED");// auto start
		filter.addAction("DEVICE_CONNECTED");
		filter.addAction("DEVICE_DISCONNECT");
		filter.addAction("REPLAY_CALL");
		filter.addAction("REPLAY_REMOVE");
		filter.addAction("HARDWARE_VERSION");
		filter.addAction("NOT_FIND_FCCO_SERVICE");
		filter.addAction("START_CONNECT");
		//filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
		btStateReceiver = new BluetoothStateReceiver();
		registerReceiver(btStateReceiver, filter);
		
		
		Intent intent  = new Intent(Consts.actionName);
		intent.setPackage(getPackageName());
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);	
		
		initView();
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
				if(mRemoteBlueTooth.isBluetoothOpen())
					startScan();
				else
					showOpenDialog(getResources().getString(R.string.reqopen));
			} catch (RemoteException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.i(TAG, "onServiceDisconnected...");
		}

	}//
	
	@Override
	protected void onResume() 
	{
		super.onResume();
		
	}

	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		mBluetoothAdapter.stopLeScan(mLeScanCallback);
		try 
		{
			unregisterReceiver(btStateReceiver);
		} 
		catch (Exception e) 
		{
		}
		unbindService(mConnection);	
	}

	private void initView() 
	{
		searchListView = (ListView) findViewById(R.id.search_list);
		deviceInfos = new ArrayList<DeviceInfo>();
		searchListAdapter = new SearchListAdapter(this, deviceInfos);
		searchListView.setOnItemClickListener(this);
		searchListView.setAdapter(searchListAdapter);

		rotateAnimation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		//Bluetooth adapter,

		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		
		deviceNameList = new ArrayList<BluetoothDevice>();
		
		initOpenDialog(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				try 
				{
					mRemoteBlueTooth.openBluetooth();
				} 
				catch (RemoteException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				dismissOpenDialog();
			}
		});

		initBindDialog(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				try 
				{
					mRemoteBlueTooth.connect(macHolder);
				} catch (RemoteException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				stopScan();
			}
		});
		initConnectedDialog(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
			}
		});
		

	}

	void startScan()
	{
		rotateAnimation.setDuration(1000);
		rotateAnimation.setRepeatCount(10000);
		((ImageView) findViewById(R.id.searching_iv)).setAnimation(rotateAnimation);
		mBluetoothAdapter.startDiscovery();
		mBluetoothAdapter.startLeScan(mLeScanCallback);
	}
	
	void stopScan()
	{
		dismissBindDialog();
		//stop animation
		rotateAnimation.cancel();
	}
	  
	/*
	 * 
	 * scan callback
	 */
	private final BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() 
	{ 
		@Override
		public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) 
		{
			runOnUiThread(new Runnable() 
			{
				@Override
				public void run() 
				{
					Log.i(TAG, "device.GETNAME="+device.getName());
					Log.i(TAG, "device.rssi="+rssi);
					Log.i(TAG, "device.address="+device.getAddress());
					
					if (deviceNameList==null) 
					{
						deviceNameList = new ArrayList<BluetoothDevice>();
					}
					if (!deviceNameList.contains(device) && device.getName()!=null&&device.getName().contains("PE")) 
					{
						deviceNameList.add(device);//
						DeviceInfo deviceInfo = new DeviceInfo();
						deviceInfo.setDevice(device);
						deviceInfo.setDeviceName(getResources().getString(R.string.prename)+device.getName());
						deviceInfo.setMac(device.getAddress());
						// Log.e(TAG, "rssi==" + rssi);
						deviceInfo.setRssi(rssi);
						deviceInfos.add(deviceInfo);
						
						searchListAdapter.notifyDataSetChanged();
					}
					searchListAdapter.notifyDataSetChanged();
				}
			});
		}
	};

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
	{
		DeviceInfo deviceInfo = deviceInfos.get(position);
		macHolder = deviceInfo.getMac();
		nameHolder=deviceInfo.getDeviceName();
		mBluetoothAdapter.stopLeScan(mLeScanCallback);
		showBindDialog(getResources().getString(R.string.ifand) + deviceInfo.getDeviceName() + getResources().getString(R.string.ifbind));
	}



	@Override
	protected void initHeader() 
	{
		setHeader_Title(getResources().getString(R.string.bind_dev));
		setHeader_LeftText("<"+getResources().getString(R.string.turn_back));
		
		setHeader_LeftClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				finish();
			}
		});
	};
	
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
					case BluetoothAdapter.STATE_TURNING_ON:
						break;
						
					case BluetoothAdapter.STATE_ON:
						startScan();
						Log.i(TAG, "open bluetooth and scan.");

						Log.i(TAG, "bluetooth on");
						break;
						
					case BluetoothAdapter.STATE_TURNING_OFF:
						break;
						
					case BluetoothAdapter.STATE_OFF:
						Log.i(TAG, "bluetooth off");
						DialogUtil.getInstance().DismissPopupWindow();
						finish();
						break;
				}
			} 
			else if (action.equals("android.bluetooth.device.action.PAIRING_REQUEST")) 
			{ 
				// BluetoothDevice btDevice =
				// intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

			} 
			else if (action.equals("DEVICE_CONNECTED")) 
			{
				Log.d(TAG, "receive broadcast ,bluetooth connected success.");
//				mBluetoothAdapter.stopLeScan(callback_scanDevice);
				//
//				PedometerProtocol pedometerProtocol = new PedometerProtocol();
//				byte[] sendCommingCall = pedometerProtocol.sendCommingCall();
//				myBluetoothManager.writeCharacteristic(sendCommingCall);

				//
				mHandler.sendEmptyMessage(SHOW_CONNECTED_CONNECTED);

				try {
					mRemoteBlueTooth.receivedTelegram();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} 
			else if (action.equals("DEVICE_DISCONNECT"))
			{
//				 mHandler.sendEmptyMessage(DISCONNECT);
			} 
		}
	}
	

}

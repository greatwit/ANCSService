package gz.lifesense.ancs.bluetooth;

import gz.lifesense.ancs.androidancs.PedometerProtocol;
import gz.lifesense.ancs.server.ShareManager;
import gz.lifesense.ancs.util.RLog;
import gz.lifesense.ancs.util.TxtLog;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

@SuppressLint("NewApi")
//@SuppressWarnings("unused")
public class DeviceManager
{
	private Context mContext;

	private final static String TAG = DeviceManager.class.getSimpleName();
	private static DeviceManager mDeviceManager;
	
	private OnCattChangeListener onCattChangeListener;
	private BluetoothManager mBluetoothManager = null;
	private static BluetoothAdapter mBluetoothAdapter;
	private String mBluetoothDeviceAddress = "";
	private static BluetoothGatt mBluetoothGatt;
	private int mConnectionState = BluetoothProfile.STATE_DISCONNECTED;

	public static BluetoothGattCharacteristic writeGattCharacteristic;
	public static BluetoothGattCharacteristic readGattCharacteristic;
	public static UUID descriptorUUID;


	// private ProtobufData protobufData; 
	private PedometerProtocol pedometerProtocol;
	private ShareManager shareManager;
	private BluetoothDevice mDevice;
	private int sendCount = 0;

	private List<BluetoothGattCharacteristic> gattCharacteristics_device;
	private String nameOrNumHolder;

	//reconnect 
	private boolean mStartConnectMonitor = true;
	private Handler mhandler = new Handler();
	private int 	mIntervalTime = 20*1000;

	//private boolean isHangUp = false;
	private boolean needSendAfterConnect = false;

	private DeviceStatReceiver mStateReceiver = new DeviceStatReceiver();
	
	private DeviceManager(Context context) 
	{
		super();
		mContext = context;
		// protobufData = new ProtobufData();
		pedometerProtocol 	= new PedometerProtocol();
		shareManager 		= new ShareManager(context);
	}
	
	public static DeviceManager getInstance(Context context) 
	{
		if (mDeviceManager == null) 
		{
			mDeviceManager = new DeviceManager(context);
		}
		return mDeviceManager;
	}


	private void initReceiver()
	{
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		filter.addAction("android.bluetooth.device.action.PAIRING_REQUEST");
		filter.addAction("android.intent.action.BOOT_COMPLETED");// 开机
		filter.addAction("android.intent.action.PHONE_STATE");
		mContext.registerReceiver(mStateReceiver, filter);
	}
	
	public class DeviceStatReceiver extends BroadcastReceiver
	{  
        private static final String TAG = "PhoneStatReceiver";
  
        @Override  
        public void onReceive(Context context, Intent intent) 
        {
	        	if (intent.getAction().equals("android.intent.action.PHONE_STATE"))
	        	{
                        TelephonyManager tm = (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);                          
                        switch (tm.getCallState()) 
                        {
	                        case TelephonyManager.CALL_STATE_RINGING:  // phone comming
	                                String incoming_number = intent.getStringExtra("incoming_number");  
	                                Log.i(TAG, "RINGING :"+ incoming_number);  
	                             
	            					receivedTelegram();
	                                break;  
	                        case TelephonyManager.CALL_STATE_OFFHOOK:  // put up   
	                        	Log.i(TAG, " CALL_STATE_OFFHOOK");
	                        	if (!shareManager.getDeviceAddress().equals("")) 
	                        	{
	                        		byte[] sendCommingCallReplay = pedometerProtocol.sendCommingCallReplay();
	                        		writeCharacteristic(sendCommingCallReplay);
	                        	}
	                        	shareManager.setMissCall(false);
	                            break;  
	                          
	                        case TelephonyManager.CALL_STATE_IDLE:     //hand up   
	                        	Log.i(TAG, "hand up CALL_STATE_IDLE");
	                        	if (!shareManager.getDeviceAddress().equals("")) 
	                        	{
	                        		byte[] sendCommingCallReject = pedometerProtocol.sendCommingCallReject();
	                        		writeCharacteristic(sendCommingCallReject);
	                        	}
	                        	shareManager.setMissCall(false);
	                            break;
                        }
	          }
        }
	}//

	/**
	 * 初始化
	 * 
	 * @return
	 */
	
	public boolean initialize() 
	{
		if(mBluetoothManager!=null)
		{
			RLog.w(TAG, "Have initialized BluetoothManager.");
			return false;
		}
		
		initReceiver();
		
		if (mBluetoothManager == null) 
		{
			mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
			if (mBluetoothManager == null) 
			{
				RLog.e(TAG, "Unable to initialize BluetoothManager.");
				return false;
			}
		}

		mBluetoothAdapter = mBluetoothManager.getAdapter();
		if (mBluetoothAdapter == null) 
		{
			RLog.e(TAG, "Unable to obtain a BluetoothAdapter.");
			return false;
		}

		return true;
	}

	public boolean unInitialize()
	{
		mContext.unregisterReceiver(mStateReceiver);
		return true;
	}
	
	/**
	 * 判断蓝牙是否已连接
	 * 
	 * @return
	 */
	public boolean isBluetoothConnected(Context context) 
	{
		RLog.i(TAG, "mConnectionState=" + mConnectionState);
		if (mConnectionState != BluetoothProfile.STATE_DISCONNECTED) 
		{
			return true;
		} 
		else 
		{
			Toast.makeText(context, "蓝牙已断开", Toast.LENGTH_SHORT).show();
		}
		return false;
	}

	/**
	 * 判断蓝牙是否已连接
	 * 
	 * @return
	 */
	public boolean isBluetoothConnected() 
	{
		if (mConnectionState != BluetoothProfile.STATE_DISCONNECTED) 
		{
			return true;
		}
		return false;
	}

	/**
	 * 判断蓝牙是否打开
	 * 
	 * @return
	 */
	public boolean isBluetoothOpen() 
	{
		boolean result = false;
		if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) 
		{
			result = true;
		}
		return result;
	}

	public boolean openBluetooth()
	{
		boolean result = false;
		if (mBluetoothAdapter != null) 
		{
			mBluetoothAdapter.enable();
			result = true;
		}
		return result;
	}
	

	public boolean connect(String address) 
	{
		// 发送广播
		Intent intent = new Intent();
		intent.setAction("START_CONNECT");
		mContext.sendBroadcast(intent);

		RLog.i(TAG, "address=" + address);

		if (mBluetoothAdapter == null || address == null) 
		{
			RLog.w(TAG, "connect,BluetoothAdapter not initialized or unspecified address.");
			return false;
		}

		mBluetoothDeviceAddress = address;

		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		if (device == null) 
		{
			RLog.w(TAG, "Device not found.  Unable to connect.");
			return false;
		}
		// We want to directly connect to the device, so we are setting the
		// autoConnect
		//mBluetoothAdapter.stopLeScan(null);
		mBluetoothGatt = device.connectGatt(mContext, false, mGattCallback);
		if(mBluetoothGatt!=null)
		{
			mBluetoothGatt.discoverServices();
		}
		mDevice = device;
		mStartConnectMonitor = true;
		mConnectionState = BluetoothProfile.STATE_CONNECTING;
		
		RLog.d(TAG, "Trying to create a new connection.");
		
		return true;
	}
	
	public void connectLastDevice() 
	{
		RLog.i(TAG, "connectLastDevice");
		String address = shareManager.getDeviceAddress();

		if (!address.equals("")) 
		{
			if (mBluetoothAdapter!=null ) 
			{
				//Log.e(TAG, "Android function startLeScan failed");
				connect(address);
			} 
			else 
			{
				Log.e(TAG, "startLeScan(callback)");
				//mBluetoothAdapter.startLeScan(callback);
			}

		} else {
			RLog.i(TAG, "address=" + address);
			//mBluetoothAdapter.startLeScan(callback2);
		}
	}

	/**
	 * 断开连接
	 */
	public void disconnect() 
	{
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				try {
						if (mBluetoothAdapter == null || mBluetoothGatt == null) 
						{
							RLog.w(TAG, "disconnect,BluetoothAdapter not initialized");
							return;
						}
						RLog.w(TAG, "disconnect");
						mStartConnectMonitor = false;
						mBluetoothGatt.disconnect();
						mConnectionState = BluetoothProfile.STATE_DISCONNECTING;// 修改状态
					} 
				catch (Exception e) 
				{
						e.printStackTrace();
					}
			}
		}).start();
		
	}
	

	public boolean isDeviceConnected() 
	{
		boolean isConnected = false;

		if (!shareManager.getDeviceAddress().equals("") && mBluetoothGatt != null) {

			if (mDevice == null) {
				mDevice = mBluetoothAdapter.getRemoteDevice(shareManager.getDeviceAddress());
			}

			List<BluetoothDevice> list = mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT);
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).getAddress().equals(shareManager.getDeviceAddress())) {
					isConnected = true;
				}
			}
		}
		Log.i(TAG, "conncectResult=" + isConnected);

		return isConnected;
	}

    private Runnable mConnectMonitor=new Runnable() 
    {
        @Override
        public void run() 
        {
            // TODO Auto-generated method stub
            Log.i("Run Service", "connecting runnable-------------");
            if(isBluetoothOpen())
            {
        		if (!shareManager.getDeviceAddress().equals("")) 
        		{
        			if (!isDeviceConnected()) 
        			{

        				mBluetoothAdapter = mBluetoothManager.getAdapter();
        				if(mBluetoothAdapter!=null)
        				{
        					mBluetoothAdapter.enable();
        					if(!mBluetoothAdapter.startDiscovery())
        					{
        						Log.e(TAG, "mBluetoothAdapter.startDiscovery()====failed");
        					}

        				}
        				
        				mBluetoothGatt.disconnect();
        				mBluetoothGatt.close();
        				
        				String address = shareManager.getDeviceAddress();
        				BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        				if (device != null) 
        				{
	        				mBluetoothGatt = device.connectGatt(mContext, false, mGattCallback);
	        				if(null == mBluetoothGatt)
	        					RLog.w(TAG, "Device reconnectGatt failed...");
        					
        				}
        				else
        				{
        					RLog.w(TAG, "Device not found.  Unable to connect.");
        				}
        			}
        		}
            }
            receivedTelegram();
            mhandler.postDelayed(mConnectMonitor, mIntervalTime);
        }
    }; 
	
    void startConnectMonitor()
    {
    	mhandler.postDelayed(mConnectMonitor, mIntervalTime);
    }
    
    void stopConnectMonitor()
    {
    	mhandler.removeCallbacks(mConnectMonitor);
    }

    
	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() 
	{
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) 
		{
			if (newState == BluetoothProfile.STATE_CONNECTED) 
			{
				RLog.i(TAG, "Connected to GATT server.");
				Log.w("DISCONNECT", "Connected to GATT server.");
				if (mBluetoothGatt != null) 
				{

					mConnectionState = BluetoothProfile.STATE_CONNECTED;
					String intentAction = BluetoothContent.ACTION_GATT_CONNECTED;
					// onCattChangeListener.onCattChange(intentAction, null);
					RLog.w(TAG, "Attempting to start service discovery:" + mBluetoothGatt.discoverServices());

					// 发送广播
					Intent intent = new Intent();
					intent.setAction("DEVICE_CONNECTED");
					mContext.sendBroadcast(intent);
					RLog.d(TAG, "发送广播，DEVICE_CONNECTED");
					
					if (needSendAfterConnect) 
					{
						byte[] sendCommingCall = pedometerProtocol.sendCommingCall();
						writeCharacteristic(sendCommingCall);
						needSendAfterConnect = false;
					}
					if (shareManager.hasMissCall()) 
					{
						byte[] sendMissingCall = pedometerProtocol.sendMissingCall();
						writeCharacteristic(sendMissingCall);
						needSendAfterConnect = false;
						shareManager.setMissCall(false);
					}
					receivedTelegram();
				}
				else 
				{
					RLog.e(TAG, "mBluetoothGatt==null");
				}
				stopConnectMonitor();
			}
			else if (newState == BluetoothProfile.STATE_DISCONNECTED) 
			{
				needSendAfterConnect = false;
				Log.w("DISCONNECT", "Disconnected from GATT server断开");

				//mBluetoothGatt.close();
				//mBluetoothGatt = null;
				mConnectionState = BluetoothProfile.STATE_DISCONNECTED;
				// 发送广播
				Intent intent = new Intent();
				intent.setAction("DEVICE_DISCONNECT");
				mContext.sendBroadcast(intent);
		        
				if(mStartConnectMonitor)
					startConnectMonitor();
				
			}
		}

		
		
		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) 
		{
			if (status == BluetoothGatt.GATT_SUCCESS) 
			{
				displayGattServices(getSupportedGattServices());
			} else 
			{
				RLog.w(TAG, "onServicesDiscovered received: " + status);
			}
			RLog.w(TAG, "onServicesDiscovered received======: " + status);
			receivedTelegram();
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) 
		{
			RLog.w(TAG, "onCharacteristicRead");
			if (status == BluetoothGatt.GATT_SUCCESS) 
			{
				// onCattChangeListener.onCattChange(BluetoothContent.ACTION_DATA_AVAILABLE,
				// characteristic);
				receiveData(characteristic);
			}
		}

		@Override
		public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) 
		{
			RLog.w(TAG, "onDescriptorWrite,status=" + status);
			if (status == BluetoothGatt.GATT_SUCCESS) 
			{
				// receiveData(characteristic);
				String uuid = descriptor.getUuid().toString();
				RLog.w(TAG, "onDescriptorWrite,uuid=" + uuid);
				uuid = uuid.substring(4, 8);
				RLog.w(TAG, "onDescriptorWrite,uuid=" + uuid);

				if (uuid.equalsIgnoreCase("2902")) 
				{
					for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics_device) 
					{
						uuid = gattCharacteristic.getUuid().toString();
						uuid = uuid.substring(4, 8);
						RLog.i(TAG, "DEVICE_INFORMATION_UUID,uuid====" + uuid);

						if (uuid.equalsIgnoreCase(BluetoothContent.DEVICE_HARDWARE_UUID)) 
						{
							mBluetoothGatt.readCharacteristic(gattCharacteristic);// A0sa
						}
					}
				}
			}
		};

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
		}

	};

	private void receiveData(BluetoothGattCharacteristic characteristic) 
	{
		final byte[] data = characteristic.getValue();

		String uuid = characteristic.getUuid().toString();
		uuid = uuid.substring(4, 8);
		if (uuid.equalsIgnoreCase(BluetoothContent.DEVICE_HARDWARE_UUID)) 
		{
			String harewareVersion = new String(data);
			shareManager.setHardwareVersion(harewareVersion);
			RLog.i(TAG, "receiveData,uuid====" + uuid);

			Intent intent = new Intent();
			intent.setAction("HARDWARE_VERSION");
			mContext.sendBroadcast(intent);
		}
		for (int i = 0; i < data.length; i++) 
		{
			Log.i(TAG, "receiveData," + data[i]);
		}
	}
    
	public void receivedTelegram()
	{
		Log.i(TAG, "receivedTelegram-------------------------");
		if (!shareManager.getDeviceAddress().equals("")) 
		{
			if (!isDeviceConnected()) 
			{
				connectLastDevice();
				needSendAfterConnect = true;
			} 
			//else 
			{
				byte[] sendCommingCall = pedometerProtocol.sendCommingCall();
				writeCharacteristic(sendCommingCall);
				Log.i(TAG, "receivedTelegram-------------------------1");
			}
		}
		shareManager.setMissCall(true);
	}

	public void writeCharacteristic(byte[] value) 
	{
		if (writeGattCharacteristic != null) 
		{
			writeGattCharacteristic.setValue(value);
			Log.i(TAG, "writeCharacteristic,," + writeGattCharacteristic.getUuid());
			// writeCharacteristic(writeGattCharacteristic);
			if (mBluetoothGatt != null) {
				boolean result = mBluetoothGatt.writeCharacteristic(writeGattCharacteristic);
				RLog.i(TAG, "writeCharacteristic,result=" + result);
			}

		}
	}
	
	public void readCharacteristic(BluetoothGattCharacteristic characteristic) 
	{ 
		if (mBluetoothAdapter == null || mBluetoothGatt == null)
		{
			RLog.w(TAG, "readCharacteristic,BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.readCharacteristic(characteristic);
	}

	public void setCharacteristicNotification(boolean enabled) 
	{
		if (mBluetoothAdapter == null || mBluetoothGatt == null) 
		{
			RLog.w(TAG, "setCharacteristicNotification,BluetoothAdapter not initialized");
			return;
		}

		if (enableIndication(mBluetoothGatt, true, readGattCharacteristic)) 
		{
			boolean result = mBluetoothGatt.setCharacteristicNotification(readGattCharacteristic, enabled);
			RLog.i(TAG, "result=" + result);
		}

	}

	public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) 
	{
		if (mBluetoothAdapter == null || mBluetoothGatt == null) 
		{
			RLog.w(TAG, "setCharacteristicNotification,BluetoothAdapter not initialized");
			return;
		}

		if (enableIndication(mBluetoothGatt, true, characteristic)) {
			mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
		}

	}

	private boolean enableIndication(BluetoothGatt gatt, boolean enable, BluetoothGattCharacteristic characteristic) 
	{
		if (gatt == null)
			return false;

		if (!gatt.setCharacteristicNotification(characteristic, enable))
			return false;

		BluetoothGattDescriptor clientConfig = characteristic.getDescriptor(descriptorUUID);

		if (clientConfig == null)
			return false;

		if (enable) 
		{
			RLog.w(TAG, "enableIndication");

			clientConfig.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
		} else {
			clientConfig.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
		}
		return gatt.writeDescriptor(clientConfig);
	}

	public List<BluetoothGattService> getSupportedGattServices() 
	{
		if (mBluetoothGatt == null)
			return null;
		return mBluetoothGatt.getServices();
	}
	
	private void displayGattServices(List<BluetoothGattService> gattServices)
	{
			for (BluetoothGattService gattService : gattServices) 
			{
				String uuid = gattService.getUuid().toString();
				uuid = uuid.substring(4, 8);
				RLog.i(TAG, "uuid=" + uuid);

				if (uuid.equalsIgnoreCase(BluetoothContent.TARGET_UUID)) 
				{
					List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();

					// Loops through available Characteristics.
					for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) 
					{
						uuid = gattCharacteristic.getUuid().toString();
						uuid = uuid.substring(4, 8);

						RLog.i(TAG, "uuid====" + uuid);
						if (uuid.equalsIgnoreCase("fd00")) 
						{
							RLog.i(TAG, "uuid=fcc7!!!");

							List<BluetoothGattDescriptor> gattDescriptors = gattCharacteristic.getDescriptors();
							for (BluetoothGattDescriptor gattDescriptor : gattDescriptors) 
							{
								RLog.e(TAG, "-------->desc uuid:" + gattDescriptor.getUuid());
								descriptorUUID = gattDescriptor.getUuid();
							}
							setCharacteristicNotification(gattCharacteristic, true);

							// 发送广播
							Intent intent = new Intent();
							intent.setAction("DEVICE_CONNECTED");
							mContext.sendBroadcast(intent);
							RLog.d(TAG, "发送广播，DEVICE_CONNECTED");

						}
						if (uuid.equalsIgnoreCase("FD18")) 
						{
							writeGattCharacteristic = gattCharacteristic;
						}
					}
				}

				if (uuid.equalsIgnoreCase(BluetoothContent.DEVICE_INFORMATION_UUID)) 
				{
					gattCharacteristics_device = gattService.getCharacteristics();
				}
			}
	}

	public void setOnCattChange(OnCattChangeListener onCattChange) 
	{
		onCattChangeListener = onCattChange;
	}

	public interface OnCattChangeListener 
	{
		public void onCattChange(String action, BluetoothGattCharacteristic characteristic);
	}


	// 查询指定电话的联系人姓名，邮箱
	public String getContactNameByNumber(String number, Context mContext) 
	{
		String name = null;
		Uri uri = Uri.parse("content://com.android.contacts/data/phones/filter/" + number);
		ContentResolver resolver = mContext.getContentResolver();
		Cursor cursor = resolver.query(uri, new String[] { "display_name" }, null, null, null);
		if (cursor.moveToFirst()) 
		{
			name = cursor.getString(0);
		} else
			name = "";

		cursor.close();
		return name;
	}

	private LeScanCallback callback = new LeScanCallback() 
	{
		@Override
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) 
		{
			if (device.getAddress().equals(shareManager.getDeviceAddress())) 
			{
				mBluetoothAdapter.stopLeScan(callback);
				connect(device.getAddress());
			}
		}
	};
	
	private LeScanCallback callback2 = new LeScanCallback() 
	{
		@Override
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) 
		{
			if (device.getName().contains("PE") && device.getAddress().equals(shareManager.getDeviceAddress())) 
			{
				mBluetoothAdapter.stopLeScan(callback);
				connect(device.getAddress());
				shareManager.setDeviceAddress(device.getAddress());
				shareManager.setDeviceName(device.getName());
			}
		}
	};
	/*
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
						break;
						
					case BluetoothAdapter.STATE_TURNING_OFF://off
						break;
						
					case BluetoothAdapter.STATE_OFF:// last off
						break;
					default:
						break;
				}
			}
		}
	}
	*/


}

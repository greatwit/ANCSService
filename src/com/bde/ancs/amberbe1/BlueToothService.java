package com.bde.ancs.amberbe1;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import gz.lifesense.ancs.aidl.DeviceInfo;
import gz.lifesense.ancs.aidl.RemoteBlueTooth;
import gz.lifesense.ancs.bluetooth.DeviceManager;

/**
 * 
 * @author 
 * @description 
 *
 */
@SuppressLint("NewApi")
public class BlueToothService extends Service 
{
	private final static String TAG = BlueToothService.class.getSimpleName();
	
	private static DeviceManager mDeviceManager = null;
	
	
	@SuppressWarnings("unused")
	private DeviceInfo mDevInfo = null;

	@Override
	public IBinder onBind(Intent intent) 
	{
		Log.i(TAG, "server OnBind");
		return new ServerBinder();
	}

	@Override
    public boolean onUnbind(Intent intent)
    {
        // All clients have unbound with unbindService()
		Log.i(TAG, "server onUnbind");
    	Intent localIntent = new Intent();
    	localIntent.setClass(this, BlueToothService.class); // 
    	startService(localIntent);
		return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent)
    {
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
    	Log.d("LOG","LocalService ->onRebind"); 
        super.onRebind(intent);
    }

    
    public void onCreate()
    { 
    	if(mDeviceManager==null)
    	{
    		mDeviceManager = DeviceManager.getInstance(BlueToothService.this);
    		mDeviceManager.initialize();
    		mDeviceManager.connectLastDevice();
    		mDeviceManager.setHandler(myHandler);
    	}
    	super.onCreate();
    	Log.v(TAG, "Service onCreate");
    }
    
    @Override  
    public void onStart(Intent intent, int startId) 
    {  
        Log.v(TAG, "Service onStart");  
        super.onStart(intent, startId);  
    }
    
    @Override
    public void onDestroy()
    {
    	Log.i(TAG, "server onDestroy");
    	Intent localIntent = new Intent();
    	localIntent.setClass(this, BlueToothService.class); // 
    	startService(localIntent);
    	super.onDestroy();
    }
	
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
    	Log.i(TAG, "onStartCommand------");
    	
    	//Notification notification=new Notification(R.drawable.logo, getResources().getString(R.string.prename) ,System.currentTimeMillis());
    	//notification.setLatestEventInfo(this,getResources().getString(R.string.app_name) , getResources().getString(R.string.prename), null);
    	//notification.flags=Notification.FLAG_SHOW_LIGHTS;
    	//startForeground(1,notification);
    	RunningNotifiction(this);
    	//return START_STICKY_COMPATIBILITY;
    	flags = START_STICKY;//START_REDELIVER_INTENT; //START_STICKY
    	return super.onStartCommand(intent, flags, startId);
    	//return START_STICKY;
    }
	
    Handler myHandler = new Handler() 
    {  
        public void handleMessage(Message msg) 
        {   
             switch (msg.what) 
             {   
                  case 0:  
                    startService(new Intent(BlueToothService.this, BlueToothService.class)); 
                       break;   
             }   
             super.handleMessage(msg);   
        }   
   };  
    
    
	NotificationManager notimanager;
	NotificationManager updatenm;
	int updatenm_id=19830512;
	int notification_id=5188;
public void RunningNotifiction(Context context)
{
    if(notimanager!=null)return;
    notimanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

    Intent intent = new Intent();
    intent.setClass(context,BlueToothService.class);
	intent.addCategory(Intent.CATEGORY_LAUNCHER);  
	intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
	
	CharSequence appName = context.getString(R.string.app_name);
	Notification notification = new Notification(R.drawable.logo,
			appName, System.currentTimeMillis());
	notification.flags = Notification.FLAG_ONGOING_EVENT;
	CharSequence appDescription = getResources().getString(R.string.prename);
	
	notification.setLatestEventInfo(context, appName,
		appDescription, PendingIntent.getActivity(((ContextWrapper) context).getBaseContext(),
		0, intent, PendingIntent.FLAG_CANCEL_CURRENT));
	
	notimanager.notify(notification_id, notification);
}
    
    
	private class ServerBinder extends RemoteBlueTooth.Stub
	{
		@Override
		public String getAllInfo() throws RemoteException
		{
			return "name:feifei age:21";
		}
		@Override
		public DeviceInfo getDeviceInfo() throws RemoteException 
		{
			DeviceInfo info = new DeviceInfo();
			return info;
		}

		@Override
		public boolean isDeviceConnected() throws RemoteException {
			// TODO Auto-generated method stub
			boolean iRes = mDeviceManager.isDeviceConnected();
			return iRes;
		}
		
		@Override
		public void setDeviceInfo(DeviceInfo info) throws RemoteException {
			// TODO Auto-generated method stub
			mDevInfo = info;
		}

	    /**
	     * Connects to the GATT server hosted on the Bluetooth LE device.
	     *
	     * @param address
	     *            The device address of the destination device.
	     *
	     * @return Return true if the connection is initiated successfully. The
	     *         connection result is reported asynchronously through the
	     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	     *         callback.
	     */
		@Override
		public boolean connect(String address) throws RemoteException 
		{
			mDeviceManager.connect(address);
			return false;
		}
		
		@Override
		public void disconnect() throws RemoteException {
			// TODO Auto-generated method stub
			mDeviceManager.disconnect();
		}
		@Override
		public void receivedTelegram() throws RemoteException {
			// TODO Auto-generated method stub
			mDeviceManager.receivedTelegram();
		}
		@Override
		public void connectLast() throws RemoteException {
			// TODO Auto-generated method stub
			mDeviceManager.connectLastDevice();
		}
		@Override
		public void writeByte(byte[] value) throws RemoteException {
			// TODO Auto-generated method stub
			mDeviceManager.writeCharacteristic(value);
		}
		@Override
		public void stopServer() throws RemoteException {
			// TODO Auto-generated method stub
			stopSelf();
		}
		@Override
		public boolean isBluetoothOpen() throws RemoteException {
			// TODO Auto-generated method stub
			
			return mDeviceManager.isBluetoothOpen();
		}
		@Override
		public void openBluetooth() throws RemoteException {
			// TODO Auto-generated method stub
			mDeviceManager.openBluetooth();
		}
		@Override
		public boolean isBlueToothConnected() throws RemoteException {
			// TODO Auto-generated method stub
			return mDeviceManager.isBluetoothConnected();
		}
		
		@Override
		public boolean getDiscoverDuration() throws RemoteException {
			// TODO Auto-generated method stub
			return mDeviceManager.getDiscoverDuration();
		}
		
		@Override
		public void setDiscoverDuration(boolean duration) throws RemoteException {
			// TODO Auto-generated method stub
			mDeviceManager.setDiscoverDuration(duration);
		}
	}
	
}

package gz.lifesense.ancs.server;

import gz.lifesense.ancs.util.RLog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;


public class ShareManager {
	
	private SharedPreferences share;
	private Editor editor;
	private String TAG="ShareManager";
	private  String SHARE_NAME="camingCall";
	private  String DEVICE_ADDRESS = "deviceAddress";
	private  String DEVICE_NAME = "deviceName";
	private  String IS_OPEN_INCOMING_CALL="is_open_incomingcall";
	private  String IS_RESPONSE_Call="is_response_call";
	private  String IS_RESPONSE_Remove="is_response_remove";
	public static String HARD_VERSION = "hardwareVersion";
	private String MISSING_CALL="missing_call";
			
	
	
	private  ShareManager() {
		super();
	}
	public void clear(){
		editor.clear().commit();
	};
	

	public ShareManager(Context context) {
		super();
		
		 share = context.getSharedPreferences(SHARE_NAME, Context.MODE_PRIVATE);
		 editor=share.edit();
	}
	
	public boolean hasMissCall(){
		boolean result= share.getBoolean(MISSING_CALL,false);
		RLog.i(TAG, "GET..hasMissCall="+result);
		return result;
	}
	public void setMissCall(boolean isHasMissCall){
		editor.putBoolean(MISSING_CALL, isHasMissCall).commit();
		RLog.i(TAG, "SET..setMissCall="+isHasMissCall);
	}
	
	public String getDeviceAddress(){
		String result= share.getString(DEVICE_ADDRESS, "");
		RLog.i(TAG, "GET..deviceAddress="+result);
		return result;
	}
	
	public void setDeviceAddress(String deviceAddress){
		editor.putString(DEVICE_ADDRESS, deviceAddress).commit();
		RLog.i(TAG, "SET..deviceAddress="+deviceAddress);
	}
	
	public String getDeviceName(){
		String result= share.getString(DEVICE_NAME, "");
		RLog.i(TAG, "GET..deviceName="+result);
		return result;
	}
	
	public void setDeviceName(String deviceName){
		editor.putString(DEVICE_NAME, deviceName).commit();
		RLog.i(TAG, "SET..deviceName="+deviceName);
	}
	
	
	public void setIsOpenIncomingCall(boolean isOpen){
		editor.putBoolean(IS_OPEN_INCOMING_CALL, isOpen).commit();
		RLog.i(TAG, "SET..setIsOpenIncomingCall="+isOpen);
	}
	public boolean getIsOpenIncomingCall(){
		boolean result= share.getBoolean(IS_OPEN_INCOMING_CALL, true);
		RLog.i(TAG, "GET...getIsOpenIncomingCall="+result);
		return result;
	}
	public void setIsResponse4Call(boolean isResponse){
		editor.putBoolean(IS_RESPONSE_Call, isResponse).commit();
		RLog.i(TAG, "SET..setIsResponse4Call="+isResponse);
	}
	public boolean getIsResponse4Call(){
		boolean result= share.getBoolean(IS_RESPONSE_Call, false);
		RLog.i(TAG, "GET...getIsResponse4Call="+result);
		return result;
	}
	public void setIsResponse4Remove(boolean isResponse){
		editor.putBoolean(IS_RESPONSE_Remove, isResponse).commit();
		RLog.i(TAG, "SET..setIsResponse4Remove="+isResponse);
	}
	public boolean getIsResponse4Remove(){
		boolean result= share.getBoolean(IS_RESPONSE_Remove, false);
		RLog.i(TAG, "GET...getIsResponse4Remove="+result);
		return result;
	}
	
	/**
	 * 设置设备硬件版本号
	 * @param hardwareVersion
	 */
	public void setHardwareVersion(String hardwareVersion){
		Log.i(TAG, "hardwareVersion==="+hardwareVersion);
		editor.putString(HARD_VERSION, hardwareVersion).commit();
	}
	
	/**
	 * 获取设备硬件版本号
	 * @return
	 */
	public String getHardwareVersion(){
		String result= share.getString(HARD_VERSION, "");
		return result;
	}
	
	
	
	/**
	 * 获取textLog文件名
	 * @return
	 */
	public String getLogFileName(){
		String result= share.getString("logFile", "");
		return result;
	}
	
	/**
	 * 设置textLog文件名
	 * @param LogFileName
	 */
	public void setLogFileName(String LogFileName){
		editor.putString("logFile", LogFileName).commit();
	}
}


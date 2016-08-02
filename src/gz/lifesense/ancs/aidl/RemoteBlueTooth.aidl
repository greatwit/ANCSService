package gz.lifesense.ancs.aidl;
import gz.lifesense.ancs.aidl.DeviceInfo;
interface RemoteBlueTooth 
{
	String getAllInfo(); 
	DeviceInfo getDeviceInfo();
	void setDeviceInfo(in DeviceInfo info);
	
	void    openBluetooth();
	boolean isBluetoothOpen();
	boolean connect( String address);
	void    connectLast();
	void 	disconnect();
	boolean isBlueToothConnected();
	boolean isDeviceConnected();
	void    writeByte(in byte[] value);
	void 	receivedTelegram();
	
	boolean getDiscoverDuration();
	void    setDiscoverDuration(boolean duration);
	
	void 	stopServer();
}

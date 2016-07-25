package gz.lifesense.ancs.bluetooth;

public class BluetoothContent {

	public final static String ACTION_GATT_START_CONNECT = "com.example.bluetooth.le.ACTION_GATT_START_CONNECT";
	public final static String ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
	public final static String ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
	public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
	public final static String ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
	public final static String EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA";
	
	public static final int REQUEST_ENABLE_BT = 1;
	public final static String TARGET_UUID="FD00";
	public final static String DEVICE_INFORMATION_UUID="180a";
	public final static String DEVICE_HARDWARE_UUID="2a26";
//	public final static String DEVICE_HARDWARE_UUID="2a26";

}

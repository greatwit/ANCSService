package gz.lifesense.ancs.bluetooth;

import gz.lifesense.ancs.util.DataTools;
import gz.lifesense.ancs.util.TxtLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.util.Log;


/**
 * 接收，发送数据的解析
 * 
 * @author zoe
 * 
 */
public class ProtobufData {
	private Context context;
	private String TAG = "ProtobufData";
	private static final String HEADER = "fe01";
	// private BluetoothLeService bluetoothLeService;
	// private final int SEND_DATA=1111;
	private final int TIME_DELAY = 1000;
	private static int currentIndex;
	private List<String> dataList = new ArrayList<String>();
	private DeviceManager myBluetoothManager;
	private int c4Length = 36;
	private int c9Length = 46;
	private int cALength = 38;
	private static String seq = "";// 数据包序号

	private OnBluetoothResponseListener onBluetoothResponseListener;
	private static ProtobufData protobufData;
	private int delayTime = 5000;
	// TODO:记得cancel
	// private Timer timer;
	// private TimerTask resendTask;

	private Timer sendDataTimer;
	private TimerTask sendDataTimerTask;

	// private ShareManager shareManager;
	/** 设备型号 **/
	private String deviceModel;
	private final int RESEND_COMMAND = 554;
//	public static String nameOrNumHolder;
	public static String commandType = "";
	private final String TYPE_COMING_CALL = "type_coming_call";
	private final String TYPE_REMOCE_CALL = "remove_call";
	private static boolean hasReceivedReply4Call;
	private static boolean isInComingCallOpen=true;

	public ProtobufData() {

	}

	// private ProtobufData(Context context) {
	// super();
	// this.context = context;
	// // bluetoothLeService=new BluetoothLeService();
	// myBluetoothManager = MyBluetoothManager.getInstance(context);
	// myBluetoothManager.initialize();
	//
	// // shareManager = new ShareManager(context);
	//
	// }
	

	public static boolean isInComingCallOpen() {
		return isInComingCallOpen;
	}

	public static void setInComingCallOpen(boolean isInComingCallOpen) {
		ProtobufData.isInComingCallOpen = isInComingCallOpen;
	}
//	public static boolean getHasReceivedReply4Call() {
//		return hasReceivedReply4Call;
//	}

//	public static void setHasReceivedReplay4Call(boolean hasReceived) {
//		ProtobufData.hasReceivedReply4Call = hasReceived;
//	}

	public String getCommandType() {
		return commandType;
	}

	public void setCommandType(String commandType) {
		this.commandType = commandType;
	}

//	public static String getNameOrNumHolder() {
//		return nameOrNumHolder;
//	}
//
//	public static void setNameOrNumHolder(String nameOrNumHolder) {
//		ProtobufData.nameOrNumHolder = nameOrNumHolder;
//	}

	/**
	 * 0:检验码错误 1.成功 2.来电提醒关 3.下发的命令异常 4.其他
	 * 
	 * @param characteristic
	 */
	@SuppressLint("NewApi")
	public void receiveData(BluetoothGattCharacteristic characteristic) {
		Log.e(TAG, "receiveData,hasReceived = true;");
		hasReceivedReply4Call = true;
		final byte[] data = characteristic.getValue();

		if (data[2] == 1) {// 信息类型是电话

			int tipType = data[3];
			if (tipType == 0) {// 提示类型是来电

				int status = data[4];
				switch (status) {
				case 0:
					Log.i(TAG, "receiveData，0,校验码错误");
					hasReceivedReply4Call = false;
					break;
				case 1:
					Log.i(TAG, "receiveData，1,成功");
					// Log.i("TASK", "handler.removeCallbacks");
					// handler.removeCallbacks(reConnectThread);
					break;
				case 2:
					Log.i(TAG, "receiveData，2,来电提醒是关着的");
					isInComingCallOpen=false;
					break;
				case 3:
					Log.i(TAG, "receiveData，3,下发的指令异常");
					hasReceivedReply4Call = false;
					break;
				case 4:
					Log.i(TAG, "receiveData，4,其他 (比如充电是不提醒的)");
					break;
				default:
					break;
				}

			} else if (tipType == 2) {// 挂断电话
				int status = data[4];
				if (status == 1) {
					Log.i(TAG, "receiveData，1,挂断成功");
				}
			}

		}

	}

	// 第几帧+长度+信息类型+提示类型+内容+2字节校验和,长度是总长度，不包括帧序号那字节
	// 比如来电提醒 0x01 ,0x0d 0x01 0x00 +电话号码 或姓名
	// 信息类型= 1表示是电话，提示类型＝0来电，2挂电话
	//
	// 回复：第几帧++长度+信息类型+提示类型+00或01
	// 第几帧 就是你发过来的帧数，长度就是回复包后面的实际长度

	public void sendComingCallNotification(String nameOrNum) {
		Log.i(TAG, "nameOrNum=" + nameOrNum);
//		nameOrNum="076085166210";
		StringBuffer instructSB = new StringBuffer();
		commandType = TYPE_COMING_CALL;
		int infoType = 1;
		int tipsType = 0;
		String content = str2HexStr(nameOrNum);
		Log.i(TAG, "content=" + content);
		// 校验和
		int checkSum = infoType + tipsType;
		byte[] contentByte = nameOrNum.getBytes();
		for (int i = 0; i < contentByte.length; i++) {
			int result;
			if (contentByte[i] < 1) {
				result = contentByte[i] + 256;
			} else {
				result = contentByte[i];
			}
			checkSum += result;
		}
		String checkSumStr = Integer.toHexString(checkSum);
		checkSumStr = formatWithZero(checkSumStr, 4);

		int length = 4 + contentByte.length;

		String lengthStr = Integer.toHexString(length);
		String infoTypeStr = Integer.toHexString(infoType);
		String tipsTypeStr = Integer.toHexString(tipsType);

		lengthStr = formatWithZero(lengthStr, 2);
		infoTypeStr = formatWithZero(infoTypeStr, 2);
		tipsTypeStr = formatWithZero(tipsTypeStr, 2);

		instructSB.append(lengthStr).append(infoTypeStr).append(tipsTypeStr).append(content).append(checkSumStr);

		Log.i(TAG, "instructSB=" + instructSB);
		// String data="01 0d 01 00 31 33 35 39 30 32 38 38 37 35 34"
		// String
		// data="01 0d 01 00 31 33 35 39 30 32 38 38 37 35 34".replace(" ", "");
		splitData(instructSB.toString().replace(" ", ""), 0);

	}

	/**
	 * 01 02 01 02
	 */
	public void sendRemoveComingCallNotifiaion() {

		commandType = TYPE_REMOCE_CALL;
		StringBuffer instructSB = new StringBuffer();

		int length = 4;
		int infoType = 1;
		int tipsType = 2;

		int checkSum = infoType + tipsType;
		String checkSumStr = Integer.toHexString(checkSum);
		checkSumStr = formatWithZero(checkSumStr, 4);

		String lengthStr = Integer.toHexString(length);
		String infoTypeStr = Integer.toHexString(infoType);
		String tipsTypeStr = Integer.toHexString(tipsType);

		lengthStr = formatWithZero(lengthStr, 2);
		infoTypeStr = formatWithZero(infoTypeStr, 2);
		tipsTypeStr = formatWithZero(tipsTypeStr, 2);

		instructSB.append(lengthStr).append(infoTypeStr).append(tipsTypeStr).append(checkSumStr);

		Log.i(TAG, "instructSB=" + instructSB);
		// String data="01 0d 01 00 31 33 35 39 30 32 38 38 37 35 34"
		// String
		// data="01 0d 01 00 31 33 35 39 30 32 38 38 37 35 34".replace(" ", "");
		splitData(instructSB.toString().replace(" ", ""), 0);

	}

	/**
	 * 字符串转换成十六进制字符串
	 * 
	 * @param String
	 *            str 待转换的ASCII字符串
	 * @return String 每个Byte之间空格分隔，如: [61 6C 6B]
	 */
	public String str2HexStr(String str) {

		char[] chars = "0123456789ABCDEF".toCharArray();
		StringBuilder sb = new StringBuilder("");
		byte[] bs = str.getBytes();
		int bit;

		for (int i = 0; i < bs.length; i++) {
			bit = (bs[i] & 0x0f0) >> 4;
			sb.append(chars[bit]);
			bit = bs[i] & 0x0f;
			sb.append(chars[bit]);
			sb.append(' ');
		}
		return sb.toString().trim();
	}

	/**
	 * 前面补0
	 * 
	 * @param content
	 * @param targetLength
	 * @return
	 */
	private String formatWithZero(String content, int targetLength) {
		String temp = "";
		for (int i = 0; i < (targetLength - content.length()); i++) {
			temp += "0";
		}
		temp += content;
		return temp;
	}

	/**
	 * 分段
	 * 
	 * @param data
	 */
	private void splitData(String data, int type) {

		dataList.clear();

		int item = 39;
		int num = data.length() / item;
		int yuNum = data.length() % item;

		for (int i = 0; i < num; i++) {

			String content = data.substring(i * item, i * item + item - 1);

			// 加上前面的帧数
			String number = Integer.toHexString(i + 1);
			number = formatWithZero(number, 2);

			dataList.add(number + content);

		}
		if (yuNum > 0) {

			String content = "";
			if (num == 0) {

				content = data.substring(num * item, data.length());
			} else {

				content = data.substring(num * item - 1, data.length());
			}

			// content = DataTools.getX0(content, 40);
			// 加上前面的帧数
			String number = Integer.toHexString(num + 1);
			number = formatWithZero(number, 2);
			dataList.add(number + content);

		}
		Log.i("splitData", dataList.toString());

		sendData();

	}

	int count;

	/**
	 * 发送
	 * 
	 * @param bArray
	 */
	private void sendData() {

		// RLog.i(TAG, "sendData" + dataList.toString());
		currentIndex = 0;
		if (sendDataTimer != null) {
			sendDataTimer.cancel();
			sendDataTimer = null;
		}
		if (sendDataTimerTask != null) {
			sendDataTimerTask.cancel();
			sendDataTimerTask = null;
		}

		sendDataTimer = new Timer();
		sendDataTimerTask = new TimerTask() {

			@Override
			public void run() {

				if (currentIndex < dataList.size()) {

					byte[] bArray = DataTools.decodeHex(dataList.get(currentIndex).toCharArray());

//					for (int i = 0; i < bArray.length; i++) {
//						Log.i("detail", "发送数据==" + "i=" + i + ",,," + bArray[i]);
//
//					}
					if (myBluetoothManager == null) {
						myBluetoothManager = DeviceManager.getInstance(context);
						myBluetoothManager.initialize();
					}
					Log.i(TAG, "myBluetoothManager.writeCharacteristic(bArray)");

					myBluetoothManager.writeCharacteristic(bArray);
					hasReceivedReply4Call = false;
					Log.i(TAG, "hasReceived = false");

				} else {
					// TODO:记得cancel
					this.cancel();
					sendDataTimer.cancel();
				}
				currentIndex = currentIndex + 1;
			}
		};

		sendDataTimer.schedule(sendDataTimerTask, 800, 800);

	}

	public void setOnBluetoothResponse(OnBluetoothResponseListener onBluetoothResponseListener) {
		this.onBluetoothResponseListener = onBluetoothResponseListener;
	}

	public interface OnBluetoothResponseListener {
		public void onBluetoothResponse(int count);
	}

//	public class ReconnectRunnable implements Runnable {
//
//		@Override
//		public void run() {
//
//			Log.i("TASK", "ReconnectThread,run");
//			if (!hasReceived) {
//
//				myBluetoothManager = MyBluetoothManager.getInstance(context);
//				if (myBluetoothManager.isBluetoothOpen()) {// 判断蓝牙是否打开
//
//					if (commandType.equals(TYPE_COMING_CALL)) {
//
//						sendComingCallNotification(nameOrNumHolder);
//
//					} else if (commandType.equals(TYPE_REMOCE_CALL)) {
//
//						sendRemoveComingCallNotifiaion();
//					}
//				}
//			}
//		}
//
//	}


}

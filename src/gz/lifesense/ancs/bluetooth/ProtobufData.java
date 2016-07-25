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
 * ���գ��������ݵĽ���
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
	private static String seq = "";// ���ݰ����

	private OnBluetoothResponseListener onBluetoothResponseListener;
	private static ProtobufData protobufData;
	private int delayTime = 5000;
	// TODO:�ǵ�cancel
	// private Timer timer;
	// private TimerTask resendTask;

	private Timer sendDataTimer;
	private TimerTask sendDataTimerTask;

	// private ShareManager shareManager;
	/** �豸�ͺ� **/
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
	 * 0:��������� 1.�ɹ� 2.�������ѹ� 3.�·��������쳣 4.����
	 * 
	 * @param characteristic
	 */
	@SuppressLint("NewApi")
	public void receiveData(BluetoothGattCharacteristic characteristic) {
		Log.e(TAG, "receiveData,hasReceived = true;");
		hasReceivedReply4Call = true;
		final byte[] data = characteristic.getValue();

		if (data[2] == 1) {// ��Ϣ�����ǵ绰

			int tipType = data[3];
			if (tipType == 0) {// ��ʾ����������

				int status = data[4];
				switch (status) {
				case 0:
					Log.i(TAG, "receiveData��0,У�������");
					hasReceivedReply4Call = false;
					break;
				case 1:
					Log.i(TAG, "receiveData��1,�ɹ�");
					// Log.i("TASK", "handler.removeCallbacks");
					// handler.removeCallbacks(reConnectThread);
					break;
				case 2:
					Log.i(TAG, "receiveData��2,���������ǹ��ŵ�");
					isInComingCallOpen=false;
					break;
				case 3:
					Log.i(TAG, "receiveData��3,�·���ָ���쳣");
					hasReceivedReply4Call = false;
					break;
				case 4:
					Log.i(TAG, "receiveData��4,���� (�������ǲ����ѵ�)");
					break;
				default:
					break;
				}

			} else if (tipType == 2) {// �Ҷϵ绰
				int status = data[4];
				if (status == 1) {
					Log.i(TAG, "receiveData��1,�Ҷϳɹ�");
				}
			}

		}

	}

	// �ڼ�֡+����+��Ϣ����+��ʾ����+����+2�ֽ�У���,�������ܳ��ȣ�������֡������ֽ�
	// ������������ 0x01 ,0x0d 0x01 0x00 +�绰���� ������
	// ��Ϣ����= 1��ʾ�ǵ绰����ʾ���ͣ�0���磬2�ҵ绰
	//
	// �ظ����ڼ�֡++����+��Ϣ����+��ʾ����+00��01
	// �ڼ�֡ �����㷢������֡�������Ⱦ��ǻظ��������ʵ�ʳ���

	public void sendComingCallNotification(String nameOrNum) {
		Log.i(TAG, "nameOrNum=" + nameOrNum);
//		nameOrNum="076085166210";
		StringBuffer instructSB = new StringBuffer();
		commandType = TYPE_COMING_CALL;
		int infoType = 1;
		int tipsType = 0;
		String content = str2HexStr(nameOrNum);
		Log.i(TAG, "content=" + content);
		// У���
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
	 * �ַ���ת����ʮ�������ַ���
	 * 
	 * @param String
	 *            str ��ת����ASCII�ַ���
	 * @return String ÿ��Byte֮��ո�ָ�����: [61 6C 6B]
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
	 * ǰ�油0
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
	 * �ֶ�
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

			// ����ǰ���֡��
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
			// ����ǰ���֡��
			String number = Integer.toHexString(num + 1);
			number = formatWithZero(number, 2);
			dataList.add(number + content);

		}
		Log.i("splitData", dataList.toString());

		sendData();

	}

	int count;

	/**
	 * ����
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
//						Log.i("detail", "��������==" + "i=" + i + ",,," + bArray[i]);
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
					// TODO:�ǵ�cancel
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
//				if (myBluetoothManager.isBluetoothOpen()) {// �ж������Ƿ��
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

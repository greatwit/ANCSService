package gz.lifesense.ancs.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import android.util.Log;

public class DataTools 
{
	private static final String AESkey = "000102030405060708090A0B0C0D0E0F";
	private static String default_crc32_poly = "edb88320";
	private static String hexStr = "0123456789ABCDEF";
	// 1970��1��1�յ�2010��1��1�վ���������
	private static final long timeoffset_2010_01_01 = 1262304000L;

    /**
     * ��16���� ת����10����
     *
     * @param str
     * @return
     */
    public static String print10(String str) {

        StringBuffer buff = new StringBuffer();
        String array[] = str.split(" ");
        for (int i = 0; i < array.length; i++) {
            int num = Integer.parseInt(array[i], 16);
            buff.append(String.valueOf((char) num));
        }
        return buff.toString();
    }

    /**
     * byteת16����
     *
     * @param b
     * @return
     */
    public static String byte2HexStr(byte[] b) {

        String stmp = "";
        StringBuilder sb = new StringBuilder("");
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            sb.append((stmp.length() == 1) ? "0" + stmp : stmp);
            sb.append(" ");
        }
        return sb.toString().toUpperCase().trim();
    }
	
	// ��16�����ַ����ݶ��� int ����
	public static int[] readData2(String data) 
	{

		int byteCount = data.length() / 2;

		int result[] = new int[data.length() / 2];

		for (int i = 0; i < byteCount; i++) {

			result[i] = Integer.valueOf(getByteHexCode(data, i, i), 16);

		}

		return result;
	}

	public static String hexString2binaryString(String hexString) 
	{
		if (hexString == null || hexString.length() % 2 != 0)
			return null;
		String bString = "", tmp;
		for (int i = 0; i < hexString.length(); i++) {
			tmp = "0000" + Integer.toBinaryString(Integer.parseInt(hexString.substring(i, i + 1), 16));
			bString += tmp.substring(tmp.length() - 4);
		}
		return bString;
	}

	/**
	 * ���油0
	 * 
	 * @param sb
	 * @param n
	 *            Ŀ��λ��
	 * @return
	 */
	public static String getX0(String sb, int n) {

		if (n <= 0) {
			return "";
		}

		int targetLength = n - sb.length();
		for (int i = 0; i < targetLength; i++) {
			sb += "0";
		}

		return sb;

	}

	/**
	 * 
	 * @param hexString
	 * @return ��ʮ������ת��Ϊ�ֽ�����
	 */
	public static byte[] HexStringToBinary(String hexString) {
		// hexString�ĳ��ȶ�2ȡ������Ϊbytes�ĳ���
		int len = hexString.length() / 2;
		byte[] bytes = new byte[len];
		byte high = 0;// �ֽڸ���λ
		byte low = 0;// �ֽڵ���λ

		for (int i = 0; i < len; i++) {
			// ������λ�õ���λ
			high = (byte) ((hexStr.indexOf(hexString.charAt(2 * i))) << 4);
			low = (byte) hexStr.indexOf(hexString.charAt(2 * i + 1));
			bytes[i] = (byte) (high | low);// �ߵ�λ��������
		}
		return bytes;
	}

	public static byte[] decodeHex(char[] data) {

		int len = data.length;

		if ((len & 0x01) != 0) {
			throw new RuntimeException("Odd number of characters.");
		}

		byte[] out = new byte[len >> 1];

		// two characters form the hex value.
		for (int i = 0, j = 0; j < len; i++) {
			int f = toDigit(data[j], j) << 4;
			j++;
			f = f | toDigit(data[j], j);
			j++;
			out[i] = (byte) (f & 0xFF);
		}

		return out;
	}

	private static int toDigit(char ch, int index) {
		int digit = Character.digit(ch, 16);
		if (digit == -1) {
			throw new RuntimeException("Illegal hexadecimal character " + ch + " at index " + index);
		}
		return digit;
	}

	/**
	 * ��ȡ��ǰ���ڵ�UTCʱ������ʮ�����Ʊ���
	 * 
	 * @return ��ǰ���ڵ�UTCʱ������ʮ�����Ʊ���
	 */
	// public static String getCurrentUTCTimeHexCode() {
	//
	// // ��ȡ��ǰʱ���UTC���������Ϊ1970��1��1��0ʱ0��0�룩
	// long utc_times = getCurrentUTCTime2();
	//
	// // UTC ʱ��� 12 ��Сʱ�� ��Ϊ�˽����Ʒ�豸��ʱ����������Ϊ��Ʒ�豸����������������
	//
	// utc_times = utc_times - 16 * 3600;
	//
	// /*
	// * System.out.println("UTC - 12 :" + utc_times);
	// *
	// * System.out.println("UTC-2010_01_01 ���:" + timeoffset_2010_01_01);
	// *
	// * System.out.println("�´���BRIDGE:" + (utc_times -
	// * timeoffset_2010_01_01));
	// */
	//
	// String utcTimeHexCode = Long.toHexString(utc_times
	// - timeoffset_2010_01_01);
	//
	// if (utcTimeHexCode.length() < 8) {
	// utcTimeHexCode = getX0(8 - utcTimeHexCode.length())
	// + utcTimeHexCode;
	// }
	//
	// return utcTimeHexCode;
	//
	// }

	public static String getCurrentUTCTimeHexCode() {

		String result = "";

		Calendar cal = Calendar.getInstance();
		// int zoneOffset = cal.get(Calendar.ZONE_OFFSET);
		// int dstOffset = cal.get(Calendar.DST_OFFSET);
		// cal.add(Calendar.MILLISECOND, -(zoneOffset+dstOffset));
		// long a = cal.getTimeInMillis()/1000;
		result = Long.toHexString(cal.getTimeInMillis() / 1000);
//		Log.i("getCurrentUTCTimeHexCode", "result="+result);
//		result="00000000";
//		Log.i("getCurrentUTCTimeHexCode", "result="+result);

		return result;
	}

	/**
	 * ��ȡ��ǰUTC��ʽʱ�䣨��������ֵ��ʽ����λΪ�룩
	 * 
	 * @return ��ǰUTC��ʽʱ�䣨��������ֵ��ʽ����λΪ�룩
	 */
//	private static long getCurrentUTCTime2() {
//
//		// long time = System.currentTimeMillis() /1000;
//
//		Date date = new Date();
//
//		return convertToUTCTime2(date);
//
//	}

	/**
	 * ����UTCʱ������ʮ�����Ʊ���ΪUTCʱ������
	 * 
	 * @param hexUTCCode
	 *            UTCʱ������ʮ�����Ʊ���
	 * @param lossPower
	 *            �����ʶ
	 * @return
	 */
	public static Date parseUTCCode(String hexUTCCode, boolean lossPower) {

		long time = Long.valueOf(hexUTCCode, 16);

		Calendar cal = Calendar.getInstance();

		if (lossPower) {

			// ��Ʒ���ֵ���

			time = System.currentTimeMillis() / 1000 - time;

			cal.setTimeInMillis(time);

		} else {

			// time += timeoffset_2010_01_01;

			cal.setTimeInMillis(time * 1000);

			// cal.add(Calendar.HOUR, 16);
			// cal.add(Calendar.HOUR, 8);

		}

		// ȡ��ϵͳʱ��
		TimeZone tz = TimeZone.getDefault();

		/* ת��UTCʱ��, �� getRawOffset() ���؁�Ć�λ�� ms, ����Ҫ���� 1000 */
		cal.add(Calendar.SECOND, -(tz.getRawOffset() / 1000));

		// System.out.println(cal.getTime());

		return cal.getTime();

	}

	// /**
	// * ������ת����UTC��ʽʱ�䣨���ڸ�ʽ��
	// * @param date ����
	// * @return UTC��ʽʱ�䣨���ڸ�ʽ��
	// */
	// private static Date convertToUTCTime(Date date) {
	//
	// // long time = System.currentTimeMillis() /1000;
	//
	// Calendar cal = Calendar.getInstance();
	//
	// cal.setTime(date);
	//
	// // ȡ��ϵͳʱ��
	// TimeZone tz = TimeZone.getDefault();
	//
	//
	// /*ת��UTCʱ��, �� getRawOffset() ���؁�Ć�λ�� ms, ����Ҫ���� 1000 */
	// cal.add(Calendar.SECOND , -(tz.getRawOffset()/1000));
	//
	// return cal.getTime();
	//
	// }
	//
	/**
	 * ������ת����UTC��ʽʱ�䣨��������ֵ��ʽ����λΪ�룩
	 * 
	 * @param date
	 *            ����
	 * @return UTC��ʽʱ�䣨��������ֵ��ʽ����λΪ�룩
	 */
	private static long convertToUTCTime2(Date date) {

		long time = date.getTime();

		return time / 1000;

	}

	/**
	 * ���� n �� "0" �� �ַ�
	 * 
	 * @param n
	 *            ��Ҫ"0"�ַ�������
	 * @return
	 */
	public static String getX0(int n) {

		if (n <= 0) {
			return "";
		}

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < n; i++) {
			sb.append("0");
		}

		return sb.toString();

	}


	/**
	 * ��ԭʼ�������CRC32У����
	 * 
	 * @param rawdata
	 *            ԭʼ����
	 * @return
	 */
//	public static String getCRC32Code(String rawdata) {
//
//		String crcCode = Long.toHexString(CRC32.CRC32(rawdata, default_crc32_poly));
//
//		if (crcCode.length() < 8) {
//			crcCode = getX0(8 - crcCode.length()) + crcCode;
//		}
//
//		// System.out.println("CRC32_CODE:" + crcCode);
//
//		if (crcCode.length() != 8) {
//			System.out.println("CRC32У���볬��4���ֽڣ�");
//		}
//
//		return crcCode;
//
//	}

	/**
	 * ����ʱ���
	 * 
	 * @return
	 */
	public static String getTimeStamp() {
		long currentTimestamp = System.currentTimeMillis();
		return currentTimestamp + "";
	}

	/**
	 * ����4λ�����
	 * 
	 * @return
	 */
	public static int getRandomNum() {

		int max = 9999;
		int min = 1000;
		Random random = new Random();

		int s = random.nextInt(max) % (max - min + 1) + min;
		System.out.println(s);
		return s;

	}

	/**
	 * ��ԭʼ�������CRC32У����
	 * 
	 * @param rawdata
	 *            ԭʼ����
	 * @return public static String getCRC32Code(String rawdata) {
	 * 
	 *         String crcCode = Long.toHexString(CRC32.CRC32(rawdata,
	 *         default_crc32_poly));
	 * 
	 *         if (crcCode.length() < 8) { crcCode = getX0(8 - crcCode.length())
	 *         + crcCode; }
	 * 
	 *         // System.out.println("CRC32_CODE:" + crcCode);
	 * 
	 *         if (crcCode.length() != 8) {
	 *         System.out.println("CRC32У���볬��4���ֽڣ�"); }
	 * 
	 *         return crcCode;
	 * 
	 *         }
	 * 
	 *         /** ��0
	 * 
	 * @param raw
	 * @param n
	 *            Ŀλλ��
	 * @return
	 */
	public static String getTargetStr(String raw, int n) {
		StringBuffer sb = new StringBuffer();
		sb.append(raw);
		for (int i = 0; i < n; i++) {
			sb.append("0");
		}
		return sb.toString();
	}

	/**
	 * ����
	 * 
	 * @param data
	 *            ����16�����ַ���
	 * @param key
	 *            ��Կ16�����ַ���
	 * @return ���ܺ�16��������
	 */
	public static String aesEncryption(String data, String key) {

		SecretKeySpec skeySpec = new SecretKeySpec(readData(key), "AES");// ����һ����չ��Կ��������һ������֮��
		Cipher cipher;
		try {
			cipher = Cipher.getInstance("AES/ECB/NoPadding");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);// ��ENCRYPT_MODEģʽ����skeySpec�����飬����AES���ܷ���
			byte[] encrypted = cipher.doFinal(readData(data));// ����message
			return asHex(encrypted);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * ����
	 * 
	 * @param data
	 *            ��Ҫ���ܵ�16������������
	 * @param key
	 *            ����16������Կ
	 * @return ���ؽ��ܺ��16��������
	 */
	public static String aesDecryption(String data, String key) {
		SecretKeySpec skeySpec = new SecretKeySpec(readData(key), "AES");// ����һ����չ��Կ��������һ������֮��
		Cipher cipher;
		try {
			cipher = Cipher.getInstance("AES/ECB/NoPadding");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);// ��ENCRYPT_MODEģʽ����skeySpec�����飬����AES���ܷ���
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			byte[] original = cipher.doFinal(readData(data));// ����
			return asHex(original);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
		return null;
	}

	// ����������ת��ʮ������
	public static String asHex(byte buf[]) {
		StringBuffer strbuf = new StringBuffer(buf.length * 2);
		int i;
		for (i = 0; i < buf.length; i++) {
			if (((int) buf[i] & 0xff) < 0x10)
				strbuf.append("0");
			strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
		}
		return strbuf.toString();
	}

	// ��16�����ַ����ݶ����ֽ�����
	public static byte[] readData(String data) {

		int byteCount = data.length() / 2;

		byte result[] = new byte[data.length() / 2];

		for (int i = 0; i < byteCount; i++) {

			// System.out.println(i + ":" + getByteHexCode(data, i, i));

			result[i] = Integer.valueOf(getByteHexCode(data, i, i), 16).byteValue();

		}

		return result;

	}

	public static String getByteHexCode(int value) {

		String result = Integer.toHexString(value);

		if (result.length() % 2 != 0) {
			result = "0" + result;
		}

		return result;

	}

	public static String getByteHexCode(String data, int start, int end) {
		return data.substring(start * 2, (end + 1) * 2);
	}

	/**
	 * ֧��1����֧��0
	 * @param model
	 * @param software
	 * @return
	 */
	public static int getIsSupportSleep(int model, int software) {
		int isSupportSleep = 0;

		if (model == 405 && software >= 10) {
			isSupportSleep = 1;
		} else if (model == 407 && software >= 20) {
			isSupportSleep = 1;
		} else if (model == 408 && software >= 3) {
			isSupportSleep = 1;
		} else if (model == 410 && software >= 22) {
			isSupportSleep = 1;
		} else {
			isSupportSleep = 0;
		}
		return isSupportSleep;
	}
	
	/**
	 * ���ַ����л�ȡ����
	 * @param string
	 * @return
	 */
	public static int getNumbers(String string){
		Pattern p = Pattern.compile("[^0-9]"); 
		Matcher m = p.matcher(string);
		String str = m.replaceAll("").trim();
		int result = Integer.parseInt(str);
		return result;
	}
}

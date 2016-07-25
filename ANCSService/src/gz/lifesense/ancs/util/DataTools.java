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
	// 1970年1月1日到2010年1月1日经过的秒数
	private static final long timeoffset_2010_01_01 = 1262304000L;

    /**
     * 将16进制 转换成10进制
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
     * byte转16进制
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
	
	// 把16进制字符内容读入 int 数组
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
	 * 后面补0
	 * 
	 * @param sb
	 * @param n
	 *            目标位数
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
	 * @return 将十六进制转换为字节数组
	 */
	public static byte[] HexStringToBinary(String hexString) {
		// hexString的长度对2取整，作为bytes的长度
		int len = hexString.length() / 2;
		byte[] bytes = new byte[len];
		byte high = 0;// 字节高四位
		byte low = 0;// 字节低四位

		for (int i = 0; i < len; i++) {
			// 右移四位得到高位
			high = (byte) ((hexStr.indexOf(hexString.charAt(2 * i))) << 4);
			low = (byte) hexStr.indexOf(hexString.charAt(2 * i + 1));
			bytes[i] = (byte) (high | low);// 高地位做或运算
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
	 * 获取当前日期的UTC时区日期十六进制编码
	 * 
	 * @return 当前日期的UTC时区日期十六进制编码
	 */
	// public static String getCurrentUTCTimeHexCode() {
	//
	// // 获取当前时间的UTC秒数（起点为1970年1月1日0时0分0秒）
	// long utc_times = getCurrentUTCTime2();
	//
	// // UTC 时间减 12 个小时， 是为了解决产品设备的时区分区，因为产品设备不方便做减法运算
	//
	// utc_times = utc_times - 16 * 3600;
	//
	// /*
	// * System.out.println("UTC - 12 :" + utc_times);
	// *
	// * System.out.println("UTC-2010_01_01 起点:" + timeoffset_2010_01_01);
	// *
	// * System.out.println("下传给BRIDGE:" + (utc_times -
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
	 * 获取当前UTC格式时间（长整型数值格式，单位为秒）
	 * 
	 * @return 当前UTC格式时间（长整型数值格式，单位为秒）
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
	 * 解析UTC时区日期十六进制编码为UTC时区日期
	 * 
	 * @param hexUTCCode
	 *            UTC时区日期十六进制编码
	 * @param lossPower
	 *            掉电标识
	 * @return
	 */
	public static Date parseUTCCode(String hexUTCCode, boolean lossPower) {

		long time = Long.valueOf(hexUTCCode, 16);

		Calendar cal = Calendar.getInstance();

		if (lossPower) {

			// 产品出现掉电

			time = System.currentTimeMillis() / 1000 - time;

			cal.setTimeInMillis(time);

		} else {

			// time += timeoffset_2010_01_01;

			cal.setTimeInMillis(time * 1000);

			// cal.add(Calendar.HOUR, 16);
			// cal.add(Calendar.HOUR, 8);

		}

		// 取出系统时区
		TimeZone tz = TimeZone.getDefault();

		/* 转成UTC时间, 由 getRawOffset() 傳回來的單位為 ms, 所以要除以 1000 */
		cal.add(Calendar.SECOND, -(tz.getRawOffset() / 1000));

		// System.out.println(cal.getTime());

		return cal.getTime();

	}

	// /**
	// * 把日期转换成UTC格式时间（日期格式）
	// * @param date 日期
	// * @return UTC格式时间（日期格式）
	// */
	// private static Date convertToUTCTime(Date date) {
	//
	// // long time = System.currentTimeMillis() /1000;
	//
	// Calendar cal = Calendar.getInstance();
	//
	// cal.setTime(date);
	//
	// // 取出系统时区
	// TimeZone tz = TimeZone.getDefault();
	//
	//
	// /*转成UTC时间, 由 getRawOffset() 傳回來的單位為 ms, 所以要除以 1000 */
	// cal.add(Calendar.SECOND , -(tz.getRawOffset()/1000));
	//
	// return cal.getTime();
	//
	// }
	//
	/**
	 * 把日期转换成UTC格式时间（长整型数值格式，单位为秒）
	 * 
	 * @param date
	 *            日期
	 * @return UTC格式时间（长整型数值格式，单位为秒）
	 */
	private static long convertToUTCTime2(Date date) {

		long time = date.getTime();

		return time / 1000;

	}

	/**
	 * 产生 n 个 "0" 的 字符
	 * 
	 * @param n
	 *            需要"0"字符的数量
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
	 * 给原始数据最近CRC32校验码
	 * 
	 * @param rawdata
	 *            原始数据
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
//			System.out.println("CRC32校验码超过4个字节！");
//		}
//
//		return crcCode;
//
//	}

	/**
	 * 生成时间戳
	 * 
	 * @return
	 */
	public static String getTimeStamp() {
		long currentTimestamp = System.currentTimeMillis();
		return currentTimestamp + "";
	}

	/**
	 * 生成4位随机数
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
	 * 给原始数据最近CRC32校验码
	 * 
	 * @param rawdata
	 *            原始数据
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
	 *         System.out.println("CRC32校验码超过4个字节！"); }
	 * 
	 *         return crcCode;
	 * 
	 *         }
	 * 
	 *         /** 补0
	 * 
	 * @param raw
	 * @param n
	 *            目位位数
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
	 * 加密
	 * 
	 * @param data
	 *            加密16进制字符串
	 * @param key
	 *            密钥16进制字符串
	 * @return 加密后16进制数据
	 */
	public static String aesEncryption(String data, String key) {

		SecretKeySpec skeySpec = new SecretKeySpec(readData(key), "AES");// 生成一组扩展密钥，并放入一个数组之中
		Cipher cipher;
		try {
			cipher = Cipher.getInstance("AES/ECB/NoPadding");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);// 用ENCRYPT_MODE模式，用skeySpec密码组，生成AES加密方法
			byte[] encrypted = cipher.doFinal(readData(data));// 加密message
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
	 * 解密
	 * 
	 * @param data
	 *            需要解密的16进制数据内容
	 * @param key
	 *            解密16进制密钥
	 * @return 返回解密后的16进制数据
	 */
	public static String aesDecryption(String data, String key) {
		SecretKeySpec skeySpec = new SecretKeySpec(readData(key), "AES");// 生成一组扩展密钥，并放入一个数组之中
		Cipher cipher;
		try {
			cipher = Cipher.getInstance("AES/ECB/NoPadding");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);// 用ENCRYPT_MODE模式，用skeySpec密码组，生成AES加密方法
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			byte[] original = cipher.doFinal(readData(data));// 解密
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

	// 二进制数据转换十六进制
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

	// 把16进制字符内容读入字节数组
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
	 * 支持1，不支持0
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
	 * 从字符串中获取数字
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

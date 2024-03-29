package gz.lifesense.ancs.util;

import java.io.File;
import java.io.RandomAccessFile;

import android.util.Log;

public class TxtLog {
	// 将字符串写入到文本文件中
	public static String filePath="AndroidANCSNotificationAAAAAAAAAA";
	public static void writeTxtToFile(String strcontent, String filePath, String fileName) {
		boolean result = isExistSDCard();
//		if (result){
//			// 生成文件夹之后，再生成文件，不然会出错
//			makeFilePath(filePath, fileName);
//			
//			String strFilePath = filePath + fileName;
//			// 每次写入时，都换行写
//			String strContent = strcontent + "\r\n";
//			try {
//				File file = new File(strFilePath);
//				if (!file.exists()) {
//					Log.d("TestFile", "Create the file:" + strFilePath);
//					file.getParentFile().mkdirs();
//					file.createNewFile();
//				}
//				RandomAccessFile raf = new RandomAccessFile(file, "rwd");
//				raf.seek(file.length());
//				raf.write(strContent.getBytes());
//				raf.close();
//			} catch (Exception e) {
//				Log.e("TestFile", "Error on write File:" + e);
//			}
//		} else {
//			Log.e("ERROR:", "SD卡不存在！");
//		}
	}

	// 生成文件
	public static File makeFilePath(String filePath, String fileName) {
		File file = null;
		makeRootDirectory(filePath);
		try {
			file = new File(filePath + fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}

	// 生成文件夹
	public static void makeRootDirectory(String filePath) {
		File file = null;
		try {
			file = new File(filePath);
			if (!file.exists()) {
				file.mkdir();
			}
		} catch (Exception e) {
			Log.i("error:", e + "");
		}
	}

	private static boolean isExistSDCard() {
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		} else
			return false;
	}
}

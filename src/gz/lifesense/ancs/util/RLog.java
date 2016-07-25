package gz.lifesense.ancs.util;

import android.util.Log;

public class RLog {

	private static final boolean DEBUG = true;
	
	public static void e(String tag,String log){
		if(DEBUG){
			Log.e(tag,log);
		}
	}
	
	public static void d(String tag,String log){
		if(DEBUG){
			Log.d(tag,log);
		}
	}
	
	public static void i(String tag,String log){
		if(DEBUG){
			Log.i(tag,log);
		}
	}
	
	public static void v(String tag,String log){
		if(DEBUG){
			Log.v(tag,log);
		}
	}
	
	public static void w(String tag,String log){
		if(DEBUG){
			Log.w(tag,log);
		}
	}
	
}

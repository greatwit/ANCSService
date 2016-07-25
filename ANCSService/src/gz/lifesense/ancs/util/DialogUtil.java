package gz.lifesense.ancs.util;

import com.bde.ancs.androidancs.R;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;


public class DialogUtil {
	public static DialogUtil dialogUtil;
	private PopupWindow mPopupWindow = null;
	
	
	public static DialogUtil getInstance() {
		if (dialogUtil == null) {
			dialogUtil = new DialogUtil();
		}
		return dialogUtil;
	}
	public void DismissPopupWindow(){
		try {
			
			if (mPopupWindow!=null&&mPopupWindow.isShowing()) {
				mPopupWindow.dismiss();
			}
		} catch (Exception e) {
 		}
	}
	public boolean isExitPopupWindow(){
		boolean result = false;
		if (mPopupWindow!=null&&mPopupWindow.isShowing()) {
			result = true;
		}
		return result;
	}

	
	public void showPopupWindowTips(Context context, String textId, int titleId, OnClickListener onClickListener) {
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		LinearLayout layout = (LinearLayout) layoutInflater.inflate(R.layout.dialog_one_btn, null);
		
		TextView tv_content = (TextView) layout.findViewById(R.id.tvt_is_content);
		if (!textId.equals("")) {
			tv_content.setText(textId);
		}
		Button btn_ok = (Button) layout.findViewById(R.id.btn_is_ok);

		btn_ok.setOnClickListener(onClickListener);

		if (mPopupWindow != null && mPopupWindow.isShowing()) {

		} else {
			Log.i("MainActivity", "showPopupWindowTips");
			mPopupWindow = new PopupWindow(layout, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			// mPopupWindow.setOutsideTouchable(true);
			View parentView = ((Activity) context).getWindow().findViewById(Window.ID_ANDROID_CONTENT);
			mPopupWindow.showAtLocation(parentView, Gravity.CENTER_VERTICAL, 0, 0);
		}
	}
	public void showPairOverTime(Context context,int textId,OnClickListener onClickListener4Left,OnClickListener onClickListener4Right) {
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		LinearLayout layout = (LinearLayout) layoutInflater.inflate(R.layout.dialog_two_btn, null);
		
		TextView tv_content = (TextView) layout.findViewById(R.id.tvt_is_content);
		if (textId!=0) {
			tv_content.setText(textId);
		}
		Button btn_ok = (Button) layout.findViewById(R.id.btn_is_ok);
		Button btn_connect_again=(Button)layout.findViewById(R.id.btn_connect_again);

		btn_ok.setOnClickListener(onClickListener4Left);
		btn_connect_again.setOnClickListener(onClickListener4Right);

		if (mPopupWindow != null && mPopupWindow.isShowing()) {

		} else {
			mPopupWindow = new PopupWindow(layout, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			// mPopupWindow.setOutsideTouchable(true);
			View parentView = ((Activity) context).getWindow().findViewById(Window.ID_ANDROID_CONTENT);
			mPopupWindow.showAtLocation(parentView, Gravity.CENTER_VERTICAL, 0, 0);
		}
	}
	

}

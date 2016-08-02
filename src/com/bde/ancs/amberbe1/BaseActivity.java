package com.bde.ancs.amberbe1;

import com.bde.ancs.amberbe1.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;



public abstract class BaseActivity extends FragmentActivity 
{

	protected String TAG = this.getClass().getName();
	protected LinearLayout Layout_center;
	protected LinearLayout layout_header;
	protected RelativeLayout layout_left;
	protected LinearLayout layout_right;
	protected RelativeLayout layout_more;
	protected TextView tv_title;
	protected TextView tv_left;
	protected TextView tv_right;
	protected ImageView iv_left;
	protected ImageView iv_right;
	protected ImageView iv_more;
	private Dialog bindDialog,successDialog,btOpenDialog;

	protected Context mContext = this;
	protected Intent intent;

	/**
	 * 
	 */
	protected abstract void initHeader();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//
		// LifesenseApplication.getApp().addActivity(this);
		setContentView(R.layout.common_base);
		initHeaderView();

	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);

		if (layoutResID != R.layout.common_base) { // 
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}

	}

	private void initHeaderView() {
		boolean result = true;
		Layout_center = (LinearLayout) findViewById(R.id.layout_center);
		layout_header = (LinearLayout) findViewById(R.id.layout_header);

		if (layout_header != null) {

			tv_title = (TextView) findViewById(R.id.tv_title);
			tv_left = (TextView) findViewById(R.id.tv_left);
			tv_right = (TextView) findViewById(R.id.tv_right);
			layout_left = (RelativeLayout) findViewById(R.id.layout_left);
			layout_right = (LinearLayout) findViewById(R.id.layout_right);
			iv_left = (ImageView) findViewById(R.id.iv_left);
			iv_right = (ImageView) findViewById(R.id.iv_right);
			layout_more = (RelativeLayout) findViewById(R.id.layout_more);
			iv_more = (ImageView) findViewById(R.id.iv_more);
		}
	}

	/**
	 * 
	 * 
	 * @param layout
	 */
	public void setCenterView(int layout) {
		Layout_center.removeAllViews();
		LayoutInflater inflater = getLayoutInflater();
		View addView = inflater.inflate(layout, null);
		Layout_center.addView(addView, new ViewGroup.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));

		initHeaderView();
		initHeader();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	protected void setHeader_Title(int resid) {
		if (resid > 0) {

			tv_title.setText(resid);
		}

	}

	protected void setHeader_Title(String text) {
		if (tv_title != null) {
			tv_title.setText(text);
		}
	}

	protected void setHeader_Title_Color(int color) {
		if (tv_title != null) {
			tv_title.setTextColor(color);
		}
	}

	protected void setHeader_LeftImage(int resid) {
		if (iv_left != null) {
			iv_left.setVisibility(View.VISIBLE);
			if (resid > 0) {
				iv_left.setImageResource(resid);
				layout_left.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				});
			}
		}
	}

	protected void setHeader_LeftText(String text) {
		if (tv_left != null) {
			tv_left.setVisibility(View.VISIBLE);
			tv_left.setText(text);
			iv_left.setVisibility(View.GONE);
		}
	}

	protected void setHeader_LeftClickListener(View.OnClickListener listener) {
		if (layout_left != null) {
			layout_left.setOnClickListener(listener);
		}
	}

	protected void setHeader_RightImage(int resid) {
		if (iv_right != null) {
			iv_right.setVisibility(View.VISIBLE);
			if (resid > 0) {
				iv_right.setImageResource(resid);
			}
		}
	}

	protected void setHeader_RightText(int resid) {
		if (tv_right != null) {
			tv_right.setVisibility(View.VISIBLE);
			if (resid > 0) {
				tv_right.setText(resid);
			}
		}
	}

	protected void setHeader_RightText(String text) {
		if (tv_right != null) {
			tv_right.setVisibility(View.VISIBLE);
			if (text != null) {
				tv_right.setText(text);
			}
		}
	}

	protected void setHeader_RightClickListener(View.OnClickListener listener) {
		if (layout_right != null) {
			layout_right.setOnClickListener(listener);
		}
	}

	protected void setHeaderVisibility(int visibility) {
		if (layout_header != null) {

			layout_header.setVisibility(visibility);
		}
	}

	protected void setHeaderBackground(int colorId) {
		layout_header.setBackgroundColor(getResources().getColor(colorId));
	}

	/**
	 * eg:#FFFAFA
	 * 
	 * @param colorStr
	 */
	protected void setHeaderBackground(String colorStr) {

		layout_header.setBackgroundColor(Color.parseColor(colorStr));
	}

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		// overridePendingTransition(R.anim.slide_in_right,
		// R.anim.slide_out_left);
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
		super.startActivityForResult(intent, requestCode, options);
		// overridePendingTransition(R.anim.slide_in_right,
		// R.anim.slide_out_left);
	}

	public String getStringById(int resid) {
		return getResources().getString(resid);
	}

	
	public void initOpenDialog(OnClickListener sureListener) 
	{
		View view = LayoutInflater.from(this).inflate(R.layout.openbt_dialog, null);
		btOpenDialog = new Dialog(this, R.style.Dialog);
		btOpenDialog.setContentView(view);
		((TextView) view.findViewById(R.id.dialog_cancel_tv)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) 
			{
				if (btOpenDialog!=null && btOpenDialog.isShowing()) 
				{
					btOpenDialog.dismiss();
				}
			}
		});
		((TextView) view.findViewById(R.id.dialog_sure_tv)).setOnClickListener(sureListener);
	}
	
	public void dismissOpenDialog()
	{
		if (btOpenDialog!=null && btOpenDialog.isShowing()) 
		{
			btOpenDialog.dismiss();
		}
	}
	
	public void showOpenDialog(String text) 
	{
		((TextView) btOpenDialog.findViewById(R.id.context_dialog_tv)).setText(text);
		btOpenDialog.show();
	}
	
	
	public void initBindDialog(OnClickListener sureListener) 
	{
		View view = LayoutInflater.from(this).inflate(R.layout.bind_dialog, null);
		bindDialog = new Dialog(this, R.style.Dialog);
		bindDialog.setContentView(view);
		((TextView) view.findViewById(R.id.dialog_cancel_tv)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismissBindDialog();
			}
		});
		((TextView) view.findViewById(R.id.dialog_sure_tv)).setOnClickListener(sureListener);
	}
	
	public void dismissBindDialog()
	{
		if (bindDialog!=null&&bindDialog.isShowing()) {
			bindDialog.dismiss();
		}
	}
	
	public void showBindDialog(String text) 
	{
		((TextView) bindDialog.findViewById(R.id.context_dialog_tv)).setText(text);
		bindDialog.show();
	}
	
	public void initConnectedDialog(OnClickListener sureListener) 
	{
		View view = LayoutInflater.from(this).inflate(R.layout.bind_success_dialog_layout, null);
		successDialog = new Dialog(this, R.style.Dialog);
		successDialog.setContentView(view);
	}
	
	public void dismissConnectedDialog(){
		if (successDialog!=null&&successDialog.isShowing()) {
			successDialog.dismiss();
		}
	} 
	
	
	public void showConnectdDialog()
	{
		if (successDialog!=null)
			successDialog.show();
	}

	
}

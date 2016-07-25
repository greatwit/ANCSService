package gz.lifesense.ancs.androidancs;

import gz.lifesense.ancs.aidl.DeviceInfo;
import gz.lifesense.ancs.aidl.RemoteBlueTooth;
import gz.lifesense.ancs.server.ShareManager;
import gz.lifesense.ancs.util.Consts;

import com.bde.ancs.androidancs.R;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


public class DeviceDetailActivity extends BaseActivity 
{
	private TextView tv_device_name;
	private LinearLayout layout_device_name_modify;
	private Dialog modifyNameDialog;
	private ShareManager shareManager;
	private EditText et_modify_name;
	private TextView tv_unbind;
	private CheckBox cb_comming_call;
	PedometerProtocol pedometerProtocol = new PedometerProtocol();
	
	RemoteBlueTooth mRemoteBlueTooth=null;
	DeviceInfo beauty = null;
	boolean isBinded=false;
	ExternServiceConnection mConnection = new ExternServiceConnection();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setCenterView(R.layout.device_setting_layout);
		shareManager = new ShareManager(DeviceDetailActivity.this);
		initView();
		
		Intent intent  = new Intent(Consts.actionName);
		intent.setPackage(getPackageName());
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);	
	}
	
	private class ExternServiceConnection implements ServiceConnection
	{
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) 
		{
			Log.i(TAG, "onServiceConnected...");
			mRemoteBlueTooth = RemoteBlueTooth.Stub.asInterface(service);
			if(mRemoteBlueTooth==null)
			{
				Log.e(TAG, "mRemoteBlueTooth is null...");
				return;
			}
			try
			{
				isBinded=true;
				beauty 	= mRemoteBlueTooth.getDeviceInfo();
			}
			catch (RemoteException e) 
			{
				e.printStackTrace();
			}
			
			try {
				mRemoteBlueTooth.receivedTelegram();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.i(TAG, "onServiceDisconnected...");
		}

	}//
	
	@Override
	protected void onResume() 
	{
		super.onResume();
		if(shareManager.getIsOpenIncomingCall())
		{
			cb_comming_call.setBackgroundResource(R.drawable.btn_open);
		}
		else
		{
			cb_comming_call.setBackgroundResource(R.drawable.btn_close);
		}
	}

	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		unbindService(mConnection);	
	}
	
	private void initView() 
	{
		tv_device_name = (TextView) findViewById(R.id.tv_device_name);
		layout_device_name_modify = (LinearLayout) findViewById(R.id.layout_device_name_modify);
		tv_unbind=(TextView)findViewById(R.id.tv_unbind);
		cb_comming_call=(CheckBox)findViewById(R.id.cb_comming_call);
		initModifyNameDialog();
		// et_modify_name=(EditText)findViewById(R.id.et_modify_name);

		initBindDialog(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				try {
					mRemoteBlueTooth.disconnect();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				shareManager.setDeviceAddress("");
				shareManager.setDeviceName("");
				dismissBindDialog();
				finish();
			}
		});
	
		tv_device_name.setText(shareManager.getDeviceName());
		layout_device_name_modify.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				showModifyNameDialog();
			}
		});
		tv_unbind.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				showBindDialog("是否取消与"+shareManager.getDeviceName()+"的绑定？");
			}
		});
		cb_comming_call.setOnCheckedChangeListener(new OnCheckedChangeListener() 
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
			{
				if(shareManager.getIsOpenIncomingCall())
				{
					cb_comming_call.setBackgroundResource(R.drawable.btn_close);
					shareManager.setIsOpenIncomingCall(false);
					
					@SuppressWarnings("static-access")
					byte[] closeCmd = pedometerProtocol.closeCommingCall();
					try {
						mRemoteBlueTooth.writeByte(closeCmd);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else
				{
					cb_comming_call.setBackgroundResource(R.drawable.btn_open);
					shareManager.setIsOpenIncomingCall(true);
					
					@SuppressWarnings("static-access")
					byte[] openCmd = pedometerProtocol.openCommingCall();
					try {
						mRemoteBlueTooth.writeByte(openCmd);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
		});
	}

	@Override
	protected void initHeader() 
	{
		setHeader_Title(getResources().getString(R.string.bind_dev));
		setHeader_LeftText("<"+getResources().getString(R.string.turn_back));
		
		setHeader_LeftClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				finish();
			}
		});
	}

	public void initModifyNameDialog() 
	{
		View view = LayoutInflater.from(this).inflate(R.layout.edit_name_dialog_layout, null);
		modifyNameDialog = new Dialog(this, R.style.Dialog);
		modifyNameDialog.setContentView(view);
		 et_modify_name = (EditText) view.findViewById(R.id.et_modify_name);
		 et_modify_name.setText(shareManager.getDeviceName());
		 
		((EditText) view.findViewById(R.id.et_modify_name)).setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				dismissBindDialog();
			}
		});
		((TextView) view.findViewById(R.id.tv_rename_sure)).setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				shareManager.setDeviceName(et_modify_name.getText().toString());
				dismissModifyNameDialog();
				tv_device_name.setText(et_modify_name.getText().toString());

			}
		});
		((TextView) view.findViewById(R.id.tv_rename_cancel)).setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				dismissModifyNameDialog();
			}
		});

	}

	public void showModifyNameDialog() 
	{
		modifyNameDialog.show();
	}

	public void dismissModifyNameDialog() 
	{
		if (modifyNameDialog != null && modifyNameDialog.isShowing()) 
		{
			modifyNameDialog.dismiss();
		}
	}

}

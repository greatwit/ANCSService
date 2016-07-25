package gz.lifesense.ancs.device;


import java.util.ArrayList;

import com.bde.ancs.androidancs.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import gz.lifesense.ancs.aidl.DeviceInfo;


public class SearchListAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<DeviceInfo> data;

	public SearchListAdapter(Context context, ArrayList<DeviceInfo> data) {
		super();
		this.context = context;
		this.data = data;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		Holder holder;
		if (arg1 == null) {
			arg1 = LayoutInflater.from(context).inflate(R.layout.device_item_layout, null);
			holder = new Holder(arg1);
			arg1.setTag(holder);
		} else {
			holder = (Holder) arg1.getTag();
		}
		holder.deviceName.setText(data.get(arg0).getDeviceName());
		holder.device_rssi.setText(data.get(arg0).getRssi()+"");
		return arg1;
	}

	private class Holder {
		private View view;
		private TextView deviceName;
		private TextView device_rssi;

		public Holder(View view) {
			this.view = view;
			deviceName = (TextView) view.findViewById(R.id.device_name_tv);
			device_rssi=(TextView)view.findViewById(R.id.device_rssi);
		}
	}
}

package gz.lifesense.ancs.aidl;
import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class DeviceInfo {

	private BluetoothDevice device;
	private int rssi;
	private String battery;
	private String cardId;
	private String carrier;
	private String communicationType;
	private String deviceName;
	private String deviceType;
	private String hardwareVersion;
	private String id;
	private String mac;
	private String maxUserQuantity;
	private String modelNum;
	private String password;
	private String picture;
	private String po;
	private String remark;
	private String saleModel;
	private String sn;
	private String sn2;
	private String softwareVersion;
	private int status;
	private String unit;
	private String qrcode;

	
	public String getQrcode() {
		return qrcode;
	}

	public void setQrcode(String qrcode) {
		this.qrcode = qrcode;
	}

	public BluetoothDevice getDevice() {
		return device;
	}

	public void setDevice(BluetoothDevice device) {
		this.device = device;
	}

	public int getRssi() {
		return rssi;
	}

	public void setRssi(int rssi) {
		this.rssi = rssi;
	}

	public String getBattery() {
		return battery;
	}

	public void setBattery(String battery) {
		this.battery = battery;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public String getCarrier() {
		return carrier;
	}

	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}

	public String getCommunicationType() {
		return communicationType;
	}

	public void setCommunicationType(String communicationType) {
		this.communicationType = communicationType;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getHardwareVersion() {
		return hardwareVersion;
	}

	public void setHardwareVersion(String hardwareVersion) {
		this.hardwareVersion = hardwareVersion;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getMaxUserQuantity() {
		return maxUserQuantity;
	}

	public void setMaxUserQuantity(String maxUserQuantity) {
		this.maxUserQuantity = maxUserQuantity;
	}

	public String getModelNum() {
		return modelNum;
	}

	public void setModelNum(String modelNum) {
		this.modelNum = modelNum;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public String getPo() {
		return po;
	}

	public void setPo(String po) {
		this.po = po;
	}


	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getSaleModel() {
		return saleModel;
	}

	public void setSaleModel(String saleModel) {
		this.saleModel = saleModel;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public String getSn2() {
		return sn2;
	}

	public void setSn2(String sn2) {
		this.sn2 = sn2;
	}

	public String getSoftwareVersion() {
		return softwareVersion;
	}

	public void setSoftwareVersion(String softwareVersion) {
		this.softwareVersion = softwareVersion;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	@Override
	public String toString() {
		return "DeviceInfo [device=" + device + ", rssi=" + rssi + ", battery="
				+ battery + ", cardId=" + cardId + ", carrier=" + carrier
				+ ", communicationType=" + communicationType + ", deviceName="
				+ deviceName + ", deviceType=" + deviceType
				+ ", hardwareVersion=" + hardwareVersion + ", id=" + id
				+ ", mac=" + mac + ", maxUserQuantity=" + maxUserQuantity
				+ ", modelNum=" + modelNum + ", password=" + password
				+ ", picture=" + picture + ", po=" + po + ", qrcode=" + qrcode
				+ ", remark=" + remark + ", saleModel=" + saleModel + ", sn="
				+ sn + ", sn2=" + sn2 + ", softwareVersion=" + softwareVersion
				+ ", status=" + status + ", unit=" + unit + "]";
	}

	public void writeToParcel(Parcel dest, int flags) 
	{
	}
	
	public static final Parcelable.Creator<DeviceInfo> CREATOR = new Creator<DeviceInfo>() {

		@Override
		public DeviceInfo createFromParcel(Parcel arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public DeviceInfo[] newArray(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}
		
	};
	
}

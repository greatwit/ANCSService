/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\android_env\\workspace\\ANCSService\\src\\gz\\lifesense\\ancs\\aidl\\RemoteBlueTooth.aidl
 */
package gz.lifesense.ancs.aidl;
public interface RemoteBlueTooth extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements gz.lifesense.ancs.aidl.RemoteBlueTooth
{
private static final java.lang.String DESCRIPTOR = "gz.lifesense.ancs.aidl.RemoteBlueTooth";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an gz.lifesense.ancs.aidl.RemoteBlueTooth interface,
 * generating a proxy if needed.
 */
public static gz.lifesense.ancs.aidl.RemoteBlueTooth asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof gz.lifesense.ancs.aidl.RemoteBlueTooth))) {
return ((gz.lifesense.ancs.aidl.RemoteBlueTooth)iin);
}
return new gz.lifesense.ancs.aidl.RemoteBlueTooth.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_getAllInfo:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getAllInfo();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getDeviceInfo:
{
data.enforceInterface(DESCRIPTOR);
DeviceInfo _result = this.getDeviceInfo();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_setDeviceInfo:
{
data.enforceInterface(DESCRIPTOR);
DeviceInfo _arg0;
if ((0!=data.readInt())) {
_arg0 = DeviceInfo.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.setDeviceInfo(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_openBluetooth:
{
data.enforceInterface(DESCRIPTOR);
this.openBluetooth();
reply.writeNoException();
return true;
}
case TRANSACTION_isBluetoothOpen:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isBluetoothOpen();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_connect:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.connect(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_connectLast:
{
data.enforceInterface(DESCRIPTOR);
this.connectLast();
reply.writeNoException();
return true;
}
case TRANSACTION_disconnect:
{
data.enforceInterface(DESCRIPTOR);
this.disconnect();
reply.writeNoException();
return true;
}
case TRANSACTION_isBlueToothConnected:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isBlueToothConnected();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_isDeviceConnected:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isDeviceConnected();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_writeByte:
{
data.enforceInterface(DESCRIPTOR);
byte[] _arg0;
_arg0 = data.createByteArray();
this.writeByte(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_receivedTelegram:
{
data.enforceInterface(DESCRIPTOR);
this.receivedTelegram();
reply.writeNoException();
return true;
}
case TRANSACTION_getDiscoverDuration:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.getDiscoverDuration();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setDiscoverDuration:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.setDiscoverDuration(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_stopServer:
{
data.enforceInterface(DESCRIPTOR);
this.stopServer();
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements gz.lifesense.ancs.aidl.RemoteBlueTooth
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public java.lang.String getAllInfo() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getAllInfo, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public DeviceInfo getDeviceInfo() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
DeviceInfo _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getDeviceInfo, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = DeviceInfo.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void setDeviceInfo(DeviceInfo info) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((info!=null)) {
_data.writeInt(1);
info.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_setDeviceInfo, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void openBluetooth() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_openBluetooth, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public boolean isBluetoothOpen() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isBluetoothOpen, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean connect(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_connect, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void connectLast() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_connectLast, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void disconnect() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_disconnect, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public boolean isBlueToothConnected() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isBlueToothConnected, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean isDeviceConnected() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isDeviceConnected, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void writeByte(byte[] value) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByteArray(value);
mRemote.transact(Stub.TRANSACTION_writeByte, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void receivedTelegram() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_receivedTelegram, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public boolean getDiscoverDuration() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getDiscoverDuration, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void setDiscoverDuration(boolean duration) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((duration)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setDiscoverDuration, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void stopServer() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_stopServer, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_getAllInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_getDeviceInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_setDeviceInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_openBluetooth = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_isBluetoothOpen = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_connect = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_connectLast = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_disconnect = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_isBlueToothConnected = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_isDeviceConnected = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_writeByte = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
static final int TRANSACTION_receivedTelegram = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
static final int TRANSACTION_getDiscoverDuration = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
static final int TRANSACTION_setDiscoverDuration = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
static final int TRANSACTION_stopServer = (android.os.IBinder.FIRST_CALL_TRANSACTION + 14);
}
public java.lang.String getAllInfo() throws android.os.RemoteException;
public DeviceInfo getDeviceInfo() throws android.os.RemoteException;
public void setDeviceInfo(DeviceInfo info) throws android.os.RemoteException;
public void openBluetooth() throws android.os.RemoteException;
public boolean isBluetoothOpen() throws android.os.RemoteException;
public boolean connect(java.lang.String address) throws android.os.RemoteException;
public void connectLast() throws android.os.RemoteException;
public void disconnect() throws android.os.RemoteException;
public boolean isBlueToothConnected() throws android.os.RemoteException;
public boolean isDeviceConnected() throws android.os.RemoteException;
public void writeByte(byte[] value) throws android.os.RemoteException;
public void receivedTelegram() throws android.os.RemoteException;
public boolean getDiscoverDuration() throws android.os.RemoteException;
public void setDiscoverDuration(boolean duration) throws android.os.RemoteException;
public void stopServer() throws android.os.RemoteException;
}

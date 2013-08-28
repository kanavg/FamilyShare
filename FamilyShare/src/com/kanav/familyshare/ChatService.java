package com.kanav.familyshare;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.format.Formatter;
import android.util.Log;

//Some code adapted from http://www.ece.ncsu.edu/wireless/MadeInWALAN/AndroidTutorial/
public class ChatService {

	private static final String TAG = "ChatService";
	private Handler mHandler;
	private ChatThread mChatThread;
	Context mContext;
	private final static int BCAST_PORT = 2564;
	DatagramSocket mSocket;
	InetAddress mBcastIP;
	private String mLocalIpv4;
	private String mLocalIpv6;
	
	
	public ChatService(Context context, Handler handler) {
		mContext = context;
		mHandler = handler;
	}
	
	public synchronized void start() {
		mChatThread = new ChatThread();
		mChatThread.start();
	}
	
	public synchronized void stop() {
		if(mChatThread != null){
			mChatThread.cancel();
			mChatThread = null;
		}
	}
	
	public void write(byte[] out) {
		//mChatThread.write(out);
		new WriteTask().execute(out);
	}
	
	private class WriteTask extends AsyncTask<byte[], Void, String> {

		@Override
		protected String doInBackground(byte[]... params) {
			try {
				String data = new String(params[0]);
				DatagramPacket packet = new DatagramPacket(data.getBytes(), data.length(), mBcastIP, BCAST_PORT);
				mSocket.send(packet);
				mHandler.obtainMessage(ChatFragment.MSG_WRITE, -1, -1, params[0]).sendToTarget();
			} catch(Exception e) {
				Log.e(TAG, "Exception during writing: " + e);
			}
			return null;
		}
		
	}
	
	private class ChatThread extends Thread {
		
		
		public ChatThread() {
			try {
				getBcastAddress();
				getLocalAddress();
				mSocket = new DatagramSocket(BCAST_PORT);
				mSocket.setBroadcast(true);
			} catch (IOException e) {
				Log.e(TAG, "Could not make socket: " + e);
			}
		}
		
		public void run() {
			byte[] buf = new byte[1024];
			while(true) {
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				try {
					mSocket.receive(packet);
					Log.e(TAG, "received packet: " + packet.toString());
					InetAddress remoteIP = packet.getAddress();
					Log.e(TAG, "packet IP " + remoteIP.getHostAddress() + ".Local ip= " + mLocalIpv4);
					// Dont display the message at the same place you sent it from, else it will appear twice.
					if(remoteIP.getHostAddress().equals(mLocalIpv4) || remoteIP.getHostAddress().equals("::1")) {
						continue;
					}
					String data = new String(packet.getData(), 0, packet.getLength());
					mHandler.obtainMessage(ChatFragment.MSG_READ, -1, -1, data).sendToTarget();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		
		private void getBcastAddress() throws IOException {
			WifiManager wifiMgr = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
			DhcpInfo dhcp = wifiMgr.getDhcpInfo();
			if(dhcp == null) {
				Log.e(TAG, "Error getting dhcp infor");
				mBcastIP = null;
			}
			
			int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
			byte[] quads = new byte[4];
			for(int i = 0; i< 4; i++) {
				quads[i] = (byte)((broadcast >>i * 8) & 0xFF);
			}
			mBcastIP = InetAddress.getByAddress(quads);
		}
		
		private void getLocalAddress() throws IOException {
			String localIP;
			try {
				for(Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
					NetworkInterface nwInterface = en.nextElement();
					for(Enumeration<InetAddress> enumIp = nwInterface.getInetAddresses(); enumIp.hasMoreElements();) {
						InetAddress inetAddress = enumIp.nextElement();
						if(!inetAddress.isLoopbackAddress()) {
							if(InetAddressUtils.isIPv4Address(localIP = inetAddress.getHostAddress()))
								mLocalIpv4 = localIP;
							else
								mLocalIpv6 = localIP;
						}
					}
				}
			} catch (SocketException e) {
				Log.e(TAG, e.toString());
				mLocalIpv4 = null;
			}
		}
		
		private void cancel() {
			try {
				mSocket.close();
			} catch(Exception e) {
				Log.e(TAG, "Exception closing socket: " + e.toString());
			}
		}
		
	}
}

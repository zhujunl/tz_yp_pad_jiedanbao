package com.example.receving_order.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;


import com.example.receving_order.MyApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

/**
 * Created by Administrator on 2020/6/30.
 */

public class Jc_Utils {
    public static String getMacFromHardware(Context context) {

        String macAddress = null;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){//5.0以下
            macAddress = getMacDefault(context);
            if (macAddress != null ) {
                Log.d("Utils", "android 5.0以前的方式获取mac"+macAddress);
                macAddress =  macAddress.replaceAll(":","");
                if(macAddress.equalsIgnoreCase("020000000000")== false){
                    return macAddress;
                }
            }
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            macAddress = getMacAddress();
            if (macAddress != null ) {
                Log.d("Utils", "android 6~7 的方式获取的mac"+macAddress);
                macAddress =  macAddress.replaceAll(":","");
                if(macAddress.equalsIgnoreCase("020000000000")== false){
                    return macAddress;
                }
            }
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            macAddress = getMacFromHardware();
            if (macAddress != null ) {
                Log.d("Utils", "android 7以后 的方式获取的mac"+macAddress);
                macAddress =  macAddress.replaceAll(":","");
                if(macAddress.equalsIgnoreCase("020000000000") == false){
                    return macAddress;
                }
            }
        }

        Log.d("Utils", "没有获取到MAC");
        return null;
    }


    /**
     * Android 6.0（包括） - Android 7.0（不包括）
     * @return
     */
    public static String getMacAddress() {
        String WifiAddress =  null;
        try {
            WifiAddress = new BufferedReader(new FileReader(new File("/sys/class/net/wlan0/address"))).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return WifiAddress;
    }

    /**
     * 遍历循环所有的网络接口，找到接口是 wlan0
     * 必须的权限 <uses-permission android:name="android.permission.INTERNET" />
     * @return
     */
    private static String getMacFromHardware() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            Log.d("Utils", "all:" + all.size());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return null;
                }
                Log.d("Utils", "macBytes:" + macBytes.length + "," + nif.getName());

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Android  6.0 之前（不包括6.0）
     * 必须的权限  <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
     * @param context
     * @return
     */
    private static String getMacDefault(Context context) {
        String mac = null;
        if (context == null) {
            return mac;
        }

        WifiManager wifi = (WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        if (wifi == null) {
            return mac;
        }
        WifiInfo info = null;
        try {
            info = wifi.getConnectionInfo();
        } catch (Exception e) {

        }
        if (info == null) {
            return null;
        }
        mac = info.getMacAddress();
        if (!TextUtils.isEmpty(mac)) {
            mac = mac.toUpperCase(Locale.ENGLISH);
        }
        return mac;
    }


    /**
     * 根据IP地址获取MAC地址
     * @return
     */
    public static String getLocalMacAddressFromIp() {
        String strMacAddr = getMacFromHardware(MyApplication.getApplication());
        if (!TextUtils.isEmpty(strMacAddr)){
            return strMacAddr;
        }

        try {
            //获得IpD地址
            InetAddress ip = getLocalInetAddress();
            byte[] b = NetworkInterface.getByInetAddress(ip).getHardwareAddress();
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < b.length; i++) {
                if (i != 0) {
                    buffer.append(':');
                }
                String str = Integer.toHexString(b[i] & 0xFF);
                buffer.append(str.length() == 1 ? 0 + str : str);
            }
            strMacAddr = buffer.toString().toUpperCase();
        } catch (Exception e) {
        }
        String newStrMacAddr = null;
        if (!TextUtils.isEmpty(strMacAddr)){
            newStrMacAddr = strMacAddr.replace(":","");
        }
        return newStrMacAddr;
    }
    /**
     * 获取移动设备本地IP
     * @return
     */
    public static InetAddress getLocalInetAddress() {
        InetAddress ip = null;
        try {
            //列举
            Enumeration<NetworkInterface> en_netInterface = NetworkInterface.getNetworkInterfaces();
            while (en_netInterface.hasMoreElements()) {//是否还有元素
                NetworkInterface ni = (NetworkInterface) en_netInterface.nextElement();//得到下一个元素
                Enumeration<InetAddress> en_ip = ni.getInetAddresses();//得到一个ip地址的列举
                while (en_ip.hasMoreElements()) {
                    ip = en_ip.nextElement();
                    if (!ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1)
                        break;
                    else
                        ip = null;
                }
                if (ip != null) {
                    break;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ip;
    }
}

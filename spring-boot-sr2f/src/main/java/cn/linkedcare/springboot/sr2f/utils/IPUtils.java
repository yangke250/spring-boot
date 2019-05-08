package cn.linkedcare.springboot.sr2f.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * 得到ip的工具类
 * @author wl
 *
 */
public class IPUtils {
	private static String ip = null;
	public static String getIp(){
		return ip;
	}
	static{
		try{
			Enumeration en = NetworkInterface.getNetworkInterfaces();
			while (en.hasMoreElements()) {  
			        NetworkInterface i = (NetworkInterface) en.nextElement();  
			        for (Enumeration en2 = i.getInetAddresses(); en2.hasMoreElements();) {  
			            InetAddress addr = (InetAddress) en2.nextElement();  
			            if (!addr.isLoopbackAddress()) {  
			                if (addr instanceof Inet4Address) {  
			                    ip = addr.getHostAddress();
			                } 
			            }  
			        }  
			 }    
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
		if(ip==null){
			throw new RuntimeException("ip is null");
		}
	}
	
	
	
}

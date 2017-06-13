package com.fgtit.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;

public class HttpConnSoap {

	

	public ArrayList<String> GetWebServre(String serverUrl, String methodName, ArrayList<String> Parameters, ArrayList<String> ParValues) {
		
		ArrayList<String> Values = new ArrayList<String>();
		String ServerUrl = serverUrl;//"http://192.168.1.124/FingermapWebApp/FingermapService.asmx";
		String soapAction = "http://www.fgtit.com/" + methodName;
		String soap = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
					+ "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
					+ "<soap:Body>";
		String tps, vps, ts;
		String mreakString = "";
		mreakString = "<" + methodName + " xmlns=\"http://www.fgtit.com/\">";
		
		for (int i = 0; i < Parameters.size(); i++) {
			tps = Parameters.get(i).toString();
			vps = ParValues.get(i).toString();
			ts = "<" + tps + ">" + vps + "</" + tps + ">";
			mreakString = mreakString + ts;
		}
		mreakString = mreakString + "</" + methodName + ">";

		String soap2 = "</soap:Body>"
					  +"</soap:Envelope>";
		String requestData = soap + mreakString + soap2;
		
		try {
			//StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			//StrictMode.setThreadPolicy(policy);
			
			//StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
			
			URL url = new URL(ServerUrl);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			byte[] bytes = requestData.getBytes("utf-8");
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			con.setConnectTimeout(6000);// ���ó�ʱʱ��
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "text/xml;charset=utf-8");
			con.setRequestProperty("SOAPAction", soapAction);
			con.setRequestProperty("Content-Length", "" + bytes.length);
			OutputStream outStream = con.getOutputStream();
			outStream.write(bytes);
			outStream.flush();
			outStream.close();
			InputStream inStream = con.getInputStream();

			//Values.add("s");
			//data=parser(inStream);
			Values = inputStreamtovaluelist(inStream, methodName);
			return Values;
		} catch (Exception e) {
			return null;
		}
	}

	public ArrayList<String> inputStreamtovaluelist(InputStream in, String MonthsName) throws IOException {
		StringBuffer out = new StringBuffer();
		String s1 = "";
		byte[] b = new byte[4096];
		ArrayList<String> Values = new ArrayList<String>();
		Values.clear();

		for (int n; (n = in.read(b)) != -1;) {
			s1 = new String(b, 0, n);
			out.append(s1);
		}

		//System.out.println(out);
		String[] s13 = s1.split("><");
		String ifString = MonthsName + "Result";
		String TS = "";
		String vs = "";

		Boolean getValueBoolean = false;
		for (int i = 0; i < s13.length; i++) {
			TS = s13[i];
			//System.out.println(TS);
			int j, k, l;
			j = TS.indexOf(ifString);
			k = TS.lastIndexOf(ifString);

			if (j >= 0) {
				System.out.println(j);
				if (getValueBoolean == false) {
					getValueBoolean = true;
				} else {

				}

				if ((j >= 0) && (k > j)) {
					//System.out.println("FFF" + TS.lastIndexOf("/" + ifString));
					//System.out.println(TS);
					l = ifString.length() + 1;
					vs = TS.substring(j + l, k - 2);
					//System.out.println("fff"+vs);
					Values.add(vs);
					//System.out.println("�˳�" + vs);
					getValueBoolean = false;
					return Values;
				}

			}
			if (TS.lastIndexOf("/" + ifString) >= 0) {
				getValueBoolean = false;
				return Values;
			}
			if ((getValueBoolean) && (TS.lastIndexOf("/" + ifString) < 0) && (j < 0)) {
				k = TS.length();
				//System.out.println(TS);
				vs = TS.substring(7, k - 8);
				//System.out.println("f"+vs);
				Values.add(vs);
			}

		}

		return Values;
	}

}

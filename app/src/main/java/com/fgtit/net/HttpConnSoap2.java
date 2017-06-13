package com.fgtit.net;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.os.StrictMode;

//连接webservice的类
public class HttpConnSoap2
{
    //获取返回的InputStream，为了增强通用性，在方法内不对其进行解析。
    //@param methodName  webservice方法名
    //@param Parameters  webservice方法对应的参数名
    //@param ParValues   webservice方法中参数对应的值
    //@return 未解析的InputStream
    public InputStream GetWebServre(String serverUrl,String methodName, ArrayList<String> Parameters, ArrayList<String> ParValues)
    {
    	String ServerUrl = serverUrl;
        String soapAction = "http://www.fgtit.com/" + methodName;
        String soap = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                      + "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                      + "<soap:Body>";
        String tps, vps, ts;
        String mreakString = "";
        mreakString = "<" + methodName + " xmlns=\"http://www.fgtit.com/\">";
        
        for (int i = 0; i < Parameters.size(); i++)
        {
            tps = Parameters.get (i).toString();
            vps = ParValues.get (i).toString();	//设置该方法的参数为.net webService中的参数名称
            ts = "<" + tps + ">" + vps + "</" + tps + ">";
            mreakString = mreakString + ts;
        }
        mreakString = mreakString + "</" + methodName + ">";
        String soap2 = "</soap:Body>"
        			   +"</soap:Envelope>";
        String requestData = soap + mreakString + soap2;	//其上所有的数据都是在拼凑requestData，即向服务器发送的数据

        try
        {
        	//StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        	
            URL url = new URL (ServerUrl); //指定服务器地址
            HttpURLConnection con = (HttpURLConnection) url.openConnection();//打开链接
            byte[] bytes = requestData.getBytes ("utf-8"); //指定编码格式，可以解决中文乱码问题
            con.setDoInput (true); //指定该链接是否可以输入
            con.setDoOutput (true); //指定该链接是否可以输出
            con.setUseCaches (false); //指定该链接是否只用caches
            con.setConnectTimeout (6000); // 设置超时时间
            con.setRequestMethod ("POST"); //指定发送方法名，包括Post和Get。
            con.setRequestProperty ("Content-Type", "text/xml;charset=utf-8"); //设置（发送的）内容类型
            con.setRequestProperty ("SOAPAction", soapAction); //指定soapAction
            con.setRequestProperty ("Content-Length", "" + bytes.length); //指定内容长度

            //发送数据
            OutputStream outStream = con.getOutputStream();
            outStream.write (bytes);
            outStream.flush();
            outStream.close();

            //获取数据
            InputStream inputStream = con.getInputStream();
            return inputStream;

            /**
             * 此类到此结束了，比原来的HttpConnSoap还短，因为这里没有对返回的数据做解析。数据完全都保存在了inputStream中。
             * 而原来的类是将数据解析成了ArrayList
             * <String>格式返回。显然，这样无法解决我们上面的需求（返回值是复杂类型的List）
             */
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    /*
HttpConnSoap2 webservice = new HttpConnSoap2();
String methodName = "showReview";//方法名
ArrayList<String> paramList = new ArrayList<String>();
ArrayList<String> parValueList = new ArrayList<String>();
ArrayList<CommentInfor>() resultList = new ArrayList<CommentInfor>();

paramList.add ("ID");//指定参数名
parValueList.add ("001");//指定参数值

InputStream inputStream = webservice.GetWebServre (methodName, paramList, parValueList);
resultList = XMLParase.paraseCommentInfors (inputStream); 
     */

}
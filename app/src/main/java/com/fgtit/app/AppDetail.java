package com.fgtit.app;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class AppDetail {

	private String	Version="";
	private int 	VersionCode=0;
	private String	Uri="";
	private String	FileName="";
	private String	AppHistory="";
	
	public String getVersion(){
		return Version;
	}
	
	public void setVersion(String ver){
		Version=ver;
	}
	
	public int getVersionCode(){
		return VersionCode;
	}
	
	public void setVersionCode(int code){
		VersionCode=code;
	}
	
	public String getUri(){
		return Uri;
	}
	
	public void setUri(String uri){
		Uri=uri;
	}
	
	public String getFileName(){
		return FileName;		
	}
	
	public void setFileName(String filename){
		FileName=filename;
	}
	
	public String getAppHistory(){
		return AppHistory;
	}
	
	public void setAppHistory(String history){
		AppHistory=history;
	}
	
	public static AppDetail parseXML(InputStream stream){
		AppDetail appdetail=new AppDetail();
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse(stream);
			Element root = dom.getDocumentElement();
			NodeList items = root.getElementsByTagName("UpdateApp");//查找所有UpdateApp节点
			if(items.getLength()>0){
				Element personNode = (Element) items.item(0);
				appdetail.setVersion(new String(personNode.getAttribute("Version")));
				NodeList childsNodes = personNode.getChildNodes();
				for (int j = 0; j < childsNodes.getLength(); j++) {
					Node node = (Node)childsNodes.item(j);	//判断是否为元素类型 					
					if(node.getNodeType() == Node.ELEMENT_NODE){
						Element childNode = (Element) node;	
						//判断是否name元素
						if ("VersionCode".equals(childNode.getNodeName())) {
							appdetail.setVersionCode(new Integer(childNode.getFirstChild().getNodeValue()));
						}else if ("FileName".equals(childNode.getNodeName())) {
							appdetail.setFileName(childNode.getFirstChild().getNodeValue());
						}else if ("Uri".equals(childNode.getNodeName())) { 
							appdetail.setUri(childNode.getFirstChild().getNodeValue());
						}else if ("History".equals(childNode.getNodeName())) { 
							appdetail.setAppHistory(childNode.getFirstChild().getNodeValue());
						}
					}
				}
			}
			stream.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return appdetail;
	}
	
	/*
	<?xml version="1.0" encoding="UTF-8"?>
	<UpdateApp>
	  <UpdateApp Version="1.01">
	    <VersionCode>2</VersionCode>
	    <FileName>MainApp.apk</FileName>
	    <Uri>http://192.168.1.124/apk/</Uri>
	    <History>Version 1.01</History>
	  </UpdateApp>
	</UpdateApp>
    */

}

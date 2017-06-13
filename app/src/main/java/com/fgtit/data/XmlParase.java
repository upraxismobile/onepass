package com.fgtit.data;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fgtit.utils.ExtApi;

public class XmlParase {
	
	public static String getNodeString(Element em,String name){
		try{ 
			return em.getElementsByTagName(name).item(0).getFirstChild().getNodeValue();
		}catch(Exception e){ 
			
		}
		return "";
	}
	
	public static ArrayList<AdminItem> paraseAdminItem(InputStream inputStream){
		ArrayList<AdminItem> list = new ArrayList<AdminItem>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document doc=null;
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.parse(inputStream);
			Element root = doc.getDocumentElement();
			NodeList items = root.getElementsByTagName("AdminItem");
			for(int i=0;i<items.getLength();i++){
				AdminItem li=new AdminItem();
				Element lin = (Element)items.item(i);
				li.username=getNodeString(lin,"username");
				li.password=getNodeString(lin,"password");
				li.fingerm=getNodeString(lin,"fingerm");
				li.fingers=getNodeString(lin,"fingers");

				li.usertype=Integer.valueOf(getNodeString(lin,"usertype"));
				
				li.realname=getNodeString(lin,"realname");
				li.idcardno=getNodeString(lin,"idcardno");
				li.photo=getNodeString(lin,"photo");
				li.phonemobile=getNodeString(lin,"phonemobile");
				li.phonefix=getNodeString(lin,"phonefix");
				li.email=getNodeString(lin,"email");
				li.address=getNodeString(lin,"address");

				list.add(li);
			}
			//inStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public static ArrayList<RecordItem> paraseRecordItemList(InputStream inputStream){
		ArrayList<RecordItem> list = new ArrayList<RecordItem>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document doc=null;
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.parse(inputStream);
			Element root = doc.getDocumentElement();
			NodeList items = root.getElementsByTagName("RecordItem");
			for(int i=0;i<items.getLength();i++){
				RecordItem ri=new RecordItem();
				Element lin = (Element)items.item(i);
				ri.id=getNodeString(lin,"id");
				ri.name=getNodeString(lin,"name");
				ri.datetime=getNodeString(lin,"datetime");
				ri.lat=getNodeString(lin,"lat");
				ri.lng=getNodeString(lin,"lng");
				ri.worktype=getNodeString(lin,"worktype");
				ri.linetype=getNodeString(lin,"linetype");
				ri.depttype=getNodeString(lin,"depttype");
				ri.type=getNodeString(lin,"type");
				
				list.add(ri);
			}
			//inStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	//WorkItem
	public static ArrayList<WorkItem> paraseWorkItemList(Document doc){
		ArrayList<WorkItem> list = new ArrayList<WorkItem>();
		//DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			//DocumentBuilder builder = factory.newDocumentBuilder();
			Element root = doc.getDocumentElement();
			NodeList items = root.getElementsByTagName("WorkItem");
			if(items.getLength()>0){
				for(int i=0;i<items.getLength();i++){
					WorkItem wi=new WorkItem();
					Element lin = (Element)items.item(i);
					wi.worktype=getNodeString(lin,"worktype");
					wi.workname=getNodeString(lin,"workname");					
					list.add(wi);
				}
			}
			//inStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public static ArrayList<WorkItem> paraseWorkItemList(InputStream inputStream){
		ArrayList<WorkItem> list = new ArrayList<WorkItem>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document doc=null;
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.parse(inputStream);
			Element root = doc.getDocumentElement();
			NodeList items = root.getElementsByTagName("WorkItem");
			if(items.getLength()>0){
				for(int i=0;i<items.getLength();i++){
					WorkItem wi=new WorkItem();
					Element lin = (Element)items.item(i);
					wi.worktype=getNodeString(lin,"worktype");
					wi.workname=getNodeString(lin,"workname");
					list.add(wi);
				}
			}
			//inStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	//LineItem
	public static ArrayList<LineItem> paraseLineItemList(Document doc){
		ArrayList<LineItem> list = new ArrayList<LineItem>();
		//DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			//DocumentBuilder builder = factory.newDocumentBuilder();
			Element root = doc.getDocumentElement();
			NodeList items = root.getElementsByTagName("LineItem");
			if(items.getLength()>0){
				for(int i=0;i<items.getLength();i++){
					LineItem li=new LineItem();
					Element lin = (Element)items.item(i);
					li.linetype=getNodeString(lin,"linetype");
					li.linename=getNodeString(lin,"linename");					
					list.add(li);
				}
			}
			//inStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public static ArrayList<LineItem> paraseLineItemList(InputStream inputStream){
		ArrayList<LineItem> list = new ArrayList<LineItem>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document doc=null;
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.parse(inputStream);
			Element root = doc.getDocumentElement();
			NodeList items = root.getElementsByTagName("LineItem");
			if(items.getLength()>0){
				for(int i=0;i<items.getLength();i++){
					LineItem li=new LineItem();
					Element lin = (Element)items.item(i);
					li.linetype=getNodeString(lin,"linetype");
					li.linename=getNodeString(lin,"linename");
					list.add(li);
				}
			}
			//inStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	//DeptItem
	public static ArrayList<DeptItem> paraseDeptItemList(Document doc){
		ArrayList<DeptItem> list = new ArrayList<DeptItem>();
		//DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			//DocumentBuilder builder = factory.newDocumentBuilder();
			Element root = doc.getDocumentElement();
			NodeList items = root.getElementsByTagName("DeptItem");
			if(items.getLength()>0){
				for(int i=0;i<items.getLength();i++){
					DeptItem di=new DeptItem();
					Element lin = (Element)items.item(i);
					di.depttype=getNodeString(lin,"depttype");
					di.deptname=getNodeString(lin,"deptname");					
					list.add(di);
				}
			}
			//inStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public static ArrayList<DeptItem> paraseDeptItemList(InputStream inputStream){
		ArrayList<DeptItem> list = new ArrayList<DeptItem>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document doc=null;
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.parse(inputStream);
			Element root = doc.getDocumentElement();
			NodeList items = root.getElementsByTagName("DeptItem");
			if(items.getLength()>0){
				for(int i=0;i<items.getLength();i++){
					DeptItem di=new DeptItem();
					Element lin = (Element)items.item(i);
					di.depttype=getNodeString(lin,"depttype");
					di.deptname=getNodeString(lin,"deptname");
					list.add(di);
				}
			}
			//inStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	//UserItem
	public static ArrayList<UserItem> paraseUserItemList(Document doc,boolean bloadphoto){
		ArrayList<UserItem> list = new ArrayList<UserItem>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Element root = doc.getDocumentElement();
			NodeList items = root.getElementsByTagName("UserItem");
			if(items.getLength()>0){
				for(int i=0;i<items.getLength();i++){
					UserItem ri=new UserItem();
					Element lin = (Element)items.item(i);
					ri.id=getNodeString(lin,"id");
					ri.name=getNodeString(lin,"name");					
					ri.worktype=getNodeString(lin,"worktype");
					ri.linetype=getNodeString(lin,"linetype");
					ri.depttype=getNodeString(lin,"depttype");
					ri.type=Integer.valueOf(getNodeString(lin,"type"));
					ri.gender=Integer.valueOf(getNodeString(lin,"gender"));
					ri.statu=Integer.valueOf(getNodeString(lin,"statu"));
					ri.enroldate=getNodeString(lin,"enroldate");
					ri.phone=getNodeString(lin,"phone");
					ri.remark=getNodeString(lin,"remark");
					ri.cardsn=getNodeString(lin,"cardsn");
					ri.template1=getNodeString(lin,"template1");
					ri.template2=getNodeString(lin,"template2");
					ri.barcode1d=getNodeString(lin,"barcode1d");
					ri.barcode2d=getNodeString(lin,"barcode2d");
					
					if(ri.template1.length()>=512)
						ri.bytes1=ExtApi.Base64ToBytes(ri.template1);
					if(ri.template2.length()>=512)
						ri.bytes2=ExtApi.Base64ToBytes(ri.template2);
					
					if(bloadphoto)
						ri.photo=getNodeString(lin,"photo");
					list.add(ri);
				}
			}
			//inStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public static ArrayList<UserItem> paraseUserItemList(InputStream inputStream){
		ArrayList<UserItem> list = new ArrayList<UserItem>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document doc=null;
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.parse(inputStream);
			Element root = doc.getDocumentElement();
			NodeList items = root.getElementsByTagName("UserItem");
			if(items.getLength()>0){
				for(int i=0;i<items.getLength();i++){
					UserItem ri=new UserItem();
					Element lin = (Element)items.item(i);
					ri.id=getNodeString(lin,"id");
					ri.name=getNodeString(lin,"name");
					//ri.worktype=Integer.valueOf(getNodeString(lin,"worktype"));
					//ri.linetype=Integer.valueOf(getNodeString(lin,"linetype"));
					//ri.depttype=Integer.valueOf(getNodeString(lin,"depttype"));
					ri.worktype=getNodeString(lin,"worktype");
					ri.linetype=getNodeString(lin,"linetype");
					ri.depttype=getNodeString(lin,"depttype");
					ri.type=Integer.valueOf(getNodeString(lin,"type"));
					ri.gender=Integer.valueOf(getNodeString(lin,"gender"));
					ri.statu=Integer.valueOf(getNodeString(lin,"statu"));
					ri.enroldate=getNodeString(lin,"enroldate");
					ri.phone=getNodeString(lin,"phone");
					ri.remark=getNodeString(lin,"remark");
					ri.cardsn=getNodeString(lin,"cardsn");
					ri.template1=getNodeString(lin,"template1");
					ri.template2=getNodeString(lin,"template2");
					ri.photo=getNodeString(lin,"photo");
					list.add(ri);
				}
			}
			//inStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public static Document UserItemListToXml(List<UserItem> list){
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder=null;
		try{
		   builder = dbf.newDocumentBuilder();
		}
		catch(Exception e){}
		
		Document doc = builder.newDocument();
		Element root = doc.createElement("UserItem");
		doc.appendChild(root); //将根元素添加到文档上
		
		for(int i=0;i<list.size();i++){
			Element le=doc.createElement("UserItem");
			root.appendChild(le);
			
			Element id=doc.createElement("id");
			le.appendChild(id);
			id.appendChild(doc.createTextNode(list.get(i).id));
			
			Element name=doc.createElement("name");
			le.appendChild(name);
			name.appendChild(doc.createTextNode(list.get(i).name));
			//
			Element worktype=doc.createElement("worktype");
			le.appendChild(worktype);
			worktype.appendChild(doc.createTextNode(String.valueOf(list.get(i).worktype)));
			
			Element linetype=doc.createElement("linetype");
			le.appendChild(linetype);
			linetype.appendChild(doc.createTextNode(String.valueOf(list.get(i).linetype)));
			
			Element depttype=doc.createElement("depttype");
			le.appendChild(depttype);
			depttype.appendChild(doc.createTextNode(String.valueOf(list.get(i).depttype)));
			
			Element type=doc.createElement("type");
			le.appendChild(type);
			type.appendChild(doc.createTextNode(String.valueOf(list.get(i).type)));
			
			Element gender=doc.createElement("gender");
			le.appendChild(gender);
			gender.appendChild(doc.createTextNode(String.valueOf(list.get(i).gender)));
			
			Element statu=doc.createElement("statu");
			le.appendChild(statu);
			statu.appendChild(doc.createTextNode(String.valueOf(list.get(i).statu)));
									
			Element phone=doc.createElement("phone");
			le.appendChild(phone);
			phone.appendChild(doc.createTextNode(list.get(i).phone));
			
			Element enroldate=doc.createElement("enroldate");
			le.appendChild(enroldate);
			enroldate.appendChild(doc.createTextNode(list.get(i).enroldate));
						
			Element remark=doc.createElement("remark");
			le.appendChild(remark);
			remark.appendChild(doc.createTextNode(list.get(i).remark));
			
			Element cardsn=doc.createElement("cardsn");
			le.appendChild(cardsn);
			cardsn.appendChild(doc.createTextNode(list.get(i).cardsn));
			
			Element template1=doc.createElement("template1");
			le.appendChild(template1);
			template1.appendChild(doc.createTextNode(list.get(i).template1));
			
			Element template2=doc.createElement("template2");
			le.appendChild(template2);
			template2.appendChild(doc.createTextNode(list.get(i).template2));
			
			Element photo=doc.createElement("photo");
			le.appendChild(photo);
			photo.appendChild(doc.createTextNode(list.get(i).photo));
			
			Element barcode1d=doc.createElement("barcode1d");
			le.appendChild(barcode1d);
			barcode1d.appendChild(doc.createTextNode(list.get(i).barcode1d));
			
			Element barcode2d=doc.createElement("barcode2d");
			le.appendChild(barcode2d);
			barcode2d.appendChild(doc.createTextNode(list.get(i).barcode2d));
		}
		
		return doc;
	}
	
	public static Document UserItemToXml(UserItem ui){
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder=null;
		try{
		   builder = dbf.newDocumentBuilder();
		}
		catch(Exception e){}
		
		Document doc = builder.newDocument();
		Element root = doc.createElement("UserItem");
		doc.appendChild(root); //将根元素添加到文档上
				
			Element le=doc.createElement("UserItem");
			root.appendChild(le);
			
			Element id=doc.createElement("id");
			le.appendChild(id);
			id.appendChild(doc.createTextNode(ui.id));
			
			Element name=doc.createElement("name");
			le.appendChild(name);
			name.appendChild(doc.createTextNode(ui.name));
			//
			Element worktype=doc.createElement("worktype");
			le.appendChild(worktype);
			worktype.appendChild(doc.createTextNode(String.valueOf(ui.worktype)));
			
			Element linetype=doc.createElement("linetype");
			le.appendChild(linetype);
			linetype.appendChild(doc.createTextNode(String.valueOf(ui.linetype)));
			
			Element depttype=doc.createElement("depttype");
			le.appendChild(depttype);
			depttype.appendChild(doc.createTextNode(String.valueOf(ui.depttype)));
			
			Element type=doc.createElement("type");
			le.appendChild(type);
			type.appendChild(doc.createTextNode(String.valueOf(ui.type)));
			
			Element gender=doc.createElement("gender");
			le.appendChild(gender);
			gender.appendChild(doc.createTextNode(String.valueOf(ui.gender)));
			
			Element statu=doc.createElement("statu");
			le.appendChild(statu);
			statu.appendChild(doc.createTextNode(String.valueOf(ui.statu)));
									
			Element phone=doc.createElement("phone");
			le.appendChild(phone);
			phone.appendChild(doc.createTextNode(ui.phone));
			
			Element enroldate=doc.createElement("enroldate");
			le.appendChild(enroldate);
			enroldate.appendChild(doc.createTextNode(ui.enroldate));
						
			Element remark=doc.createElement("remark");
			le.appendChild(remark);
			remark.appendChild(doc.createTextNode(ui.remark));
			
			Element cardsn=doc.createElement("cardsn");
			le.appendChild(cardsn);
			cardsn.appendChild(doc.createTextNode(ui.cardsn));
			
			Element template1=doc.createElement("template1");
			le.appendChild(template1);
			template1.appendChild(doc.createTextNode(ui.template1));
			
			Element template2=doc.createElement("template2");
			le.appendChild(template2);
			template2.appendChild(doc.createTextNode(ui.template2));
			
			Element photo=doc.createElement("photo");
			le.appendChild(photo);
			photo.appendChild(doc.createTextNode(ui.photo));
			
			Element barcode1d=doc.createElement("barcode1d");
			le.appendChild(barcode1d);
			barcode1d.appendChild(doc.createTextNode(ui.barcode1d));
			
			Element barcode2d=doc.createElement("barcode2d");
			le.appendChild(barcode2d);
			barcode2d.appendChild(doc.createTextNode(ui.barcode2d));
				
		return doc;
	}
	
	//XML File Write
	public static void WriteXmlFile(Document doc,String filename,String codetype) {
		   try {
			   FileOutputStream fos = new FileOutputStream(filename);
			   OutputStreamWriter outwriter = new OutputStreamWriter(fos);
			   try {
					Source source = new DOMSource(doc);
					Result result = new StreamResult(outwriter);
					Transformer xformer = TransformerFactory.newInstance().newTransformer();
					xformer.setOutputProperty(OutputKeys.ENCODING, codetype);	//"gb2312" "utf-8"
					xformer.transform(source, result);
				}
				catch (TransformerConfigurationException e) {
					e.printStackTrace();
				}
				catch (TransformerException e) {
					e.printStackTrace();
				}
			   outwriter.close();
			   fos.close();
		   } catch (Exception e) {
			   e.printStackTrace();
		   }
	}
	
	//XML File Read
	public static Document ReadXmlFile(String filename){
		FileInputStream fin=null;
		InputStream inStream=null;
		Document doc=null;
		try {
			fin = new FileInputStream(filename);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}		
		inStream = new BufferedInputStream(fin);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			//从字符串读
			//ByteArrayInputStream is = new ByteArrayInputStream(txt.getBytes());
			doc = builder.parse(inStream);			
			//inStream.close();			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return doc;
	}
		
}

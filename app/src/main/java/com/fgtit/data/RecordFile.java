package com.fgtit.data;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class RecordFile {
	
	public static void CreateFile(String fileName){
		new File(fileName);
	}
	
	public static void AppendToFile(String fileName, RecordItem rs) {
		try {
			
			// 打开一个随机访问文件流，按读写方式
			RandomAccessFile randomFile = new RandomAccessFile(fileName, "rw");
			// 文件长度，字节数
			long fileLength = randomFile.length();
			// 	将写文件指针移到文件尾。
			randomFile.seek(fileLength);
			
			byte[] content=new byte[124];
			System.arraycopy(rs.id.getBytes(), 0, content, 0, rs.id.getBytes().length);
			System.arraycopy(rs.name.getBytes(), 0, content, 16, rs.name.getBytes().length);
			System.arraycopy(rs.datetime.getBytes(), 0, content, 32, rs.datetime.getBytes().length);
			System.arraycopy(rs.lat.getBytes(), 0, content, 64, rs.lat.getBytes().length);
			System.arraycopy(rs.lng.getBytes(), 0, content, 80, rs.lng.getBytes().length);
			System.arraycopy(rs.type.getBytes(), 0, content, 96, rs.type.getBytes().length);
			
			System.arraycopy(rs.type.getBytes(), 0, content, 100, rs.worktype.getBytes().length);
			System.arraycopy(rs.type.getBytes(), 0, content, 108, rs.linetype.getBytes().length);
			System.arraycopy(rs.type.getBytes(), 0, content, 116, rs.depttype.getBytes().length);
			
			randomFile.write(content);
			randomFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static ArrayList<RecordItem> ReadFromFile(String fileName){
		ArrayList<RecordItem> list=new ArrayList<RecordItem>();
		try {
			// 打开一个随机访问文件流，按读写方式
			RandomAccessFile randomFile = new RandomAccessFile(fileName, "rw");
			// 文件长度，字节数
			long fileLength = randomFile.length();
			long count=fileLength/124;
			for(long i=0;i<count;i++){
				byte[] content=new byte[124];
				randomFile.read(content);				
				RecordItem rc=new RecordItem();
				rc.id=new String(content, 0, 16);
				rc.id=rc.id.replaceAll("\\s","");
				rc.name=new String(content, 16, 16);
				rc.name=rc.name.replaceAll("\\s","");
				rc.datetime=new String(content, 32, 32);
				rc.datetime=rc.datetime.replaceAll("\\s","");
				rc.lat=new String(content, 64, 16);
				rc.lat=rc.lat.replaceAll("\\s","");
				rc.lng=new String(content, 80, 16);
				rc.lng=rc.lng.replaceAll("\\s","");
				rc.type=new String(content, 96, 4);
				rc.type=rc.type.replaceAll("\\s","");
				
				rc.worktype=new String(content, 100, 8);
				rc.worktype=rc.worktype.replaceAll("\\s","");
				rc.linetype=new String(content, 108, 8);
				rc.linetype=rc.linetype.replaceAll("\\s","");
				rc.depttype=new String(content, 116, 8);
				rc.depttype=rc.depttype.replaceAll("\\s","");
				
				list.add(rc);
			}
			randomFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static boolean IsFileExists(String filename){
		File f=new File(filename);
		if(f.exists()){
			return true;
		}
		return false;
	}
	
	public static void DeleteFile(String filename){
		File f=new File(filename);
		if(f.exists()){
			f.delete();
		} 
	}
	
	public static void ReCreate(String filename){
		DeleteFile(filename);
		CreateFile(filename);
	}
}

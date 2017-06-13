package com.fgtit.data;

public class UserItem {
	public String 	id="";			//工号
	public String 	name="";		//姓名
	public String 	worktype="";	//工站
	public String	linetype="";	//线别
	public String 	depttype="";	//部门
	public int		type=0;			//类型
	public int		gender=0;		//性别
	public int		statu=0;		//状态
	public String	enroldate="";		//入职时间
	public String	phone="";		//电话
	public String	remark="";		//备注
	public String	cardsn="";		//卡号
	public String 	template1="";	//指纹
	public String 	template2="";	//指纹
	public String  	photo="";		//照片
	public String 	barcode1d="";	//指纹
	public String  	barcode2d="";		//照片
	
	public byte[] 	bytes1=null;	//指纹
	public byte[] 	bytes2=null;	//指纹
}

package android.fpi;

import android.content.Context;
import android.widget.Toast;

public class MtRfid {
	//MF522命令字
	private final int PCD_IDLE              =0x00;               //取消当前命令
	private final int PCD_AUTHENT           =0x0E;               //验证密钥
	private final int PCD_RECEIVE           =0x08;               //接收数据
	private final int PCD_TRANSMIT          =0x04;               //发送数据
	private final int PCD_TRANSCEIVE        =0x0C;               //发送并接收数据
	private final int PCD_RESETPHASE        =0x0F;               //复位
	private final int PCD_CALCCRC           =0x03;               //CRC计算

	//Mifare_One卡片命令字
	private final int PICC_REQIDL           =0x26;               //寻天线区内未进入休眠状态
	private final int PICC_REQALL           =0x52;               //寻天线区内全部卡
	private final int PICC_ANTICOLL1        =0x93;               //防冲撞
	private final int PICC_ANTICOLL2        =0x95;               //防冲撞
	private final int PICC_AUTHENT1A        =0x60;               //验证A密钥
	private final int PICC_AUTHENT1B        =0x61;               //验证B密钥
	private final int PICC_READ             =0x30;               //读块
	private final int PICC_WRITE            =0xA0;               //写块
	private final int PICC_DECREMENT        =0xC0;               //扣款
	private final int PICC_INCREMENT        =0xC1;               //充值
	private final int PICC_RESTORE          =0xC2;               //调块数据到缓冲区
	private final int PICC_TRANSFER         =0xB0;               //保存缓冲区中数据
	private final int PICC_HALT             =0x50;               //休眠

	//MF522 FIFO长度定义
	private final int DEF_FIFO_LENGTH       =64;                 //FIFO size=64byte
	
	//MF522寄存器定义
	//PAGE 0
	private final int RFU00                 =0x00;    
	private final int CommandReg            =0x01;    
	private final int ComIEnReg             =0x02;    
	private final int DivlEnReg             =0x03;    
	private final int ComIrqReg             =0x04;    
	private final int DivIrqReg             =0x05;
	private final int ErrorReg              =0x06;    
	private final int Status1Reg            =0x07;    
	private final int Status2Reg            =0x08;    
	private final int FIFODataReg           =0x09;
	private final int FIFOLevelReg          =0x0A;
	private final int WaterLevelReg         =0x0B;
	private final int ControlReg            =0x0C;
	private final int BitFramingReg         =0x0D;
	private final int CollReg               =0x0E;
	private final int RFU0F                 =0x0F;
	//PAGE 1     
	private final int RFU10                 =0x10;
	private final int ModeReg               =0x11;
	private final int TxModeReg             =0x12;
	private final int RxModeReg             =0x13;
	private final int TxControlReg          =0x14;
	private final int TxAutoReg             =0x15;
	private final int TxSelReg              =0x16;
	private final int RxSelReg              =0x17;
	private final int RxThresholdReg        =0x18;
	private final int DemodReg              =0x19;
	private final int RFU1A                 =0x1A;
	private final int RFU1B                 =0x1B;
	private final int MifareReg             =0x1C;
	private final int RFU1D                 =0x1D;;
	private final int RFU1E                 =0x1E;
	private final int SerialSpeedReg        =0x1F;
	//PAGE 2    
	private final int RFU20                 =0x20;  
	private final int CRCResultRegM         =0x21;
	private final int CRCResultRegL         =0x22;
	private final int RFU23                 =0x23;
	private final int ModWidthReg           =0x24;
	private final int RFU25                 =0x25;
	private final int RFCfgReg              =0x26;
	private final int GsNReg                =0x27;
	private final int CWGsCfgReg            =0x28;
	private final int ModGsCfgReg           =0x29;
	private final int TModeReg              =0x2A;
	private final int TPrescalerReg         =0x2B;
	private final int TReloadRegH           =0x2C;
	private final int TReloadRegL           =0x2D;
	private final int TCounterValueRegH     =0x2E;
	private final int TCounterValueRegL     =0x2F;
	//PAGE 3      
	private final int RFU30                 =0x30;
	private final int TestSel1Reg           =0x31;
	private final int TestSel2Reg           =0x32;
	private final int TestPinEnReg          =0x33;
	private final int TestPinValueReg       =0x34;
	private final int TestBusReg            =0x35;
	private final int AutoTestReg           =0x36;
	private final int VersionReg            =0x37;
	private final int AnalogTestReg         =0x38;
	private final int TestDAC1Reg           =0x39; 
	private final int TestDAC2Reg           =0x3A;   
	private final int TestADCReg            =0x3B;  
	private final int RFU3C                 =0x3C;   
	private final int RFU3D                 =0x3D;   
	private final int RFU3E                 =0x3E;   
	private final int RFU3F		  			=0x3F;

	//和MF522通讯时返回的错误代码
	private final int MI_OK                 =0;
	private final int MI_NOTAGERR           =(-1);
	private final int MI_ERR                =(-2);

	//private final int MAXRLEN 			=18;
	private final int MAXRLEN 				=256;
	
	private Context	pContext=null;
	
	private MtGpio mt=null;
		
	public void SetContext(Context context){
		pContext=context;		
	}
	
	public void ShowToast(String txt){
		if(pContext!=null)
			Toast.makeText(pContext, txt, Toast.LENGTH_SHORT).show();
	}
	
	private void Sleep(int n){
		try {
			Thread.currentThread();
			Thread.sleep(n);
		}catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	private void SleepNs(int n){
		/*
		try {
			Thread.currentThread();
			Thread.sleep(0,n);
		}catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		*/
	}
	
	private void RfidEnable(){
		
		mt.RFInit();
		
		mt.RFCS(1);
		mt.RFReset(1);
		Sleep(100);
		mt.RFReset(0);
		mt.RFCS(0);
		mt.RFCLK(0);
	}
	
	private void RfidDisable(){
		mt.RFCS(1);
		mt.RFReset(1);
	}
	
	private void RfidWrite(int c){
		//mt.RFCS(0);
		for(int i=0;i<8;i++){
			if((c & 0x80)>0)
				mt.RFSet(1);
			else
				mt.RFSet(0);
			SleepNs(1);
			mt.RFCLK(1);
			SleepNs(1);
			mt.RFCLK(0);
			SleepNs(1);
			c<<=1;
		}
		//mt.RFCS(1);
	}
	
	private int RfidRead(){
		//mt.RFCS(0);
		int r=0;
		for(int i=0;i<8;i++){
			mt.RFCLK(1);
			SleepNs(1);
			if(mt.RFGet()>0)
				r|=0x01;
			if(i<7)
				r<<=0x01;
			SleepNs(1);
			mt.RFCLK(0);
			SleepNs(1);
		}
		//mt.RFCS(1);
		return r;
	}
	
	//功    能：读RC632寄存器
	//参数说明：Address[IN]:寄存器地址
	//返    回：读出的值
	private int ReadRawRC(int address){
		mt.RFCS(0);
		
		//RfidWrite(address);
		RfidWrite(((address & 0x3F) << 1) | 0x80);
		int r=RfidRead();
		
		//if(r>0){
		//	ShowToast(String.valueOf(r));
		//}
		
		mt.RFCS(1);
		return r;
	}
	
	//功    能：写RC632寄存器
	//参数说明：Address[IN]:寄存器地址
	//	  value[IN]:写入的值
	private void WriteRawRC(int address,int value){
		mt.RFCS(0);
		
		//RfidWrite(address);
		RfidWrite((address<<1) & 0x7F);	//0x7E
		RfidWrite(value);
		
		mt.RFCS(1);
	}
	
	//功    能：置RC522寄存器位
	//参数说明：reg[IN]:寄存器地址
	//         	mask[IN]:置位值
	private void SetBitMask(int reg,int mask)  
	{
	    int tmp = 0x0;
	    tmp = ReadRawRC(reg);
	    WriteRawRC(reg,tmp | mask);  // set bit mask
	}
	
	//功    能：清RC522寄存器位
	//参数说明：reg[IN]:寄存器地址
	//          mask[IN]:清位值
	private void ClearBitMask(int reg,int mask)  
	{
	    int tmp = 0x0;
	    tmp = ReadRawRC(reg);
	    WriteRawRC(reg, tmp & ~mask);  // clear bit mask
	} 

	//用MF522计算CRC16函数
	public void CalulateCRC(int[] pIndata,int len,int[] pOutData,int outoffset){
		int i,n;
		ClearBitMask(DivIrqReg,0x04);
		WriteRawRC(CommandReg,PCD_IDLE);
		SetBitMask(FIFOLevelReg,0x80);
		for (i=0; i<len; i++){
			WriteRawRC(FIFODataReg,pIndata[i]);
		}
		WriteRawRC(CommandReg, PCD_CALCCRC);
		i = 0xFF;
		do {
			n = ReadRawRC(DivIrqReg);
			i--;
		}
		//while ((i!=0) && !(n&0x04));
		while ((i!=0) && (n&0x04)==0);
		pOutData[outoffset+0] = ReadRawRC(CRCResultRegL);
		pOutData[outoffset+1] = ReadRawRC(CRCResultRegM);
	}
	

	//功    能：命令卡片进入休眠状态
	//返    回: 成功返回MI_OK
	public int PcdHalt(){
		int status;
		int[] unLen=new int[1];
		int[] ucComMF522Buf=new int[MAXRLEN]; 

		ucComMF522Buf[0] = PICC_HALT;
		ucComMF522Buf[1] = 0;
		CalulateCRC(ucComMF522Buf,2,ucComMF522Buf,2);
		status = PcdComMF522(PCD_TRANSCEIVE,ucComMF522Buf,4,ucComMF522Buf,unLen);
		return status;
	}

	//功    能：复位RC522
	//返    回: 成功返回MI_OK
	public int PcdReset(){
		WriteRawRC(CommandReg,PCD_RESETPHASE);
		Sleep(10);
		
	    WriteRawRC(ModeReg,0x3D);
	    WriteRawRC(TReloadRegL,30);
	    WriteRawRC(TReloadRegH,0);
	    WriteRawRC(TModeReg,0x8D);
	    WriteRawRC(TPrescalerReg,0x3E);
	   
		WriteRawRC(TxAutoReg,0x40);  
		return MI_OK;
	}
	
	//关闭天线
	public void PcdAntennaOff()
	{
	    ClearBitMask(TxControlReg, 0x03);
	}
	
	//开启天线  
	//每次启动或关闭天险发射之间应至少有1ms的间隔
	public void PcdAntennaOn()
	{
	    int i;
	    i = ReadRawRC(TxControlReg);
	    //if (!(i & 0x03))
	    if ((i & 0x03)==0)
	    {
	        SetBitMask(TxControlReg, 0x03);
	    }
	}
	
	//设置RC632的工作方式
	public int M500PcdConfigISOType()
	{
		ClearBitMask(Status2Reg, 0x08);  //清MFCrypto1On
		WriteRawRC(ModeReg, 0x3D);       //CRC初始值0x6363
		/* Modulation signal from the internal analog part, default. */
		WriteRawRC(RxSelReg,0x86); 
		WriteRawRC(RFCfgReg,0x7F);    //RxGain=48dB
		WriteRawRC(TReloadRegL, 30);  //定时器重装值           
		WriteRawRC(TReloadRegH, 0);
		WriteRawRC(TModeReg, 0x8D);   		//TAuto=1，及定时器预分频高4位
		WriteRawRC(TPrescalerReg, 0x3E);  //定时器预分频低8位
		WriteRawRC(TxAutoReg, 0x40);      //Force100ASK(必须要)
		PcdAntennaOn();    //打开天线
	   return MI_OK;
	}
	
	//功    能：通过RC522和ISO14443卡通讯
	//参数说明：Command[IN]:RC522命令字
	//          pInData[IN]:通过RC522发送到卡片的数据
	//          InLenByte[IN]:发送数据的字节长度
	//          pOutData[OUT]:接收到的卡片返回数据
	//          *pOutLenBit[OUT]:返回数据的位长度
	public int PcdComMF522(int Command,int[] pInData,int InLenByte,int[] pOutData,int[] pOutLenBit)
	{
		int status = MI_ERR;
		int irqEn   = 0x00;
		int waitFor = 0x00;
		int lastBits;
		int n;
		int i;
		switch (Command)
		{
		case PCD_AUTHENT:
			irqEn   = 0x12;
			waitFor = 0x10;
			break;
		case PCD_TRANSCEIVE:
			irqEn   = 0x77;
			waitFor = 0x30;
			break;
		default:
			break;
		}
		WriteRawRC(ComIEnReg,irqEn|0x80);
		ClearBitMask(ComIrqReg,0x80);
		WriteRawRC(CommandReg,PCD_IDLE);
		SetBitMask(FIFOLevelReg,0x80);

		for (i=0; i<InLenByte; i++){
			WriteRawRC(FIFODataReg, pInData[i]);
		}
		WriteRawRC(CommandReg, Command);

		if (Command == PCD_TRANSCEIVE){    
			SetBitMask(BitFramingReg,0x80);
		}
			//i = 600;//根据时钟频率调整，操作M1卡最大等待时间25ms
			i=800;
			do {
				n = ReadRawRC(ComIrqReg);
				i--;
			}
			//while ((i!=0) && !(n&0x01) && !(n&waitFor));
			while ((i!=0) && (n&0x01)==0 && (n&waitFor)==0);
			ClearBitMask(BitFramingReg,0x80);
     
			if (i!=0){    
				if((ReadRawRC(ErrorReg)&0x1B)==0){
					status = MI_OK;
					if ((n & irqEn & 0x01)>0){
						status = MI_NOTAGERR;
					}
					if (Command == PCD_TRANSCEIVE){
						n = ReadRawRC(FIFOLevelReg);
						lastBits = ReadRawRC(ControlReg) & 0x07;
						if (lastBits>0){
							pOutLenBit[0] = (n-1)*8 + lastBits;
						}else{
							pOutLenBit[0] = n*8;
						}
						if (n == 0){
							n = 1;
						}
						if (n > MAXRLEN){
							n = MAXRLEN;
						}
						for (i=0; i<n; i++)
						{   
							pOutData[i] = ReadRawRC(FIFODataReg);
						}
					}
				}else{
					status = MI_ERR;
				}
			}
			SetBitMask(ControlReg,0x80);           // stop timer now
			WriteRawRC(CommandReg,PCD_IDLE); 
			return status;
	}
	
	//功    能：寻卡
	//参数说明: req_code[IN]:寻卡方式
	//0x52 = 寻感应区内所有符合14443A标准的卡
	//0x26 = 寻未进入休眠状态的卡
	//pTagType[OUT]：卡片类型代码
	//0x4400 = Mifare_UltraLight
	//0x0400 = Mifare_One(S50)
	//0x0200 = Mifare_One(S70)
	//0x0800 = Mifare_Pro(X)
	//0x4403 = Mifare_DESFire
	//返    回: 成功返回MI_OK
	public int PcdRequest(int req_code,int[] pTagType)
	{
		   int status;  
		   int[] unLen=new int[1];
		   int[] ucComMF522Buf=new int[MAXRLEN]; 

		   ClearBitMask(Status2Reg,0x08);
		   WriteRawRC(BitFramingReg,0x07);
		   SetBitMask(TxControlReg,0x03);
		 
		   ucComMF522Buf[0] = req_code;

		   status = PcdComMF522(PCD_TRANSCEIVE,ucComMF522Buf,1,ucComMF522Buf,unLen);
		   
		   if ((status == MI_OK) && (unLen[0] == 0x10)){    
			   pTagType[0] = ucComMF522Buf[0];
			   pTagType[1] = ucComMF522Buf[1];
		   }else{
			   status = MI_ERR;
		   }
		   
		   return status;
	}
	
	//功    能：防冲撞
	//参数说明: pSnr[OUT]:卡片序列号，4字节
	//返    回: 成功返回MI_OK
	public int PcdAnticoll(int[] pSnr)
	{
	    int status;
	    int i,snr_check=0;
	    int[]  unLen=new int[1];
	    int[] ucComMF522Buf=new int[MAXRLEN]; 
	    

	    ClearBitMask(Status2Reg,0x08);
	    WriteRawRC(BitFramingReg,0x00);
	    ClearBitMask(CollReg,0x80);
	 
	    ucComMF522Buf[0] = PICC_ANTICOLL1;
	    ucComMF522Buf[1] = 0x20;

	    status = PcdComMF522(PCD_TRANSCEIVE,ucComMF522Buf,2,ucComMF522Buf,unLen);

	    if (status == MI_OK){
	    	 for (i=0; i<4; i++){   
	    		 pSnr[i]  = ucComMF522Buf[i];
	             snr_check ^= ucComMF522Buf[i];
	         }
	         if (snr_check != ucComMF522Buf[i]){   
	        	 status = MI_ERR;    
	         }
	    }
	    
	    SetBitMask(CollReg,0x80);
	    return status;
	}
	
	//功    能：选定卡片
	//参数说明: pSnr[IN]:卡片序列号，4字节
	//返    回: 成功返回MI_OK
	public int PcdSelect(int[] pSnr)
	{
		int status;
		int i;
		int[] unLen=new int[1];
		int[] ucComMF522Buf=new int[MAXRLEN]; 

		ucComMF522Buf[0] = PICC_ANTICOLL1;
		ucComMF522Buf[1] = 0x70;
		ucComMF522Buf[6] = 0;
		for (i=0; i<4; i++)
		{
			ucComMF522Buf[i+2] = pSnr[i];
			ucComMF522Buf[6]  ^= pSnr[i];
		}
		CalulateCRC(ucComMF522Buf,7,ucComMF522Buf,7);
		ClearBitMask(Status2Reg,0x08);
		status = PcdComMF522(PCD_TRANSCEIVE,ucComMF522Buf,9,ucComMF522Buf,unLen);

		if ((status == MI_OK) && (unLen[0] == 0x18)){
			status = MI_OK;
		}else{
			status = MI_ERR;
		}

		return status;
	}
	
	//功    能：验证卡片密码
	//参数说明: auth_mode[IN]: 密码验证模式
	//0x60 = 验证A密钥
	//0x61 = 验证B密钥 
	//addr[IN]：块地址
	//pKey[IN]：密码
	//pSnr[IN]：卡片序列号，4字节
	//返    回: 成功返回MI_OK             
	public int PcdAuthState(int auth_mode,int addr,int[] pKey,int[] pSnr){
		int  status,i;
		int[] unLen=new int[1];
		int[] ucComMF522Buf=new int[MAXRLEN]; 

		ucComMF522Buf[0] = auth_mode;
		ucComMF522Buf[1] = addr;
		for (i=0; i<6; i++){
			ucComMF522Buf[i+2] = pKey[i];
		}
		//for (i=0; i<6; i++)
		for (i=0; i<4; i++)
		{
			ucComMF522Buf[i+8] = pSnr[i];
		}
		status = PcdComMF522(PCD_AUTHENT,ucComMF522Buf,12,ucComMF522Buf,unLen);
		//if ((status != MI_OK) || (!(ReadRawRC(Status2Reg) & 0x08))){
		if ((status != MI_OK) || ((ReadRawRC(Status2Reg) & 0x08)==0)){
			status = MI_ERR;
		}
		return status;
	}
	
	//功    能：读取M1卡一块数据
	//参数说明: addr[IN]：块地址
	//pData[OUT]：读出的数据，16字节
	//返    回: 成功返回MI_OK
	public int PcdRead(int addr,int[] pData,int offset){
		int status,i;
		int[] unLen=new int[1];
		int[] ucComMF522Buf=new int[MAXRLEN]; 

		ucComMF522Buf[0] = PICC_READ;
		ucComMF522Buf[1] = addr;
		CalulateCRC(ucComMF522Buf,2,ucComMF522Buf,2);

		status = PcdComMF522(PCD_TRANSCEIVE,ucComMF522Buf,4,ucComMF522Buf,unLen);

		if ((status == MI_OK) && (unLen[0] == 0x90)){
			for (i=0; i<16; i++){
				pData[i+offset] = ucComMF522Buf[i];
			}
		}else{
			status = MI_ERR;
		}
		return status;
	}
	

	//功    能：写数据到M1卡一块
	//参数说明: addr[IN]：块地址
	//pData[IN]：写入的数据，16字节
	//返    回: 成功返回MI_OK                  
	public int PcdWrite(int addr,int[] pData,int offset)
	{
		int status,i;
		int[] unLen=new int[1];
		int[] ucComMF522Buf=new int[MAXRLEN]; 

		ucComMF522Buf[0] = PICC_WRITE;
		ucComMF522Buf[1] = addr;
		CalulateCRC(ucComMF522Buf,2,ucComMF522Buf,2);

		status = PcdComMF522(PCD_TRANSCEIVE,ucComMF522Buf,4,ucComMF522Buf,unLen);

		if ((status != MI_OK) || (unLen[0] != 4) || ((ucComMF522Buf[0] & 0x0F) != 0x0A)){
			status = MI_ERR;
		}
		if (status == MI_OK){
			for (i=0; i<16; i++){
				ucComMF522Buf[i] = pData[i+offset];
			}
			CalulateCRC(ucComMF522Buf,16,ucComMF522Buf,16);
			status = PcdComMF522(PCD_TRANSCEIVE,ucComMF522Buf,18,ucComMF522Buf,unLen);
			if ((status != MI_OK) || (unLen[0] != 4) || ((ucComMF522Buf[0] & 0x0F) != 0x0A)){
				status = MI_ERR;
			}
		}
		return status;
	}
	

	//功    能：扣款和充值
	//参数说明: dd_mode[IN]：命令字
	//0xC0 = 扣款
	//0xC1 = 充值
	//addr[IN]：钱包地址
	//pValue[IN]：4字节增(减)值，低位在前
	//返    回: 成功返回MI_OK                
	public int PcdValue(int dd_mode,int addr,int[] pValue)
	{
		int status,i;
		int[] unLen=new int[1];
		int[] ucComMF522Buf=new int[MAXRLEN]; 

		ucComMF522Buf[0] = dd_mode;
		ucComMF522Buf[1] = addr;
		CalulateCRC(ucComMF522Buf,2,ucComMF522Buf,2);
		
		status = PcdComMF522(PCD_TRANSCEIVE,ucComMF522Buf,4,ucComMF522Buf,unLen);

		if ((status != MI_OK) || (unLen[0] != 4) || ((ucComMF522Buf[0] & 0x0F) != 0x0A)){
			status = MI_ERR;
		}
		if (status == MI_OK){
			for (i=0; i<16; i++){
				ucComMF522Buf[i] = pValue[i];
			}
			CalulateCRC(ucComMF522Buf,4,ucComMF522Buf,4);
			unLen[0] = 0;
			status = PcdComMF522(PCD_TRANSCEIVE,ucComMF522Buf,6,ucComMF522Buf,unLen);
			if (status != MI_ERR){
				status = MI_OK;
			}
		}
		if (status == MI_OK){
			ucComMF522Buf[0] = PICC_TRANSFER;
			ucComMF522Buf[1] = addr;
			CalulateCRC(ucComMF522Buf,2,ucComMF522Buf,2); 
			
			status = PcdComMF522(PCD_TRANSCEIVE,ucComMF522Buf,4,ucComMF522Buf,unLen);
			if ((status != MI_OK) || (unLen[0] != 4) || ((ucComMF522Buf[0] & 0x0F) != 0x0A)){
				status = MI_ERR;
			}
		}
		return status;
	}
	
	//功    能：备份钱包
	//参数说明: sourceaddr[IN]：源地址
	//goaladdr[IN]：目标地址
	//返    回: 成功返回MI_OK
	public int PcdBakValue(int sourceaddr, int goaladdr){
		int status;
		int[] unLen=new int[1];
		int[] ucComMF522Buf=new int[MAXRLEN]; 

		ucComMF522Buf[0] = PICC_RESTORE;
		ucComMF522Buf[1] = sourceaddr;
		CalulateCRC(ucComMF522Buf,2,ucComMF522Buf,2);

		status = PcdComMF522(PCD_TRANSCEIVE,ucComMF522Buf,4,ucComMF522Buf,unLen);
		if ((status != MI_OK) || (unLen[0] != 4) || ((ucComMF522Buf[0] & 0x0F) != 0x0A)){
			status = MI_ERR;
		}
		if (status == MI_OK){
			ucComMF522Buf[0] = 0;
			ucComMF522Buf[1] = 0;
			ucComMF522Buf[2] = 0;
			ucComMF522Buf[3] = 0;
			CalulateCRC(ucComMF522Buf,4,ucComMF522Buf,4);
			
			status = PcdComMF522(PCD_TRANSCEIVE,ucComMF522Buf,6,ucComMF522Buf,unLen);
			if (status != MI_ERR){
				status = MI_OK;
			}
		}
		if (status != MI_OK){
			return MI_ERR;
		}

		ucComMF522Buf[0] = PICC_TRANSFER;
		ucComMF522Buf[1] = goaladdr;
		CalulateCRC(ucComMF522Buf,2,ucComMF522Buf,2);

		status = PcdComMF522(PCD_TRANSCEIVE,ucComMF522Buf,4,ucComMF522Buf,unLen);

		if ((status != MI_OK) || (unLen[0] != 4) || ((ucComMF522Buf[0] & 0x0F) != 0x0A)){
			status = MI_ERR;
		}
		return status;
	}

	/////////////////////////////////////////////////////////////////////
	public void RfidInit(){
		if(mt==null)
			mt=new MtGpio();
		
		RfidEnable();
				
		PcdReset();
		PcdAntennaOff(); 
		Sleep(10);
		PcdAntennaOn();  
		M500PcdConfigISOType();
	}
	
	public void RfidClose(){
		RfidDisable();
	}
	
	public int RfidGetSn(int[] sn){
		int status;
		status = PcdRequest(PICC_REQALL, sn);
		if (status== MI_OK){    			
			status = PcdAnticoll(sn);
			if (status == MI_OK){
				return 0;
			}
		}
		return -1;
	}
	
	public int RfidReadFullCard(int[] sn,int[] buffer,int length){
		int status,i,m;
		int p=0;
		int sector;
		int tpcount=0;
		int[] key=new int[6];
		for(int n=0;n<6;n++){
			key[n]=0xff;
		}
		status=0;
		status = PcdSelect(sn);
		if (status == MI_OK){
			//S70卡前面32个区是可读3个块
			for(i=0;i<32;i++){					
				sector=i*4;	
				for(m=0;m<3;m++){
					status=PcdAuthState(PICC_AUTHENT1A,(m+sector),key, sn);
					if(status != MI_OK)
						return -4;
					if(i!=0||m!=0){
						status=PcdRead((m+sector), buffer,p);
						if(status>0)
							return -5;
						p+=16;
						if(p>length)
							return 0;
					}
				}
			}
			//S70卡前后8个区是可读15个块(只写7区)
			for(i=0;i<7;i++){					
				sector=i*16+32*4;
				for(m=0;m<15;m++){
					status=PcdAuthState(PICC_AUTHENT1A,(m+sector),key,sn);
					if(status>0)
						return -4;
					status=PcdRead((m+sector), buffer,p);
					if(status>0)
						return -5;
					p+=16;
					if(p>length)
						return 0;
				}
			}
		}
		return -3;
	}
	
	public int RfidWriteFullCard(int[] sn,int[] buffer,int length){
		int status,i,m,p,sector;
		int[] key=new int[6];
		for(int n=0;n<6;n++){
			key[n]=0xff;
		}
		status=0;
		status = PcdSelect(sn);
		if (status == MI_OK){
			//p=16;	//前面的卡不能写
			p=0;
			//S70卡前面32个区是可读3个块
			for(i=0;i<32;i++){					
				sector=i*4;	
				for(m=0;m<3;m++){
					status=PcdAuthState(PICC_AUTHENT1A,(m+sector),key, sn);
					if(status != MI_OK){
						ShowToast("错误");
						return -4;
					}
					if(i!=0||m!=0)
					{
						status=PcdWrite((m+sector), buffer,p);
						if(status>0)
							return -5;
						p+=16;
						if(p>length)
							return 0;
					}
				}
			}
			//S70卡前后8个区是可读15个块(只写7区)
			for(i=0;i<7;i++){					
				sector=i*16+32*4;
				for(m=0;m<15;m++){
					status=PcdAuthState(PICC_AUTHENT1A,(m+sector),key,sn);
					if(status>0)
						return -4;
					status=PcdWrite((m+sector), buffer,p);
					if(status>0)
						return -5;
					p+=16;
					if(p>length)
						return 0;
				}
			}
		}		
		return -3;	
	}
	
	public byte[] IntArrayToByteArray(int[] pint,int size){
		byte[] result = new byte[size];
		for(int i=0;i<size;i++){
			result[i]=(byte)(pint[i] & 0xFF);
		}
		return result;
	}
	
	public int[] ByteArrayToIntArray(byte[] pint,int size){
		int[] result = new int[size];
		for(int i=0;i<size;i++){
			result[i]=pint[i];
		}
		return result;
	}
	
}

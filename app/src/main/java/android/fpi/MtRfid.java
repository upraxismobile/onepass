package android.fpi;

import android.content.Context;
import android.widget.Toast;

public class MtRfid {
	//MF522������
	private final int PCD_IDLE              =0x00;               //ȡ����ǰ����
	private final int PCD_AUTHENT           =0x0E;               //��֤��Կ
	private final int PCD_RECEIVE           =0x08;               //��������
	private final int PCD_TRANSMIT          =0x04;               //��������
	private final int PCD_TRANSCEIVE        =0x0C;               //���Ͳ���������
	private final int PCD_RESETPHASE        =0x0F;               //��λ
	private final int PCD_CALCCRC           =0x03;               //CRC����

	//Mifare_One��Ƭ������
	private final int PICC_REQIDL           =0x26;               //Ѱ��������δ��������״̬
	private final int PICC_REQALL           =0x52;               //Ѱ��������ȫ����
	private final int PICC_ANTICOLL1        =0x93;               //����ײ
	private final int PICC_ANTICOLL2        =0x95;               //����ײ
	private final int PICC_AUTHENT1A        =0x60;               //��֤A��Կ
	private final int PICC_AUTHENT1B        =0x61;               //��֤B��Կ
	private final int PICC_READ             =0x30;               //����
	private final int PICC_WRITE            =0xA0;               //д��
	private final int PICC_DECREMENT        =0xC0;               //�ۿ�
	private final int PICC_INCREMENT        =0xC1;               //��ֵ
	private final int PICC_RESTORE          =0xC2;               //�������ݵ�������
	private final int PICC_TRANSFER         =0xB0;               //���滺����������
	private final int PICC_HALT             =0x50;               //����

	//MF522 FIFO���ȶ���
	private final int DEF_FIFO_LENGTH       =64;                 //FIFO size=64byte
	
	//MF522�Ĵ�������
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

	//��MF522ͨѶʱ���صĴ������
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
	
	//��    �ܣ���RC632�Ĵ���
	//����˵����Address[IN]:�Ĵ�����ַ
	//��    �أ�������ֵ
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
	
	//��    �ܣ�дRC632�Ĵ���
	//����˵����Address[IN]:�Ĵ�����ַ
	//	  value[IN]:д���ֵ
	private void WriteRawRC(int address,int value){
		mt.RFCS(0);
		
		//RfidWrite(address);
		RfidWrite((address<<1) & 0x7F);	//0x7E
		RfidWrite(value);
		
		mt.RFCS(1);
	}
	
	//��    �ܣ���RC522�Ĵ���λ
	//����˵����reg[IN]:�Ĵ�����ַ
	//         	mask[IN]:��λֵ
	private void SetBitMask(int reg,int mask)  
	{
	    int tmp = 0x0;
	    tmp = ReadRawRC(reg);
	    WriteRawRC(reg,tmp | mask);  // set bit mask
	}
	
	//��    �ܣ���RC522�Ĵ���λ
	//����˵����reg[IN]:�Ĵ�����ַ
	//          mask[IN]:��λֵ
	private void ClearBitMask(int reg,int mask)  
	{
	    int tmp = 0x0;
	    tmp = ReadRawRC(reg);
	    WriteRawRC(reg, tmp & ~mask);  // clear bit mask
	} 

	//��MF522����CRC16����
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
	

	//��    �ܣ����Ƭ��������״̬
	//��    ��: �ɹ�����MI_OK
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

	//��    �ܣ���λRC522
	//��    ��: �ɹ�����MI_OK
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
	
	//�ر�����
	public void PcdAntennaOff()
	{
	    ClearBitMask(TxControlReg, 0x03);
	}
	
	//��������  
	//ÿ��������ر����շ���֮��Ӧ������1ms�ļ��
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
	
	//����RC632�Ĺ�����ʽ
	public int M500PcdConfigISOType()
	{
		ClearBitMask(Status2Reg, 0x08);  //��MFCrypto1On
		WriteRawRC(ModeReg, 0x3D);       //CRC��ʼֵ0x6363
		/* Modulation signal from the internal analog part, default. */
		WriteRawRC(RxSelReg,0x86); 
		WriteRawRC(RFCfgReg,0x7F);    //RxGain=48dB
		WriteRawRC(TReloadRegL, 30);  //��ʱ����װֵ           
		WriteRawRC(TReloadRegH, 0);
		WriteRawRC(TModeReg, 0x8D);   		//TAuto=1������ʱ��Ԥ��Ƶ��4λ
		WriteRawRC(TPrescalerReg, 0x3E);  //��ʱ��Ԥ��Ƶ��8λ
		WriteRawRC(TxAutoReg, 0x40);      //Force100ASK(����Ҫ)
		PcdAntennaOn();    //������
	   return MI_OK;
	}
	
	//��    �ܣ�ͨ��RC522��ISO14443��ͨѶ
	//����˵����Command[IN]:RC522������
	//          pInData[IN]:ͨ��RC522���͵���Ƭ������
	//          InLenByte[IN]:�������ݵ��ֽڳ���
	//          pOutData[OUT]:���յ��Ŀ�Ƭ��������
	//          *pOutLenBit[OUT]:�������ݵ�λ����
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
			//i = 600;//����ʱ��Ƶ�ʵ���������M1�����ȴ�ʱ��25ms
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
	
	//��    �ܣ�Ѱ��
	//����˵��: req_code[IN]:Ѱ����ʽ
	//0x52 = Ѱ��Ӧ�������з���14443A��׼�Ŀ�
	//0x26 = Ѱδ��������״̬�Ŀ�
	//pTagType[OUT]����Ƭ���ʹ���
	//0x4400 = Mifare_UltraLight
	//0x0400 = Mifare_One(S50)
	//0x0200 = Mifare_One(S70)
	//0x0800 = Mifare_Pro(X)
	//0x4403 = Mifare_DESFire
	//��    ��: �ɹ�����MI_OK
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
	
	//��    �ܣ�����ײ
	//����˵��: pSnr[OUT]:��Ƭ���кţ�4�ֽ�
	//��    ��: �ɹ�����MI_OK
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
	
	//��    �ܣ�ѡ����Ƭ
	//����˵��: pSnr[IN]:��Ƭ���кţ�4�ֽ�
	//��    ��: �ɹ�����MI_OK
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
	
	//��    �ܣ���֤��Ƭ����
	//����˵��: auth_mode[IN]: ������֤ģʽ
	//0x60 = ��֤A��Կ
	//0x61 = ��֤B��Կ 
	//addr[IN]�����ַ
	//pKey[IN]������
	//pSnr[IN]����Ƭ���кţ�4�ֽ�
	//��    ��: �ɹ�����MI_OK             
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
	
	//��    �ܣ���ȡM1��һ������
	//����˵��: addr[IN]�����ַ
	//pData[OUT]�����������ݣ�16�ֽ�
	//��    ��: �ɹ�����MI_OK
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
	

	//��    �ܣ�д���ݵ�M1��һ��
	//����˵��: addr[IN]�����ַ
	//pData[IN]��д������ݣ�16�ֽ�
	//��    ��: �ɹ�����MI_OK                  
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
	

	//��    �ܣ��ۿ�ͳ�ֵ
	//����˵��: dd_mode[IN]��������
	//0xC0 = �ۿ�
	//0xC1 = ��ֵ
	//addr[IN]��Ǯ����ַ
	//pValue[IN]��4�ֽ���(��)ֵ����λ��ǰ
	//��    ��: �ɹ�����MI_OK                
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
	
	//��    �ܣ�����Ǯ��
	//����˵��: sourceaddr[IN]��Դ��ַ
	//goaladdr[IN]��Ŀ���ַ
	//��    ��: �ɹ�����MI_OK
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
			//S70��ǰ��32�����ǿɶ�3����
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
			//S70��ǰ��8�����ǿɶ�15����(ֻд7��)
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
			//p=16;	//ǰ��Ŀ�����д
			p=0;
			//S70��ǰ��32�����ǿɶ�3����
			for(i=0;i<32;i++){					
				sector=i*4;	
				for(m=0;m<3;m++){
					status=PcdAuthState(PICC_AUTHENT1A,(m+sector),key, sn);
					if(status != MI_OK){
						ShowToast("����");
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
			//S70��ǰ��8�����ǿɶ�15����(ֻд7��)
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

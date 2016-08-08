package zhuboss.framework.util;
/**
 * DES���� ����ֱ��ϵͳ������ܽ���
 * @Author:     Binbin
 * @CreateDate: Mar 1, 2010
 * @Copyright:  (c)Copyright 2010 Hiiso Technology. All rights reserved.
 * @Version:    2.0
 */
public class DesUtil4CL {

    private String key = "OaksNuUn" ;

    public DesUtil4CL() {
    }

    public void setKey(String strKey) {
        key = strKey;
    }
    public String getKey() {
        return key ;
    }

    /**
     * ����
     * @param str
     * @return
     * @author KevinZhu
	 * @since 2010/11/01
     */
    public String decryptOld(String str) {
        return decrypt(str.getBytes()).trim();
    }
    
    public String decrypt(String str) {
    	if(str==null)return null;
    	String[] arry=null;
    	String desStr="";
    	if(str.length()>16){
    		arry = new String[str.length()/16+1];
    		for(int i=0;i<arry.length;i++){
    			if(i==arry.length-1){
    				arry[i]=str.substring(i*16);
    			} else {
    				arry[i]=str.substring(i*16, i*16+16);
    			}
    			desStr=desStr+decryptOld(arry[i]);
    		}
    	} else {
    		desStr = decryptOld(str);
    	}
    	return desStr;
    }

    private String decrypt(byte[] bstr) {
        String dst = "";
        byte[] szInStr = this.getEmptyByteArray(8);
        byte[] szOutStr = this.getEmptyByteArray(8);

        if ( bstr.length < 16)
            return "" ;

        int index ;
        for(int i=0 ; i<8; i++){
            //byte hBit = bstr[2*i +1] ;
            byte hBit = bstr[2*i] ;
            if(hBit > 57)
                hBit -= 55 ;
            else
                hBit -= 48 ;

            //byte lBit = bstr[2*i + 2] ;
            byte lBit = bstr[2*i + 1] ;
            if(lBit > 57)
                lBit -= 55 ;
            else
                lBit -= 48 ;
            byte bit = (byte) ((hBit << 4) | lBit) ;
            szInStr[i] = bit ;
        }
        des(szOutStr,szInStr,key.getBytes(),1) ;//DES����
        //szOutStr[8] = 0 ;
        return new String(szOutStr) ;
    }

    /**
     * ��������
     * @param str
     * @return
     * @author KevinZhu
	 * @since 2010/11/01
     */
    public String encryptOld(String str) {
    	if(str==null||str.length()==0){
    		return str;
    	}
        return encrypt(str.getBytes());
    }
    
    //��λ����
    public  String encrypt(String str){
    	if(str == null)return null;
    	String[] arry=null;
    	String  encryptStr="";
    	if(str.length()>8){
    		arry = new String[str.length()/8+1];
    		for(int i=0;i<arry.length;i++){
    			if(i==arry.length-1){
    				arry[i]=str.substring(i*8);
    			} else {
    				arry[i]=str.substring(i*8, i*8+8);
    			}
    			 encryptStr= encryptStr+encryptOld(arry[i]);
    		}
    	} else {
    		 encryptStr = encryptOld(str);
    	}
		return  encryptStr;
	}
    
    

    private String encrypt(byte[] InStr) {
        String s = "";
        String sTemp = "" ;

        byte[] tmpOutStr = this.getEmptyByteArray(30);

        byte bit;
        byte[] tmpInStr = this.getEmptyByteArray(80);
        int len=InStr.length;
        for(int j=0;j<len;j++) {
            tmpInStr[j]=InStr[j];
        }
        byte[] buffer = this.getEmptyByteArray(1);
        des(tmpOutStr,tmpInStr,this.key.getBytes(),0);
        for(int i=0;i<8;i++) {
            bit=tmpOutStr[i];
            bit>>=4;
            bit &= 0xF;
            sTemp = Integer.toHexString((int) ub(bit)).toUpperCase();
            s += sTemp ;
            bit=tmpOutStr[i];
            bit &= 0xF;
            sTemp = Integer.toHexString((int) ub(bit)).toUpperCase();
            s += sTemp ;
        }
        return s;
    }

    private void des(byte[] out_str, byte[] in_str, byte[] key_str, int type ) {
        int ii, kk;
        byte[] l_str = this.getEmptyByteArray(69) ;
        byte[] r_str = this.getEmptyByteArray(69) ;
        byte[] keys  = this.getEmptyByteArray(864) ;
        byte[] tmp_key = this.getEmptyByteArray(69) ;

        ip( in_str, tmp_key );
        //memcpy( l_str, tmp_key, 32 ); /* L0 */
        System.arraycopy(tmp_key,0,l_str,0,32) ;

        //memcpy( r_str, &tmp_key[32], 32 ); /* R0 */
        System.arraycopy(tmp_key, 32, r_str, 0,32);
        produce_keys( key_str, keys, type ); /* ��� type �����Ͳ�����Կ.*/

        for (ii=0; ii<16; ii++) {
            byte[] keys1 = this.getEmptyByteArray(keys.length - ii*48) ;
            System.arraycopy(keys, ii*48, keys1, 0, keys1.length) ;
            //function_f( r_str, tmp_key, &keys[ii*48] ); /* f( R(ii-1), keys(ii));*/
            function_f( r_str, tmp_key, keys1);
            System.arraycopy(keys1, 0, keys, ii*48, keys1.length) ;

            for (kk=0; kk<32; kk++) {
                tmp_key[kk] ^= l_str[kk];
            }
            //memcpy( l_str, r_str, 32 );
            System.arraycopy(r_str, 0, l_str, 0, 32) ;

            //memcpy( r_str, tmp_key, 32 );
            System.arraycopy(tmp_key, 0, r_str, 0, 32) ;
        }
        //memcpy( &tmp_key[32], l_str, 32 );
        System.arraycopy(l_str, 0, tmp_key, 32, 32);

        ip_1( tmp_key );

        //memset( out_str, 0x0, 8 );
        for(int i=0;i<8;i++)
            out_str[i] = 0x0;

        for (ii=0; ii<8; ii++) {
            for (kk=0; kk<8; kk++) {
                out_str[ii] <<= 1;
                out_str[ii] |= tmp_key[ii*8+kk];
            }
        }
    }


    private void function_f(byte[] in_data, byte[] out_data, byte[] key_str ) {
        int ii;
        byte[] tmp_key = this.getEmptyByteArray(69) ;

        e( in_data, tmp_key ); /* �Ŵ�λ�� E.*/

        for (ii=0; ii<48; ii++) { /* �� key_str ���.*/
            tmp_key[ii] ^= key_str[ii];
        }

        for (ii=0; ii<8; ii++) { /* ѡ���� Si.*/
            byte[] tmp_key1 = this.getEmptyByteArray(tmp_key.length - ii*6) ;
            byte[] out_data1 = this.getEmptyByteArray(out_data.length - ii*4) ;
            System.arraycopy(tmp_key, ii*6, tmp_key1, 0, tmp_key1.length);
            System.arraycopy(out_data, ii*4, out_data1, 0, out_data1.length);
            //s( & tmp_key[ii*6], &out_data[ii*4], ii+1 );
            s( tmp_key1, out_data1, ii+1 );
            System.arraycopy(tmp_key1, 0, tmp_key, ii*6, tmp_key1.length);
            System.arraycopy(out_data1, 0, out_data, ii*4, out_data1.length);
        }

        p(out_data, tmp_key ); /* ������λ�� P.*/
        //memcpy( out_data, tmp_key, 32 );
        System.arraycopy(tmp_key, 0, out_data, 0, 32) ;
    }

    private void produce_keys(byte[] in_key, byte[] out_key, int type ) {

        int ii, times;
        byte[] tmp_key = this.getEmptyByteArray(69) ;

        pc_1( in_key, tmp_key );

        for (ii=1; ii<17; ii++) {
            times = 2;
            if ((ii< 3) || (ii==9) || (ii==16)) times=1;
            shift_left( tmp_key, 28, times );

            byte[] tmp_key1 = this.getEmptyByteArray(tmp_key.length - 28) ;
            System.arraycopy(tmp_key, 28, tmp_key1, 0, tmp_key1.length);
            //shift_left( &tmp_key[28], 28, times );
            shift_left(tmp_key1, 28, times );
            System.arraycopy(tmp_key1, 0, tmp_key, 28, tmp_key1.length);

            if (type == 0) {
                byte[] out_key1 = this.getEmptyByteArray(out_key.length - (ii-1)*48) ;
                System.arraycopy(out_key, (ii-1)*48, out_key1,0,out_key1.length) ;
                //pc_2( tmp_key, &out_key[(ii-1)*48] );
                pc_2( tmp_key, out_key1);
                System.arraycopy(out_key1, 0, out_key, (ii-1)*48, out_key1.length);
            } else {
                byte[] out_key1 = this.getEmptyByteArray(out_key.length - (16-ii)*48) ;
                System.arraycopy(out_key, (16-ii)*48, out_key1,0,out_key1.length) ;
                //pc_2( tmp_key, &out_key[(16-ii)*48] );
                pc_2( tmp_key, out_key1);
                System.arraycopy(out_key1, 0, out_key, (16-ii)*48, out_key1.length);
            }
        }
    }

    private void ip(byte[] data, byte[] out_data ) {
        byte[] table = { 58, 50, 42, 34, 26, 18, 10, 2,
            60, 52, 44, 36, 28, 20, 12, 4,
            62, 54, 46, 38, 30, 22, 14, 6,
            64, 56, 48, 40, 32, 24, 16, 8,
            57, 49, 41, 33, 25, 17, 9, 1,
            59, 51, 43, 35, 27, 19, 11, 3,
            61, 53, 45, 37, 29, 21, 13, 5,
            63, 55, 47, 39, 31, 23, 15, 7 };
        change(data,out_data,table,64);
    }

    private void p(byte[] in_data, byte[] out_data ) {
        byte[] table = { 16, 7, 20, 21,
            29, 12, 28, 17,
            1, 15, 23, 26,
            5, 18, 31, 10,
            2, 8, 24, 14,
            32, 27, 3, 9,
            19, 13, 30, 6,
            22, 11, 4, 25 };
        change2(in_data,out_data,table,32);
    }

    private void pc_1(byte[] in_key, byte[] out_key ) {
        byte[] table={ 57, 49, 41, 33, 25, 17, 9,
            1, 58, 50, 42, 34, 26, 18,
            10, 2, 59, 51, 43, 35, 27, /* 42--->43*/
            19, 11, 3, 60, 52, 44, 36,
            63, 55, 47, 39, 31, 23, 15,
            7, 62, 54, 46, 38, 30, 22,
            14, 6, 61, 53, 45, 37, 29,
            21, 13, 5, 28, 20, 12, 4 };
        change(in_key,out_key,table,56);
    }

    private void pc_2(byte[] in_key, byte[] out_key) {
        byte[] table={ 14, 17, 11, 24, 1, 5,
            3, 28, 15, 6, 21, 10,
            23, 19, 12, 4, 26, 8,
            16, 7, 27, 20, 13, 2,
            41, 52, 31, 37, 47, 55,
            30, 40, 51, 45, 33, 48,
            44, 49, 39, 56, 34, 53,
            46, 42, 50, 36, 29, 32 };
        change2(in_key,out_key,table,48);
    }

    private void ip_1(byte[] data ) {
        byte[] table = { 40, 8, 48, 16, 56, 24, 64, 32,
            39, 7, 47, 15, 55, 23, 63, 31,
            38, 6, 46, 14, 54, 22, 62, 30,
            37, 5, 45, 13, 53, 21, 61, 29,
            36, 4, 44, 12, 52, 20, 60, 28,
            35, 3, 43, 11, 51, 19, 59, 27,
            34, 2, 42, 10, 50, 18, 58, 26,
            33, 1, 41, 9, 49, 17, 57, 25 };

        byte[] tmp_key = this.getEmptyByteArray(69) ;
        change2(data,tmp_key,table,64);
        //memcpy( data, tmp_key, 64 );
        System.arraycopy(tmp_key, 0, data, 0 ,64) ;
    }
    private void e(byte[] in_data, byte[] out_data) {
        byte[] table = { 32, 1, 2, 3, 4, 5,
            4, 5, 6, 7, 8, 9,
            8, 9, 10, 11, 12, 13,
            12, 13, 14, 15, 16, 17,
            16, 17, 18, 19, 20, 21,
            20, 21, 22, 23, 24, 25,
            24, 25, 26, 27, 28, 29,
            28, 29, 30, 31, 32, 1 };
        change2(in_data,out_data,table,48);
    }

    private void s( byte[] in_data, byte[] out_data, int number ) {
        byte[][] table = {
            { 14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7,
                0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8,
                4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0,
                15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13 }, /* s1 */

            { 15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10,
                3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5,
                0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15,
                13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9 }, /* s2 */

            { 10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8,
                13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1,
                13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7,
                1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12 }, /* s3 */

            { 7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15,
                13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9,
                10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4,
                3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14 }, /* s4 */

            { 2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9,
                14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6,
                4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14,
                11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3 }, /* s5 */

            { 12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11,
                10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8,
                9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6,
                4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13 }, /* s6 */

            { 4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1,
                13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6,
                1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2,
                6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12 }, /* s7 */

            { 13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7,
                1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2,
                7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8,
                2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11 } }; /* s8 */

        int ii, kk, tmp;

        ii=kk=0;
        ii = in_data[0] * 2;
        ii += in_data[5];
        kk = in_data[1] * 8;
        kk += in_data[2] * 4;
        kk += in_data[3] * 2;
        kk += in_data[4];
        tmp = table[number-1][ii * 16 + kk];
        out_data[0] = (byte) ((tmp & 0x08) >> 3);
        out_data[1] = (byte) ((tmp & 0x04) >> 2);
        out_data[2] = (byte) ((tmp & 0x02) >> 1);
        out_data[3] = (byte) ((tmp & 0x01));
    }

    private void change(byte[] str,byte[] dst,byte[] table,int num) {
        byte[] tmp_key =this.getEmptyByteArray(69) ;
        change1(str,tmp_key);
        change2(tmp_key,dst,table,num);
    }

    private void change1(byte[] str, byte[] dst) {
        int ii, kk;
        for (ii=0; ii<8; ii++) {
            kk = ii * 8;
            dst[kk++] = (byte) ((str[ii] & 0x80) >> 7);
            dst[kk++] = (byte) ((str[ii] & 0x40) >> 6);
            dst[kk++] = (byte) ((str[ii] & 0x20) >> 5);
            dst[kk++] = (byte) ((str[ii] & 0x10) >> 4);
            dst[kk++] = (byte) ((str[ii] & 0x08) >> 3);
            dst[kk++] = (byte) ((str[ii] & 0x04) >> 2);
            dst[kk++] = (byte) ((str[ii] & 0x02) >> 1);
            dst[kk] = (byte) (str[ii] & 0x01);
        }
        return ;
    }

    void shift_left(byte[] key_str, int length, int times ) {
        byte[] tmp_key = this.getEmptyByteArray(69) ;

        //memcpy( tmp_key, key_str, length );
        System.arraycopy(key_str, 0,tmp_key, 0, length ) ;

        //memcpy( &tmp_key[length], key_str, length );
        System.arraycopy(key_str, 0, tmp_key, length, length) ;

        //memcpy( key_str, &tmp_key[times], length );
        System.arraycopy(tmp_key, times, key_str , 0, length) ;
    }

    private void change2(byte[] str,byte[] dst,byte[] table,int num) {
        int ii;
        for (ii=0; ii<num; ii++) {
            dst[ii] = str[ table[ii] - 1 ];
        }
    }

    //ȡ�յ��ֽ�����
    public byte[] getEmptyByteArray(int num) {
        byte[] ret = new byte[num];
        for (int i=0;i<num;i++)
            ret[i] = 0;
        return ret ;
    }
    //ȡ�յ���������
    private int[] getEmptyIntArray(int num) {
        int[] ret = new int[num];
        for (int i=0;i<num;i++)
            ret[i] = 0;
        return ret ;
    }

    /**
     * convert the byte to int
     */
    public final static int ub(byte b) {
        return b < 0 ? (int)(256 + b) : b;
    }

    private void test(byte[] str, byte[] dst) {
        for ( int i=0;i<str.length;i++) {
            dst[i] = str[i];
        }
    }
    public static void main(String[] args) {
    		String str="D63AC0905E218627";
    		DesUtil4CL d = new DesUtil4CL();
//    		d.setKey("Mn099099");
    		System.out.println(d.encrypt("ds"));
    		System.out.println(d.decrypt("ff95336dd20b8ae1b662789e2368f593"));
/*    	DesUtil des1 = new DesUtil();
//        String str = "ling_!huchong";
        String str = "1234567890123456";
        String stmp = str;
        String smp = "";
        if(str.length()>8){
        	
        	stmp = str.substring(0, 8);
        	System.out.println("stmp:"+stmp);
        	smp = str.substring(8);
        	System.out.println("smp:"+smp);
        }
        
        
        String s = des1.encrypt(str) ;
        System.out.println(s);
        String s1 = des1.decrypt(s);
        System.out.println(s1);
        String s2 = des1.encrypt(stmp);
        String s3 = "";
        if(!StringUtil.isEmpty(smp)){
        	s3 = des1.encrypt(smp);
        }
        System.out.println("s2+s3:["+(s2+s3)+"]");
        
        System.out.println(des1.encrypt("chong"));*/
    }
}

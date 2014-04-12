package netfetch;

public class kkkdm_decode_piclink {
public static String decode(String encodestr){
	char[] encode_chars = encodestr.toCharArray();
	int i = encode_chars.length%2;
	if(i==1) return null;
	int reslength=encode_chars.length/2;
	char[][] encode_chars_d2= new char[reslength][2];
	byte[] res=new byte[reslength];
	
	for (i=0;i<encode_chars_d2.length;i++)
	{
		encode_chars_d2[i][0]=encode_chars[i*2];
		encode_chars_d2[i][1]=encode_chars[i*2+1];
		//System.out.println(String.valueOf(encode_chars_d2[i]));
		int x=Integer.parseInt(String.valueOf(encode_chars_d2[i]), 16);
		res[i]=(byte) x;
		
	}
	
	
	
	return new String(res);
	
}

public  static void main(String args[]){
	String a=kkkdm_decode_piclink.decode("2f636f6d696364617461332f792f796a6477622f66777030372f303130617078796966612e6a7067");
	System.out.println(a);
}
//2f636f6d696364617461332f792f796a6477622f66777030372f303130617078796966612e6a7067
//http://www.kkkmh.com/manhua/common/server.js
//var pic_server = new Array();var pic_server_num = 0;pic_server[0] = {id:14, name:'智能', url:'http://mhauto.kkkmh.com'};pic_server[1] = {id:18, name:'电信1', url:'http://mht1.kkkmh.com'};pic_server[2] = {id:17, name:'电信2', url:'http://mht2.kkkmh.com'};pic_server[3] = {id:16, name:'网通1', url:'http://mhc1.kkkmh.com'};pic_server_num = 4;
}

package netfetch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jdo.PersistenceManager;

import com.sick.gooserver.server.kukuana;

public class FetchMangaIndex {
	private static final Logger log = Logger.getLogger(kukuana.class.getName());
	private static List<Cookieinfo> cookielist =getcoockiehashinfo();;
	 URL url = null;
	// URLConnection urlc = null; 
	 HttpURLConnection urlc = null; 
	 String urlstr;
//	 Map headerfields;
	 int BUFFER_SIZE=399999;
	 StringBuilder s;
	 CharBuffer cb; 
	 Pattern pgp;
	// ArrayList<ChapterItem> indexarr=new ArrayList<ChapterItem>();
	 String conencode;
	 String regxstr;
	 int returncode=0;
public int getReturncode() {
		return returncode;
	}

public FetchMangaIndex(String urlstr,String regxstr) throws IOException{
	 this.urlstr=urlstr;
	 this.regxstr=regxstr;
}

public String getRegxstr() {
	return regxstr;
}

public void setRegxstr(String regxstr) {
	this.regxstr = regxstr;
}

public String getconencode(){
	return conencode;
}

public String initreq() throws IOException{

	try{ 
	url = new URL(urlstr);
	}catch(MalformedURLException e){
		log.info("FetchMangaIndex error due to "+urlstr+"  error:"+e.toString());
		return "11004####error URL!";
	}
	
	//cookielist.size()*
	int usecookie=(int) (Math.random()*cookielist.size());
	
	 urlc = (HttpURLConnection) url.openConnection();
	 urlc.setInstanceFollowRedirects(false);
	 urlc.setConnectTimeout(30000);
	 urlc.setRequestProperty("Cache-Control", "no-cache");
	 urlc.setRequestProperty("Pragma", "no-cache");
	 urlc.setRequestProperty("Accept-Charset", "UTF-8");
//	 urlc.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.14) Gecko/20110218 Firefox/3.6.14");
	 urlc.setRequestProperty("Cookie","ipb_member_id="+cookielist.get(usecookie).getHashid()+";ipb_pass_hash="+
			 											cookielist.get(usecookie).getHashps()+
			 											";nw=1;domain=.exhentai.org;"
	 	//	+ "uconfig=tl_m-uh_y-rc_0-cats_0-xns_0-ts_m-tr_1-prn_y-dm_l-rx_0-ry_0-sa_y-oi_n-qb_n-tf_n-hp_-hk_-xl_;"
	 	//	+ "yay=louder;"
	 	//	+ "lv=1387043522-1387048141"
	 		);
	 
	 log.info("Testing debug for usecookie: "+cookielist.get(usecookie).getHashid()+" / "+cookielist.get(usecookie).getHashps());

	 
	 urlc.connect();
     
    // conencode=urlc.getContentEncoding();
	/* 
	 headerfields = urlc.getHeaderFields();
	 ArrayList<String> TMPAR=(ArrayList)headerfields.get("content-length");
	 if (TMPAR == null){return "11004####Server trucated or error URL!";}
	 String tempstr= TMPAR.get(0);
	 
	 
	 BUFFER_SIZE = Integer.parseInt(tempstr);
	 if (BUFFER_SIZE > 999999)
	 {
		 BUFFER_SIZE = 1000;
	 }
	 */
	 
	 //cb=CharBuffer.allocate(BUFFER_SIZE);
	 
	 /*	 
	 Set headers = headerfields.entrySet(); 
	 for(Iterator i = headers.iterator(); i.hasNext();){ 
	  Map.Entry map = (Map.Entry)i.next();
	 System.out.println(map.getKey() + " : " + map.getValue()); 
	  }
	*/ 
	 Object o=null;
	 try{
		 
		 returncode=urlc.getResponseCode(); 
	 o=urlc.getContent();
	 }catch(java.io.IOException ioex)
	 {
		 log.info("FetchMangaIndex error due to "+urlstr+"  error:"+ioex.toString());
		 return "11006####FET IO ERROR!";
	 }
	 
    BufferedReader reader = new BufferedReader(new InputStreamReader((InputStream) o,"UTF-8"));     
    
    s = new StringBuilder(); 
   String line;
    while((line = reader.readLine()) != null)
    {
    	s.append(line);
    }
    
    //int n=reader.read(cb);
    //cb.flip();
   
    
    reader.close();
	
	return "OK";
}


public Map<String,ArrayList<String>> Getheader()
{
	//return headerfields;
	return null;
	}

public String GetContent()
{
return s.toString();	
}


//regex
public Pattern setPt(String p)
	{
		Pattern pt=null;
		try{
			pt=Pattern.compile(p,Pattern.CASE_INSENSITIVE);
			return pt;
		}catch(Exception e){
			return null;
			}
	}

public String[] matchPt(String s,Pattern pt)
{
	if(pt==null) return null;
	String[] result=null;
	Matcher m=pt.matcher(s);
	boolean b=m.find();
	if(b)
	{
		int lgt=m.groupCount();
		result=new String[lgt+1];
		for(int i=0;i<=lgt;i++)
		{
			result[i]=m.group(i);
			}
	}
	return result;
}

public ArrayList<ChapterItem> matchPtloop(String s,Pattern pt)
{
	ArrayList<ChapterItem> indexarr=new ArrayList<ChapterItem>();
	if(pt==null) return null;
	Matcher m=pt.matcher(s);
	boolean b=m.find();	
	while(b)
	{
		ChapterItem ci=new ChapterItem();
		ci.seturl(m.group(1));
		ci.setdesc(m.group(2));
		indexarr.add(ci);
		b=m.find();
	}
	
	return indexarr;
}


public ArrayList<ChapterItem> getIndex()
{
	pgp=setPt(regxstr);//"<li><a href=\"([a-zA-Z0-9/\\.]+)\" title=\"([^\"]+)\" target=\"_blank\">");
			// title=\"(^[\"]+)\" target=\"_blank\">	");
	
	// <li><a href="/manhua/1104/12968/88475.html" title="白银之匙 特別篇前篇" target="_blank">
	ArrayList<ChapterItem> res= matchPtloop(GetContent(),pgp);	
	
	/*
	for(int i=0;i<res.size();i++)
		{System.out.println(res.get(i).geturl());
		System.out.println(res.get(i).getdesc());
		}
	*/
	
	return res;
}

public ArrayList<ChapterItem> getIndex(String regxstr11)
{
	pgp=setPt(regxstr11);
	ArrayList<ChapterItem> res= matchPtloop(GetContent(),pgp);	
	return res;
}
	

private static List<Cookieinfo> getcoockiehashinfo(){
	
	PersistenceManager pm = PMF.get().getPersistenceManager();
    javax.jdo.Query q=pm.newQuery(Cookieinfo.class);
    List<Cookieinfo> list=(List<Cookieinfo>) q.execute();
    
    if(list.isEmpty())
    {
    	Cookieinfo cii=new Cookieinfo("id_null","pass_null","null");
    	list.add(cii);
    }
    
    log.info("now get the coockie from database: "+list.get(0).getHashid()+" / "+list.get(0).getHashps());
    return list;
	
}


}

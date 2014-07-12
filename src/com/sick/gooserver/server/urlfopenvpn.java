package com.sick.gooserver.server;
/*
import java.io.BufferedReader;

import java.io.InputStream;
import java.io.InputStreamReader;




import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
*/
import javax.servlet.http.*;

import netfetch.ChapterItem;
import netfetch.FetchAnyWeb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
public class urlfopenvpn extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(urlfopenvpn.class.getName());
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
	// res.setHeader("Content-Type", "text/plain");

	// res.getWriter().append("Requests: " + "\n");
	// URL url = null;
	 HttpURLConnection urlc = null; 
	// url = new URL("http://g.e-hentai.org/manga");
	// urlc = url.openConnection();
	 

	 //res.getWriter().append(urlc.getHeaderField(0));
	 //res.getWriter().append(urlc.getHeaderField(1));
	 //res.getWriter().append(urlc.getHeaderField(2));
	 
//	 Map headerfields = urlc.getHeaderFields();
//	 Set headers = headerfields.entrySet(); 
//	 for(Iterator i = headers.iterator(); i.hasNext();){ 
//	  Map.Entry map = (Map.Entry)i.next();
//	  }
	 String q =req.getParameter("qtype");
	 String id =req.getParameter("id");
	 String ps =req.getParameter("ps");
	 
		if(q == null || q.equals(""))
		{
			res.getWriter().append("1Error Query!");
			return;
		}


	  try {
		  StringBuilder s=new StringBuilder();
          URL url = new URL(q);
     	 urlc = (HttpURLConnection)url.openConnection();
     	 
     	urlc.setUseCaches(false);
     	 
    	 urlc.setInstanceFollowRedirects(false);
    	 urlc.setConnectTimeout(30000);
    	 urlc.setRequestProperty("Cache-Control", "no-cache");
    	 urlc.setRequestProperty("Pragma", "no-cache");
    	 //urlc.setRequestProperty("Content-Type", "text/plain");
    	 urlc.setRequestProperty("Accept-Charset", "UTF-8");
    	// urlc.setRequestProperty("Cache-Control", "max-age=300");
    	// urlc.setRequestProperty("Referer", "http://exhentai.org/");
    	// urlc.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.14) Gecko/20110218 Firefox/3.6.14  AppEngine-Google; (+http://code.google.com/appengine; appid: s~sickcrawler)");
    	// urlc.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)");
    	// urlc.setRequestProperty("Cookie","");
    	
    	 //urlc.setRequestProperty("Cookie","ipb_member_id=1400179;ipb_pass_hash=a3e01018c9b282d29864852ab8403d59;nw=1;domain=.exhentai.org;");
    	// urlc.setRequestProperty("Cookie","ipb_member_id="+id+";ipb_pass_hash="+ps+";nw=1;domain=.exhentai.org;");
    	 
    	 // urlc.setRequestProperty("Cookie","__utma=185428086.860750601.1395546199.1395546199.1395591056.2");
    	 urlc.connect();
    	 

    	 
    	 
    	 Map headerfields = urlc.getHeaderFields();
    	 Set headers = headerfields.entrySet(); 
    	 
    	 
    	 

    	 Object o=null;
    	 try{
   
    	 o=urlc.getContent();
    	 }catch(java.io.IOException ioex)
    	 {
    		 log.info("  error:"+ioex.toString());
    		 res.getWriter().append(ioex.toString());
    	 }
    	    BufferedReader reader = new BufferedReader(new InputStreamReader((InputStream) o,"UTF-8"));     

          
          
         // BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
          String line;
          
          while ((line = reader.readLine()) != null) {
              // ...
        	  s.append(line);
        	  s.append("\r\n");
        	  
          }
          
          reader.close();

          res.getWriter().append(s.toString());
          res.getWriter().append("sickjohnsisick1122356l112355iaaaoss");
  		String reg="<td class='vg_table_row_[0-9]+'.+?([0-9a-zA-Z]+).opengw.net.+?UDP: ([0-9]+).+?</span></b></td></tr>";
  		FetchAnyWeb faw=new FetchAnyWeb("http://www.vpngate.net/en/",reg);
  		faw.initreq();
  		ArrayList<ChapterItem> mappingforudp=faw.getIndex();
  		for(int i=0;i<mappingforudp.size();i++)
		{
  		res.getWriter().append(mappingforudp.get(i).geturl());
  		res.getWriter().append(",");
		res.getWriter().append(mappingforudp.get(i).getdesc());
		res.getWriter().append(",");
		}
          

      } catch (MalformedURLException e) {
    	  log.info("FetchMangaIndex error due to  error1:"+e.toString());
          // ...
      } catch (IOException e) {
          // ...
    	  log.info("FetchMangaIndex error due to  error2:"+e.toString());
      }



	}

}
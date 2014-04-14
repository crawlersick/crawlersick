package com.sick.gooserver.server;


import java.io.IOException;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import netfetch.ChapterItem;
import netfetch.Cookieinfo;
import netfetch.FetchMangaIndex;
import netfetch.PMF;

import java.net.SocketTimeoutException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

// http://127.0.0.1:8888/kukuana?qtype=kkkmh&qvalue=http://www.kkkmh.com/manhua/0611/yao-jing-di-wei-ba.html
//http://aiworkserver.appspot.com/kukuana?qtype=kkkmh&qvalue=http://www.kkkmh.com/manhua/0611/yao-jing-di-wei-ba.html
//http://aiworkserver.appspot.com/kukuana?qtype=kkkmh_lv2&qvalue=http://www.kkkmh.com/manhua/0611/513/37994.html
//http://aiworkserver.appspot.com/kukuana?qtype=kkkmh_ser&qvalue=nnn
//<div class=\"it5\"><a href=\"[a-zA-Z0-9/:\\.]+\" onmouseover=\"show_image_pane\\([0-9]+\\)\" onmouseout=\"hide_image_pane\\([0-9]+\\)\">([^<]+)</a></div>
public class pagehandler extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(pagehandler.class.getName());

	private static final String regxstrhentai="<div class=\"it5\"><a href=\"([^\"]+)\" onmouseover=\"show_image_pane\\([0-9]+\\)\" onmouseout=\"hide_image_pane\\([0-9]+\\)\">([^<]+)";
	private static  ArrayList<ChapterItem> recentlist;
	private static  ArrayList<Cookieinfo> cookieinfolist;
	
	  public void init(ServletConfig config) throws ServletException {
		    super.init(config);
		    
		    PersistenceManager pm = PMF.get().getPersistenceManager();
		    javax.jdo.Query q=pm.newQuery(Cookieinfo.class);
		    List<Cookieinfo> list=(List<Cookieinfo>) q.execute();
		    if(list.isEmpty())
		    {
		    	
		    	Cookieinfo cii =new Cookieinfo("hashid","hashpassword","yooooo!");
		    	pm.makePersistent(cii);
		    }
		    pm.close();
		    
		    
		    /*
		    try {
		    	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		    	Key k = KeyFactory.createKey("recentlist", "recentlist00");
				Entity tempitem00=datastore.get(k);
				recentlist = (ArrayList<ChapterItem>) tempitem00.getProperty("00list");
				
			} catch (EntityNotFoundException e) {
				// TODO Auto-generated catch block
				recentlist=null;
				e.printStackTrace();
			}
		    */
		  }
	
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String qtype;
		String qvalue ;
		String pagenn;
		req.setCharacterEncoding("UTF-8");  
		qtype =req.getParameter("qtype");
		qvalue =req.getParameter("qvalue");
		pagenn =req.getParameter("p");
			
		res.setHeader("Content-Type", "text/plain; charset=utf-8");
		res.setCharacterEncoding("UTF-8");
		res.setHeader("Server", "I_AM_HENTAI");
		if(qtype == null || qvalue == null || pagenn == null)
		{
			res.getWriter().append("1Error Query!");
			return;
		}
		if(qtype.equals("") || qvalue.equals("") || pagenn.equals(""))
		{
			res.getWriter().append("2Error Query!");
			return;
		}
		
		FetchMangaIndex fmi=null;
		ArrayList<ChapterItem> res1=null;
		
		if(qtype.equals("ehentai"))
		{
			res1=null;
			String requrlwithpara;
			if(qvalue.indexOf("?")==-1)
				requrlwithpara=qvalue+pagenn;
			else
				requrlwithpara=qvalue+"&page="+pagenn;
			//log.info("the requrstr is "+requrlwithpara);
						
			int cntlv1=0;
			while(res1==null||res1.size()==0)
			{
			cntlv1++;
			fmi=new FetchMangaIndex(requrlwithpara,regxstrhentai);
			res1=this.dokkkdmextra(res, fmi,true);
			if(cntlv1==5)
				break;
			if(cntlv1>1)
				try {
					log.info("lv1 re-try "+cntlv1);
					Thread.sleep(300);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if (qvalue.indexOf("search=")!=-1&&res1!=null && res1.size()!=0)
			{
			
			
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			Entity histitem = new Entity("searchhist");
			String temphistitem=URLDecoder.decode(qvalue,"UTF-8").split("=")[1];
			histitem.setProperty("searchurl", temphistitem);
			histitem.setProperty("searchdate", new Date());
			histitem.setProperty("ipproxy", getClientIpAddr(req));
			histitem.setProperty("ipremote", req.getRemoteAddr());
			histitem.setProperty("agent",req.getHeader("User-Agent") );
			datastore.put(histitem);
			}
			
		}
		
		if(qtype.equals("ehentai_get_recentitems"))
		{
			if (recentlist==null)
			{
				recentlist=new ArrayList<ChapterItem>();
			}
			else
			{	
				synchronized(recentlist)
				{
				res1=new ArrayList<ChapterItem>();
				 for(ChapterItem citem : recentlist) {
					 ChapterItem tempcitem=new ChapterItem();
					 tempcitem.setdesc(citem.getdesc());
					 tempcitem.seturl(citem.geturl());
					 res1.add(tempcitem);
					}
				}
			}
		}

		if(qtype.equals("ehentai_lv2_getpagenum"))
		{
			ArrayList<ChapterItem> pagelist=null;
			ArrayList<ChapterItem> temppage=null;
			res1=new ArrayList<ChapterItem>();
			
			if (recentlist==null)
			{
				recentlist=new ArrayList<ChapterItem>();
			}
			

			
			ChapterItem tempcitoaddrec= new ChapterItem();
			tempcitoaddrec.seturl(qvalue);
			tempcitoaddrec.setdesc(qvalue);
			if(!recentlist.contains(tempcitoaddrec))
			{
				synchronized(recentlist) {
				 recentlist.add(tempcitoaddrec); 
				 
					if(recentlist.size()>10)
					{
							 recentlist.remove(0);
					}
				 
				 }
			}
			
			int cnt=0;
		if (pagenn.equals("0"))
		{
			while(pagelist==null || pagelist.size()==0)
			{
				if (cnt==5)
				{
				log.warning("this ehentai lv2 request for page count makes "+cnt+" times!!!"+qvalue+"\r\n"+fmi.GetContent());
				res.getWriter().append("10058#####ehentai lv2 reading fail after 5 try!");
				return;
				}
				
			fmi=new FetchMangaIndex(qvalue+"?p="+pagenn,"onclick=\"return (false)\">([0-9]+)</a></td><td");
			pagelist=this.dokkkdmextra(res, fmi);	 
					if(cnt>1)
					{
						log.info("this ehentai lv2 request for page count makes "+cnt+" times!!!"+qvalue);
					}
					cnt++;
				 
			}
			
			String xxxxcnt=pagelist.get(pagelist.size()-1).getdesc();
			temppage=fmi.getIndex("<a href=\"([^\"]+)\"><img alt=\"[^\"]+\" title=\"([^\"]+)\"");
			ChapterItem tempchaitem=new ChapterItem();
			tempchaitem.setdesc(xxxxcnt);tempchaitem.seturl(xxxxcnt);
			res1.add(tempchaitem);
			res1.addAll(temppage);
		}
		else
		{
			 fmi=null;
			 temppage=null; cnt=0;
			 while(temppage==null){
				 
					if (cnt==5)
					{
					log.warning("this ehentai lv2 request makes "+cnt+" times!!!"+qvalue+"\r\n"+fmi.GetContent());
					res.getWriter().append("10078#####ehentai lv2 reading fail after 5 try!");
					return;
					}
					
			//fmi=new FetchMangaIndex(qvalue+"?p="+pageindex,"no-repeat\"><a href=\"([^\"]+)\"><img alt=\"[^\"]+\" title=\"([^\"]+)\"");
			fmi=new FetchMangaIndex(qvalue+"?p="+pagenn,"<a href=\"([^\"]+)\"><img alt=\"[^\"]+\" title=\"([^\"]+)\"");
			temppage=this.dokkkdmextra(res, fmi);	
			if(temppage==null||temppage.size()==0)
			{temppage=null;}


			
				 
					if(cnt>1)
					{
						log.info("this ehentai lv2 request makes "+cnt+" times!!!"+qvalue);
					}
					cnt++;
				 
			 }
			
			 res1.addAll(temppage);
			 	/*
			 for(ChapterItem citem : temppage) {
				 ChapterItem tempcitem=new ChapterItem();
				 tempcitem.setdesc(citem.getdesc());
				 tempcitem.seturl(citem.geturl());
				 res1.add(citem);
				}
			 */
		}

	}
		
		if(res1 != null)
		{

		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String json = gson.toJson(res1);  
		res.getWriter().append(json);
		
		}
		else
			res.getWriter().append("100001#####resutl null!");
		

	}
	
	public ArrayList<ChapterItem> dokkkdmextra(HttpServletResponse res,FetchMangaIndex fmi) throws IOException
	{

		String serres="";
		try{
			 serres=fmi.initreq();
			}catch(SocketTimeoutException ste){
				res.getWriter().append("Read URL time out!!! - "+ste.toString());
			}
				
			if (!serres.equals("OK"))
			{

				return null;
			}		

			ArrayList<ChapterItem> res1 = fmi.getIndex();
			

			return res1;
		
	}
	
	public ArrayList<ChapterItem> dokkkdmextra(HttpServletResponse res,FetchMangaIndex fmi,boolean previewfetchflag) throws IOException
	{

		String serres="";
		try{
			 serres=fmi.initreq();
			}catch(SocketTimeoutException ste){
				res.getWriter().append("Read URL time out!!! - "+ste.toString());
			}
				
			if (!serres.equals("OK"))
			{

				return null;
			}		

			ArrayList<ChapterItem> res1 = fmi.getIndex();
			
			fmi.setRegxstr("<div class=\"it2\" id=\"i([0-9]+)\" style=\"[^\"]+\">(.*?)</div>");
			ArrayList<ChapterItem> res1_previewimage = fmi.getIndex();
			
			if(res1_previewimage.size()>=1 && res1_previewimage.size()==res1.size())
			{	
				
				String firststr[]=res1_previewimage.get(0).getdesc().split("\"");
				if (firststr!=null&&firststr.length>0)
				{
					res1.get(0).setUrl2(res1_previewimage.get(0).getdesc().split("\"")[1]);
				}else
				{
					res1.get(0).setUrl2("empty first previewurl!");
				}
				res1.get(0).setDesc2(res1_previewimage.get(0).geturl());
				
				for (int i=1;i<res1.size();i++)
				{
					String tempstrarr[]=res1_previewimage.get(i).getdesc().split("~");
					if (tempstrarr!=null&&tempstrarr.length>=3)
					{
						res1.get(i).setUrl2("http://"+tempstrarr[1]+"/"+tempstrarr[2]);
					}
					res1.get(i).setDesc2(res1_previewimage.get(i).geturl());
				}
				/*
				for(ChapterItem tempci:res1)
				{
					
					log.info("desc:  "+tempci.getDesc2());
					log.info("url:  "+tempci.getUrl2());
				}
			*/
			}
			


			

			return res1;
		
	}
	
	 public void destroy() {
		 /*
		    try {
		    	if(recentlist!=null){
		    	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		    	Key k = KeyFactory.createKey("recentlist", "recentlist00");
		    	Entity rec = new Entity("recentlist", "recentlist00");
		    	
		    	rec.setProperty("recentlist", recentlist);
		    	try{
		    	datastore.delete(k);
		    		}catch (Exception e) {}
		    	
		    	datastore.put(rec);
		    	}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				recentlist=null;
				e.printStackTrace();
			}
			*/
		  }
	 
	 public static String getClientIpAddr(HttpServletRequest request) {  
	        String ip = request.getHeader("X-Forwarded-For");  
	        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
	            ip = request.getHeader("Proxy-Client-IP");  
	        }  
	        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
	            ip = request.getHeader("WL-Proxy-Client-IP");  
	        }  
	        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
	            ip = request.getHeader("HTTP_CLIENT_IP");  
	        }  
	        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
	            ip = request.getHeader("HTTP_X_FORWARDED_FOR");  
	        }  
	        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
	            ip = request.getRemoteAddr();  
	        }  
	        return ip;  
	    } 
}

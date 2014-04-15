package com.sick.gooserver.server;


import java.io.IOException;

import javax.servlet.http.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import netfetch.ChapterItem;
import netfetch.FetchMangaIndex;
import netfetch.kkkdm_decode_piclink;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

// http://127.0.0.1:8888/kukuana?qtype=kkkmh&qvalue=http://www.kkkmh.com/manhua/0611/yao-jing-di-wei-ba.html
//http://aiworkserver.appspot.com/kukuana?qtype=kkkmh&qvalue=http://www.kkkmh.com/manhua/0611/yao-jing-di-wei-ba.html
//http://aiworkserver.appspot.com/kukuana?qtype=kkkmh_lv2&qvalue=http://www.kkkmh.com/manhua/0611/513/37994.html
//http://aiworkserver.appspot.com/kukuana?qtype=kkkmh_ser&qvalue=nnn
//<div class=\"it5\"><a href=\"[a-zA-Z0-9/:\\.]+\" onmouseover=\"show_image_pane\\([0-9]+\\)\" onmouseout=\"hide_image_pane\\([0-9]+\\)\">([^<]+)</a></div>
public class kukuana extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(kukuana.class.getName());
	
	
	private static final String regxstrkkkmh="<li><a href=\"([a-zA-Z0-9/\\.]+)\" title=\"([^\"]+)\" target=\"_blank\"";
	/*
	 *http://www.kkkmh.com/manhua/1311/bu-liang-xi-yi-nan-di-shan-guang-ri-chang.html 
<li>
   <li><a href="/manhua/1311/18600/107864.html" title="不良蜥蜴男的闪光日常 第1话" target="_blank" class="red">
</li>
	 * 
	 */
	
	
	private static final String regxstrkkkmh_lv2="pic\\[[0-9]+\\] = '([a-f0-9]+)'(;)";
	private static final String regxstrkkkmh_ser="name:'([^']+)', url:'([^']+)'";
	private static final String regxstrhentai="<div class=\"it5\"><a href=\"([^\"]+)\" onmouseover=\"show_image_pane\\([0-9]+\\)\" onmouseout=\"hide_image_pane\\([0-9]+\\)\">([^<]+)";
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		req.setCharacterEncoding("UTF-8");  
		String qtype =req.getParameter("qtype");
		String qvalue =req.getParameter("qvalue");
		
			
		res.setHeader("Content-Type", "text/plain; charset=utf-8");
		res.setCharacterEncoding("UTF-8");
		res.setHeader("Server", "I_AM_HENTAI");
		if(qtype == null || qvalue == null)
		{
			res.getWriter().append("Error Query!");
			return;
		}
		if(qtype.equals("") || qvalue.equals(""))
		{
			res.getWriter().append("Error Query!");
			return;
		}
		

		//res.getWriter().append("your qtype is "+qtype+"\n");
		//res.getWriter().append("your qvalue is "+qvalue+"\n");
		
		FetchMangaIndex fmi;
		ArrayList<ChapterItem> res1=null;
		if(qtype.equals("kkkmh"))
		{
			fmi=new FetchMangaIndex(qvalue,regxstrkkkmh); 
			res1=this.dokkkdmextra(res, fmi);
			if (res1.isEmpty())
			{
			//	System.out.println("XXXXXXXXXXXXXX");
				res.getWriter().append("11009####list empty!");
				log.warning("LV1 error : "+qvalue+"\r\n"+fmi.GetContent());
				return;
			}
		}
		if(qtype.equals("kkkmh_lv2"))
		{
			fmi=new FetchMangaIndex(qvalue,regxstrkkkmh_lv2); 
			res1=this.dokkkdmextra(res, fmi);
			ChapterItem ci;
			try{
			for(Iterator<ChapterItem> i = res1.iterator(); i.hasNext();){ 
				 ci = i.next();
				 ci.seturl(kkkdm_decode_piclink.decode(ci.geturl()));
																		}
				}catch(NullPointerException ne){
					
					res.getWriter().append("11005####Server trucated or error URL!");
					
				}
		}
		if(qtype.equals("kkkmh_ser"))
		{
			fmi=new FetchMangaIndex("http://www.kkkmh.com/manhua/common/server.js",regxstrkkkmh_ser); 
			res1=this.dokkkdmextra(res, fmi);	
		}
		if(qtype.equals("ehentai"))
		{
			fmi=new FetchMangaIndex(qvalue,regxstrhentai);
			//"<div class=\"it5\"><a href=\"([^\"]+)\" onmouseover=\"show_image_pane\\([0-9]+\\)\" onmouseout=\"hide_image_pane\\([0-9]+\\)\">([^<]+)"
			res1=this.dokkkdmextra(res, fmi);			
						
		}
		if(qtype.equals("ehentai_lv2"))
		{
			ArrayList<ChapterItem> pagelist=null;
			ArrayList<ChapterItem> temppage=null;
			res1=new ArrayList<ChapterItem>();
			
			int cnt=0;

			while(pagelist==null || pagelist.size()==0)
			{
			fmi=new FetchMangaIndex(qvalue,"onclick=\"return (false)\">([0-9]+)</a></td><td");
			pagelist=this.dokkkdmextra(res, fmi);	
			cnt++;
				if (cnt==5)
				{
				log.warning("this ehentai lv2 request for page count makes "+cnt+" times!!!"+qvalue+"\r\n"+fmi.GetContent());
				res.getWriter().append("10058#####ehentai lv2 reading fail after 5 try!");
				return;
				}
				else
				 {
					if(cnt>1)
					{
						log.info("this ehentai lv2 request for page count makes "+cnt+" times!!!"+qvalue);
					}
				 }
			}
			
			String xxxxcnt=pagelist.get(pagelist.size()-1).getdesc();
			int xcnt=Integer.parseInt(xxxxcnt);
			for(int pageindex=0;pageindex<=xcnt-1;pageindex++)
			{
				 fmi=null;
				 temppage=null; cnt=0;
				 while(temppage==null){
				//fmi=new FetchMangaIndex(qvalue+"?p="+pageindex,"no-repeat\"><a href=\"([^\"]+)\"><img alt=\"[^\"]+\" title=\"([^\"]+)\"");
				fmi=new FetchMangaIndex(qvalue+"?p="+pageindex,"<a href=\"([^\"]+)\"><img alt=\"[^\"]+\" title=\"([^\"]+)\"");
				temppage=this.dokkkdmextra(res, fmi);	
				if(temppage==null||temppage.size()==0)
				{temppage=null;}
				cnt++;
				if (cnt==5)
					{
					log.warning("this ehentai lv2 request makes "+cnt+" times!!!"+qvalue+"\r\n"+fmi.GetContent());
					res.getWriter().append("10078#####ehentai lv2 reading fail after 5 try!");
					return;
					}
				 else
					 {
						if(cnt>1)
						{
							log.info("this ehentai lv2 request makes "+cnt+" times!!!"+qvalue);
						}
					 }
				 }
				

				 for(ChapterItem citem : temppage) {
					 ChapterItem tempcitem=new ChapterItem();
					 tempcitem.setdesc(citem.getdesc());
					 tempcitem.seturl(citem.geturl());
					 res1.add(citem);
					}
			}
			 
//			 if(res1.size()==20)
//			 {	 
//			 
//				 ArrayList<ChapterItem> res13=null;
//				 cnt=0;
//				for(int indexpage=2;;indexpage++) 
//				{
//					
//					
//				fmi=new FetchMangaIndex(qvalue+"?p="+indexpage,"no-repeat\"><a href=\"([^\"]+)\"><img alt=\"[^\"]+\" title=\"([^\"]+)\"");
//				res13=this.dokkkdmextra(res, fmi);
//				if(res13==null||res13.size()==0)
//					{
//						break;
//					}
//				else
//					res1.addAll(res13);
//				
//				if(res13!=null||res13.size()<20)
//					break;
//				
//				}
//			 }
			
			//http://sickcrawler.appspot.com/kukuana?qtype=ehentai_lv2&qvalue=http://g.e-hentai.org/g/653068/66367bb936/
		}
		if(qtype.equals("ehentai_lv3"))
			// lv3 is done on lv2
		{
			 ChapterItem errorci=new ChapterItem();
			 errorci.seturl("/404.png");
			 errorci.setdesc("/404.png");
			 fmi=null;
			 res1=null;int cnt=0;
			 while(res1==null){
				 fmi=new FetchMangaIndex(qvalue,"<img id=\"img\" src=\"([^\"]+)\" (style)=");					
					res1=this.dokkkdmextra(res, fmi);						
					if(res1!=null && res1.size()!=0)
					{
						String lv3code=qvalue.split("[/]+")[3];
						
						if(fmi.GetContent().indexOf(lv3code)==-1)
						{	
						res1=null;
						log.info("this ehentai lv3 gets code "+lv3code+"\r\n");
						log.warning("this ehentai lv3 request gets wrong server reply "+qvalue+"\r\n"+fmi.GetContent());
						}
					}
					
					
					if(res1==null || res1.size()==0)
					{
						res1=null;
					}
					cnt++;
					if (cnt==2)
						break;
					
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			 					}
			 
			 if(cnt == 2 && (res1==null || res1.size()==0))
			 {				 
				 log.warning("this ehentai lv3 request makes 3 times and result in error !!!"+qvalue+"\r\n"+fmi.GetContent());
				 res1=new ArrayList<ChapterItem>();
				 res1.add(errorci);
			 }
			 else
			 {
				if(cnt>1)
				{
					log.info("this ehentai lv3 request makes "+cnt+" times!!!"+qvalue);
				}
			 }
			
			
		}
		
		
		
		if(res1 != null)
		{
		/*
		for(int i=0;i<res1.size();i++)
		{
			res.getWriter().append(res1.get(i).geturl()+"#######" + res1.get(i).getdesc() + "\r\n");
			
		}
		*/
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String json = gson.toJson(res1);  
		res.getWriter().append(json);
		
		}
		else
			res.getWriter().append("100001#####resutl null!");
		

	}
	
	public ArrayList<ChapterItem> dokkkdmextra(HttpServletResponse res,FetchMangaIndex fmi) throws IOException
	{
		//String hostname="http://www.kkkmh.com";
		String serres="";
		try{
			 serres=fmi.initreq();
			}catch(SocketTimeoutException ste){
				res.getWriter().append("Read URL time out!!! - "+ste.toString());
			}
				
			if (!serres.equals("OK"))
			{
				//res.getWriter().append(serres);
				return null;
			}		
			//res.getWriter().append(fmi.GetContent());
			
			/*
			Map headersMap=fmi.Getheader();
			Set headers = headersMap.entrySet(); 
			 for(Iterator i = headers.iterator(); i.hasNext();){ 
				  Map.Entry map = (Map.Entry)i.next();
				  res.getWriter().append(map.getKey() + " : " + map.getValue()+ "\n"); 
				  }
			*/
			ArrayList<ChapterItem> res1 = fmi.getIndex();
			

			return res1;
		
	}

}

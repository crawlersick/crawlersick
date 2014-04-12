package com.sick.gooserver.server;

import java.io.IOException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import netfetch.ChapterItem;


import com.google.appengine.api.channel.ChannelFailureException;
import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import java.util.Date;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ChatSev extends HttpServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(pagehandler.class.getName());
//	public static String chatid="1";
	public static int initid=1;
	public static ArrayList<ChapterItem> res1=new ArrayList<ChapterItem>();
	public static ArrayList<String> chatids =new ArrayList<String> (); 
	char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
	String token ="null";
	DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	long minuslong=1396500000000L;
	/*
	  public void init(ServletConfig config) throws ServletException {
		    super.init(config);
			ChannelService channelService = ChannelServiceFactory.getChannelService();
			token = channelService.createChannel(chatid);
	  }
	*/
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException
	{
		req.setCharacterEncoding("UTF-8");  
		res.setHeader("Content-Type", "text/plain; charset=utf-8");
		res.setCharacterEncoding("UTF-8");
		String uid =req.getParameter("uid");
		//String prod =req.getParameter("prod");
		if(uid == null || uid.equals(""))
		{
			res.getWriter().append("Error Query!");
			return;
		}

		if(uid.equals("request_token"))
		{
			
			String clientid;
			synchronized(chatids)
			{
				clientid=String.valueOf(initid);
				chatids.add(clientid);
				initid=initid+1;	
			}
			
			ChannelService channelService = ChannelServiceFactory.getChannelService();
			token = channelService.createChannel(clientid);
			ArrayList<ChapterItem> res1= new ArrayList<ChapterItem>();
			ChapterItem ci = new ChapterItem();
			ci.setdesc(token);
			ci.seturl(clientid);
			res1.add(ci);
			sendmsg(res1,res);
		}else if(uid.equals("sentmsg"))
		{
			String msgcont =req.getParameter("msg");
			ChannelService channelService = ChannelServiceFactory.getChannelService();
			try{
				for(String tempid : chatids)
				{
					log.info("sent channel using client id "+tempid);
					channelService.sendMessage(new ChannelMessage(tempid, msgcont));
				}
				
			
			}catch (ChannelFailureException cfe)
			{
				log.info("Error in channel sent: "+ cfe.toString());
			}
			
			ArrayList<ChapterItem> res1= new ArrayList<ChapterItem>();
			ChapterItem ci = new ChapterItem();
			ci.setdesc("OK");
			ci.seturl("response code");
			res1.add(ci);
			sendmsg(res1,res);
		}

	}
	
	public void sendmsg(ArrayList<ChapterItem> res1,HttpServletResponse res) throws IOException{
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String json = gson.toJson(res1);  
		res.getWriter().append(json);
	}
	public static void deleteid(String idtodel)
	{
		synchronized(chatids)
		{
		boolean removeflag=chatids.remove(idtodel);
		log.info("remove client id "+idtodel+" result: "+ removeflag);
		}
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException
	{
		req.setCharacterEncoding("UTF-8");  
		res.setHeader("Content-Type", "text/plain; charset=utf-8");
		res.setCharacterEncoding("UTF-8");
		String retype=req.getParameter("reqtype");
		Date ChatDate = new Date();
		if(retype.equals("sendmsg"))
		{
			String reqcont=req.getParameter("reqcont");
			String reqchatid=req.getParameter("reqchatid");
			ChapterItem sendmsgfbitem=null;
			ArrayList<ChapterItem> fblist=null;
			if(reqchatid==null || reqchatid.equals("") || reqchatid.equals("undefined"))
			{
				StringBuilder sb = new StringBuilder();
				Random random = new Random();
				for (int i = 0; i < 10; i++) {
				    char c = chars[random.nextInt(chars.length)];
				    sb.append(c);
				}
				sb.append('_');
				reqchatid=sb.toString()+(ChatDate.getTime()-minuslong);
			}
			
			fblist=new ArrayList<ChapterItem> ();
			sendmsgfbitem=new ChapterItem();
			sendmsgfbitem.setdesc(reqchatid);
			sendmsgfbitem.seturl("id");
			fblist.add(sendmsgfbitem);
			
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
			reqcont=URLDecoder.decode(reqcont,"UTF-8");
		//log.info("sendmsg debug info1: "+reqcont);
			if (reqcont.indexOf("exec:history")==0)
			{
				Query q = new Query("searchhist").addSort("searchdate", SortDirection.ASCENDING);
			
				PreparedQuery pq = datastore.prepare(q);
				for (Entity result : pq.asIterable()) 
				{
					sendmsgfbitem=new ChapterItem();
					sendmsgfbitem.setdesc((String) result.getProperty("searchurl"));
					sendmsgfbitem.seturl("hist");
					if(!fblist.contains(sendmsgfbitem))
					{
						fblist.add(sendmsgfbitem);
					}
				}
				
				
			}
			else
			{
				Entity chatitem = new Entity("chatitem");
				chatitem.setProperty("ChatCont", reqcont);
				chatitem.setProperty("Chatid", reqchatid);
	 
				try {
					ChatDate=df.parse(df.format(ChatDate));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				chatitem.setProperty("ChatDate", ChatDate);
				datastore.put(chatitem);
			}
			
			
			sendfeedback(fblist,res);
			//res.getWriter().append(reqchatid);
			return;
		}
		if(retype.equals("getmsg"))
		{
			String reqcont=req.getParameter("reqcont");
			String reqchatid=req.getParameter("reqchatid");
			
			 Date currentdate=null;
			try {
				currentdate = df.parse(reqcont);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 Filter datefilter =
					  new FilterPredicate("ChatDate",
					                      FilterOperator.GREATER_THAN, //   .GREATER_THAN_OR_EQUAL,
					                      currentdate);
			 /*
			 Filter chatidfilter =
					  new FilterPredicate("Chatid", FilterOperator.NOT_EQUAL, reqchatid);
			 
			 Filter totalfilter =
					  CompositeFilterOperator.and(chatidfilter,datefilter);
			 */
			ArrayList<ChapterItem> fblist=new ArrayList<ChapterItem> ();
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();		
			Query q = new Query("chatitem").setFilter(datefilter)
											.addSort("ChatDate", SortDirection.ASCENDING);
			PreparedQuery pq = datastore.prepare(q);
			for (Entity result : pq.asIterable()) {
				  ChapterItem tpci =new ChapterItem();
				  tpci.setdesc((String) result.getProperty("ChatCont"));
				  tpci.seturl((String) result.getProperty("Chatid"));
				  tpci.setDatetime(df.format((Date)result.getProperty("ChatDate")));
				  
				  if (!tpci.geturl().equals(reqchatid))
				  fblist.add(tpci);
				}
			 sendfeedback(fblist,res);
			
			
			return;
		}
		if(retype.equals("getmsg_all"))
		{
			ArrayList<ChapterItem> fblist=new ArrayList<ChapterItem> ();
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();		
			Query q = new Query("chatitem").addSort("ChatDate", SortDirection.ASCENDING);
			PreparedQuery pq = datastore.prepare(q);
			for (Entity result : pq.asIterable()) {
				  
				  ChapterItem tpci =new ChapterItem();
				  tpci.setdesc((String) result.getProperty("ChatCont"));
				  tpci.seturl((String) result.getProperty("Chatid"));
				  tpci.setDatetime(df.format((Date)result.getProperty("ChatDate")));
				  fblist.add(tpci);
				}
			 sendfeedback(fblist,res);
			
			
			return;
		}
	
	}
	public void sendfeedback(ArrayList<ChapterItem> fblist,HttpServletResponse res) throws IOException{
		if(fblist != null)
		{
	
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String json = gson.toJson(fblist);  
		res.getWriter().append(json);
		
		}
	}

}

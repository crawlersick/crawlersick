package com.sick.gooserver.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import netfetch.ChapterItem;

import com.google.appengine.api.channel.ChannelFailureException;
import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelPresence;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class disconnected extends HttpServlet{
	private static final Logger log = Logger.getLogger(pagehandler.class.getName());

	/*
	  public void init(ServletConfig config) throws ServletException {
		    super.init(config);
			ChannelService channelService = ChannelServiceFactory.getChannelService();
			token = channelService.createChannel(chatid);
	  }
	*/
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException
	{
		//req.setCharacterEncoding("UTF-8");  
		//res.setHeader("Content-Type", "text/plain; charset=utf-8");
		//res.setCharacterEncoding("UTF-8");
		//String uid =req.getParameter("uid");
		ChannelService channelService = ChannelServiceFactory.getChannelService();
		ChannelPresence presence = channelService.parsePresence(req);
		log.info("+++++++++++++-----------------disconnected , id is : "+presence.clientId());
		ChatSev.deleteid(presence.clientId());

	}
	
	public void sendmsg(ArrayList<ChapterItem> res1,HttpServletResponse res) throws IOException{
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String json = gson.toJson(res1);  
		res.getWriter().append(json);
	}

}

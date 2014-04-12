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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
public class URLf2 extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(kukuana.class.getName());
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		
		try {
		    URL url = new URL("http://e-hentai.org/");
		    BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
		    String line;

		    while ((line = reader.readLine()) != null) {
		    	res.getWriter().append(line);
		    }
		    reader.close();

		} catch (MalformedURLException e) {
		    // ...
		} catch (IOException e) {
		    // ...
		}
	


	}

}
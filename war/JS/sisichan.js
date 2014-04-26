/*
window.onbeforeunload = function() {
    return 'Leave?';
}
<link href="http://jquery-loadmask.googlecode.com/svn/trunk/src/jquery.loadmask.css" />
<script type='text/javascript' src='http://jquery-loadmask.googlecode.com/svn/trunk/src/jquery.loadmask.js'>
*/

var serverurl="";
var serverurlT="NULL";
var targetanaurl="NULL";
var indexarry;
var counter=0;
var allqtype="";
var mainhost="";
var vvflag=false;
var currentbook="";
var currenpagenum=0;
var maxpagenum=0;
var popupwindowflag=false;
var picloadingflag=false;
var lv1pagenum0=0;
var analystserver="http://sickcrawler-001.appspot.com/";



$(document).ready(function(){
	
	getrecentlist("recdiv");
	
	function getDocHeight() {
	    var D = document;
	    return Math.max(
	        Math.max(D.body.scrollHeight, D.documentElement.scrollHeight),
	        Math.max(D.body.offsetHeight, D.documentElement.offsetHeight),
	        Math.max(D.body.clientHeight, D.documentElement.clientHeight)
	    );
	}
	
	$(window).scroll(function() {
		   if($(window).scrollTop() + $(window).height() > (getDocHeight() * 0.5)) {
				if(allqtype=="ehentai"&&!vvflag&&picloadingflag)
			   {
					vvflag=true;
					currenpagenum=currenpagenum+1;
				if(currenpagenum<maxpagenum)
				{
					lv2hentaipageget("inxspec",allqtype,targetanaurl,serverurl,currenpagenum);
				}
			   }
	
		   }
		   
		   if($(window).scrollTop() + $(window).height() > (getDocHeight() * 0.9)) {
			   if(allqtype=="ehentai"&&!picloadingflag&&lv1pagenum0>0&&!vvflag)
				{vvflag=true;
				lv1pageget(lv1pagenum0);
				lv1pagenum0++;}
		   }
		});
	
	
	$("#urlpicker").keypress(function(e) {
	    if(e.which == 13) {
	    	$("button").click();
	    }
	});
	
	
	$("#urlpicker").focus(
		    function(){
		        //$("#urlpicker").val('');
		        $("#urlpicker").select();
		    });
	
  $("button").click(function(){lv1pageget(0)});

	var keys=(new RegExp('[?|&]' + 'k' + '=' + '([^&;]+?)(&|#|;|$)').exec(location.search)||[,""])[1].replace(/\+/g, '%20');
	if(keys)
	{
		if(keys.match("http://[^/]+/g/[0-9]+/[0-9a-z]+"))
		{
			$("#urlpicker").val(keys);
			$("#button").click();
		}
		else
		{
			if(keys.match("[^/]+/g/[0-9]+/[0-9a-z]+"))
			{
				$("#urlpicker").val("http://"+keys);
				$("#button").click();
			}
			
		}
		
	}
  
  
});

// document ready part end there

function lv1pageget(lv1pagenum){
	$("#taldiv").html('');
	lv1pagenum;
	var ajaxurlstring;
	if(lv1pagenum==0) 
		{lv1pagenum0=1;picloadingflag=false;vvflag=false;}	
	targetanaurl = $("#urlpicker").val();
	if(!targetanaurl){
		targetanaurl="http://g.e-hentai.org/manga/";
		mainhost="";
		serverurl="";
		allqtype="ehentai";
		ajaxurlstring="pagehandler?qtype="+allqtype+"&qvalue="+targetanaurl+"&p="+lv1pagenum;
		}

	targetanaurl=targetanaurl.trim();
	if(targetanaurl.search("http://g.e-hentai.org")==0 || targetanaurl.search("http://exhentai.org")==0)
		{
		allqtype="ehentai";mainhost="";serverurl="";
		ajaxurlstring="pagehandler?qtype="+allqtype+"&qvalue="+targetanaurl+"&p="+lv1pagenum;
		if(targetanaurl.match("http://[^/]+/g/[0-9]+/[0-9a-z]+"))
			{
				$("#taldiv").append("<div id=inxspec>"+targetanaurl+"</div>");
				picloadingflag=true;
				lv2hentaipageget("inxspec",allqtype,targetanaurl,serverurl,currenpagenum);			
				return;
			}
		
		}
	else if(targetanaurl.search("http://www.kkkmh.com")==0)
		{	
		allqtype="kkkmh";mainhost="http://www.kkkmh.com";serverurl='http://mhauto.kkkmh.com';
		ajaxurlstring="kukuana?qtype="+allqtype+"&qvalue="+targetanaurl;
		}
	else
		{

		targetanaurl=targetanaurl.replace(new RegExp('\\s+', 'g'), "%2B");
		//targetanaurl=targetanaurl.replace(" ","%2B");
		targetanaurl="http://exhentai.org/?f_search="+targetanaurl;
		allqtype="ehentai";
		mainhost="";
		serverurl="";
		ajaxurlstring="pagehandler?qtype="+allqtype+"&qvalue="+targetanaurl+"&p="+lv1pagenum;
		}
	vvflag=true;
	$("#LoadingImage").show();
	$.ajax({url:ajaxurlstring,
		timeout: 10000,
		cache: false,
		type: "GET",
		dataType: "json",
		error: function(error) {
			//$("#LoadingImage").hide();
			
			//alert("Error "+error.responseText);
			$("#LoadingImage").append("reloading for app warmup");
			lv1pageget(lv1pagenum);
		},
		success:function(result){
			$("#LoadingImage").hide();
			vvflag=false;
			indexarry = result;
			for(i=0;i<indexarry.length;i++)
			{

				$("#taldiv").append("<div   id=inx"+i+">"+"<a href='javascript:;'>"+indexarry[i]["desc"] +"</a>"+" --- http://sickcrawler.appspot.com/?k="+indexarry[i]["url"] +"</div>"+"<div class='preview' id='picture"+i+"'>"+"<img src='/preimage?imgurl="+indexarry[i]["url2"]+"'</div>");
				//$("#taldiv").append("<div id=inx"+i+">"+"<a href='javascript:;'>"+indexarry[i]["desc"] +"</a>"+" --- http://sickcrawler.appspot.com/?k="+indexarry[i]["url"] +"</div>");
					

				$("#picture"+i).hide();
				$("#inx"+i).mouseover(function() {
					 thisdivid = $(this).attr("id");
					x=thisdivid.replace("inx","");	
					$("#picture"+x).show();
				});
				$("#inx"+i).mouseout(function() {
					thisdivid = $(this).attr("id");
					x=thisdivid.replace("inx","");
					$("#picture"+x).hide();
					});
				
				$("#inx"+i).unbind('click');
				$("#inx"+i).click(function() {			
					 thisdivid = $(this).attr("id");
					x=thisdivid.replace("inx","");	
					xxx=mainhost+indexarry[x]["url"];
					currentbook=indexarry[x]["desc"];
					if (allqtype=="ehentai")
					{
						window.open('?k='+xxx, currentbook);
						return;
					}
					lv2hentaiget(thisdivid,allqtype,xxx,serverurl);
					});

			}		
			
		}});
  }

function lv2hentaipageget(thisdivid,allqtype,xxx,serverurl,currenpagenum)
{
	$("#LoadingImage").show();
	$("#button").prop("disabled", true);
	vvflag=true;
	$.ajax({url:"pagehandler?qtype="+allqtype+"_lv2_getpagenum&qvalue="+xxx+"&p="+currenpagenum,
		type: "GET",
		dataType: "json",
		error: function(error) {
//			alert("Error in page get: "+error.responseText);
			$("#"+thisdivid).append("<div>"+"lv2 Error in page get, reloading: "+error.responseText+"</div> ");
			lv2hentaipageget(thisdivid,allqtype,xxx,serverurl,currenpagenum);
		},
		success:function(result){
		//alert(serverurl+result[0]["url"]);
		$("#"+thisdivid).unbind('click');
		if(currenpagenum==0)
			{
			maxpagenum=result[0]["desc"];
			result.shift();
			}
		
			 loopitemfun(thisdivid,result,allqtype,result.length,0);

								},
	timeout: 10000,
	cache: false
	});	
	
}

function lv2hentaiget(thisdivid,allqtype,xxx,serverurl)
{
	$.ajax({url:"kukuana?qtype="+allqtype+"_lv2&qvalue="+xxx,
		type: "GET",
		dataType: "json",
		error: function(error) {
			alert("Error "+error.responseText);
			lv2hentaiget(thisdivid,allqtype,xxx,serverurl);
		},
		success:function(result){
		//alert(serverurl+result[0]["url"]);
		$("#"+thisdivid).unbind('click');
		 if (allqtype=="ehentai")
				 {
			 loopitemfun(thisdivid,result,allqtype,result.length,0);
			
				 }		
		 else{
			for(j=0;j<result.length;j++)
				{			
					$("#"+thisdivid).append("<div> <img src="+serverurl+result[j]["url"]+" /></div> ");
				}
		 }
			//	$("#"+thisdivid).click(function{alert('this capter done!');});
				 
			//	$("#"+thisdivid).click(function() { alert(($("#"+thisdivid).html().split("<div>"))[0]+"done!!!"); });
			//	$("#"+thisdivid).unmask();
	},
	timeout: 10000,
	cache: false
	});	
}


function loopitemfun(thiselem,urlitem,allqtype,maxlength,loopii)
{
	if(loopii>=maxlength) 
		{
		$("#LoadingImage").hide();
		$("#button").prop("disabled", false);
		vvflag=false;
		return;}
	$.ajax({
        type:"GET",
        url: analystserver+"kukuana?qtype="+allqtype+"_lv3&qvalue="+urlitem[loopii]["url"],
        xhrFields: {
            // The 'xhrFields' property sets additional fields on the XMLHttpRequest.
            // This can be used to set the 'withCredentials' property.
            // Set the value to 'true' if you'd like to pass cookies to the server.
            // If this is enabled, your server must respond with the header
            // 'Access-Control-Allow-Credentials: true'.
            withCredentials: false
          },
		dataType: "json",
		timeout: 10000,
		cache: false,
		async: true,
		error: function(error) {
			$("#"+thiselem).append("Error "+error.responseText);
			$("#"+thiselem).append("<div>"+"Server to busy 1. re-loading - timeout :"+ urlitem[loopii]["url"] +"</div> ");
			loopitemfun(thiselem,urlitem,allqtype,maxlength,loopii);
		},
		success: function(data){
			
			if (data[0]["url"]=="/404.png")
				{
				$("#"+thiselem).append("<div>"+"Server to busy 2. re-loading  -  :"+ urlitem[loopii]["url"] +"</div> ");
				if(analystserver=="http://sorryformynet.appspot.com/")
					{analystserver="http://sickcrawler-001.appspot.com/";}
				else
					{analystserver="http://sorryformynet.appspot.com/";}
				setTimeout(
						function(){loopitemfun(thiselem,urlitem,allqtype,maxlength,loopii);}
						,60000);
				return;
				}
				
        	$("#"+thiselem).append("<div> <img src="+data[0]["url"]+" /></div> ");
        	
			$("#"+thiselem).children().last().click(function() {
				var loadingimg='http://lh3.googleusercontent.com/-czzb5jvQhcI/Uqu4YvHh6LI/AAAAAAAAAzc/jL8QhmDfq2Y/w426-h639/ryomou_shimei_b_w_by_grishnakh666-d6jciz7.jpg';
				var tempchile=$(this).children().last();
				
				var temppicurl=tempchile.attr('src');
				
				if (temppicurl==loadingimg)
					return;
				
				tempchile.attr('src', loadingimg);
				tempchile.load(function(){
					d = new Date();
					tempchile.attr('src', temppicurl+'#'+d.getTime());
					//alert('updated');
					});
				
				//$(this).children().last().attr('src', $(this).children().last().attr('src')+'#'+Math.random());
				//$(this).children().last().attr('src', 'http://static.adzerk.net/Advertisers/4c4f1be011a447efbce49c1811022e7a.png');
		    });
        	
        	//alert(thiselem+"======="+data[0]['url']);
			loopitemfun(thiselem,urlitem,allqtype,maxlength,loopii+1);
        }
    });
	
}

function getrecentlist(targetdiv){
	$.ajax({url:"pagehandler?qtype=ehentai_get_recentitems&qvalue=nnn&p=nnn",
		type: "GET",
		dataType: "json",
		error: function(error) {
			$("#"+targetdiv).append("<div> no recent list get </div> ");
		},
		success:function(result){
			$("#"+targetdiv).append("<div> recent:");
			for(j=0;j<result.length;j++)
				{			
				
				if(result[j]["url"]=="myword")
					{
						$("#divword").html("<h6>"+result[j]["desc"]+"</h6>");
					}
				else
					{
					$("#"+targetdiv).append("<a href=\"/?k="+result[j]["url"]+"\">"+result[j]["desc"].split("/")[4]+" </a> | ");
					}
				}
			$("#"+targetdiv).append("</div>");

	},
	timeout: 10000,
	cache: false
	});	
}




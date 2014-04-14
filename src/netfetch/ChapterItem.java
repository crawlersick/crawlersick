package netfetch;


public class ChapterItem {
private String url;
private String desc;
private String url2;
private String desc2;
private String datetime;

public String getUrl2() {
	return url2;
}
public void setUrl2(String url2) {
	this.url2 = url2;
}
public String getDesc2() {
	return desc2;
}
public void setDesc2(String desc2) {
	this.desc2 = desc2;
}

public String getDatetime() {
	return datetime;
}
public void setDatetime(String datetime) {
	this.datetime = datetime;
}
public void seturl(String url)
{
	this.url=url;
}
public void setdesc(String desc)
{
	this.desc=desc;
}
public String geturl()
{
	return url;
}
public String getdesc()
{
	return desc;
}

public int hashCode(Object o){
    return (int)	url.hashCode() *
    		desc.hashCode();
  }

public boolean equals(Object o){
    if(o == null)                return false;
    if(!(o instanceof ChapterItem)) return false;

    ChapterItem other = (ChapterItem) o;
    if(! this.desc.equals(other.desc)) return false;
    if(! this.url.equals(other.url))   return false;

    return true;
  }


}

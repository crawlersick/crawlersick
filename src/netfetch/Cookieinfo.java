package netfetch;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
@PersistenceCapable
public class Cookieinfo {
	
@PrimaryKey
@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
private Key key;
@Persistent
private String hashid;
@Persistent
private String hashps;
@Persistent
private String others;

public Cookieinfo(String hashid, String hashps, String others) {
	this.hashid = hashid;
	this.hashps = hashps;
	this.others = others;
}

public Key getKey() {
    return key;
}

public String getHashid() {
	return hashid;
}



public void setHashid(String hashid) {
	this.hashid = hashid;
}



public String getHashps() {
	return hashps;
}



public void setHashps(String hashps) {
	this.hashps = hashps;
}



public String getOthers() {
	return others;
}



public void setOthers(String others) {
	this.others = others;
}


/*
public boolean equals(Object o){
    if(o == null)                return false;
    if(!(o instanceof Cookieinfo)) return false;

    Cookieinfo other = (Cookieinfo) o;
    if(! this.hashid.equals(other.hashid)) return false;
    if(! this.hashps.equals(other.hashps))   return false;

    return true;
  }
*/

}

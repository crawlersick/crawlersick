package netfetch;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class Mywords {
@PrimaryKey
@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
private Key key;
@Persistent
private String contenttext1;
@Persistent
private String contenttext2;
@Persistent
private String contenttext3;
@Persistent
private int p;
@Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result
			+ ((contenttext1 == null) ? 0 : contenttext1.hashCode());
	result = prime * result
			+ ((contenttext2 == null) ? 0 : contenttext2.hashCode());
	result = prime * result
			+ ((contenttext3 == null) ? 0 : contenttext3.hashCode());
	result = prime * result + p;
	return result;
}
public Mywords(String contenttext1, String contenttext2, String contenttext3,
		int p) {
	super();
	this.contenttext1 = contenttext1;
	this.contenttext2 = contenttext2;
	this.contenttext3 = contenttext3;
	this.p = p;
}
@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (getClass() != obj.getClass())
		return false;
	Mywords other = (Mywords) obj;
	if (contenttext1 == null) {
		if (other.contenttext1 != null)
			return false;
	} else if (!contenttext1.equals(other.contenttext1))
		return false;
	if (contenttext2 == null) {
		if (other.contenttext2 != null)
			return false;
	} else if (!contenttext2.equals(other.contenttext2))
		return false;
	if (contenttext3 == null) {
		if (other.contenttext3 != null)
			return false;
	} else if (!contenttext3.equals(other.contenttext3))
		return false;
	if (p != other.p)
		return false;
	return true;
}

public String getContenttext1() {
	return contenttext1;
}
public void setContenttext1(String contenttext1) {
	this.contenttext1 = contenttext1;
}
public String getContenttext2() {
	return contenttext2;
}
public void setContenttext2(String contenttext2) {
	this.contenttext2 = contenttext2;
}
public String getContenttext3() {
	return contenttext3;
}
public void setContenttext3(String contenttext3) {
	this.contenttext3 = contenttext3;
}
public int getP() {
	return p;
}
public void setP(int p) {
	this.p = p;
}
}

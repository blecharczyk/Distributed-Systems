package com.blecharczyk;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class LocationID {

@SerializedName("title")
@Expose
private String title;
@SerializedName("location_type")
@Expose
private String locationType;
@SerializedName("woeid")
@Expose
private String woeid;
@SerializedName("latt_long")
@Expose
private String lattLong;

public String getTitle() {
return title;
}

public void setTitle(String title) {
this.title = title;
}

public String getLocationType() {
return locationType;
}

public void setLocationType(String locationType) {
this.locationType = locationType;
}

public String getWoeid() {
return woeid;
}

public void setWoeid(String woeid) {
this.woeid = woeid;
}

public String getLattLong() {
return lattLong;
}

public void setLattLong(String lattLong) {
this.lattLong = lattLong;
}

@Override
public String toString() {
	return "LocationID [title=" + title + ", locationType=" + locationType + ", woeid=" + woeid + ", lattLong="
			+ lattLong + "]";
}



}
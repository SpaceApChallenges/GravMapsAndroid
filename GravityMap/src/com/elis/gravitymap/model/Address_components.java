
package com.elis.gravitymap.model;

import java.sql.Types;
import java.util.List;

public class Address_components{
   	private String long_name;
   	private String short_name;
   	private List<Types> types;

 	public String getLong_name(){
		return this.long_name;
	}
	public void setLong_name(String long_name){
		this.long_name = long_name;
	}
 	public String getShort_name(){
		return this.short_name;
	}
	public void setShort_name(String short_name){
		this.short_name = short_name;
	}
 	public List<Types> getTypes(){
		return this.types;
	}
	public void setTypes(List<Types> types){
		this.types = types;
	}
}

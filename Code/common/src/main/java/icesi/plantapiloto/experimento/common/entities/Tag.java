package icesi.plantapiloto.experimento.common.entities;

import java.sql.Timestamp;

public class Tag {
	
	private String name;
	private Integer value;
	private Timestamp time;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}
	public Timestamp getTime() {
		return time;
	}
	public void setTime(Timestamp time) {
		this.time = time;
	}	

}

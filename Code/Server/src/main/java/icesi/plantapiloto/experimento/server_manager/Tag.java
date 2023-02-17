package icesi.plantapiloto.experimento.server_manager;

import java.security.Timestamp;

public class Tag {
	 String name;
	Double value;
	Timestamp timeTag;
	
	
	public Tag (String name, Double value, Timestamp timeTag) {
		
		this.name=name;
		this.value=value;
		this.timeTag=timeTag;
		
		
	}

	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public Double getValue() {
		return value;
	}


	public void setValue(Double value) {
		this.value = value;
	}


	public Timestamp getTimeTag() {
		return timeTag;
	}


	public void setTimeTag(Timestamp timeTag) {
		this.timeTag = timeTag;
	}
	
	
	
	

}

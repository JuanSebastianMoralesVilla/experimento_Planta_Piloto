package icesi.plantapiloto.experimento.common.entities;

import java.io.Serializable;
import java.sql.Timestamp;

public class Measure implements Serializable {
    private String value;
    private String name;
    private Timestamp requestTime;

    public Measure() {

    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the request time
     */
    public Timestamp getRequestTime(){
        return this.requestTime;
    }

    /**
     * @param time the request time to set
     */
    public void setRequestTime(Timestamp time){
        this.requestTime = time;
    }

}

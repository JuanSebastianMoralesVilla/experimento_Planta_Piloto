package icesi.plantapiloto.experimento.common.entities;
public class Experiment {
        String experiment_name;
        long duration_second;
        long lapse;
        int server_ammount;
        int tag_ammount;
        int repeats;

    public String getExperimentName() {
        return experiment_name;
    }
    
    public long getDurationSecond() {
        return duration_second;
    }
    
    public long getLapse() {
        return lapse;
    }
    
    public int getServerAmount() {
        return server_ammount;
    }
    
    public int getTagAmount() {
        return tag_ammount;
    }
    
    public int getRepeats() {
        return repeats;
    }
    public void setExperimentName(String experiment_name) {
    this.experiment_name = experiment_name;
}

public void setDurationSecond(long duration_second) {
    this.duration_second = duration_second;
}

public void setLapse(long lapse) {
    this.lapse = lapse;
}

public void setServerAmount(int server_ammount) {
    this.server_ammount = server_ammount;
}

public void setTagAmount(int tag_ammount) {
    this.tag_ammount = tag_ammount;
}

public void setRepeats(int repeats) {
    this.repeats = repeats;
}
}
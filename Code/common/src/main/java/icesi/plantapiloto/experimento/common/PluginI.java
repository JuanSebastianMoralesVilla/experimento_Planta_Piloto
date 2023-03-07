package icesi.plantapiloto.experimento.common;

import java.io.IOException;

import icesi.plantapiloto.experimento.common.entities.Message;

public interface PluginI {
    
    public Message getMessage();

    public void connect() throws IOException;

    public void disconnet() throws IOException;
}

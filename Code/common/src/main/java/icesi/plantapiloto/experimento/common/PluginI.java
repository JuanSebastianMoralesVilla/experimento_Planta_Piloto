package icesi.plantapiloto.experimento.common;

import java.util.HashMap;

import icesi.plantapiloto.experimento.common.entities.Message;

public interface PluginI {
    public Message getMessage();

    public HashMap<String, String> getSettings();
}

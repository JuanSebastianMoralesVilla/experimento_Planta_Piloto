package icesi.plantapiloto.experimento.common;

import java.util.Properties;

import icesi.plantapiloto.experimento.common.entities.Message;

public interface PluginI {
    public Message getMessage();
    public Properties getSettings();
    public void setSettings(Properties props);
    public void addSettings(Properties props);
}

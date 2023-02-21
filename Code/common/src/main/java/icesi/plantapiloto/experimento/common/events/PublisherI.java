package icesi.plantapiloto.experimento.common.events;

import icesi.plantapiloto.experimento.common.encoders.ObjectEncoder;

public interface PublisherI {

    public void setEncoder(ObjectEncoder encoder);

    public void setHost(String host);

    public void setName(String name);

    public void setTopic(String topic);

    public <T> void publish(T msg);

    public void close();

    public void connect();

}
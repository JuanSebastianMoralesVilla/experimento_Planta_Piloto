package icesi.plantapiloto.experimento.common.events;

import icesi.plantapiloto.experimento.common.encoders.ObjectEncoder;

public interface SubscriberI {
    public void setEncoder(ObjectEncoder encoder);

    public ObjectEncoder getEncoder();

    public void setHost(String host);

    public void setName(String name);

    public <T> void subscribe(String topic, CallbackSubI call, Class<T> type);

    public void close();

    public void connect();

}

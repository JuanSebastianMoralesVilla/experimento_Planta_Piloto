package icesi.plantapiloto.experimento.common.encoders;

public interface ObjectEncoder {

    public <T> String encode(T message);

    public <T> T decode(String json, Class<T> type);
}

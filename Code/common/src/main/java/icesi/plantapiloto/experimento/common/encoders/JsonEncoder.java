package icesi.plantapiloto.experimento.common.encoders;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;



public class JsonEncoder implements ObjectEncoder {

    private Gson parser;

    public JsonEncoder() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new DateSerializer());
        builder.registerTypeAdapter(Date.class, new DateDeserializer());
        parser = builder.create();
    }

    @Override
    public <T> String encode(T message) {
        return parser.toJson(message);
    }

    @Override
    public <T> T decode(String json, Class<T> type) {
        return parser.fromJson(json, type);
    }

    private static class DateSerializer implements JsonSerializer<Date> {

        @Override
        public JsonElement serialize(Date date, Type type, JsonSerializationContext context) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            String dateString = dateFormat.format(date);
            return new JsonPrimitive(dateString);
        }
    }

    private static class DateDeserializer implements JsonDeserializer<Date> {

        @Override
        public Date deserialize(JsonElement json, Type type, JsonDeserializationContext context)
                throws JsonParseException {
            String dateString = json.getAsJsonPrimitive().getAsString();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            try {
                return dateFormat.parse(dateString);
            } catch (ParseException e) {
                throw new JsonParseException("Error parsing date string: " + dateString, e);
            }
        }
    }

}

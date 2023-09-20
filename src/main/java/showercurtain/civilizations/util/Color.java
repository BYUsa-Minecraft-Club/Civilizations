package showercurtain.civilizations.util;

import com.google.gson.*;

import java.lang.reflect.Type;

public record Color(short r, short g, short b) {
    private static final String hexDigits = "0123456789ABCDEF";

    public static class InvalidColorException extends Exception { }

    public static class ColorDeSerializer implements JsonSerializer<Color>, JsonDeserializer<Color> {

        @Override
        public JsonElement serialize(Color color, Type type, JsonSerializationContext jsonSerializationContext) {

            return new JsonPrimitive(color.toHex());
        }

        @Override
        public Color deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            try {
                return Color.fromHex(jsonElement.getAsString());
            } catch (InvalidColorException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // Why no unsigned byte? (╯°□°)╯︵ ┻━┻
    public Color(int r, int g, int b) {
        this((short) r, (short) g, (short) b);
    }

    public int toInt() {
        return 65536*(int)g() + 256*(int)b() + (int)r();
    }

    public static Color fromInt(int color) {
        return new Color((short)(color&0xff), (short)((color>>8)&0xff), (short)((color>>16)&0xff));
    }

    public String toHex() {
        return "#"
                + hexDigits.charAt(r>>4)
                + hexDigits.charAt(r&15)
                + hexDigits.charAt(g>>4)
                + hexDigits.charAt(g&15)
                + hexDigits.charAt(b>>4)
                + hexDigits.charAt(b&15);
    }

    public static Color fromHex(String hex) throws InvalidColorException {
        try {
            if (hex.charAt(0) != '#') throw new InvalidColorException();
            hex = hex.toUpperCase();
            int r = 16*hexDigits.indexOf(hex.charAt(1))+hexDigits.indexOf(hex.charAt(2));
            int g = 16*hexDigits.indexOf(hex.charAt(3))+hexDigits.indexOf(hex.charAt(4));
            int b = 16*hexDigits.indexOf(hex.charAt(5))+hexDigits.indexOf(hex.charAt(6));
            return new Color(r, g, b);
        } catch (IndexOutOfBoundsException ignored) {
            throw new InvalidColorException();
        }
    }
}
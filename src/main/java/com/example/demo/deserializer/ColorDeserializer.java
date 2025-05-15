package com.example.demo.deserializer;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceGray;

import java.io.IOException;

public class ColorDeserializer extends JsonDeserializer<Color> {

    @Override
    public Color deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String colorHex = p.getText();
        return decodeColor(colorHex);
    }

    private Color decodeColor(String hex) {
        // Assuming the hex format is "RRGGBB"
        if (hex != null && hex.length() == 6) {
            int r = Integer.parseInt(hex.substring(0, 2), 16);
            int g = Integer.parseInt(hex.substring(2, 4), 16);
            int b = Integer.parseInt(hex.substring(4, 6), 16);

            return new DeviceGray(r / 255.0f); // For grayscale, you can map it to one value (average)
        }
        return new DeviceGray(0); // Default to black if the hex is invalid
    }
}


package sturdycobble.createrevision.utils;

import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.DyeColor;

public class RGBColor {

    protected float[] color;

    public RGBColor(float r, float g, float b) {
        color = new float[]{r, g, b};
    }

    public RGBColor(int r, int g, int b) {
        color = new float[]{r / 255F, g / 255F, b / 255F};
    }

    public RGBColor(float[] rgb) {
        if (rgb.length == 3) {
            color = rgb;
        } else {
            color = new float[]{1, 1, 1};
        }
    }

    public RGBColor(DyeColor dyeColor) {
        color = dyeColor.getTextureDiffuseColors();
    }

    public static RGBColor fromInt(int num) {
        int b = num % 256;
        int g = (num >> 8) % 256;
        int r = (num >> 16) % 256;
        return new RGBColor(r, g, b);
    }

    public static RGBColor byName(String name) {
        return new RGBColor(DyeColor.byName(name, DyeColor.WHITE));
    }

    public static RGBColor fromJsonFloat(JsonObject entry) {
        float r = 1F;
        float g = 1F;
        float b = 1F;

        if (GsonHelper.isValidNode(entry, "red") &&
                GsonHelper.isValidNode(entry, "blue") && GsonHelper.isValidNode(entry, "green")) {
            r = GsonHelper.getAsFloat(entry, "red");
            g = GsonHelper.getAsFloat(entry, "green");
            b = GsonHelper.getAsFloat(entry, "blue");
        }
        return new RGBColor(r, g, b);
    }

    public static RGBColor white() {
        return new RGBColor(1F, 1F, 1F);
    }

    public static float squareDistance(RGBColor a, RGBColor b) {
        float distance = 0;
        for (int i = 0; i < 3; i++)
            distance += (a.asFloatArray()[i] - b.asFloatArray()[i]) * (a.asFloatArray()[i] - b.asFloatArray()[i]);
        return distance;
    }

    public float[] asFloatArray() {
        return color;
    }

    public String toHex() {
        return Integer.toHexString(toInt()).toUpperCase();
    }

    public int toInt() {
        return Math.round(color[0] * 255F * 256F * 256F + color[1] * 255F * 256F + color[2] * 255F);
    }

}

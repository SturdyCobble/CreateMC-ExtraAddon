package sturdycobble.createrevision.utils;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.DyeColor;

import java.awt.*;
import java.util.Random;
import java.util.function.Predicate;

public class RGBColor {

    protected float[] color;

    public RGBColor(float r, float g, float b) {
        color = new float[]{r, g, b};
    }

    public RGBColor(float[] rgb) {
        if (rgb.length == 3) {
            color = rgb;
        } else {
            color = new float[]{249F / 255F, 1F, 254F / 255F};
        }
    }

    public RGBColor(DyeColor dyeColor) {
        color = dyeColor.getTextureDiffuseColors();
    }

    public RGBColor(int num) {
        float b = num % 256;
        float g = (num >>> 8) % 256;
        float r = (num >>> 16) % 256;
        color = new float[]{r / 255F, g / 255F, b / 255F};
    }

    public static RGBColor byString(String colorString) {
        if (colorString.startsWith("#")) {
            return RGBColor.byHex(colorString);
        } else {
            return RGBColor.byName(colorString);
        }
    }

    public static RGBColor byName(String name) {
        return new RGBColor(DyeColor.byName(name, DyeColor.WHITE));
    }

    public static RGBColor byHex(String hexString) {
        String hexCode = hexString.substring(1);
        try {
            return new RGBColor(Integer.parseInt(hexCode, 16));
        } catch (NumberFormatException ex) {
            return RGBColor.white();
        }
    }

    public static RGBColor white() {
        return new RGBColor(DyeColor.WHITE);
    }

    public static float squareRGBDistance(RGBColor a, RGBColor b) {
        float distance = 0;
        for (int i = 0; i < 3; i++)
            distance += (a.asFloatArray()[i] - b.asFloatArray()[i]) * (a.asFloatArray()[i] - b.asFloatArray()[i]);
        return distance;
    }

    public static RGBColor fromHSBFloatArray(float[] hsb) {
        if (hsb.length != 3)
            return RGBColor.white();
        Color new_color = new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]), true);
        return new RGBColor(new_color.getRed() / 255F, new_color.getGreen() / 255F, new_color.getBlue() / 255F);
    }

    public static ColorCondition getColorMatchConditionFromString(String color) {
        return ColorConditions.COLOR.create(RGBColor.byString(color));
    }

    public static RGBColor getRandomColor(long seed1, long seed2) {
        Random gen1 = new Random(seed1);
        int result1 = gen1.nextInt(15);
        RGBColor color1 = new RGBColor(DyeColor.byId(result1));
        Random gen2 = new Random(seed2);
        int result2 = gen2.nextInt(15);
        RGBColor color2 = new RGBColor(DyeColor.byId(result2));
        return color1.mixWith(color2);
    }

    public float[] asFloatArray() {
        return color;
    }

    public String toHex() {
        String hex = Integer.toHexString(toInt()).toUpperCase();
        if (hex.length() < 6) {
            hex = "0".repeat(6 - hex.length()) + hex;
        }
        return hex;
    }

    public int toInt() {
        return Math.round(color[0] * 255F * 256 * 256 + color[1] * 255F *256 + color[2] * 255F);
    }

    public float[] asHSBFloatArray() {
        float[] hsb = Color.RGBtoHSB((int) (color[0] * 255F), (int) (color[1] * 255F), (int) (color[2] * 255F), null);
        return hsb;
    }

    public RGBColor mixWith(RGBColor rgb2) {
        float[] color3 = new float[3];
        float[] color2 = rgb2.asFloatArray();
        for (int i = 0; i < 3; i++) {
            color3[i] = (color[i] + color2[i]) / 2;
        }
        return new RGBColor(color3);
    }

    public enum RequiredType {

        NONE(o -> true),
        COLOR(o -> o instanceof RGBColor),
        RADIUS(o -> o instanceof Pair && ((Pair) o).getFirst() instanceof RGBColor && ((Pair) o).getSecond() instanceof Float),
        RANGE(o -> o instanceof float[] && ((float[]) o).length == 2),
        CUBE_RANGE(o -> o instanceof float[] && ((float[]) o).length == 6),
        LONG2(o -> o instanceof long[] && ((long[]) o).length == 2);

        private final Predicate<Object> isValid;

        RequiredType(Predicate<Object> isValid) {
            this.isValid = isValid;
        }

        public boolean isValidType(Object obj) {
            return isValid.test(obj);
        }

    }

}

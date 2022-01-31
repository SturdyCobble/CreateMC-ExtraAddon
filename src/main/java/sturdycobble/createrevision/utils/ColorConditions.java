package sturdycobble.createrevision.utils;

import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import sturdycobble.createrevision.CreateRevision;
import sturdycobble.createrevision.init.ModConfigs;
import sturdycobble.createrevision.utils.RGBColor.RequiredType;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Predicate;

public enum ColorConditions {

    NONE(pre(0, "none", RequiredType.NONE, c -> true).addDescriptionGenerator(textDescription("none.desc"))),
    HAS_HIGH_CHROMA(pre(1, "high_chroma", RequiredType.NONE, c -> c.asHSBFloatArray()[1] >= ModConfigs.getHighChromaThreshold()).addDescriptionGenerator(
            o -> new Component[]{translate("high_chroma.desc").withStyle(ChatFormatting.BLUE)}
    )),
    HAS_LOW_CHROMA(pre(2, "low_chroma", RequiredType.NONE, c -> c.asHSBFloatArray()[1] <= ModConfigs.getLowChromaThreshold()).addDescriptionGenerator(
            o -> new Component[]{translate("low_chroma.desc").withStyle(ChatFormatting.BLACK)}
    )),
    HAS_HIGH_BRIGHTNESS(pre(3, "high_value", RequiredType.NONE, c -> c.asHSBFloatArray()[2] >= ModConfigs.getHighValueThreshold()).addDescriptionGenerator(
            o -> new Component[]{translate("high_value.desc").withStyle(ChatFormatting.WHITE)}
    )),
    HAS_LOW_BRIGHTNESS(pre(4, "low_value", RequiredType.NONE, c -> c.asHSBFloatArray()[2] <= ModConfigs.getLowValueThreshold()).addDescriptionGenerator(
            o -> new Component[]{translate("low_value.desc").withStyle(ChatFormatting.BLACK)}
    )),
    COLOR(pre(5, "color", RequiredType.COLOR, (c, d) -> RGBColor.squareRGBDistance(c, (RGBColor) d) <= ModConfigs.getMinimumDistinguishableRGBDistanceSquared()).addDescriptionGenerator(
            o -> new Component[]{translate("color.desc", ((RGBColor) o).toHex()).withStyle(Style.EMPTY.applyFormat(ChatFormatting.BOLD).withColor(((RGBColor) o).toInt()))}
    )),
    COLOR_RADIUS(pre(6, "color_radius", RequiredType.RADIUS, (c, r) -> {
        Pair<RGBColor, Float> pair = (Pair<RGBColor, Float>) r;
        return RGBColor.squareRGBDistance(c, pair.getFirst()) <= pair.getSecond();
    }).addDescriptionGenerator(
            o -> {
                Pair<RGBColor, Float> pair = (Pair<RGBColor, Float>) o;
                return new Component[]{
                        translate("color_radius.desc1", pair.getFirst().toHex()).withStyle(Style.EMPTY.applyFormat(ChatFormatting.BOLD).withColor(pair.getFirst().toInt())),
                        translate("color_radius.desc2", Math.sqrt(pair.getSecond())).withStyle(ChatFormatting.BOLD)};
            }
    )),
    HUE_RANGE(pre(7, "hue_range", RequiredType.RANGE, (c, r) -> inRange(c.asHSBFloatArray()[0], (float[]) r)).addDescriptionGenerator(
            o -> {
                float[] pair = (float[]) o;
                return new Component[]{
                        translate("range.desc_from_hue", Math.round(pair[0] * 360)).withStyle(Style.EMPTY.applyFormat(ChatFormatting.BOLD).withColor(getRepresentativeHue(pair[0]))),
                        translate("range.desc_to_degree", Math.round(pair[1] * 360)).withStyle(Style.EMPTY.applyFormat(ChatFormatting.BOLD).withColor(getRepresentativeHue(pair[1])))};
            }
    )),
    SAT_RANGE(pre(8, "saturation_range", RequiredType.RANGE, (c, r) -> inRange(c.asHSBFloatArray()[0], ((float[]) r))).addDescriptionGenerator(
            o -> {
                float[] pair = (float[]) o;
                return new Component[]{
                        translate("range.desc_from_sat", Math.round(pair[0] * 100)).withStyle(Style.EMPTY.applyFormat(ChatFormatting.BOLD).withColor(getRepresentativeSat(pair[0]))),
                        translate("range.desc_to_percent", Math.round(pair[1] * 100)).withStyle(Style.EMPTY.applyFormat(ChatFormatting.BOLD).withColor(getRepresentativeSat(pair[1])))};
            }
    )),
    VAL_RANGE(pre(9, "value_range", RequiredType.RANGE, (c, r) -> inRange(c.asHSBFloatArray()[2], ((float[]) r))).addDescriptionGenerator(
            o -> {
                float[] pair = (float[]) o;
                return new Component[]{
                        translate("range.desc_from_val", Math.round(pair[0] * 100)).withStyle(Style.EMPTY.applyFormat(ChatFormatting.BOLD).withColor(getRepresentativeVal(pair[0]))),
                        translate("range.desc_to_percent", Math.round(pair[1] * 100)).withStyle(Style.EMPTY.applyFormat(ChatFormatting.BOLD).withColor(getRepresentativeVal(pair[1])))};
            }
    )),
    HSB_RANGE(pre(10, "hsb_range", RequiredType.CUBE_RANGE, (c, r) -> inCubeRange(c.asHSBFloatArray(), (float[]) r)).addDescriptionGenerator(
            o -> {
                float[] ranges = (float[]) o;
                float[] color1 = new float[]{ranges[0], ranges[2], ranges[4]};
                float[] color2 = new float[]{ranges[1], ranges[3], ranges[5]};
                return new Component[]{
                        translate("range.desc_from_hsb", RGBColor.fromHSBFloatArray(color1).toHex()).withStyle(Style.EMPTY.applyFormat(ChatFormatting.BOLD).withColor(getRepresentativeHSB(color1))),
                        translate("range.desc_to_hsb", RGBColor.fromHSBFloatArray(color2).toHex()).withStyle(Style.EMPTY.applyFormat(ChatFormatting.BOLD).withColor(getRepresentativeHSB(color2)))};
            }
    )),
    RANDOM(pre(11, "random", RequiredType.LONG2, (c, d) -> {
        return RGBColor.squareRGBDistance(c, RGBColor.getRandomColor(((long[]) d)[0], ((long[]) d)[1])) <= ModConfigs.getMinimumDistinguishableRGBDistanceSquared();
    }).addDescriptionGenerator(textDescription("random.desc")));

    private final Precondition precondition;

    ColorConditions(Precondition pre) {
        precondition = pre;
    }

    private static Precondition pre(int ordinal, String id, RequiredType required, Predicate<RGBColor> func) {
        return new Precondition(id, required, (a, b) -> func.test(a), ordinal);
    }

    private static Precondition pre(int ordinal, String id, RequiredType required, ColorCondition.ColorFunction func) {
        return new Precondition(id, required, func, ordinal);
    }

    public static Function<Object, Component[]> textDescription(String key) {
        return (o) -> new Component[]{translate(key).withStyle(ChatFormatting.BOLD)};
    }

    public static TranslatableComponent translate(String key, Object... args) {
        return new TranslatableComponent(CreateRevision.MODID + ".jei.beacon_depot." + key, args);
    }

    public static TranslatableComponent translate(String key) {
        return new TranslatableComponent(CreateRevision.MODID + ".jei.beacon_depot." + key);
    }

    public static ColorConditions fromName(String name) {
        for (ColorConditions val : ColorConditions.values()) {
            if (val.precondition.getId().equals(name))
                return val;
        }
        return ColorConditions.NONE;
    }

    private static boolean inRange(float value, float[] range) {
        if (range.length != 2)
            return false;
        float lhs = range[0];
        float rhs = range[1];

        return lhs <= rhs ? (lhs <= value && value <= rhs) : (value <= rhs || value >= lhs);
    }

    private static boolean inCubeRange(float[] vals, float[] rans) {
        if (vals.length != 3 && rans.length != 6)
            return false;
        boolean first = rans[0] <= rans[1] ? (rans[0] <= vals[0] && vals[0] <= rans[1]) : (vals[0] >= rans[0] || vals[0] <= rans[1]);
        boolean second = rans[2] <= rans[3] ? (rans[2] <= vals[1] && vals[1] <= rans[3]) : (vals[1] >= rans[2] || vals[1] <= rans[3]);
        boolean third = rans[4] <= rans[5] ? (rans[4] <= vals[2] && vals[2] <= rans[5]) : (vals[2] >= rans[4] || vals[2] <= rans[5]);
        return first && second && third;
    }

    private static int getRepresentativeHue(float hue) {
        return RGBColor.fromHSBFloatArray(new float[]{hue, 0.5F, 0.5F}).toInt();
    }

    private static int getRepresentativeSat(float sat) {
        return RGBColor.fromHSBFloatArray(new float[]{0.6F, sat, 0.5F}).toInt();
    }

    private static int getRepresentativeVal(float val) {
        return RGBColor.fromHSBFloatArray(new float[]{0.5F, 0.6F, val}).toInt();
    }

    private static int getRepresentativeHSB(float[] hsb) {
        return RGBColor.fromHSBFloatArray(hsb).toInt();
    }

    public ColorCondition create(@Nullable Object subcondition) {
        if (precondition.required == RequiredType.NONE)
            subcondition = null;
        if (!matchDetailType(subcondition)) {
            CreateRevision.LOGGER.warn("ColorCondition's Sub-condition Type does not match.");
        }
        return new ColorCondition(precondition.ordinal, precondition.id, precondition.required, precondition.func, subcondition)
                .addDescription(precondition.getDescription(subcondition));
    }

    public boolean matchDetailType(Object obj) {
        return precondition.required.isValidType(obj);
    }

    public RequiredType requiredType() {
        return precondition.required;
    }

    public String getID() {
        return precondition.id;
    }

    private static class Precondition {

        public String id;
        public RequiredType required;
        public ColorCondition.ColorFunction func;
        public int ordinal;
        private Function<Object, Component[]> descriptionGenerator;

        public Precondition(String id, RequiredType required, ColorCondition.ColorFunction func, int ordinal) {
            this.id = id;
            this.required = required;
            this.func = func;
            this.ordinal = ordinal;
        }

        public String getId() {
            return id;
        }

        public Component[] getDescription(Object obj) {
            if (required.isValidType(obj))
                return descriptionGenerator.apply(obj);
            else
                return new Component[]{new TextComponent("")};
        }

        public Precondition addDescriptionGenerator(Function<Object, Component[]> descriptionGenerator) {
            this.descriptionGenerator = descriptionGenerator;
            return this;
        }

    }

}

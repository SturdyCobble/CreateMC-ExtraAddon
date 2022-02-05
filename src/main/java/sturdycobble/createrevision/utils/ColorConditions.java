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

    NONE(RequiredType.NONE, c -> true, textDescription("none.desc")),
    HIGH_CHROMA(RequiredType.NONE, c -> c.asHSBFloatArray()[1] >= ModConfigs.getHighChromaThreshold(),
            o -> new Component[]{translate("high_chroma.desc").withStyle(ChatFormatting.BLUE)}),
    LOW_CHROMA(RequiredType.NONE, c -> c.asHSBFloatArray()[1] <= ModConfigs.getLowChromaThreshold(),
            o -> new Component[]{translate("low_chroma.desc").withStyle(ChatFormatting.BLACK)}),
    HIGH_VALUE(RequiredType.NONE, c -> c.asHSBFloatArray()[2] >= ModConfigs.getHighValueThreshold(),
            o -> new Component[]{translate("high_value.desc").withStyle(ChatFormatting.WHITE)}),
    LOW_VALUE(RequiredType.NONE, c -> c.asHSBFloatArray()[2] <= ModConfigs.getLowValueThreshold(),
            o -> new Component[]{translate("low_value.desc").withStyle(ChatFormatting.BLACK)}),
    COLOR(RequiredType.COLOR, (c, d) -> RGBColor.squareRGBDistance(c, (RGBColor) d) <= ModConfigs.getDiscernibleRGBDistanceSquared(),
            o -> new Component[]{translate("color.desc", ((RGBColor) o).toHex()).withStyle(Style.EMPTY.applyFormat(ChatFormatting.BOLD).withColor(((RGBColor) o).toInt()))}),
    COLOR_RADIUS(RequiredType.RADIUS, (c, r) -> {
        Pair<RGBColor, Float> pair = (Pair<RGBColor, Float>) r;
        return RGBColor.squareRGBDistance(c, pair.getFirst()) <= pair.getSecond();
    },
            o -> {
                Pair<RGBColor, Float> pair = (Pair<RGBColor, Float>) o;
                return new Component[]{
                        translate("color_radius.desc1", pair.getFirst().toHex()).withStyle(Style.EMPTY.applyFormat(ChatFormatting.BOLD).withColor(pair.getFirst().toInt())),
                        translate("color_radius.desc2", Math.sqrt(pair.getSecond())).withStyle(ChatFormatting.BOLD)};
            }
    ),
    HUE_RANGE(RequiredType.RANGE, (c, r) -> inRange(c.asHSBFloatArray()[0], (float[]) r),
            o -> {
                float[] pair = (float[]) o;
                return new Component[]{
                        translate("range.desc_from_hue", Math.round(pair[0] * 360)).withStyle(Style.EMPTY.applyFormat(ChatFormatting.BOLD).withColor(getRepresentativeHue(pair[0]))),
                        translate("range.desc_to_degree", Math.round(pair[1] * 360)).withStyle(Style.EMPTY.applyFormat(ChatFormatting.BOLD).withColor(getRepresentativeHue(pair[1])))};
            }
    ),
    SAT_RANGE(RequiredType.RANGE, (c, r) -> inRange(c.asHSBFloatArray()[0], ((float[]) r)),
            o -> {
                float[] pair = (float[]) o;
                return new Component[]{
                        translate("range.desc_from_sat", Math.round(pair[0] * 100)).withStyle(Style.EMPTY.applyFormat(ChatFormatting.BOLD).withColor(getRepresentativeSat(pair[0]))),
                        translate("range.desc_to_percent", Math.round(pair[1] * 100)).withStyle(Style.EMPTY.applyFormat(ChatFormatting.BOLD).withColor(getRepresentativeSat(pair[1])))};
            }
    ),
    VAL_RANGE(RequiredType.RANGE, (c, r) -> inRange(c.asHSBFloatArray()[2], ((float[]) r)),
            o -> {
                float[] pair = (float[]) o;
                return new Component[]{
                        translate("range.desc_from_val", Math.round(pair[0] * 100)).withStyle(Style.EMPTY.applyFormat(ChatFormatting.BOLD).withColor(getRepresentativeVal(pair[0]))),
                        translate("range.desc_to_percent", Math.round(pair[1] * 100)).withStyle(Style.EMPTY.applyFormat(ChatFormatting.BOLD).withColor(getRepresentativeVal(pair[1])))};
            }
    ),
    HSB_RANGE(RequiredType.CUBE_RANGE, (c, r) -> inCubeRange(c.asHSBFloatArray(), (float[]) r),
            o -> {
                float[] ranges = (float[]) o;
                float[] color1 = new float[]{ranges[0], ranges[2], ranges[4]};
                float[] color2 = new float[]{ranges[1], ranges[3], ranges[5]};
                return new Component[]{
                        translate("range.desc_from_hsb", RGBColor.fromHSBFloatArray(color1).toHex()).withStyle(Style.EMPTY.applyFormat(ChatFormatting.BOLD).withColor(getRepresentativeHSB(color1))),
                        translate("range.desc_to_hsb", RGBColor.fromHSBFloatArray(color2).toHex()).withStyle(Style.EMPTY.applyFormat(ChatFormatting.BOLD).withColor(getRepresentativeHSB(color2)))};
            }
    ),
    RANDOM(RequiredType.SEED, (c, d, w) -> {
        return RGBColor.squareRGBDistance(c, RGBColor.getRandomColor((long) d, w)) <= ModConfigs.getDiscernibleRGBDistanceSquared();
    }, textDescription("random.desc"));

    private final RequiredType required;
    private final ColorCondition.ColorFunction func;
    private Function<Object, Component[]> descriptor;

    ColorConditions(RequiredType required, Predicate<RGBColor> func, Function<Object, Component[]> descriptor) {
        this(required, (c, o, w) -> func.test(c), descriptor);
    }

    ColorConditions(RequiredType required, ColorOnlyFunction func, Function<Object, Component[]> descriptor) {
        this(required, (c, o, w) -> func.apply(c, o), descriptor);
    }

    ColorConditions(RequiredType required, ColorCondition.ColorFunction func, Function<Object, Component[]> descriptor) {
        this.required = required;
        this.func = func;
        this.descriptor = descriptor;
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
        String parsedId = name.toUpperCase().replace(" ", "_");
        try {
            return ColorConditions.valueOf(parsedId);
        } catch (IllegalArgumentException ex) {
            return ColorConditions.NONE;
        }
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

    public ColorCondition create(@Nullable Object subCondition) {
        if (required == RequiredType.NONE)
            subCondition = null;
        if (!matchDetailType(subCondition)) {
            CreateRevision.LOGGER.warn("ColorCondition's Sub-condition Type does not match.");
        }
        return new ColorCondition(ordinal(), name(), required, func, subCondition)
                .addDescription(getDescription(subCondition));
    }

    public Component[] getDescription(Object obj) {
        if (required.isValidType(obj))
            return descriptor.apply(obj);
        else
            return new Component[]{new TextComponent("")};
    }

    public boolean matchDetailType(Object obj) {
        return required.isValidType(obj);
    }

    public RequiredType requiredType() {
        return required;
    }

    public String getName() {
        return name();
    }

    @FunctionalInterface
    public interface ColorOnlyFunction {

        boolean apply(RGBColor color, Object object);

    }

}

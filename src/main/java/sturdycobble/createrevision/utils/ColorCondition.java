package sturdycobble.createrevision.utils;

import com.mojang.datafixers.util.Pair;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import sturdycobble.createrevision.CreateRevision;
import sturdycobble.createrevision.init.ModConfigs;

import javax.annotation.Nullable;
import java.util.Arrays;

public class ColorCondition {

    public static final int SERIAL_SIZE = 6;

    private final String id;
    private final ColorFunction func;
    @Nullable
    private final Object subCondition;
    private final RGBColor.RequiredType required;
    private final int ordinal;

    private Component[] description = new Component[]{};

    public ColorCondition(int ordinal, String id, RGBColor.RequiredType required, ColorFunction func, Object subCondition) {
        this.id = id;
        this.func = func;
        this.subCondition = subCondition;
        this.required = required;
        this.ordinal = ordinal;
    }

    public static ColorCondition deserialize(Pair<Integer, float[]> serial) {
        ColorConditions prec = ColorConditions.NONE;
        for (ColorConditions val : ColorConditions.values()) {
            if (val.ordinal() == serial.getFirst()) {
                prec = val;
                break;
            }
        }
        Object subcond = null;
        if (prec.requiredType() == RGBColor.RequiredType.COLOR) {
            subcond = new RGBColor(Arrays.copyOfRange(serial.getSecond(), 0, 3));
        } else if (prec.requiredType() == RGBColor.RequiredType.CUBE_RANGE) {
            subcond = Arrays.copyOfRange(serial.getSecond(), 0, 6);
        } else if (prec.requiredType() == RGBColor.RequiredType.RADIUS) {
            RGBColor c = new RGBColor(Arrays.copyOfRange(serial.getSecond(), 0, 3));
            float r = serial.getSecond()[3];
            subcond = Pair.of(c, r);
        } else if (prec.requiredType() == RGBColor.RequiredType.RANGE) {
            subcond = Arrays.copyOfRange(serial.getSecond(), 0, 2);
        }

        return prec.create(subcond);
    }

    public Component[] getDescription() {
        return description;
    }

    public ColorCondition addDescription(Component[] desc) {
        description = desc;
        return this;
    }

    public String getID() {
        return id;
    }

    public Pair<Integer, float[]> serialize() {
        int lhs = ordinal;
        float[] rhs = new float[SERIAL_SIZE];
        Arrays.fill(rhs, -1.0F);
        if (required == RGBColor.RequiredType.COLOR) {
            float[] arr = ((RGBColor) subCondition).asFloatArray();
            for (int i = 0; i < 3; i++)
                rhs[i] = arr[i];
        } else if (required == RGBColor.RequiredType.RADIUS) {
            Pair<RGBColor, Float> pair = (Pair<RGBColor, Float>) subCondition;
            float[] arr = pair.getFirst().asFloatArray();
            for (int i = 0; i < 3; i++)
                rhs[i] = arr[i];
            rhs[3] = pair.getSecond();
        } else if (required == RGBColor.RequiredType.CUBE_RANGE) {
            float[] arr = (float[]) subCondition;
            for (int i = 0; i < 6; i++)
                rhs[i] = arr[i];
        } else if (required == RGBColor.RequiredType.RANGE) {
            float[] arr = (float[]) subCondition;
            for (int i = 0; i < 2; i++)
                rhs[i] = arr[i];
        }
        return Pair.of(lhs, rhs);
    }

    public boolean test(RGBColor rgbColor, Level world) {
        return func.apply(rgbColor, subCondition, world);
    }

    public boolean test(RGBColor rgbColor) {
        if (id != ColorConditions.RANDOM.getName())
            return func.apply(rgbColor, subCondition, null);
        else
            return false;
    }

    @FunctionalInterface
    public interface ColorFunction {

        boolean apply(RGBColor color, Object object, Level world);

    }

}
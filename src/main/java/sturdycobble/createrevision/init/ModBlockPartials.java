package sturdycobble.createrevision.init;

import com.jozufozu.flywheel.core.PartialModel;
import sturdycobble.createrevision.CreateRevision;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class ModBlockPartials {

    public static final Map<Direction, PartialModel> HEAT_PIPE_RIMS = new HashMap<>();
    public static final PartialModel HEAT_PIPE_CASING = get("heat_pipe/casing"),
            FRICTION_PLATE = get("friction_plate"),
            THERMOMETER_NEEDLE = get("thermometer_needle");

    public static PartialModel get(String path) {
        return new PartialModel(new ResourceLocation(CreateRevision.MODID, "block/" + path));
    }

    public static void clientInit() {
        for (Direction d : Direction.values())
            ModBlockPartials.HEAT_PIPE_RIMS.put(d, ModBlockPartials.get("heat_pipe/rim/" + d.getName()));
    }

}
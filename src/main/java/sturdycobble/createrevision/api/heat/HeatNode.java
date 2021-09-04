package sturdycobble.createrevision.api.heat;

import java.util.Objects;

import javax.annotation.Nullable;

import sturdycobble.createrevision.contents.heat.HeatPipeTileEntity;
import sturdycobble.createrevision.utils.HeatUtils;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.LazyOptional;

public class HeatNode {

    public static final int HEAT_UPDATE_TICK = 25;

    private final TileEntity te;
    private final WritableHeatContainer container;
    private final Connection[] connections;
    private boolean isNode;

    public HeatNode(TileEntity te, WritableHeatContainer container) {
        this.te = Objects.requireNonNull(te);
        this.container = Objects.requireNonNull(container);

        connections = new Connection[6];
        for (Direction d : Direction.values()) {
            connections[d.ordinal()] = new Connection(d);
            connections[d.ordinal()].markForUpdate();
        }
    }

    public Connection getConnection(Direction direction) {
        return connections[direction.ordinal()];
    }

    public void updateTemp() {
        if (te.getLevel().isClientSide())
            return;

        te.getLevel().sendBlockUpdated(te.getBlockPos(), te.getBlockState(), te.getBlockState(), 0);

        double heatCurrent = 0;
        if (isNode) {
            for (Connection c : connections)
                heatCurrent += c.calcHeatCurrent();
            container.addHeat(heatCurrent);
        } else {
            double sumTemp = 0;
            for (Connection c : connections) {
                sumTemp += c.getTemp();
            }
            container.setTemp((sumTemp - 300 * 4) / 2);
        }
    }

    public void markAllDirectionForUpdate() {
        for (Connection c : connections)
            c.markForUpdate();
    }

    public final class Connection {

        private final Direction direction;
        private LazyOptional<HeatContainer> linkedContainer = LazyOptional.empty();
        private boolean needsUpdate;

        public Connection(Direction direction) {
            this.direction = Objects.requireNonNull(direction);
        }

        private void updateConnection() {
            isNode = te instanceof HeatPipeTileEntity ? ((HeatPipeTileEntity) te).isNode() : true;
            if (needsUpdate) {
                needsUpdate = false;
                linkedContainer = HeatUtils.findNeighborNodes(te, direction);
                if (linkedContainer.isPresent()) {
                    linkedContainer.addListener(l -> markForUpdate());
                } else
                    linkedContainer = LazyOptional.empty();
            }
        }

        private double calcHeatCurrent() {
            HeatContainer c = getContainer();
            return c != null
                    ? (c.getTemp() - container.getTemp()) * container.getConductivity() * c.getConductivity() * 6
                    : 0;
        }

        public double getTemp() {
            HeatContainer c = getContainer();
            return c != null ? c.getTemp() : 300;
        }

        @Nullable
        public HeatContainer getContainer() {
            updateConnection();
            return linkedContainer.orElse(null);
        }

        public void markForUpdate() {
            needsUpdate = true;
        }

    }

}

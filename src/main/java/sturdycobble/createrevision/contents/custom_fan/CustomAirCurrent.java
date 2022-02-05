package sturdycobble.createrevision.contents.custom_fan;

import com.simibubi.create.content.contraptions.components.fan.AirCurrent;
import com.simibubi.create.content.contraptions.processing.InWorldProcessing;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.belt.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.foundation.utility.Iterate;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;
import sturdycobble.createrevision.init.ModTags;
import sturdycobble.createrevision.mixin.AirCurrentAccessor;
import sturdycobble.createrevision.utils.FluidOrBlock;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CustomAirCurrent {

    public AirCurrent baseCurrent;
    public List<CustomFanProcess.CustomAirCurrentSegment> customSegments = new ArrayList();
    protected List<Pair<TransportedItemStackHandlerBehaviour, FluidOrBlock>> customAffectedItemHandlers = new ArrayList();

    public CustomAirCurrent(AirCurrent current) {
        this.baseCurrent = current;
    }

    public void tickAffectedEntities(Level world, Direction facing) {
        for (Iterator<Entity> iterator = ((AirCurrentAccessor) baseCurrent).getCaughtEntities().iterator(); iterator.hasNext(); ) {
            Entity entity = iterator.next();
            Vec3 center = VecHelper.getCenterOf(baseCurrent.source.getAirCurrentPos());
            double entityDistance = entity.position().distanceTo(center);
            entityDistance -= 0.5F;
            FluidOrBlock customType = getCustomSegmentAt((float) (entityDistance));
            InWorldProcessing.Type processingType = baseCurrent.getSegmentAt((float) entityDistance);
            if ((processingType == null || processingType.equals(InWorldProcessing.Type.NONE)) && !customType.isEmpty()) {
                if (entity instanceof ItemEntity) {
                    ItemEntity itemEntity = (ItemEntity) entity;
                    if (world.isClientSide) {
                        spawnParticlesForCustomProcessing(world, entity.position(), customType);
                    } else if (CustomFanProcess.canCustomFanProcess(itemEntity, customType)) {
                        CustomFanProcess.applyCustomProcessing(itemEntity, customType);
                    }
                }
            }
        }
    }

    public void rebuild() {
        if (baseCurrent.source.getSpeed() == 0) {
            customSegments.clear();
            return;
        }

        int limit = (int) (baseCurrent.maxDistance + .5f);
        int searchStart = baseCurrent.pushing ? 0 : limit;
        int searchEnd = baseCurrent.pushing ? limit : 0;
        int searchStep = baseCurrent.pushing ? 1 : -1;

        Level world = baseCurrent.source.getAirCurrentWorld();
        BlockPos start = baseCurrent.source.getAirCurrentPos();

        CustomFanProcess.CustomAirCurrentSegment currentCustomSegment = new CustomFanProcess.CustomAirCurrentSegment();
        customSegments.clear();
        currentCustomSegment.startOffset = 0;
        FluidOrBlock type = FluidOrBlock.empty();


        for (int i = searchStart; i * searchStep <= searchEnd * searchStep; i += searchStep) {
            BlockPos currentPos = start.relative(baseCurrent.direction, i);
            BlockState newTypeBlockState = world.getBlockState(currentPos);
            Fluid newTypeFluid = world.getFluidState(currentPos).getType();
            Fluid newSourceFluid = !(newTypeFluid instanceof FlowingFluid) ? newTypeFluid : ((FlowingFluid) newTypeFluid).getSource();


            if (newSourceFluid.is(ModTags.CUSTOM_FAN_SOURCE_FLUID)) {
                type = new FluidOrBlock(newSourceFluid);
            } else if (newTypeBlockState.is(ModTags.CUSTOM_FAN_SOURCE_BLOCK)) {
                type = new FluidOrBlock(newTypeBlockState.getBlock());
            }

            if (currentCustomSegment.type.isEmpty() || !currentCustomSegment.type.equals(type) || currentCustomSegment.startOffset == 0) {
                currentCustomSegment.endOffset = i;
                if (currentCustomSegment.startOffset != 0) {
                    customSegments.add(currentCustomSegment);
                }

                currentCustomSegment = new CustomFanProcess.CustomAirCurrentSegment();
                currentCustomSegment.startOffset = i;
                currentCustomSegment.type = type;
            }
        }

        currentCustomSegment.endOffset = searchEnd + searchStep;
        customSegments.add(currentCustomSegment);

        findAffectedHandlers();
    }

    public void findAffectedHandlers() {
        Level world = baseCurrent.source.getAirCurrentWorld();
        BlockPos start = baseCurrent.source.getAirCurrentPos();
        customAffectedItemHandlers.clear();

        for (int i = 0; (float) i < baseCurrent.maxDistance + 1.0F; ++i) {
            FluidOrBlock type = getCustomSegmentAt((float) i);
            InWorldProcessing.Type processingType = baseCurrent.getSegmentAt((float) i);

            if ((processingType.equals(InWorldProcessing.Type.NONE) || processingType == null) && (type != null && !type.isEmpty())) {
                for (int offset : Iterate.zeroAndOne) {
                    BlockPos pos = start.relative(baseCurrent.direction, i).below(offset);
                    TransportedItemStackHandlerBehaviour behaviour = TileEntityBehaviour.get(world, pos, TransportedItemStackHandlerBehaviour.TYPE);
                    if (behaviour != null) {
                        customAffectedItemHandlers.add(Pair.of(behaviour, type));
                    }

                    if (baseCurrent.direction.getAxis().isVertical()) {
                        break;
                    }
                }
            }
        }
    }

    public void tickAffectedHandlers() {
        for (Pair<TransportedItemStackHandlerBehaviour, FluidOrBlock> pair : customAffectedItemHandlers) {
            TransportedItemStackHandlerBehaviour handler = pair.getKey();
            Level world = handler.getWorld();
            FluidOrBlock type = pair.getRight();
            handler.handleProcessingOnAllItems((transported) -> {
                if (world.isClientSide) {
                    if (world != null) {
                        spawnParticlesForCustomProcessing(world, handler.getWorldPositionOf(transported), type);
                    }

                    return TransportedItemStackHandlerBehaviour.TransportedResult.doNothing();
                } else {
                    return CustomFanProcess.applyCustomProcessing(transported, world, type);
                }
            });
        }
    }

    private void spawnParticlesForCustomProcessing(Level world, Vec3 pos, FluidOrBlock type) {
        if (world.random.nextInt(8) == 0) {
            if (type.isBlock()) {
                world.addDestroyBlockEffect(new BlockPos(pos), type.block.defaultBlockState());
            } else {
                world.addParticle(ParticleTypes.COMPOSTER, pos.x, pos.y + 0.5D, pos.z, 0.0D, 0.0625D, 0.0D);
            }
        }
    }

    public FluidOrBlock getCustomSegmentAt(float offset) {
        for (CustomFanProcess.CustomAirCurrentSegment customAirCurrentSegment : customSegments) {
            if (offset > customAirCurrentSegment.endOffset && baseCurrent.pushing)
                continue;
            if (offset < customAirCurrentSegment.endOffset && !baseCurrent.pushing)
                continue;
            return customAirCurrentSegment.type;
        }
        return FluidOrBlock.empty();
    }

}

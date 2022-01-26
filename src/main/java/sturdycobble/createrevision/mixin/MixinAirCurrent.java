package sturdycobble.createrevision.mixin;

import com.simibubi.create.content.contraptions.components.fan.AirCurrent;
import com.simibubi.create.content.contraptions.processing.InWorldProcessing;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.belt.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.foundation.utility.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import sturdycobble.createrevision.contents.custom_fan.CustomFanProcess;
import sturdycobble.createrevision.contents.custom_fan.CustomFanProcess.CustomAirCurrentSegment;
import sturdycobble.createrevision.init.ModTags;
import sturdycobble.createrevision.utils.FluidOrBlock;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mixin(AirCurrent.class)
public abstract class MixinAirCurrent {

    public List<CustomAirCurrentSegment> customSegments = new ArrayList();
    protected List<Pair<TransportedItemStackHandlerBehaviour, FluidOrBlock>> customAffectedItemHandlers = new ArrayList();

    @Inject(method = "tick", at = @At(value = "INVOKE",
            target = "Lcom/simibubi/create/content/contraptions/components/fan/AirCurrent;tickAffectedEntities(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/Direction;)V"), remap = false)
    public void onTick(CallbackInfo ci) {
        tickCustomAffectedHandlers();
    }

    @Inject(method = "tickAffectedEntities", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/contraptions/components/fan/AirCurrent;getSegmentAt(F)Lcom/simibubi/create/content/contraptions/processing/InWorldProcessing$Type;"),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION, remap = false)
    protected void onTickAffectedEntities(Level world, Direction facing, CallbackInfo ci, Iterator it, Entity entity, Vec3 v1, Vec3i vi1, float f1,
                                          float f2, double entityDistance, float f3, Vec3 v3, float f4, double d2, double d3, double d4) {
        FluidOrBlock customType = getCustomSegmentAt((float) (entityDistance));
        InWorldProcessing.Type processingType = ((AirCurrent) (Object) this).getSegmentAt((float) entityDistance);
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

    @Inject(method = "rebuild", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/contraptions/components/fan/AirCurrent;findAffectedHandlers()V"),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION, remap = false)
    protected void onRebuild(CallbackInfo ci, Level world, BlockPos start, float max, Direction d, Vec3 directionVec,
                             AirCurrent.AirCurrentSegment seg, InWorldProcessing.Type t1, int i1, int searchStart, int searchEnd, int searchStep) {
        CustomAirCurrentSegment currentCustomSegment = new CustomAirCurrentSegment();
        this.customSegments.clear();
        currentCustomSegment.startOffset = 0;
        FluidOrBlock type = FluidOrBlock.empty();


        for (int i = searchStart; i * searchStep <= searchEnd * searchStep; i += searchStep) {
            BlockPos currentPos = start.relative(((AirCurrent) (Object) this).direction, i);
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
                    this.customSegments.add(currentCustomSegment);
                }

                currentCustomSegment = new CustomAirCurrentSegment();
                currentCustomSegment.startOffset = i;
                currentCustomSegment.type = type;
            }
        }

        currentCustomSegment.endOffset = searchEnd + searchStep;
        this.customSegments.add(currentCustomSegment);

        findCustomAffectedHandlers();
    }

    public void findCustomAffectedHandlers() {
        Level world = ((AirCurrent) (Object) this).source.getAirCurrentWorld();
        BlockPos start = ((AirCurrent) (Object) this).source.getAirCurrentPos();
        this.customAffectedItemHandlers.clear();

        for (int i = 0; (float) i < ((AirCurrent) (Object) this).maxDistance + 1.0F; ++i) {
            FluidOrBlock type = getCustomSegmentAt((float) i);
            InWorldProcessing.Type processingType = ((AirCurrent) (Object) this).getSegmentAt((float) i);

            if ((processingType.equals(InWorldProcessing.Type.NONE) || processingType == null) && (type != null && !type.isEmpty())) {
                for (int offset : Iterate.zeroAndOne) {
                    BlockPos pos = start.relative(((AirCurrent) (Object) this).direction, i).below(offset);
                    TransportedItemStackHandlerBehaviour behaviour = TileEntityBehaviour.get(world, pos, TransportedItemStackHandlerBehaviour.TYPE);
                    if (behaviour != null) {
                        this.customAffectedItemHandlers.add(Pair.of(behaviour, type));
                    }

                    if (((AirCurrent) (Object) this).direction.getAxis().isVertical()) {
                        break;
                    }
                }
            }
        }
    }

    public void tickCustomAffectedHandlers() {
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
        for (CustomAirCurrentSegment customAirCurrentSegment : customSegments) {
            if (offset > customAirCurrentSegment.endOffset && ((AirCurrent) (Object) this).pushing)
                continue;
            if (offset < customAirCurrentSegment.endOffset && !((AirCurrent) (Object) this).pushing)
                continue;
            return customAirCurrentSegment.type;
        }
        return FluidOrBlock.empty();
    }

}

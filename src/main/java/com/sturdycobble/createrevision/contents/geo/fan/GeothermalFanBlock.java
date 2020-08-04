package com.sturdycobble.createrevision.contents.geo.fan;

import com.simibubi.create.content.contraptions.base.KineticBlock;
import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.utility.worldWrappers.WrappedWorld;
import com.sturdycobble.createrevision.init.ModTileEntityTypes;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
/**
 * Geothermal Fan Block
 * A Modified version of Encased Fan(com.simibubi.create.content.contraptions.components.fan.EncasedFanBlock)
 * 
 * @author SturdyCobble
 *
 */
public class GeothermalFanBlock extends KineticBlock implements ITE<GeothermalFanTileEntity>{
	
	public GeothermalFanBlock(final Properties properties) {
		super(properties);
	}

	@Override
	public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
		blockUpdate(state, worldIn, pos);
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos,
		boolean isMoving) {
		blockUpdate(state, worldIn, pos);
	}

	protected void blockUpdate(BlockState state, World worldIn, BlockPos pos) {
		if (worldIn instanceof WrappedWorld)
			return;
		notifyFanTile(worldIn, pos);
		if (worldIn.isRemote)
			return;
		withTileEntityDo(worldIn, pos, te -> te.updateGenerator());
	}

	protected void notifyFanTile(IWorld world, BlockPos pos) {
		withTileEntityDo(world, pos, GeothermalFanTileEntity::blockBelowChanged);
	}
	
	@Override
    public boolean hasShaftTowards(IWorldReader world, BlockPos pos, BlockState state, Direction face) {
        return face == Direction.UP;
    }

	@Override
	public Axis getRotationAxis(BlockState state) {
		return Direction.UP.getAxis();
	}

	@Override
	public boolean showCapacityWithAnnotation() {
		return true;
	}

	@Override
	public Class<GeothermalFanTileEntity> getTileEntityClass() {
		return GeothermalFanTileEntity.class;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return ModTileEntityTypes.GEOTHERMAL_FAN.get().create();
	}

	@Override
	protected boolean hasStaticPart() {
		return true;
	}
	
}

package sturdycobble.createrevision.contents.heat;

import static net.minecraft.state.properties.BlockStateProperties.FACING;

import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.utility.VoxelShaper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class HeatExchangerBlock extends Block implements ITE<HeatExchangerTileEntity> {

    private static final VoxelShaper HEAT_EXCHANGER_SHAPER = VoxelShaper.forDirectional(Block.box(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D), Direction.UP);

    public HeatExchangerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(new Property[]{FACING}));
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        Direction preferredFacing = context.getNearestLookingDirection();
        return defaultBlockState().setValue(FACING, context.getPlayer() != null && context.getPlayer().isSecondaryUseActive() ? preferredFacing.getOpposite() : preferredFacing);
    }

    @Override
    public BlockRenderType getRenderShape(BlockState iBlockState) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        withTileEntityDo(world, pos, te -> te.updateAllNeighbors(world.getBlockState(pos)));
    }

    @Override
    public void onPlace(BlockState state, World world, BlockPos pos, BlockState oldState, boolean isMoving) {
        withTileEntityDo(world, pos, te -> te.updateAllNeighbors(world.getBlockState(pos)));
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader blockReader) {
        return new HeatExchangerTileEntity();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public Class<HeatExchangerTileEntity> getTileEntityClass() {
        return HeatExchangerTileEntity.class;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return HEAT_EXCHANGER_SHAPER.get(state.getValue(FACING));
    }

}

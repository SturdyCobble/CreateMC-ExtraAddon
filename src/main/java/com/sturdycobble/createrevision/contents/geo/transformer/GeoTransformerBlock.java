package com.sturdycobble.createrevision.contents.geo.transformer;

/**
 * Geothermal Block Transformer Block
 * Modified version of com.simibubi.create.content.contraptions.components.saw.SawBlock
 * 
 * @author SturdyCobble
 * 
 */
public class GeoTransformerBlock /*extends DirectionalAxisKineticBlock implements ITE<GeoTransformerTileEntity>*/ {
	/*public static final BooleanProperty RUNNING = BooleanProperty.create("running");
	public static DamageSource damageSourceSaw = new DamageSource("create.saw").setDamageBypassesArmor();*/
	
	public GeoTransformerBlock(/*final Properties properties*/) {
		/*super(properties);
		setDefaultState(getDefaultState().with(RUNNING, false));*/
	}


	/*@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockState stateForPlacement = super.getStateForPlacement(context);
		Direction facing = stateForPlacement.get(FACING);
		if (facing.getAxis().isVertical())
			return stateForPlacement;
		return stateForPlacement.with(AXIS_ALONG_FIRST_COORDINATE, facing.getAxis() == Axis.X);
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(RUNNING);
		super.fillStateContainer(builder);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return ModTileEntityTypes.GEO_TRANSFORMER.get().create();
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return AllShapes.CASING_12PX.get(state.get(FACING));
	}

	@Override
	public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		if (entityIn instanceof ItemEntity)
			return;
		if (!new AxisAlignedBB(pos).shrink(.1f).intersects(entityIn.getBoundingBox()))
			return;
		withTileEntityDo(worldIn, pos, te -> {
			if (te.getSpeed() == 0)
				return;
			entityIn.attackEntityFrom(damageSourceSaw, MathHelper.clamp(Math.abs(te.getSpeed() / 16f) + 1, 0, 20));
		});
	}

	@Override
	public void onLanded(IBlockReader worldIn, Entity entityIn) {
		super.onLanded(worldIn, entityIn);
		if (!(entityIn instanceof ItemEntity))
			return;
		if (entityIn.world.isRemote)
			return;

		BlockPos pos = entityIn.getPosition();
		withTileEntityDo(entityIn.world, pos, te -> {
			if (te.getSpeed() == 0)
				return;
			te.insertItem((ItemEntity) entityIn);
		});
	}

	@Override
	public PushReaction getPushReaction(BlockState state) {
		return PushReaction.NORMAL;
	}

	public static boolean isHorizontal(BlockState state) {
		return state.get(FACING).getAxis().isHorizontal();
	}

	@Override
	public Axis getRotationAxis(BlockState state) {
		return isHorizontal(state) ? state.get(FACING).getAxis() : super.getRotationAxis(state);
	}

	@Override
	public boolean hasShaftTowards(IWorldReader world, BlockPos pos, BlockState state, Direction face) {
		return isHorizontal(state) ? face == state.get(FACING).getOpposite()
				: super.hasShaftTowards(world, pos, state, face);
	}

	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.hasTileEntity() || state.getBlock() == newState.getBlock())
			return;

		withTileEntityDo(worldIn, pos, te -> ItemHelper.dropContents(worldIn, pos, te.inventory));
		TileEntityBehaviour.destroy(worldIn, pos, FilteringBehaviour.TYPE);
      		worldIn.removeTileEntity(pos);
	}


	@Override
	public Class<GeoTransformerTileEntity> getTileEntityClass() {
		return GeoTransformerTileEntity.class;
	}


	@Override
	protected boolean hasStaticPart() {
		return true;
	}*/
	
}
package com.sturdycobble.createrevision.contents.geo.transformer;

/**
 * Geothermal Transformer Tile Entity
 * Modified version of Code From com.simibubi.create.content.contraptions.components.saw.SawTileEntity
 * 
 * @author SturdyCobble
 *
 */

public class GeoTransformerTileEntity /* extends  KineRMticTileEntity*/{
	public GeoTransformerTileEntity() {
		/*super(ModTileEntityTypes.GEO_TRANSFOER.get());*/
	}
	/*private static final Object stoneTransformingRecipesKey = new Object();

	public ProcessingInventory inventory;
	private int recipeIndex;
	private LazyOptional<IItemHandler> invProvider = LazyOptional.empty();
	private FilteringBehaviour filtering;
	private boolean destroyed;
	protected int ticksUntilNextProgress;

	public GeoTransformerTileEntity(TileEntityType<? extends GeoTransformerTileEntity> type) {
		super(type);
		inventory = new ProcessingInventory(this::start);
		inventory.remainingTime = -1;
		recipeIndex = 0;
		invProvider = LazyOptional.of(() -> inventory);
	}


	public void addBehaviours(List<TileEntityBehaviour> behaviours) {
		filtering = new FilteringBehaviour(this, new GeoTransformerFilterSlot());
		behaviours.add(filtering);
	}

	@Override
	public boolean hasFastRenderer() {
		return false;
	}

	public void onSpeedChanged(float prevSpeed) {
		boolean shouldRun = Math.abs(getSpeed()) > 1 / 32f;
		boolean running = getBlockState().get(GeoTransformerBlock.RUNNING);
		if (shouldRun != running && !destroyed)
			world.setBlockState(pos, getBlockState().with(GeoTransformerBlock.RUNNING, shouldRun), 2 | 16);
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.put("Inventory", inventory.serializeNBT());
		compound.putInt("RecipeIndex", recipeIndex);
		return super.write(compound);
	}

	@Override
	public void read(CompoundNBT compound) {
		super.read(compound);
		//inventory.deserializeNBT(compound.getCompound("Inventory"));
		recipeIndex = compound.getInt("RecipeIndex");
	}
	
	@Override
	public void tick() {
		if (shouldRun() && ticksUntilNextProgress < 0)
			destroyNextTick();
		if (!canProcess())
			return;
		if (getSpeed() == 0)
			return;
		if (inventory.remainingTime == -1) {
			if (!inventory.isEmpty() && !inventory.appliedRecipe)
				start(inventory.getStackInSlot(0));
			return;
		}

		float processingSpeed = MathHelper.clamp(Math.abs(getSpeed()) / 32, 1, 128);
		inventory.remainingTime -= processingSpeed;
		if (world.isRemote)
			return;
		if (inventory.remainingTime > 0)
			spawnParticles(inventory.getStackInSlot(0));

		if (inventory.remainingTime < 20 && !inventory.appliedRecipe) {
			applyRecipe();
			inventory.appliedRecipe = true;
			sendData();
			return;
		}
		Vec3d itemMovement = getItemMovementVec();
		Direction itemMovementFacing = Direction.getFacingFromVector(itemMovement.x, itemMovement.y, itemMovement.z);
		Vec3d outPos = VecHelper.getCenterOf(pos).add(itemMovement.scale(.5f).add(0, .5, 0));
		Vec3d outMotion = itemMovement.scale(.0625).add(0, .125, 0);

		if (inventory.remainingTime <= 0) {

			BlockPos nextPos = pos.add(itemMovement.x, itemMovement.y, itemMovement.z);
			if (AllBlocks.BELT.has(world.getBlockState(nextPos))) {
				TileEntity te = world.getTE(nextPos);
				if (te != null && te instanceof BeltTileEntity) {
					for (int slot = 0; slot < inventory.getSlots(); slot++) {
						ItemStack stack = inventory.getStackInSlot(slot);
						if (stack.isEmpty())
							continue;

						if (((BeltTileEntity) te).tryInsertingFromSide(itemMovementFacing, stack, false))
							inventory.setStackInSlot(slot, ItemStack.EMPTY);
						else {
							inventory.remainingTime = 0;
							return;
						}
					}
					inventory.clear();
					inventory.remainingTime = -1;
					sendData();
				}
			}
			// Try moving items onto next saw
			if (ModBlocks.GEO_TRANSFORMER.get() == world.getBlockState(nextPos).getBlock()) {
				TileEntity te = world.getTE(nextPos);
				if (te != null && te instanceof GeoTransformerTileEntity) {
					GeoTransformerTileEntity geoTileEntity = (GeoTransformerTileEntity) te;
					Vec3d otherMovement = geoTileEntity.getItemMovementVec();
					if (Direction.getFacingFromVector(otherMovement.x, otherMovement.y,
							otherMovement.z) != itemMovementFacing.getOpposite()) {
						for (int slot = 0; slot < inventory.getSlots(); slot++) {
							ItemStack stack = inventory.getStackInSlot(slot);
							if (stack.isEmpty())
								continue;

							ProcessingInventory sawInv = geoTileEntity.inventory;
							if (sawInv.isEmpty()) {
								sawInv.insertItem(0, stack, false);
								inventory.setStackInSlot(slot, ItemStack.EMPTY);

							} else {
								inventory.remainingTime = 0;
								return;
							}
						}
						inventory.clear();
						inventory.remainingTime = -1;
						sendData();
					}
				}
			}

			// Eject Items
			for (int slot = 0; slot < inventory.getSlots(); slot++) {
				ItemStack stack = inventory.getStackInSlot(slot);
				if (stack.isEmpty())
					continue;
				ItemEntity entityIn = new ItemEntity(world, outPos.x, outPos.y, outPos.z, stack);
				entityIn.setMotion(outMotion);
				world.addEntity(entityIn);
			}
			inventory.clear();
			world.updateComparatorOutputLevel(pos, getBlockState().getBlock());
			inventory.remainingTime = -1;
			sendData();
			return;
		}
		return;
	}

	@Override
	public void remove() {
		invProvider.invalidate();
		destroyed = true;
		super.remove();
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return invProvider.cast();
		return super.getCapability(cap, side);
	}

	protected void spawnParticles(ItemStack stack) {
		if (stack == null || stack.isEmpty())
			return;

		IParticleData particleData = null;
		float speed = 1;
		if (stack.getItem() instanceof BlockItem)
			particleData = new BlockParticleData(ParticleTypes.BLOCK, ((BlockItem) stack.getItem()).getBlock()
				.getDefaultState());
		else {
			particleData = new ItemParticleData(ParticleTypes.ITEM, stack);
			speed = .125f;
		}

		Random r = world.rand;
		Vec3d vec = getItemMovementVec();
		Vec3d pos = VecHelper.getCenterOf(this.pos);
		float offset = inventory.recipeDuration != 0 ? (float) (inventory.remainingTime) / inventory.recipeDuration : 0;
		offset -= .5f;
		world.addParticle(particleData, pos.getX() + -vec.x * offset, pos.getY() + .45f, pos.getZ() + -vec.z * offset,
			-vec.x * speed, r.nextFloat() * speed, -vec.z * speed);
	}

	public Vec3d getItemMovementVec() {
		boolean alongX = !getBlockState().get(GeoTransformerBlock.AXIS_ALONG_FIRST_COORDINATE);
		int offset = getSpeed() < 0 ? -1 : 1;
		return new Vec3d(offset * (alongX ? 1 : 0), 0, offset * (alongX ? 0 : -1));
	}

	private void applyRecipe() {
		List<? extends IRecipe<?>> recipes = getRecipes();
		if (recipes.isEmpty())
			return;
		if (recipeIndex >= recipes.size())
			recipeIndex = 0;

		IRecipe<?> recipe = recipes.get(recipeIndex);

		int rolls = inventory.getStackInSlot(0)
			.getCount();
		inventory.clear();

		List<ItemStack> list = new ArrayList<>();
		for (int roll = 0; roll < rolls; roll++) {
			List<ItemStack> results = new LinkedList<ItemStack>();
			if (recipe instanceof CuttingRecipe)
				results = ((CuttingRecipe) recipe).rollResults();
			for (int i = 0; i < results.size(); i++) {
				ItemStack stack = results.get(i);
				ItemHelper.addToList(stack, list);
			}
		}
		for (int slot = 0; slot < list.size() && slot + 1 < inventory.getSlots(); slot++)
			inventory.setStackInSlot(slot + 1, list.get(slot));

	}

	private List<? extends IRecipe<?>> getRecipes() {
		List<IRecipe<?>> startedSearch = RecipeFinder.get(stoneTransformingRecipesKey, world,
			RecipeConditions.isOfType(AllRecipeTypes.CUTTING.getType()));
		return startedSearch.stream()
			.filter(RecipeConditions.outputMatchesFilter(filtering))
			.filter(RecipeConditions.firstIngredientMatches(inventory.getStackInSlot(0)))
			.collect(Collectors.toList());
	}

	public void insertItem(ItemEntity entity) {
		if (!canProcess())
			return;
		if (!inventory.isEmpty())
			return;
		if (world.isRemote)
			return;

		inventory.clear();
		inventory.insertItem(0, entity.getItem()
			.copy(), false);
		entity.remove();
	}

	public void start(ItemStack inserted) {
		if (world.isRemote)
			return;
		if (!canProcess())
			return;
		if (inventory.isEmpty())
			return;
	}


	protected boolean shouldRun() {
		return getBlockState().get(SawBlock.FACING).getAxis().isHorizontal();
	}
	
	public void destroyNextTick() {
		ticksUntilNextProgress = 1;
	}
	
	protected boolean canProcess() {
		return getBlockState().get(GeoTransformerBlock.FACING) == Direction.UP;
	}*/

}
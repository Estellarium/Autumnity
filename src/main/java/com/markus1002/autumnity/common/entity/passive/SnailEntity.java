package com.markus1002.autumnity.common.entity.passive;

import java.util.EnumSet;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.markus1002.autumnity.core.Config;
import com.markus1002.autumnity.core.registry.ModBlocks;
import com.markus1002.autumnity.core.registry.ModEntities;
import com.markus1002.autumnity.core.registry.ModItems;
import com.markus1002.autumnity.core.registry.ModSoundEvents;

import net.minecraft.block.BlockState;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

public class SnailEntity extends AnimalEntity
{
	private static final UUID HIDING_ARMOR_BONUS_ID = UUID.fromString("73BF0604-4235-4D4C-8A74-6A633E526E24");
	private static final AttributeModifier HIDING_ARMOR_BONUS_MODIFIER = (new AttributeModifier(HIDING_ARMOR_BONUS_ID, "Hiding armor bonus", 20.0D, AttributeModifier.Operation.ADDITION)).setSaved(false);
	private static final DataParameter<Integer> EATING_TIME = EntityDataManager.createKey(SnailEntity.class, DataSerializers.VARINT);
	private static final DataParameter<Boolean> HIDING = EntityDataManager.createKey(SnailEntity.class, DataSerializers.BOOLEAN);
	private int slimeAmount = 0;
	private float hideTicks;
	private float prevHideTicks;
	private static final Predicate<LivingEntity> ENEMY_MATCHER = (livingentity) -> {
		if (livingentity == null)
		{
			return false;
		}
		else if (livingentity instanceof PlayerEntity)
		{
			return !livingentity.isSneaking() && !livingentity.isSpectator() && !((PlayerEntity)livingentity).isCreative();
		}
		else
		{
			return !(livingentity instanceof SnailEntity);
		}
	};

	public SnailEntity(EntityType<? extends AnimalEntity> type, World worldIn)
	{
		super(type, worldIn);
		this.lookController = new SnailEntity.LookHelperController();
		this.moveController = new SnailEntity.MoveHelperController();
	}

	public SnailEntity(FMLPlayMessages.SpawnEntity packet, World worldIn)
	{
		super(ModEntities.SNAIL, worldIn);
	}

	protected void registerGoals()
	{
		this.goalSelector.addGoal(0, new SnailEntity.HideGoal());
		this.goalSelector.addGoal(1, new SnailEntity.GetOutOfShellGoal());
		this.goalSelector.addGoal(2, new BreedGoal(this, 0.5D));
		this.goalSelector.addGoal(3, new SnailEntity.FollowFoodGoal());
		this.goalSelector.addGoal(4, new SnailEntity.EatMushroomsGoal());
		this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 0.5D));
		this.goalSelector.addGoal(6, new SnailEntity.WatchGoal());
		this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
	}

	protected void registerAttributes()
	{
		super.registerAttributes();
		this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(18.0D);
		this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
		this.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
	}

	protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn)
	{
		return sizeIn.height * 0.5F;
	}

	@Nullable
	protected SoundEvent getDeathSound()
	{
		return ModSoundEvents.ENTITY_SNAIL_HURT;
	}

	@Nullable
	protected SoundEvent getHurtSound(DamageSource damageSourceIn)
	{
		return ModSoundEvents.ENTITY_SNAIL_HURT;
	}

	protected void playStepSound(BlockPos pos, BlockState blockIn)
	{
	}

	public SoundEvent getEatSound(ItemStack itemStackIn)
	{
		return ModSoundEvents.ENTITY_SNAIL_EAT;
	}

	public void tick()
	{
		super.tick();

		if (this.world.isRemote)
		{
			this.prevHideTicks = this.hideTicks;
			if (this.getHiding())
			{
				this.hideTicks = MathHelper.clamp(this.hideTicks + 1, 0, 3);
			}
			else
			{
				this.hideTicks = MathHelper.clamp(this.hideTicks - 0.5F, 0, 3);
			}
		}
	}

	public void livingTick()
	{
		if (!this.canMove() || this.isMovementBlocked())
		{
			this.isJumping = false;
			this.moveStrafing = 0.0F;
			this.moveForward = 0.0F;
			this.randomYawVelocity = 0.0F;
		}

		super.livingTick();

		if (this.isEating())
		{
			this.eat();
			if (!this.world.isRemote)
			{
				this.setEatingTime(this.getEatingTime() - 1);
			}
		}

		ItemStack itemstack = this.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
		if (!itemstack.isEmpty())
		{
			if (!this.world.isRemote)
			{
				if (this.isFoodItem(itemstack))
				{
					if (!this.isEating())
					{
						this.setSlimeAmount(this.rand.nextInt(3) + 5);

						Item item = itemstack.getItem();
						ItemStack itemstack1 = itemstack.onItemUseFinish(this.world, this);
						if (!itemstack1.isEmpty())
						{
							if (itemstack1.getItem() != item)
							{
								this.setItemStackToSlot(EquipmentSlotType.MAINHAND, itemstack1);
								this.spitOutItem();
							}
							else
							{
								this.setItemStackToSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
							}
						}
					}
				}
				else
				{
					this.spitOutItem();
				}
			}
		}

		if (!this.world.isRemote && this.getSlimeAmount() > 0)
		{
			if (net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, this))
			{
				int i = MathHelper.floor(this.posX);
				int j = MathHelper.floor(this.posY);
				int k = MathHelper.floor(this.posZ);
				BlockState blockstate = ModBlocks.SNAIL_SLIME.getDefaultState();

				for(int l = 0; l < 4; ++l)
				{
					i = MathHelper.floor(this.posX + (double)((float)(l % 2 * 2 - 1) * 0.25F));
					j = MathHelper.floor(this.posY);
					k = MathHelper.floor(this.posZ + (double)((float)(l / 2 % 2 * 2 - 1) * 0.25F));
					BlockPos blockpos = new BlockPos(i, j, k);
					if (this.getSlimeAmount() > 0 && this.world.isAirBlock(blockpos) && blockstate.isValidPosition(this.world, blockpos))
					{
						this.world.setBlockState(blockpos, blockstate);
						this.setSlimeAmount(this.getSlimeAmount() - 1);
					}
				}
			}
		}
	}

	public void eat()
	{
		if ((this.getEatingTime() + 1) % 12 == 0 && !this.getItemStackFromSlot(EquipmentSlotType.MAINHAND).isEmpty())
		{
			this.playSound(ModSoundEvents.ENTITY_SNAIL_EAT, 0.25F + 0.5F * (float)this.rand.nextInt(2), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);

			for(int i = 0; i < 6; ++i)
			{
				Vec3d vec3d = new Vec3d(((double)this.rand.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, ((double)this.rand.nextFloat() - 0.5D) * 0.1D);
				vec3d = vec3d.rotatePitch(-this.rotationPitch * ((float)Math.PI / 180F));
				vec3d = vec3d.rotateYaw(-this.rotationYaw * ((float)Math.PI / 180F));
				double d0 = (double)(-this.rand.nextFloat()) * 0.2D;
				Vec3d vec3d1 = new Vec3d(((double)this.rand.nextFloat() - 0.5D) * 0.2D, d0, 0.8D + ((double)this.rand.nextFloat() - 0.5D) * 0.2D);
				vec3d1 = vec3d1.rotateYaw(-this.renderYawOffset * ((float)Math.PI / 180F));
				vec3d1 = vec3d1.add(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ);
				this.world.addParticle(new ItemParticleData(ParticleTypes.ITEM, this.getItemStackFromSlot(EquipmentSlotType.MAINHAND)), vec3d1.x, vec3d1.y, vec3d1.z, vec3d.x, vec3d.y + 0.05D, vec3d.z);
			}
		}
	}

	protected void onGrowingAdult()
	{
		super.onGrowingAdult();
		if (!this.isChild() && this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT))
		{
			this.entityDropItem(ModItems.SNAIL_SHELL_PIECE, 1);
		}
	}

	public boolean processInteract(PlayerEntity player, Hand hand)
	{
		if (!this.getHiding() && !this.isEating())
		{
			ItemStack itemstack = player.getHeldItem(hand);
			if (!itemstack.isEmpty() && this.getItemStackFromSlot(EquipmentSlotType.MAINHAND).isEmpty())
			{
				if (this.isFoodItem(itemstack))
				{
					if (!this.isChild() && this.getSlimeAmount() <= 0)
					{
						if (!this.world.isRemote)
						{
							ItemStack itemstack1 = itemstack.copy();
							itemstack1.setCount(1);
							this.setItemStackToSlot(EquipmentSlotType.MAINHAND, itemstack1);
							this.setEatingTime(192);
						}
						this.consumeItemFromStack(player, itemstack);
						return true;
					}
				}
				else if (this.isSnailBreedingItem(itemstack))
				{
					boolean flag = false;

					if (this.getGrowingAge() == 0 && !this.isChild() && this.canBreed())
					{
						if (!this.world.isRemote)
						{
							this.setInLove(player);
						}
						flag = true;
					}
					else if (this.isChild())
					{
						this.ageUp((int)((float)(-this.getGrowingAge() / 20) * 0.1F), true);
						flag = true;
					}

					if (flag)
					{
						if (!this.world.isRemote)
						{
							ItemStack itemstack1 = itemstack.onItemUseFinish(this.world, this);
							if (!player.abilities.isCreativeMode && !itemstack1.isEmpty())
							{
								player.setHeldItem(hand, itemstack1);
							}
						}

						return true;
					}
				}
			}
		}

		return super.processInteract(player, hand);
	}

	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		if (this.isInvulnerableTo(source))
		{
			return false;
		}
		else
		{
			if (!this.world.isRemote)
			{
				this.setHiding(true);
				this.spitOutItem();
			}
			return super.attackEntityFrom(source, amount);
		}

	}

	private void spitOutItem()
	{
		ItemStack itemstack = this.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
		if (!itemstack.isEmpty())
		{
			if (!this.world.isRemote)
			{
				ItemEntity itementity = new ItemEntity(this.world, this.posX + this.getLookVec().x, this.posY + this.getEyeHeight(), this.posZ + this.getLookVec().z, itemstack);
				itementity.setPickupDelay(40);
				itementity.setThrowerId(this.getUniqueID());
				this.world.addEntity(itementity);
				this.setItemStackToSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
				this.setEatingTime(0);
			}
		}
	}

	public int getEatingTime()
	{
		return this.dataManager.get(EATING_TIME);
	}

	public void setEatingTime(int eatingTimeIn)
	{
		this.dataManager.set(EATING_TIME, eatingTimeIn);
	}

	public boolean isEating()
	{
		return this.getEatingTime() > 0;
	}

	public boolean getHiding()
	{
		return this.dataManager.get(HIDING);
	}

	public void setHiding(boolean hiding)
	{
		if (hiding)
		{
			this.dataManager.set(HIDING, true);
		}
		else
		{
			this.dataManager.set(HIDING, false);
		}

		if (!this.world.isRemote)
		{
			this.getAttribute(SharedMonsterAttributes.ARMOR).removeModifier(HIDING_ARMOR_BONUS_MODIFIER);
			if (hiding)
			{
				this.getAttribute(SharedMonsterAttributes.ARMOR).applyModifier(HIDING_ARMOR_BONUS_MODIFIER);
			}
		}
	}

	public boolean canMove()
	{
		return !this.getHiding() && !this.isEating();
	}

	private int getSlimeAmount()
	{
		return this.slimeAmount;
	}

	private void setSlimeAmount(int slimeAmountIn)
	{
		this.slimeAmount = slimeAmountIn;
	}

	@OnlyIn(Dist.CLIENT)
	public float getHidingAnimationScale(float partialTicks)
	{
		return MathHelper.lerp(partialTicks, this.prevHideTicks, this.hideTicks) / 3.0F;
	}

	@OnlyIn(Dist.CLIENT)
	public float getHideTicks()
	{
		return this.hideTicks;
	}

	protected void registerData()
	{
		super.registerData();
		this.dataManager.register(EATING_TIME, 0);
		this.dataManager.register(HIDING, false);
	}

	public void writeAdditional(CompoundNBT compound)
	{
		super.writeAdditional(compound);
		compound.putInt("SlimeAmount", this.getSlimeAmount());
		compound.putBoolean("Hiding", this.getHiding());
	}

	public void readAdditional(CompoundNBT compound)
	{
		super.readAdditional(compound);
		this.setSlimeAmount(compound.getInt("SlimeAmount"));
		this.setHiding(compound.getBoolean("Hiding"));
	}

	private boolean isFoodItem(ItemStack stack)
	{
		Item item = stack.getItem();
		return Config.COMMON.snailFood.get().contains(item.getRegistryName().toString());
	}

	public boolean isBreedingItem(ItemStack stack)
	{
		return false;
	}

	private boolean isSnailBreedingItem(ItemStack stack)
	{
		Item item = stack.getItem();
		return Config.COMMON.snailBreedingItems.get().contains(item.getRegistryName().toString());
	}

	public AgeableEntity createChild(AgeableEntity ageable)
	{
		return ModEntities.SNAIL.create(this.world);
	}

	public IPacket<?> createSpawnPacket()
	{
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	protected float determineNextStepDistance()
	{
		return this.distanceWalkedOnStepModified + 0.15F;
	}

	public class HideGoal extends Goal
	{
		public HideGoal()
		{
			super();
		}

		public boolean shouldExecute()
		{
			if (!SnailEntity.this.getHiding() && !SnailEntity.this.isEating())
			{
				for(LivingEntity livingentity : SnailEntity.this.world.getEntitiesWithinAABB(LivingEntity.class, SnailEntity.this.getBoundingBox().grow(0.3D), ENEMY_MATCHER))
				{
					if (livingentity.isAlive())
					{
						return true;
					}
				}
			}

			return false;
		}

		public void startExecuting()
		{
			SnailEntity.this.setHiding(true);
		}

		public boolean shouldContinueExecuting()
		{
			return false;
		}
	}

	public class GetOutOfShellGoal extends Goal
	{
		public GetOutOfShellGoal()
		{
			super();
		}

		public boolean shouldExecute()
		{
			return SnailEntity.this.getRevengeTarget() == null && SnailEntity.this.rand.nextInt(100) == 0;
		}

		public void startExecuting()
		{
			SnailEntity.this.setHiding(false);
		}

		public boolean shouldContinueExecuting()
		{
			return false;
		}
	}

	class FollowFoodGoal extends TemptGoal
	{
		public FollowFoodGoal()
		{
			super(SnailEntity.this, 0.5F, false, null);
		}

		public boolean shouldExecute()
		{
			return !SnailEntity.this.canMove() ? false : super.shouldExecute();
		}

		protected boolean isTempting(ItemStack stack)
		{
			return SnailEntity.this.isFoodItem(stack) || SnailEntity.this.isSnailBreedingItem(stack);
		}
	}

	class WatchGoal extends LookAtGoal
	{
		public WatchGoal()
		{
			super(SnailEntity.this, PlayerEntity.class, 6.0F);
		}

		public boolean shouldExecute()
		{
			return super.shouldExecute() && !SnailEntity.this.getHiding();
		}

		public boolean shouldContinueExecuting()
		{
			return super.shouldContinueExecuting() && !SnailEntity.this.getHiding();
		}
	}

	class EatMushroomsGoal extends Goal
	{
		private double mushroomX;
		private double mushroomY;
		private double mushroomZ;

		public EatMushroomsGoal()
		{
			super();
			this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
		}

		public boolean shouldExecute()
		{
			if (SnailEntity.this.getRNG().nextInt(20) != 0)
			{
				return false;
			}
			else
			{
				return !SnailEntity.this.isChild() && SnailEntity.this.canMove() && SnailEntity.this.getSlimeAmount() <= 0 && net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(SnailEntity.this.world, SnailEntity.this) && this.canMoveToMushroom();
			}
		}

		protected boolean canMoveToMushroom()
		{
			Vec3d vec3d = this.findMushroom();
			if (vec3d == null)
			{
				return false;
			}
			else
			{
				this.mushroomX = vec3d.x;
				this.mushroomY = vec3d.y;
				this.mushroomZ = vec3d.z;
				return true;
			}
		}

		public boolean shouldContinueExecuting()
		{
			return !SnailEntity.this.getNavigator().noPath();
		}

		public void startExecuting()
		{
			SnailEntity.this.getNavigator().tryMoveToXYZ(this.mushroomX, this.mushroomY, this.mushroomZ, 0.5D);
		}

		@Nullable
		protected Vec3d findMushroom()
		{
			Random random = SnailEntity.this.getRNG();
			BlockPos blockpos = new BlockPos(SnailEntity.this.posX, SnailEntity.this.getBoundingBox().minY, SnailEntity.this.posZ);

			for(int i = 0; i < 10; ++i)
			{
				BlockPos blockpos1 = blockpos.add(random.nextInt(20) - 10, random.nextInt(6) - 3, random.nextInt(20) - 10);
				if (this.isBlockMushroom(blockpos1))
				{
					return new Vec3d((double)blockpos1.getX(), (double)blockpos1.getY(), (double)blockpos1.getZ());
				}
			}

			return null;
		}

		public void tick()
		{
			if (!SnailEntity.this.isChild() && SnailEntity.this.canMove() && SnailEntity.this.getSlimeAmount() <= 0)
			{
				BlockPos blockpos = new BlockPos(SnailEntity.this);

				if (this.isBlockMushroom(blockpos))
				{
					if (net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(SnailEntity.this.world, SnailEntity.this))
					{
						SnailEntity.this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(SnailEntity.this.world.getBlockState(blockpos).getBlock().asItem(), 1));
						SnailEntity.this.setEatingTime(192);
						SnailEntity.this.world.destroyBlock(blockpos, false);
					}
				}
			}
		}

		private boolean isBlockMushroom(BlockPos pos)
		{
			return Config.COMMON.snailBlockFood.get().contains(SnailEntity.this.world.getBlockState(pos).getBlock().getRegistryName().toString());
		}
	}

	public class LookHelperController extends LookController
	{
		public LookHelperController()
		{
			super(SnailEntity.this);
		}

		public void tick()
		{
			if (!SnailEntity.this.getHiding())
			{
				super.tick();
			}
		}
	}

	class MoveHelperController extends MovementController
	{
		public MoveHelperController()
		{
			super(SnailEntity.this);
		}

		public void tick()
		{
			if (SnailEntity.this.canMove())
			{
				super.tick();
			}
		}
	}
}
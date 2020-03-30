package com.markus1002.autumnity.core.util;

import java.util.UUID;

import com.markus1002.autumnity.core.registry.ModBiomes;
import com.markus1002.autumnity.core.registry.ModBlocks;
import com.markus1002.autumnity.core.registry.ModItems;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class EventHandler
{
	private static final AttributeModifier KNOCKBACK_MODIFIER = (new AttributeModifier(UUID.fromString("98D5CD1F-601F-47E6-BEEC-5997E1C4216F"), "Knockback modifier", 1.0D, AttributeModifier.Operation.ADDITION));
	
	@SubscribeEvent
	public void handleBlockRightClick(PlayerInteractEvent.RightClickBlock event)
	{
		PlayerEntity playerentity = event.getPlayer();
		World world = playerentity.getEntityWorld();
		BlockPos blockpos = event.getPos();
		BlockState blockstate = world.getBlockState(blockpos);

		boolean flag = false;

		if ((flag || world.getRandom().nextInt(4) == 0) && event.getItemStack().getItem() instanceof AxeItem)
		{
			Block block = blockstate.getBlock();

			if (block == ModBlocks.MAPLE_LOG.get() || block == ModBlocks.MAPLE_WOOD.get())
			{
				Block block1 = block == ModBlocks.MAPLE_LOG.get() ? ModBlocks.SAPPY_MAPLE_LOG.get() : ModBlocks.SAPPY_MAPLE_WOOD.get();
				world.playSound(playerentity, blockpos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0F, 1.0F);
				if (!world.isRemote)
				{
					world.setBlockState(blockpos, block1.getDefaultState().with(RotatedPillarBlock.AXIS, blockstate.get(RotatedPillarBlock.AXIS)), 11);
					if (playerentity != null)
					{
						event.getItemStack().damageItem(1, playerentity, (p_220040_1_) -> {
							p_220040_1_.sendBreakAnimation(event.getHand());
						});
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onLivingSpawn(LivingSpawnEvent.SpecialSpawn event)
	{
		LivingEntity livingentity = event.getEntityLiving();
		if (livingentity instanceof ZombieEntity || livingentity instanceof AbstractSkeletonEntity)
		{
			if (livingentity.getItemStackFromSlot(EquipmentSlotType.HEAD).isEmpty())
			{
				if (event.getWorld().getBiome(new BlockPos(livingentity)) == ModBiomes.PUMPKIN_FIELDS.get() && event.getWorld().getRandom().nextFloat() < 0.05F)
				{
					livingentity.setItemStackToSlot(EquipmentSlotType.HEAD, new ItemStack(Blocks.CARVED_PUMPKIN));
					((MobEntity) livingentity).setDropChance(EquipmentSlotType.HEAD, 0.0F);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityUpdate(LivingUpdateEvent event)
	{
		LivingEntity entity = event.getEntityLiving();
		
		entity.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).removeModifier(KNOCKBACK_MODIFIER);
		if(entity.getItemStackFromSlot(EquipmentSlotType.CHEST).getItem() == ModItems.SNAIL_SHELL_CHESTPLATE.get() && entity.isShiftKeyDown())
		{
			entity.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).applyModifier(KNOCKBACK_MODIFIER);
		}
	}

	@SubscribeEvent
	public void onWandererTradesEvent(WandererTradesEvent event)
	{
		event.getGenericTrades().add(new TradeHelper.ItemsForEmeraldsTrade(ModBlocks.MAPLE_SAPLING.get().asItem(), 5, 1, 8, 1));
		event.getGenericTrades().add(new TradeHelper.ItemsForEmeraldsTrade(ModBlocks.YELLOW_MAPLE_SAPLING.get().asItem(), 5, 1, 8, 1));
		event.getGenericTrades().add(new TradeHelper.ItemsForEmeraldsTrade(ModBlocks.ORANGE_MAPLE_SAPLING.get().asItem(), 5, 1, 8, 1));
		event.getGenericTrades().add(new TradeHelper.ItemsForEmeraldsTrade(ModBlocks.RED_MAPLE_SAPLING.get().asItem(), 5, 1, 8, 1));
		event.getGenericTrades().add(new TradeHelper.ItemsForEmeraldsTrade(ModBlocks.SNAIL_SLIME.get().asItem(), 4, 1, 5, 1));
	}

	@SubscribeEvent
	public void onVillagerTradesEvent(VillagerTradesEvent event)
	{
	}
}
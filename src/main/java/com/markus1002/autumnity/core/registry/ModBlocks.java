package com.markus1002.autumnity.core.registry;

import java.util.function.Supplier;

import com.markus1002.autumnity.common.block.BookshelfBlock;
import com.markus1002.autumnity.common.block.ColoredMapleLeavesBlock;
import com.markus1002.autumnity.common.block.LeafCarpetBlock;
import com.markus1002.autumnity.common.block.MapleLeavesBlock;
import com.markus1002.autumnity.common.block.ModDoorBlock;
import com.markus1002.autumnity.common.block.ModLadderBlock;
import com.markus1002.autumnity.common.block.ModPressurePlateBlock;
import com.markus1002.autumnity.common.block.ModSaplingBlock;
import com.markus1002.autumnity.common.block.ModTrapDoorBlock;
import com.markus1002.autumnity.common.block.ModWoodButtonBlock;
import com.markus1002.autumnity.common.block.PancakeBlock;
import com.markus1002.autumnity.common.block.SappyLogBlock;
import com.markus1002.autumnity.common.block.SnailSlimeBlock;
import com.markus1002.autumnity.common.block.SnailSlimeFullBlock;
import com.markus1002.autumnity.common.block.VerticalSlabBlock;
import com.markus1002.autumnity.common.block.trees.MapleTree;
import com.markus1002.autumnity.common.block.trees.OrangeMapleTree;
import com.markus1002.autumnity.common.block.trees.RedMapleTree;
import com.markus1002.autumnity.common.block.trees.YellowMapleTree;
import com.markus1002.autumnity.core.util.Reference;

import net.minecraft.block.Block;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.block.LogBlock;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBlocks
{
	public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, Reference.MOD_ID);

	public static final RegistryObject<Block> SNAIL_SLIME = registerBlock("snail_slime", ItemGroup.MISC, () -> new SnailSlimeBlock(Block.Properties.create(Material.CLAY, MaterialColor.WHITE_TERRACOTTA).notSolid().doesNotBlockMovement().sound(SoundType.SLIME)));
	public static final RegistryObject<Block> SNAIL_SLIME_BLOCK = registerBlock("snail_slime_block", ItemGroup.DECORATIONS, () -> new SnailSlimeFullBlock(Block.Properties.create(Material.CLAY, MaterialColor.WHITE_TERRACOTTA).notSolid().sound(SoundType.SLIME)));
	public static final RegistryObject<Block> PANCAKE = registerBlock("pancake", ItemGroup.FOOD, () -> new PancakeBlock(Block.Properties.create(Material.CAKE).hardnessAndResistance(0.5F).sound(SoundType.CLOTH)));

	// Maple Stuff
	public static final RegistryObject<Block> MAPLE_LOG = registerBlock("maple_log", ItemGroup.BUILDING_BLOCKS, () -> new LogBlock(MaterialColor.ORANGE_TERRACOTTA, Block.Properties.create(Material.WOOD, MaterialColor.WHITE_TERRACOTTA).hardnessAndResistance(2.0F).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> MAPLE_WOOD = registerBlock("maple_wood", ItemGroup.BUILDING_BLOCKS, () -> new RotatedPillarBlock(Block.Properties.create(Material.WOOD, MaterialColor.WHITE_TERRACOTTA).hardnessAndResistance(2.0F).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> STRIPPED_MAPLE_LOG = registerBlock("stripped_maple_log", ItemGroup.BUILDING_BLOCKS, () -> new LogBlock(MaterialColor.ORANGE_TERRACOTTA, Block.Properties.create(Material.WOOD, MaterialColor.ORANGE_TERRACOTTA).hardnessAndResistance(2.0F).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> STRIPPED_MAPLE_WOOD = registerBlock("stripped_maple_wood", ItemGroup.BUILDING_BLOCKS, () -> new RotatedPillarBlock(Block.Properties.create(Material.WOOD, MaterialColor.ORANGE_TERRACOTTA).hardnessAndResistance(2.0F).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> SAPPY_MAPLE_LOG = registerBlock("sappy_maple_log", ItemGroup.BUILDING_BLOCKS, () -> new SappyLogBlock(STRIPPED_MAPLE_LOG.get(), MaterialColor.ORANGE_TERRACOTTA, Block.Properties.create(Material.WOOD, MaterialColor.ORANGE_TERRACOTTA).hardnessAndResistance(2.0F).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> SAPPY_MAPLE_WOOD = registerBlock("sappy_maple_wood", ItemGroup.BUILDING_BLOCKS, () -> new SappyLogBlock(STRIPPED_MAPLE_WOOD.get(), MaterialColor.ORANGE_TERRACOTTA, Block.Properties.create(Material.WOOD, MaterialColor.ORANGE_TERRACOTTA).hardnessAndResistance(2.0F).sound(SoundType.WOOD)));

	public static final RegistryObject<Block> MAPLE_PLANKS = registerBlock("maple_planks", ItemGroup.BUILDING_BLOCKS, () -> new Block(Block.Properties.create(Material.WOOD, MaterialColor.ORANGE_TERRACOTTA).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> MAPLE_STAIRS = registerBlock("maple_stairs", ItemGroup.BUILDING_BLOCKS, () -> new StairsBlock(() -> MAPLE_PLANKS.get().getDefaultState(), Block.Properties.from(MAPLE_PLANKS.get())));
	public static final RegistryObject<Block> MAPLE_SLAB = registerBlock("maple_slab", ItemGroup.BUILDING_BLOCKS, () -> new SlabBlock(Block.Properties.create(Material.WOOD, MaterialColor.ORANGE_TERRACOTTA).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> MAPLE_PRESSURE_PLATE = registerBlock("maple_pressure_plate", ItemGroup.REDSTONE, () -> new ModPressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, Block.Properties.create(Material.WOOD, MaterialColor.ORANGE_TERRACOTTA).doesNotBlockMovement().hardnessAndResistance(0.5F).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> MAPLE_BUTTON = registerBlock("maple_button", ItemGroup.REDSTONE, () -> new ModWoodButtonBlock(Block.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(0.5F).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> MAPLE_FENCE = registerBlock("maple_fence", ItemGroup.DECORATIONS, () -> new FenceBlock(Block.Properties.create(Material.WOOD, MaterialColor.ORANGE_TERRACOTTA).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> MAPLE_FENCE_GATE = registerBlock("maple_fence_gate", ItemGroup.DECORATIONS, () -> new FenceGateBlock(Block.Properties.create(Material.WOOD, MaterialColor.ORANGE_TERRACOTTA).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> MAPLE_DOOR = registerBlock("maple_door", ItemGroup.REDSTONE, () -> new ModDoorBlock(Block.Properties.create(Material.WOOD, MaterialColor.ORANGE_TERRACOTTA).notSolid().hardnessAndResistance(3.0F).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> MAPLE_TRAPDOOR = registerBlock("maple_trapdoor", ItemGroup.REDSTONE, () -> new ModTrapDoorBlock(Block.Properties.create(Material.WOOD, MaterialColor.ORANGE_TERRACOTTA).notSolid().hardnessAndResistance(3.0F).sound(SoundType.WOOD)));

	public static final RegistryObject<Block> MAPLE_LEAVES = registerBlock("maple_leaves", ItemGroup.DECORATIONS, () -> new MapleLeavesBlock(Block.Properties.create(Material.LEAVES).notSolid().hardnessAndResistance(0.2F).tickRandomly().sound(SoundType.PLANT)));
	public static final RegistryObject<Block> YELLOW_MAPLE_LEAVES = registerBlock("yellow_maple_leaves", ItemGroup.DECORATIONS, () -> new ColoredMapleLeavesBlock(Block.Properties.create(Material.LEAVES).notSolid().hardnessAndResistance(0.2F).tickRandomly().sound(SoundType.PLANT), 16766735));
	public static final RegistryObject<Block> ORANGE_MAPLE_LEAVES = registerBlock("orange_maple_leaves", ItemGroup.DECORATIONS, () -> new ColoredMapleLeavesBlock(Block.Properties.create(Material.LEAVES).notSolid().hardnessAndResistance(0.2F).tickRandomly().sound(SoundType.PLANT), 16745768));
	public static final RegistryObject<Block> RED_MAPLE_LEAVES = registerBlock("red_maple_leaves", ItemGroup.DECORATIONS, () -> new ColoredMapleLeavesBlock(Block.Properties.create(Material.LEAVES).notSolid().hardnessAndResistance(0.2F).tickRandomly().sound(SoundType.PLANT), 12665871));

	public static final RegistryObject<Block> MAPLE_SAPLING = registerBlock("maple_sapling", ItemGroup.DECORATIONS, () -> new ModSaplingBlock(new MapleTree(), Block.Properties.create(Material.PLANTS).doesNotBlockMovement().tickRandomly().hardnessAndResistance(0.0F).sound(SoundType.PLANT)));
	public static final RegistryObject<Block> YELLOW_MAPLE_SAPLING = registerBlock("yellow_maple_sapling", ItemGroup.DECORATIONS, () -> new ModSaplingBlock(new YellowMapleTree(), Block.Properties.create(Material.PLANTS).doesNotBlockMovement().tickRandomly().hardnessAndResistance(0.0F).sound(SoundType.PLANT)));
	public static final RegistryObject<Block> ORANGE_MAPLE_SAPLING = registerBlock("orange_maple_sapling", ItemGroup.DECORATIONS, () -> new ModSaplingBlock(new OrangeMapleTree(), Block.Properties.create(Material.PLANTS).doesNotBlockMovement().tickRandomly().hardnessAndResistance(0.0F).sound(SoundType.PLANT)));
	public static final RegistryObject<Block> RED_MAPLE_SAPLING = registerBlock("red_maple_sapling", ItemGroup.DECORATIONS, () -> new ModSaplingBlock(new RedMapleTree(), Block.Properties.create(Material.PLANTS).doesNotBlockMovement().tickRandomly().hardnessAndResistance(0.0F).sound(SoundType.PLANT)));

	public static final RegistryObject<Block> POTTED_MAPLE_SAPLING = registerBlock("potted_maple_sapling", () -> new FlowerPotBlock(MAPLE_SAPLING.get(), Block.Properties.create(Material.MISCELLANEOUS).hardnessAndResistance(0.0F)));
	public static final RegistryObject<Block> POTTED_YELLOW_MAPLE_SAPLING = registerBlock("potted_yellow_maple_sapling", () -> new FlowerPotBlock(YELLOW_MAPLE_SAPLING.get(), Block.Properties.create(Material.MISCELLANEOUS).hardnessAndResistance(0.0F)));
	public static final RegistryObject<Block> POTTED_ORANGE_MAPLE_SAPLING = registerBlock("potted_orange_maple_sapling", () -> new FlowerPotBlock(ORANGE_MAPLE_SAPLING.get(), Block.Properties.create(Material.MISCELLANEOUS).hardnessAndResistance(0.0F)));
	public static final RegistryObject<Block> POTTED_RED_MAPLE_SAPLING = registerBlock("potted_red_maple_sapling", () -> new FlowerPotBlock(RED_MAPLE_SAPLING.get(), Block.Properties.create(Material.MISCELLANEOUS).hardnessAndResistance(0.0F)));

	public static final RegistryObject<Block> MAPLE_LEAF_CARPET = registerCompatibilityBlock("quark", "maple_leaf_carpet", ItemGroup.DECORATIONS, () -> new LeafCarpetBlock(Block.Properties.create(Material.LEAVES).notSolid().hardnessAndResistance(0.2F).tickRandomly().sound(SoundType.PLANT)));
	public static final RegistryObject<Block> YELLOW_MAPLE_LEAF_CARPET = registerBlock("yellow_maple_leaf_carpet", ItemGroup.DECORATIONS, () -> new LeafCarpetBlock(Block.Properties.create(Material.LEAVES).notSolid().hardnessAndResistance(0.2F).tickRandomly().sound(SoundType.PLANT)));
	public static final RegistryObject<Block> ORANGE_MAPLE_LEAF_CARPET = registerBlock("orange_maple_leaf_carpet", ItemGroup.DECORATIONS, () -> new LeafCarpetBlock(Block.Properties.create(Material.LEAVES).notSolid().hardnessAndResistance(0.2F).tickRandomly().sound(SoundType.PLANT)));
	public static final RegistryObject<Block> RED_MAPLE_LEAF_CARPET = registerBlock("red_maple_leaf_carpet", ItemGroup.DECORATIONS, () -> new LeafCarpetBlock(Block.Properties.create(Material.LEAVES).notSolid().hardnessAndResistance(0.2F).tickRandomly().sound(SoundType.PLANT)));

	// Quark
	public static final RegistryObject<Block> MAPLE_VERTICAL_SLAB = registerCompatibilityBlock("quark", "maple_vertical_slab", ItemGroup.BUILDING_BLOCKS, () -> new VerticalSlabBlock(Block.Properties.create(Material.WOOD, MaterialColor.ORANGE_TERRACOTTA).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> VERTICAL_MAPLE_PLANKS = registerCompatibilityBlock("quark", "vertical_maple_planks", ItemGroup.BUILDING_BLOCKS, () -> new Block(Block.Properties.create(Material.WOOD, MaterialColor.ORANGE_TERRACOTTA).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> MAPLE_BOOKSHELF = registerCompatibilityBlock("quark", "maple_bookshelf", ItemGroup.BUILDING_BLOCKS, () -> new BookshelfBlock(Block.Properties.create(Material.WOOD, MaterialColor.ORANGE_TERRACOTTA).hardnessAndResistance(1.5F).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> MAPLE_LADDER = registerCompatibilityBlock("quark", "maple_ladder", ItemGroup.DECORATIONS, () -> new ModLadderBlock(Block.Properties.create(Material.MISCELLANEOUS).notSolid().hardnessAndResistance(0.4F).harvestTool(ToolType.AXE).sound(SoundType.LADDER)));

	private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<? extends T> supplier)
	{
		RegistryObject<T> block = BLOCKS.register(name, supplier);
		return block;
	}
	
	private static <T extends Block> RegistryObject<T> registerBlock(String name, ItemGroup group, Supplier<? extends T> supplier)
	{
		RegistryObject<T> block = BLOCKS.register(name, supplier);
		ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().group(group)));
		return block;
	}

	private static <T extends Block> RegistryObject<T> registerCompatibilityBlock(String mod, String name, ItemGroup group, Supplier<? extends T> supplier)
	{
		RegistryObject<T> block = BLOCKS.register(name, supplier);
		ItemGroup itemgroup = ModList.get().isLoaded(mod) ? group : null;
		ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().group(itemgroup)));
		return block;
	}
}
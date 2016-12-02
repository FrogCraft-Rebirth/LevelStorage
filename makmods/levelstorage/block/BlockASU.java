package makmods.levelstorage.block;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import ic2.api.item.ElectricItem;
import ic2.api.item.IC2Items;
import ic2.api.recipe.Recipes;
import ic2.core.block.BlockTileEntity;
import ic2.core.block.ITeBlock;
import ic2.core.block.TileEntityBlock;
import ic2.core.item.block.ItemBlockTileEntity;
import ic2.core.ref.TeBlock;
import ic2.core.ref.TeBlock.DefaultDrop;
import ic2.core.util.Util;
import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.init.IHasRecipe;
import makmods.levelstorage.item.SimpleItems.SimpleItemShortcut;
import makmods.levelstorage.lib.Reference;
import makmods.levelstorage.tileentity.TileEntityASU;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class BlockASU implements ITeBlock, IHasRecipe {
	
	public static final ResourceLocation ASU_ID = new ResourceLocation(Reference.MOD_ID, "asu");
	
	private TeBlock.ITePlaceHandler placeHandler;
	
	//Legacy constructor. The id parameter does NOTHING after Minecraft 1.7 Release.
	public BlockASU(int id) {}

	@Override
	public void addCraftingRecipe() {
		// TODO: make the recipe more expensive. MUCH MORE EXPENSIVE.
		/*Recipes.advRecipes.addRecipe(new ItemStack(LSBlockItemList.blockASU),
				"epe", "eme", "epe", 
				'e', new ItemStack(LSBlockItemList.itemAntimatterCrystal), 
				'm', IC2Items.getItem("te", "mfsu"), 
				'p', SimpleItemShortcut.PLATE_ANTIMATTER_IRIDIUM.getItemStack());*/
	}

	@Override
	public int getId() {
		return 0;
	}

	@Override
	public String getName() {
		return "asu";
	}

	@Override
	public void addSubBlocks(List<ItemStack> list, BlockTileEntity teBlock, ItemBlockTileEntity itemTeBlock, CreativeTabs tab) {
		list.add(new ItemStack(teBlock));
		ItemStack stack = new ItemStack(teBlock);
		ElectricItem.manager.charge(stack, Integer.MAX_VALUE, Integer.MAX_VALUE, true, false);
		list.add(stack);
	}

	@Override
	public boolean allowWrenchRotating() {
		return true;
	}

	@Override
	public TeBlock.DefaultDrop getDefaultDrop() {
		return DefaultDrop.AdvMachine;
	}

	@Override
	public TileEntityBlock getDummyTe() {
		return new TileEntityASU();
	}

	@Override
	public float getExplosionResistance() {
		return 15.0F;
	}

	@Override
	public float getHardness() {
		return 10.0F;
	}

	@Override
	public TeBlock.HarvestTool getHarvestTool() {
		return TeBlock.HarvestTool.Pickaxe;
	}

	@Override
	public ResourceLocation getIdentifier() {
		return BlockASU.ASU_ID;
	}

	@Override
	public Material getMaterial() {
		return Material.IRON;
	}

	@Override
	public TeBlock.ITePlaceHandler getPlaceHandler() {
		return this.placeHandler;
	}

	@Override
	public EnumRarity getRarity() {
		return EnumRarity.EPIC;
	}

	@Override
	public Set<EnumFacing> getSupportedFacings() {
		return Util.allFacings;
	}

	@Override
	public Class<? extends TileEntityBlock> getTeClass() {
		return TileEntityASU.class;
	}

	@Override
	public boolean hasActive() {
		return false;
	}

	@Override
	public boolean hasItem() {
		return false;
	}

	@Override
	public void setPlaceHandler(TeBlock.ITePlaceHandler placeHandler) {
		this.placeHandler = Optional.of(placeHandler).orElse(this.placeHandler);
	}

}

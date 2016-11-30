package makmods.levelstorage.block;

import ic2.api.recipe.Recipes;

import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LSCreativeTab;
import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.init.IHasRecipe;
import makmods.levelstorage.lib.IC2ItemsShortcut;
import makmods.levelstorage.logic.util.LogHelper;
import makmods.levelstorage.tileentity.TileEntityMulticoreSolarPanel;
import net.minecraft.block.SoundType;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class BlockMulticoreSolarPanel extends BlockMachineStandart implements IHasRecipe {

	public BlockMulticoreSolarPanel(int id) {
		super(id);
		if (FMLCommonHandler.instance().getSide().isClient()) {
			this.setCreativeTab(LSCreativeTab.instance);
		}
		this.setSoundType(SoundType.METAL);
		this.setHardness(3.0F);
	}

	public void addCraftingRecipe() {
		if (!LevelStorage.isAnySolarModLoaded()) {
			LogHelper
					.warning("No solar mods loaded. Not adding Multicore Solar Panel's recipe");
			return;
		}
		try {
			Recipes.advRecipes
					.addRecipe(new ItemStack(
							LSBlockItemList.blockMulticoreSolarPanel), "csc",
							"sns", "csc", Character.valueOf('c'),
							IC2ItemsShortcut.CARBON_PLATE.copy(), Character
									.valueOf('n'), "itemEnergizedStar",
							Character.valueOf('s'), "solarPanelHV");
		} catch (Throwable t) {
			t.printStackTrace();
		}
		try {
			Recipes.advRecipes
					.addRecipe(new ItemStack(
							LSBlockItemList.blockMulticoreSolarPanel), "csc",
							"sns", "csc", Character.valueOf('c'),
							IC2ItemsShortcut.CARBON_PLATE.copy(), Character
									.valueOf('n'), "itemEnergizedStar",
							Character.valueOf('s'), "craftingSolarPanelHV");
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityMulticoreSolarPanel();
	}

}

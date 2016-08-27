package makmods.levelstorage;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * A simple creative tab all my stuff is located on
 * @author mak326428
 *
 */
public class LSCreativeTab extends CreativeTabs {
	
	@SideOnly(Side.CLIENT)
	public static LSCreativeTab instance;

	public LSCreativeTab() {
		super("levelstorage");
	}

	@Override
	public ItemStack getIconItemStack() {
		return new ItemStack(LSBlockItemList.itemQuantumRing);
	}

	@Override
	public Item getTabIconItem() {
		return LSBlockItemList.itemQuantumRing;
	}
}

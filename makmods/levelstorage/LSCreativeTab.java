package makmods.levelstorage;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * A simple creative tab all my stuff is located on
 * @author mak326428
 *
 */
public class LSCreativeTab extends CreativeTabs {
	
	public static LSCreativeTab instance = new LSCreativeTab();

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

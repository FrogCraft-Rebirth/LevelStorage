package makmods.levelstorage.block.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockRockDesintegrator extends ItemBlock {

	public ItemBlockRockDesintegrator(Block block) {
		super(block);
	}
	
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean f3) {
    	tooltip.add("Maximum input: 512 EU/t");
    	tooltip.add("1 Cobblestone = 64 EU");
    }
}

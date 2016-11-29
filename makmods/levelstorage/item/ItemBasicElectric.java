package makmods.levelstorage.item;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;

import java.util.List;

import makmods.levelstorage.LSCreativeTab;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Provides basic functions for easier electric items implementation
 * 
 * @author mak326428
 * 
 */
public abstract class ItemBasicElectric extends Item implements IElectricItem {

	private int storage;
	private int tier;
	private int transferLimit;
	private int energyPerUse;

	/**
	 * <p>
	 * Called upon item gets clicked on a block
	 * </p>
	 * <p>
	 * It is only called on simulating side (server), so you don't need to check
	 * for !world.isRemote
	 * </p>
	 * 
	 * @param item
	 *            ItemStack representing the item
	 * @param world
	 *            World (can be safely casted to WorldServer, if needed)
	 * @param player
	 *            EntityPlayer (can be safely casted to EntityPlayerMP, if
	 *            needed)
	 * @return whether or not {@link #energyPerUse} should be consumed
	 */
	public abstract boolean onBlockClick(ItemStack item, World world,
			EntityPlayer player, int x, int y, int z, int side);

	@SideOnly(Side.CLIENT)
	public abstract String getItemTexture();

	/**
	 * <p>
	 * Called upon item gets right clicked (not on a block)
	 * </p>
	 * <p>
	 * It is only called on simulating side (server), so you don't need to check
	 * for !world.isRemote
	 * </p>
	 * 
	 * @param item
	 *            ItemStack representing the item
	 * @param world
	 *            World (can be safely casted to WorldServer, if needed)
	 * @param player
	 *            EntityPlayer (can be safely casted to EntityPlayerMP, if
	 *            needed)
	 * @return whether or not {@link #energyPerUse} should be consumed
	 */
	public abstract boolean onRightClick(ItemStack item, World world,
			EntityPlayer player);

	public ItemBasicElectric(int id, int tier, int storage, int transferLimit,
			int energyPerUse) {
		super();
		this.setMaxDamage(27);
		this.setNoRepair();
		if (FMLCommonHandler.instance().getSide().isClient()) {
			this.setCreativeTab(LSCreativeTab.instance);
		}
		this.setMaxStackSize(1);
		this.storage = storage;
		this.tier = tier;
		this.transferLimit = transferLimit;
		this.energyPerUse = energyPerUse;
	}

	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World,
			EntityPlayer par3EntityPlayer) {
		if (!par2World.isRemote)
			if (ElectricItem.manager.canUse(par1ItemStack, energyPerUse))
				if (onRightClick(par1ItemStack, par2World, par3EntityPlayer))
					ElectricItem.manager.use(par1ItemStack, energyPerUse,
							par3EntityPlayer);
		return par1ItemStack;
	}

	public boolean onItemUse(ItemStack par1ItemStack,
			EntityPlayer par2EntityPlayer, World par3World, int x, int y,
			int z, int side, float par8, float par9, float par10) {
		if (!par3World.isRemote)
			if (ElectricItem.manager.canUse(par1ItemStack, energyPerUse))
				if (onBlockClick(par1ItemStack, par3World, par2EntityPlayer, x,
						y, z, side)) {
					ElectricItem.manager.use(par1ItemStack, energyPerUse,
							par2EntityPlayer);
					return false;
				}
		return true;
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
		ItemStack var4 = new ItemStack(this, 1);
		ElectricItem.manager.charge(var4, Integer.MAX_VALUE, Integer.MAX_VALUE,
				true, false);
		list.add(var4);
		list.add(new ItemStack(this, 1, this.getMaxDamage()));

	}

	@Override
	public boolean canProvideEnergy(ItemStack itemStack) {
		return false;
	}

	@Override
	public double getMaxCharge(ItemStack itemStack) {
		return this.storage;
	}

	@Override
	public int getTier(ItemStack itemStack) {
		return this.tier;
	}

	@Override
	public double getTransferLimit(ItemStack itemStack) {
		return this.transferLimit;
	}
/*
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		this.itemIcon = iconRegister.registerIcon(getItemTexture());
	}*/
}

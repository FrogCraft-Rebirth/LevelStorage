package makmods.levelstorage.item;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.proxy.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemAdvancedScanner extends Item implements IElectricItem {
	public static final int TIER = 2;
	public static final int STORAGE = 100000;
	public static final int COOLDOWN_PERIOD = 60;
	public static final String NBT_COOLDOWN = "cooldown";

	public ItemAdvancedScanner(int id) {
		super(id);
		this.setUnlocalizedName("item.advScanner");
		this.setMaxDamage(27);
		this.setNoRepair();
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
			this.setCreativeTab(ClientProxy.getCreativeTab("IC2"));
		this.setMaxStackSize(1);
	}

	public static void verifyStack(ItemStack stack) {
		// Just in case... Whatever!
		if (stack.stackTagCompound == null) {
			stack.stackTagCompound = new NBTTagCompound();
			if (!stack.stackTagCompound.hasKey("charge")) {
				stack.stackTagCompound.setInteger("charge", 0);
			}
		}
	}

	public static void setNBTInt(ItemStack stack, String name, int value) {
		verifyStack(stack);
		stack.stackTagCompound.setInteger(name, value);
	}

	public static int getNBTInt(ItemStack stack, String name) {
		verifyStack(stack);
		if (!stack.stackTagCompound.hasKey(name))
			stack.stackTagCompound.setInteger(name, 0);
		return stack.stackTagCompound.getInteger(name);
	}

	public void printMessage(String what) {
		if (LevelStorage.getSide() == Side.CLIENT) {
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(
					what);
		}
	}
	// TODO: Add energy consumption and counting how much stuff is in the chunk
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World,
			EntityPlayer par3EntityPlayer) {
		if (!par2World.isRemote) {
			if (!(getNBTInt(par1ItemStack, NBT_COOLDOWN) == 0))
				return par1ItemStack;
			setNBTInt(par1ItemStack, NBT_COOLDOWN, COOLDOWN_PERIOD);
		}

		ArrayList<ItemStack> blocksFound = new ArrayList<ItemStack>();

		int chunkX = (int) ((int) par3EntityPlayer.posX / 16);
		int chunkZ = (int) ((int) par3EntityPlayer.posZ / 16);
		int playerY = (int) par3EntityPlayer.posY;

		for (int y = 0; y < playerY; y++) {
			for (int x = chunkX * 16; x < chunkX * 16 + 16; x++) {
				for (int z = chunkZ * 16; z < chunkZ * 16 + 16; z++) {
					ItemStack foundStack = new ItemStack(par2World.getBlockId(
							x, y, z), 1, par2World.getBlockMetadata(x, y, z));
					blocksFound.add(foundStack);
				}
			}
		}
		ArrayList<String> prettyNames = new ArrayList<String>();
		Map<String, Integer> amounts = new HashMap<String, Integer>();
		printMessage("");
		printMessage("");
		printMessage("Found materials at chunk " + chunkX + ":" + chunkZ);
		printMessage("");
		for (ItemStack stack : blocksFound) {
			if (stack == null || stack.getItem() == null)
				continue;
			String currentName = stack.getDisplayName();
			
			if (!prettyNames.contains(currentName)) {
				prettyNames.add(currentName);
				printMessage(currentName);
			}
		}
		return par1ItemStack;
	}

	public void onUpdate(ItemStack par1ItemStack, World par2World,
			Entity par3Entity, int par4, boolean par5) {
		if (!par2World.isRemote) {
			verifyStack(par1ItemStack);
			if (getNBTInt(par1ItemStack, NBT_COOLDOWN) > 0) {
				setNBTInt(par1ItemStack, NBT_COOLDOWN,
						getNBTInt(par1ItemStack, NBT_COOLDOWN) - 1);
			}
		}
	}

	@Override
	public boolean canProvideEnergy(ItemStack itemStack) {
		return false;
	}

	@Override
	public int getChargedItemId(ItemStack itemStack) {
		return this.itemID;
	}

	@Override
	public int getEmptyItemId(ItemStack itemStack) {
		return this.itemID;
	}

	@Override
	public int getMaxCharge(ItemStack itemStack) {
		return STORAGE;
	}

	@Override
	public int getTier(ItemStack itemStack) {
		return TIER;
	}

	@Override
	public int getTransferLimit(ItemStack itemStack) {
		return 200;
	}

	public void getSubItems(int par1, CreativeTabs par2CreativeTabs,
			List par3List) {
		ItemStack var4 = new ItemStack(this, 1);
		ElectricItem.manager.charge(var4, Integer.MAX_VALUE, Integer.MAX_VALUE,
				true, false);
		par3List.add(var4);
		par3List.add(new ItemStack(this, 1, this.getMaxDamage()));

	}

	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister
				.registerIcon(ClientProxy.ADV_SCANNER_TEXTURE);
	}
}

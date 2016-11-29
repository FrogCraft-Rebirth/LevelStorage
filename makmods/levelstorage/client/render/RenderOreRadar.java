package makmods.levelstorage.client.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import makmods.levelstorage.armor.ItemArmorTeslaHelmet;
import makmods.levelstorage.logic.util.BlockLocation;
import makmods.levelstorage.logic.util.OreDictHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderOreRadar {

	public static final int RENDER_DISTANCE = 16;
	public static final int REFRESH_RATE = 40;
	public static ArrayList<BlockLocation> oreLoc = new ArrayList<BlockLocation>();

	public static int currFresh = 0;

	@SubscribeEvent
	public void radarRender(DrawBlockHighlightEvent event) {
		currFresh++;
		if (ItemArmorTeslaHelmet.playerGetArmor(event.getPlayer()) != null) {
			// REFRESHING
			if (currFresh > REFRESH_RATE) {
				currFresh = 0;
				oreLoc.clear();
				int initialPosX = (int) event.getPlayer().posX;
				int initialPosY = (int) event.getPlayer().posY;
				int initialPosZ = (int) event.getPlayer().posZ;

				for (int x = -RENDER_DISTANCE; x < RENDER_DISTANCE; x++) {
					for (int y = -RENDER_DISTANCE; y < RENDER_DISTANCE; y++) {
						for (int z = -RENDER_DISTANCE; z < RENDER_DISTANCE; z++) {
							try {
								World w = event.getPlayer().worldObj;
								int currX = initialPosX + x;
								int currY = initialPosY + y;
								int currZ = initialPosZ + z;
								if (!w.isAirBlock(currX, currY, currZ)) {
									int blockId = w.getBlockId(currX, currY,
									        currZ);
									int meta = w.getBlockMetadata(currX, currY,
									        currZ);
									Block bl = Block.blocksList[blockId];
									if (OreDictHelper.getOreName(
									        new ItemStack(bl))
									        .startsWith("ore")) {
										oreLoc.add(new BlockLocation(
										        w.provider.dimensionId, currX,
										        currY, currZ));
									}
								}
							} catch (Exception e) {

							}
						}
					}
				}
			}
			// END OF REFRESHING
			// BlockID : Amount
			HashMap<Integer, Integer> ores = new HashMap<Integer, Integer>();

			for (BlockLocation bl : oreLoc) {
				IBlockState bs = event.getPlayer().worldObj.getBlockState(bl.toBlockPos());
				if (!ores.containsKey(bs)) {
					ores.put(bs, 1);
				} else {
					int prevAmount = ores.get(bs);
					ores.remove(bs);
					ores.put(bs, prevAmount + 1);
				}
			}

			for (Entry<Integer, Integer> entry : ores.entrySet()) {

			}
		}

	}

}

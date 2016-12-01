package makmods.levelstorage.client.render;

import java.util.ArrayList;
import java.util.HashMap;

import makmods.levelstorage.armor.ItemArmorTeslaHelmet;
import makmods.levelstorage.logic.util.OreDictHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderOreRadar {

	public static final int RENDER_DISTANCE = 16;
	public static final int REFRESH_RATE = 40;
	private static ArrayList<BlockPos> oreLoc = new ArrayList<>();

	public static int currFresh = 0;

	@SubscribeEvent
	public void radarRender(DrawBlockHighlightEvent event) {
		currFresh++;
		if (ItemArmorTeslaHelmet.playerGetArmor(event.getPlayer()) != null) {
			// REFRESHING
			if (currFresh > REFRESH_RATE) {
				currFresh = 0;
				oreLoc.clear();
				BlockPos initialPos = event.getPlayer().getPosition();
				Iterable<BlockPos> searchRange = BlockPos.getAllInBox(initialPos.add(-RENDER_DISTANCE, -RENDER_DISTANCE, -RENDER_DISTANCE), initialPos.add(RENDER_DISTANCE, RENDER_DISTANCE, RENDER_DISTANCE));

				for (BlockPos currPos : searchRange) {
					try {
						World w = event.getPlayer().worldObj;
						if (!w.isAirBlock(currPos)) {
							IBlockState bs = w.getBlockState(currPos);
							if (OreDictHelper.getOreName(new ItemStack(bs.getBlock(), 1, bs.getBlock().getMetaFromState(bs))).startsWith("ore")) {
								oreLoc.add(currPos);
							}
						}
					} catch (Exception e) {

					}
				}
			}
			// END OF REFRESHING
			// BlockID : Amount -> Time is changing. Now BlockState : Amount.
			final HashMap<IBlockState, Integer> ores = new HashMap<>();
			
			oreLoc.forEach(pos -> {
				final IBlockState bs = event.getPlayer().getEntityWorld().getBlockState(pos);
				ores.merge(bs, 1, (prevValue, newValue) -> prevValue + newValue);
			});

			ores.entrySet().forEach(enrty -> {}); //There is such a weird thing.
		}

	}

}

package makmods.levelstorage.client.render;

import org.lwjgl.opengl.GL11;

import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.logic.util.RenderHelper;
import makmods.levelstorage.proxy.ClientProxy;
import makmods.levelstorage.registry.ConductorType;
import makmods.levelstorage.tileentity.TileEntityWirelessConductor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class WirelessConductorRender extends TileEntitySpecialRenderer<TileEntityWirelessConductor> {

	public ConductorModel model = new ConductorModel();

	public WirelessConductorRender() {
	}

	@Override
	public void renderTileEntityAt(TileEntityWirelessConductor cnd, double x, double y, double z, float partialTicks, int destoryState) {
		// The PushMatrix tells the renderer to "start" doing something.

		GL11.glPushMatrix();
		// This is setting the initial location.
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		// This is the texture of your block. It's pathed to be the same place
		// as your other blocks here.
		// BIND TEXTURE HERE
		RenderHelper.bindTexture(ClientProxy.CONDUCTOR_MODEL);
		// This rotation part is very important! Without it, your model will
		// render upside-down! And for some reason you DO need PushMatrix again!
		GL11.glPushMatrix();
		GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
		// A reference to your Model file. Again, very important.
		this.model.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		// Tell it to stop rendering for both the PushMatrix's
		GL11.glPopMatrix();
		GL11.glPopMatrix();

		// if (LevelStorage.getSide().isClient()) {
		if (LevelStorage.fancyGraphics) {
			if (cnd.getType() == ConductorType.SOURCE) {
				if (cnd.safePair != null) {
					if (cnd.safePair.getDimId() == cnd.getDimId()) {
						EnergyRayFX p = new EnergyRayFX(cnd.getWorld(),
								cnd.getCoordinate().getX(), cnd.getCoordinate().getY(), cnd.getCoordinate().getZ(),
						        cnd.safePair.getCoordinate().getX(), cnd.safePair.getCoordinate().getY(), cnd.safePair.getCoordinate().getZ(),
						        48, 141, 255, 10);
						Minecraft.getMinecraft().effectRenderer.addEffect(p);
					}
				}
			}
		}
		// }

		/*
		 * GL11.glPushMatrix(); Tessellator.instance.startDrawingQuads();
		 * Tessellator.instance.addVertex(0, 0, 1);
		 * Tessellator.instance.addVertex(1, 0, 1);
		 * Tessellator.instance.addVertex(1, 1, 1);
		 * Tessellator.instance.addVertex(0, 1, 1); Tessellator.instance.draw();
		 * GL11.glPopMatrix();
		 */
	}

}

package makmods.levelstorage.client.render;

import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

import makmods.levelstorage.logic.util.RenderHelper;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.world.World;

public class TestParticleFX extends Particle {

	public TestParticleFX(World world, double posX, double posY, double posZ) {
		super(world, posX, posY, posZ);
		this.particleAge = 100;
		setSize(0.02f, 0.02f);
	}

	public TestParticleFX(World world, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed) {
		super(world, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed);
	}

	@Override
	public void renderParticle(VertexBuffer worldRenderer, Entity entity, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDepthMask(false);
		RenderHelper.bindSingleSlotGUI();
		worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
		tessellator.setBrightness(200);
		worldRenderer.pos(0.0D, 0.0D, 0.0D).tex(1.0D, 0.0D);
		worldRenderer.pos(1.0D, 0.0D, 0.0D).tex(1.0D, 0.0D);
		worldRenderer.pos(1.0D, 1.0D, 0.0D).tex(0.0D, 1.0D);
		worldRenderer.pos(0.0D, 1.0D, 0.0D).tex(0.0D, 1.0D);
		worldRenderer.finishDrawing();
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}
	
	public void onUpdate() {
		if (this.particleAge++ >= this.particleMaxAge) {
			setExpired();// per setDead();
		}
	}

}

package makmods.levelstorage.fluid;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

public class FluidIV extends Fluid {

	public FluidIV() {
		super("iv", new ResourceLocation("levelstorage", "iv"), new ResourceLocation("levelstorage", "iv"));
		this.setUnlocalizedName("iv");
		this.setGaseous(false);
		//FluidContainerRegistry.registerFluidContainer(this,
		//		Items.getItem("electrolyzedWaterCell"), Items.getItem("cell"));
	}
}

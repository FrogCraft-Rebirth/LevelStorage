package makmods.levelstorage.api.event;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fluids.Fluid;

@Cancelable
/**
 * Fired when a fluid gets registered in CombustiblesRegistry.
 * @author mak326428
 *
 */
public class CombustibleRegisterEvent extends Event {

	private int eu;
	private Fluid fluid;

	public CombustibleRegisterEvent(Fluid fluid, int eu) {
		this.eu = eu;
		this.fluid = fluid;
	}

	public int getEU() {
		return eu;
	}

	public void setEU(int eu) {
		this.eu = eu;
	}

	public Fluid getFluid() {
		return fluid;
	}

	public void setFluid(Fluid fluid) {
		this.fluid = fluid;
	}
}

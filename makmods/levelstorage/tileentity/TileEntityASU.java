package makmods.levelstorage.tileentity;

import ic2.core.block.wiring.TileEntityElectricBlock;

public class TileEntityASU extends TileEntityElectricBlock  {

	//But why it used to be long? it is smaller than Integer.MAX_VALUE.
	public static final int EU_STORAGE = 2000000000;
	public static final int EU_PER_TICK = 8192;
	public static final int TIER = 5;

	public TileEntityASU() {
		super(TIER, EU_PER_TICK, EU_STORAGE);
	}

}

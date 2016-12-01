package makmods.levelstorage.tileentity;

import com.chocohead.advsolar.tiles.TileEntitySolarPanel;

//Yes, I am kidding everyone - simply put ASP as dependencies
//If ASP is absent, it simply don't load at all.
public class TileEntityMulticoreSolarPanel extends TileEntitySolarPanel {

	public TileEntityMulticoreSolarPanel() {
		//int dayPower, int nightPower, int storage, int tier
		super(2048, 256, (int)2E8, 4);
	}
}

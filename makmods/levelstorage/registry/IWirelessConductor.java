package makmods.levelstorage.registry;

import net.minecraft.util.math.BlockPos;

/**
 * Basic interface for all the wireless conductors.
 * 
 * @author mak326428
 */
public interface IWirelessConductor {
	/**
	 * Coordinate for the current instance
	 */
	public BlockPos getCoordinate();

	/**
	 * Type for current instance
	 */
	public ConductorType getType();

	/**
	 * Dimension id for the current instance
	 */
	public int getDimId();

	/**
	 * ConductorType.SINK only, called when some other paired conductor sends
	 * you energy
	 * 
	 * @param amount
	 *            Energy being sent
	 * @return Energy not being consumed
	 */
	public double receiveEnergy(double amount, IWirelessConductor transmitter);

	/**
	 * Returns pair for the current instance
	 * 
	 * @return linked pair
	 */
	public IWirelessConductor getPair();
	
	public boolean canReceive(double amount);
}

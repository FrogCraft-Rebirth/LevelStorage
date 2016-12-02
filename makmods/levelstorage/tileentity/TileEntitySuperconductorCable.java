package makmods.levelstorage.tileentity;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergyConductor;
import ic2.api.energy.tile.IEnergyEmitter;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;

public class TileEntitySuperconductorCable extends TileEntity implements
        IEnergyConductor, ITickable {

	public int connectivity;

	public float getCableThickness() {
		return 0.25f;
	}

	public static double getCableThickness(int meta) {
		return 0.25f;
	}

	public int[] retextureRefId;
	public int[] retextureRefMeta;
	public int[] retextureRefSide;
	public int renderSide;

	public boolean retexture(int side, int referencedBlockId,
	        int referencedMeta, int referencedSide) {
		boolean ret = false;
		boolean updateAll = false;

		if (this.retextureRefId == null) {
			this.retextureRefId = new int[6];
			this.retextureRefMeta = new int[6];
			this.retextureRefSide = new int[6];
			updateAll = true;
		}

		if ((this.retextureRefId[side] != referencedBlockId) || (updateAll)) {
			this.retextureRefId[side] = referencedBlockId;
			ret = true;
		}

		if ((this.retextureRefMeta[side] != referencedMeta) || (updateAll)) {
			this.retextureRefMeta[side] = referencedMeta;
			ret = true;
		}

		if ((this.retextureRefSide[side] != referencedSide) || (updateAll)) {
			this.retextureRefSide[side] = referencedSide;
			ret = true;
		}

		return ret;
	}

	public boolean needsUpdate = true;

	@Override
	public void update() {
		if (!this.worldObj.isRemote) {
			if (!addedToENet) {
				MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
				this.addedToENet = true;
			}
		}
		onNeighborBlockChange();
	}

	public void remove() {
		if (addedToENet) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			this.addedToENet = false;
		}
	}

	@Override
	public void invalidate() {
		remove();
		super.invalidate();
	}

	@Override
	public void onChunkUnload() {
		remove();
		super.onChunkUnload();
	}

	public void onNeighborBlockChange() {
		byte newConnectivity = 0;
		byte newRenderSide = 0;

		int mask = 1;

		for (EnumFacing direction : EnumFacing.VALUES) {
			// TileEntity neighbor = EnergyNet.getForWorld(this.worldObj)
			// .getNeighbor(this, direction);
			BlockPos nextLoc = this.getPos().add(direction.getDirectionVec());
			TileEntity neighbor = worldObj.getTileEntity(nextLoc);

			if ((((neighbor instanceof IEnergyAcceptor)) && (((IEnergyAcceptor) neighbor)
			        .acceptsEnergyFrom(this, direction.getOpposite())))
			        || (((neighbor instanceof IEnergyEmitter)) && (((IEnergyEmitter) neighbor)
			                .emitsEnergyTo(this, direction.getOpposite())))) {
				newConnectivity = (byte) (newConnectivity | mask);

				if (((neighbor instanceof TileEntitySuperconductorCable))
				        && (((TileEntitySuperconductorCable) neighbor)
				                .getCableThickness() < getCableThickness())) {
					newRenderSide = (byte) (newRenderSide | mask);
				}
			}

			mask *= 2;
		}

		if (this.connectivity != newConnectivity) {
			this.connectivity = newConnectivity;
		}

		if (this.renderSide != newRenderSide) {
			this.renderSide = newRenderSide;
		}
	}

	public boolean addedToENet = false;

	@Override
	public double getConductionLoss() {
		return 0;
	}

	@Override
	public double getInsulationEnergyAbsorption() {
		return Integer.MAX_VALUE;
	}

	@Override
	public double getInsulationBreakdownEnergy() {
		return Integer.MAX_VALUE;
	}

	@Override
	public double getConductorBreakdownEnergy() {
		return Integer.MAX_VALUE;
	}

	@Override
	public void removeInsulation() {

	}

	@Override
	public void removeConductor() {

	}

	@Override
	public boolean acceptsEnergyFrom(IEnergyEmitter emitter, EnumFacing direction) {
		return true;
	}

	@Override
	public boolean emitsEnergyTo(IEnergyAcceptor receiver, EnumFacing direction) {
		return true;
	}
}

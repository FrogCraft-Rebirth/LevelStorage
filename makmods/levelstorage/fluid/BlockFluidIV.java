package makmods.levelstorage.fluid;

import makmods.levelstorage.init.LSFluids;
import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.BlockFluidClassic;

public class BlockFluidIV extends BlockFluidClassic {

	public BlockFluidIV() {
		super(LSFluids.instance.fluidIV, Material.WATER);
		LSFluids.instance.fluidIV.setBlock(this);
		this.setUnlocalizedName("iv");
	}
/*
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		Icon still = iconRegister
		        .registerIcon(ClientProxy.FLUID_IV_STILL_TEXTURE);
		Icon flowing = iconRegister
		        .registerIcon(ClientProxy.FLUID_IV_FLOWING_TEXTURE);
		
		this.blockIcon = still;
		LSFluids.instance.fluidIV.setStillIcon(still);
		LSFluids.instance.fluidIV.setFlowingIcon(flowing);
	}

	@Override
	public boolean canDisplace(IBlockAccess world, BlockPos pos) {
		if (world.getBlockState(pos).getBlock().getMaterial())
			return false;
		return super.canDisplace(world, pos;
	}

	@Override
	public boolean displaceIfPossible(World world, int x, int y, int z) {
		if (world.getBlockMaterial(x, y, z).isLiquid())
			return false;
		return super.displaceIfPossible(world, x, y, z);
	}
*/
}

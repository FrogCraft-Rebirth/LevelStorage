package makmods.levelstorage.block;

import ic2.core.block.BlockTileEntity;
import ic2.core.block.ITeBlock;
import ic2.core.block.TileEntityBlock;
import ic2.core.item.block.ItemBlockTileEntity;
import ic2.core.ref.TeBlock;
import ic2.core.util.Util;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.Set;

public enum BlockLevelStorageMachines implements ITeBlock {

    IV_GENERATOR,
    LAVA_FABRICATOR,
    MASS_MELTER,
    PARTICLE_ACCELERATOR,
    ROCK_DISINTEGRATOR;

    @Override
    public ResourceLocation getIdentifier() {
        return null;
    }

    @Override
    public boolean hasItem() {
        return false;
    }

    @Override
    public Class<? extends TileEntityBlock> getTeClass() {
        return null;
    }

    @Override
    public boolean hasActive() {
        return true;
    }

    @Override
    public Set<EnumFacing> getSupportedFacings() {
        return Util.horizontalFacings;
    }

    @Override
    public float getHardness() {
        return 10.0F;
    }

    @Override
    public float getExplosionResistance() {
        return 15.0F;
    }

    @Override
    public TeBlock.HarvestTool getHarvestTool() {
        return TeBlock.HarvestTool.None;
    }

    @Override
    public TeBlock.DefaultDrop getDefaultDrop() {
        return TeBlock.DefaultDrop.AdvMachine;
    }

    @Override
    public EnumRarity getRarity() {
        return EnumRarity.RARE;
    }

    @Override
    public boolean allowWrenchRotating() {
        return true;
    }

    @Override
    public Material getMaterial() {
        return Material.IRON;
    }

    @Override
    public void addSubBlocks(List<ItemStack> list, BlockTileEntity blockTileEntity, ItemBlockTileEntity itemBlockTileEntity, CreativeTabs creativeTabs) {

    }

    @Override
    public void setPlaceHandler(TeBlock.ITePlaceHandler iTePlaceHandler) {

    }

    @Override
    public TeBlock.ITePlaceHandler getPlaceHandler() {
        return null;
    }

    @Override
    public TileEntityBlock getDummyTe() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public int getId() {
        return 0;
    }
}

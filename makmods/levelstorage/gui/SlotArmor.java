package makmods.levelstorage.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotArmor extends Slot
{
  private final InventoryPlayer inventory;
  private final EntityEquipmentSlot armorType;

  public SlotArmor(InventoryPlayer inventory, EntityEquipmentSlot armorType, int xDisplayPosition, int yDisplayPosition)
  {
    super(inventory, 36 + (3 - armorType.ordinal()), xDisplayPosition, yDisplayPosition);

    this.inventory = inventory;
    this.armorType = armorType;
  }

  public boolean isItemValid(ItemStack itemStack)
  {
    return itemStack.getItem().isValidArmor(itemStack, this.armorType, this.inventory.player);
  }
/*
  @SideOnly(Side.CLIENT)
  public Icon getBackgroundIconIndex()
  {
    return ItemArmor.func_94602_b(this.armorType);
  }*/
}

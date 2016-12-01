package makmods.levelstorage.logic;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

public class LSDamageSource extends DamageSource {

	public static LSDamageSource energyField = (new LSDamageSource(
	        "energyFieldKill"));
	public static LSDamageSource forcefieldArmor = (new LSDamageSource(
	        "forcefieldArmor"));
	public static DamageSource forcefieldArmorInstaKill = (DamageSource) ((new LSDamageSource(
	        "player"))).setDamageBypassesArmor();
	public static DamageSource teslaRay = (DamageSource) ((new LSDamageSource(
	        "teslaHelmetKill")));
	public static DamageSource disassembled = (DamageSource) ((new LSDamageSource(
	        "disassemble"))).setDamageBypassesArmor();
	

	private String killMessage;

	@Override
	public ITextComponent getDeathMessage(
	        EntityLivingBase par1EntityLivingBase) {
		if (par1EntityLivingBase instanceof EntityPlayer)
			return new TextComponentTranslation(this.killMessage,
			        ((EntityPlayer) par1EntityLivingBase).getName());
		return new TextComponentString(this.killMessage);
	}

	protected LSDamageSource(String par1Str) {
		super(par1Str);
		this.setKillMessage("death." + par1Str);
	}

	private LSDamageSource setKillMessage(String message) {
		this.killMessage = message;
		return this;
	}
}

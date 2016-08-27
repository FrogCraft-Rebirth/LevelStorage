package makmods.levelstorage.command;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.server.FMLServerHandler;

public class CommandChargeItems extends CommandBase {

	@Override
	public String getCommandName() {
		return "ls-charge-items";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "/" + getCommandName() + " [<player name>]";
	}

	@Override
	public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args,
			@Nullable BlockPos pos) {
		if (args.length == 1)
			return getListOfStringsMatchingLastWord(args, getPlayers());
		return null;
	}

	protected String[] getPlayers() {
		return FMLServerHandler.instance().getServer().getAllUsernames();
	}

	public int chargeItems(EntityPlayerMP player) {
		int charged = 0;
		InventoryPlayer inv = player.inventory;
		for (ItemStack stack : inv.armorInventory)
			if (stack != null)
				if (stack.getItem() instanceof IElectricItem)
					charged += ElectricItem.manager.charge(stack, Integer.MAX_VALUE, 10, true, false);
		for (ItemStack stack : inv.mainInventory)
			if (stack != null)
				if (stack.getItem() instanceof IElectricItem)
					charged += ElectricItem.manager.charge(stack, Integer.MAX_VALUE, 10, true, false);
		return charged;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] astring) throws CommandException {
		if (astring.length == 1) {
			String playerName = astring[0];
			Entity player = server
					.getEntityFromUuid(server.getPlayerProfileCache().getGameProfileForUsername(playerName).getId());
			if (player == null || !(player instanceof EntityPlayerMP))
				throw new PlayerNotFoundException();
			int charged = chargeItems((EntityPlayerMP) player);
			sender.addChatMessage(new TextComponentString(
					"\247dCharged all items inside " + playerName + "'s inventory. (used " + charged + " EU)"));
			return;
		}
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		int charged = chargeItems(player);
		sender.addChatMessage(
				new TextComponentString("\247dCharged all items inside your inventory. (used " + charged + " EU)"));
	}

}

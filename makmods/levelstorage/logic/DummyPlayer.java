package makmods.levelstorage.logic;

import makmods.levelstorage.lib.Reference;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public class DummyPlayer extends EntityPlayer {
	public DummyPlayer(World world) {
		super(world, "[" + Reference.MOD_ID + "]");
	}

	public boolean canCommandSenderUseCommand(int i, String s) {
		return false;
	}
/*
	public ChunkPos getPlayerCoordinates() {
		return null;
	}*/

	public void addChatComponentMessage(ITextComponent chatComponent) {
	}
}

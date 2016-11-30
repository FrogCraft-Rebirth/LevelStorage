package makmods.levelstorage.gui.client;

import makmods.levelstorage.gui.container.ContainerWirelessConductor;
import makmods.levelstorage.logic.util.RenderHelper;
import makmods.levelstorage.network.PacketDispatcher;
import makmods.levelstorage.network.packet.PacketPressButton;
import makmods.levelstorage.registry.ConductorType;
import makmods.levelstorage.tileentity.TileEntityWirelessConductor;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;

public class GuiWirelessConductor extends GuiContainer {

	public TileEntityWirelessConductor tileEntity;

	public GuiWirelessConductor(InventoryPlayer inventoryPlayer,
	        TileEntityWirelessConductor tileEntity) {
		super(new ContainerWirelessConductor(inventoryPlayer, tileEntity));
		this.tileEntity = tileEntity;
	}

	@Override
	public void initGui() {
		super.initGui();
		int xGuiPos = (this.width - this.xSize) / 2; // j
		int yGuiPos = (this.height - this.ySize) / 2;
		this.buttonList.add(new GuiButton(1, xGuiPos + 50, yGuiPos + 15, 75,
		        15, "Change mode"));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int param1, int param2) {
		// draw text and stuff here
		// the parameters for drawString are: string, x, y, color
		// this.fontRenderer.drawString("Wireless Conductor", 8, 6, 4210752);
		// draws "Inventory" or your regional equivalent
		int xGuiPos = (this.width - this.xSize) / 2; // j
		int yGuiPos = (this.height - this.ySize) / 2;
		this.fontRendererObj.drawString(I18n.format("container.inventory"), 
				8, this.ySize - 96 + 2, 4210752);
		String mode = "Mode: "
		        + (this.tileEntity.type == ConductorType.SOURCE ? "Energy transmitter"
		                : "Energy receiver");
		this.fontRendererObj.drawString(mode, 8, 55, 4210752);
	}

	@Override
	protected void actionPerformed(GuiButton par1GuiButton) {
		// NetworkHelper.initiateClientTileEntityEvent(tileEntity,
		// par1GuiButton.id);
		PacketPressButton packet = new PacketPressButton();
		packet.buttonId = par1GuiButton.id;
		packet.x = this.tileEntity.getPos().getX();
		packet.y = this.tileEntity.getPos().getY();
		packet.z = this.tileEntity.getPos().getZ();
		packet.dimId = this.tileEntity.getWorld().provider.getDimension();
		PacketDispatcher.sendPacketToServer(packet);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2,
	        int par3) {
		// draw your Gui here, only thing you need to change is the path
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		// this.mc.renderEngine.bindTexture(ClientProxy.GUI_SINGLE_SLOT);
		RenderHelper.bindSingleSlotGUI();
		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
	}
}

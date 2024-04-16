package net.programmer.igoodie.twitchspawn.network.packet;


import net.programmer.igoodie.twitchspawn.client.gui.StatusIndicatorOverlay;
import net.programmer.igoodie.twitchspawn.network.SimplePacketBase;

import net.minecraft.network.FriendlyByteBuf;

public class StatusChangedPacket extends SimplePacketBase {

	private final boolean status;

	public StatusChangedPacket(FriendlyByteBuf buf) {
		this.status = buf.readBoolean();
	}

	public StatusChangedPacket(boolean status) {
		this.status = status;
	}


	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeBoolean(this.status);
	}

	@Override
	public boolean handle(Context context) {
		context.enqueueWork(() -> StatusIndicatorOverlay.setRunning(this.status));
		return true;
	}
}

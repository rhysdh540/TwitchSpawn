package net.programmer.igoodie.twitchspawn.network.packet;


import net.programmer.igoodie.twitchspawn.client.gui.GlobalChatCooldownOverlay;
import net.programmer.igoodie.twitchspawn.network.SimplePacketBase;

import net.minecraft.network.FriendlyByteBuf;

public class GlobalChatCooldownPacket extends SimplePacketBase {

	/**
	 * Timestamp of the cooldown.
	 */
	private final long timestamp;

	public GlobalChatCooldownPacket(long timestamp) {
		this.timestamp = timestamp;
	}

	public GlobalChatCooldownPacket(FriendlyByteBuf buffer) {
		this.timestamp = buffer.readLong();
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeLong(this.timestamp);
	}

	@Override
	public boolean handle(Context context) {
		context.enqueueWork(() -> GlobalChatCooldownOverlay.setCooldownTimestamp(this.timestamp));
		return true;
	}
}

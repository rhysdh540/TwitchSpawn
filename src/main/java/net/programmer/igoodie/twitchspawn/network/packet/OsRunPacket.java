package net.programmer.igoodie.twitchspawn.network.packet;

import net.programmer.igoodie.twitchspawn.network.SimplePacketBase;
import net.programmer.igoodie.twitchspawn.tslanguage.action.OsRunAction;

import net.minecraft.network.FriendlyByteBuf;

public class OsRunPacket extends SimplePacketBase {
	private final OsRunAction.Shell shell;
	private final String script;

	public OsRunPacket(FriendlyByteBuf buf) {
		this.shell = OsRunAction.Shell.values()[buf.readInt()];
		this.script = buf.readUtf();
	}

	public OsRunPacket(OsRunAction.Shell shell, String script) {
		this.shell = shell;
		this.script = script;
	}


	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeInt(this.shell.ordinal());
		buffer.writeUtf(this.script);
	}

	@Override
	public boolean handle(Context context) {
		context.enqueueWork(() -> OsRunAction.handleLocalScript(shell, script));
		return true;
	}
}

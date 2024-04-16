package net.programmer.igoodie.twitchspawn.network;


import me.pepperbell.simplenetworking.SimpleChannel;
import net.fabricmc.api.EnvType;
import net.programmer.igoodie.twitchspawn.TwitchSpawn;
import net.programmer.igoodie.twitchspawn.network.packet.GlobalChatCooldownPacket;
import net.programmer.igoodie.twitchspawn.network.packet.OsRunPacket;
import net.programmer.igoodie.twitchspawn.network.packet.StatusChangedPacket;
import net.programmer.igoodie.twitchspawn.util.EnvironmentExecutor;

import net.minecraft.network.FriendlyByteBuf;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import static net.programmer.igoodie.twitchspawn.network.SimplePacketBase.NetworkDirection.*;

public class NetworkManager {
	public static final SimpleChannel CHANNEL = new SimpleChannel(TwitchSpawn.asResource("network"));

	public static void initialize() {
		register(GlobalChatCooldownPacket.class, PLAY_TO_CLIENT);
		register(StatusChangedPacket.class, PLAY_TO_CLIENT);
		register(OsRunPacket.class, PLAY_TO_CLIENT);

		EnvironmentExecutor.runInEnv(EnvType.CLIENT, () -> CHANNEL::initClientListener);
	}

	@SuppressWarnings("unchecked")
	private static <T extends SimplePacketBase> void register(Class<T> packetClass, SimplePacketBase.NetworkDirection direction) {
		MethodHandle constructor;
		MethodHandles.Lookup lookup = MethodHandles.lookup();

		try {
			constructor = lookup.findConstructor(packetClass, MethodType.methodType(void.class, FriendlyByteBuf.class));
		} catch (NoSuchMethodException | IllegalAccessException e) {
			throw new RuntimeException("Failed to find constructor for packet class " + packetClass.getName(), e);
		}

		switch(direction) {
			case PLAY_TO_SERVER -> CHANNEL.registerC2SPacket(packetClass, id(), buf -> {
				try {
					return (T) constructor.invoke(buf);
				} catch (Throwable throwable) {
					throw new RuntimeException("Failed to create packet instance", throwable);
				}
			});
			case PLAY_TO_CLIENT -> CHANNEL.registerS2CPacket(packetClass, id(), buf -> {
				try {
					return (T) constructor.invoke(buf);
				} catch (Throwable throwable) {
					throw new RuntimeException("Failed to create packet instance", throwable);
				}
			});
		}
	}

	private static int currentId = 0;
	private static int id() {
		return currentId++;
	}
}

package net.programmer.igoodie.twitchspawn.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import net.minecraft.server.level.ServerPlayer;

import java.util.function.Consumer;

// stuff from arch api
public class TwitchSpawnExtraEvents {
	public static Event<Consumer<ServerPlayer>> PLAYER_JOIN = EventFactory.createArrayBacked(Consumer.class, listeners -> player -> {
		for (Consumer<ServerPlayer> listener : listeners) {
			listener.accept(player);
		}
	});

	public static Event<Consumer<ServerPlayer>> PLAYER_QUIT = EventFactory.createArrayBacked(Consumer.class, listeners -> player -> {
		for (Consumer<ServerPlayer> listener : listeners) {
			listener.accept(player);
		}
	});
}

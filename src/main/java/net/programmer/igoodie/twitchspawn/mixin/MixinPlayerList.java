package net.programmer.igoodie.twitchspawn.mixin;

import net.programmer.igoodie.twitchspawn.events.TwitchSpawnExtraEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;

@Mixin(PlayerList.class)
public class MixinPlayerList {
	@Inject(method = "placeNewPlayer", at = @At("RETURN"))
	private void placeNewPlayer(Connection connection, ServerPlayer serverPlayer, CommonListenerCookie commonListenerCookie, CallbackInfo ci) {
		TwitchSpawnExtraEvents.PLAYER_JOIN.invoker().accept(serverPlayer);
	}

	@Inject(method = "remove", at = @At("HEAD"))
	private void remove(ServerPlayer serverPlayer, CallbackInfo ci) {
		TwitchSpawnExtraEvents.PLAYER_QUIT.invoker().accept(serverPlayer);
	}
}

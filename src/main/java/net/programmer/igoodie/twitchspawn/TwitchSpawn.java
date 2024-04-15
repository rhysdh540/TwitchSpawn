package net.programmer.igoodie.twitchspawn;


import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.programmer.igoodie.twitchspawn.client.gui.GlobalChatCooldownOverlay;
import net.programmer.igoodie.twitchspawn.client.gui.StatusIndicatorOverlay;
import net.programmer.igoodie.twitchspawn.client.gui.screens.LoadingErrorScreen;
import net.programmer.igoodie.twitchspawn.command.TwitchSpawnCommand;
import net.programmer.igoodie.twitchspawn.configuration.ConfigManager;
import net.programmer.igoodie.twitchspawn.configuration.PreferencesConfig;
import net.programmer.igoodie.twitchspawn.events.TwitchSpawnClientGuiEvent;
import net.programmer.igoodie.twitchspawn.events.TwitchSpawnExtraEvents;
import net.programmer.igoodie.twitchspawn.network.NetworkManager;
import net.programmer.igoodie.twitchspawn.network.packet.StatusChangedPacket;
import net.programmer.igoodie.twitchspawn.registries.TwitchSpawnArgumentTypes;
import net.programmer.igoodie.twitchspawn.registries.TwitchSpawnSoundEvent;
import net.programmer.igoodie.twitchspawn.tracer.TraceManager;
import net.programmer.igoodie.twitchspawn.udl.NotepadUDLUpdater;
import net.programmer.igoodie.twitchspawn.util.EnvironmentExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;


public class TwitchSpawn implements ModInitializer {
	/**
	 * The plugin mod-id
	 */
	public static final String MOD_ID = "twitchspawn";

	/**
	 * Minecraft server instance.
	 */
	public static MinecraftServer SERVER;

	/**
	 * Trace manager.
	 */
	public static TraceManager TRACE_MANAGER;

	/**
	 * Logger.
	 */
	public static final Logger LOGGER = LogManager.getLogger();


	/**
	 * The main init class.
	 */
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registry, env) ->
				TwitchSpawnCommand.register(dispatcher)
		);
	}


	/**
	 * List of common tasks initialized by both environments.
	 */
	private static void initializeCommon() {
		ServerLifecycleEvents.SERVER_STARTING.register(server -> {
			SERVER = server;
			TRACE_MANAGER = new TraceManager();
			if(ConfigManager.PREFERENCES.autoStart == PreferencesConfig.AutoStartEnum.ENABLED) {
				LOGGER.info("Auto-start is enabled. Attempting to start tracers.");
				TRACE_MANAGER.start();
			}
		});

		// Trigger server stop that would disable tracers.
		ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
			SERVER = null;

			if(TRACE_MANAGER.isRunning()) {
				TRACE_MANAGER.stop(null, "Server stopping");
			}

			ConfigManager.RULESET_COLLECTION.clearQueue();
		});

		// Do stuff on player joining the server.
		TwitchSpawnExtraEvents.PLAYER_JOIN.register(player -> {
			String translationKey = TRACE_MANAGER.isRunning() ?
					"commands.twitchspawn.status.on" : "commands.twitchspawn.status.off";

			player.sendSystemMessage(Component.translatable(translationKey));

			if(TRACE_MANAGER.isRunning()) {
				TRACE_MANAGER.connectStreamer(player.getName().getString());
			}

			NetworkManager.CHANNEL.sendToPlayer(player, new StatusChangedPacket(TRACE_MANAGER.isRunning()));
		});

		// Do stuff on player leaving the server.
		TwitchSpawnExtraEvents.PLAYER_QUIT.register(player -> {
			if(TRACE_MANAGER.isRunning()) {
				TRACE_MANAGER.disconnectStreamer(player.getName().getString());
			}
		});

		try {
			TwitchSpawnSoundEvent.REGISTRY.register();
			TwitchSpawnArgumentTypes.registerArgumentTypes();

			NetworkManager.initialize();
			ConfigManager.loadConfigs();
		} catch (TwitchSpawnLoadingErrors exception) {
			EnvironmentExecutor.runInEnv(EnvType.CLIENT, () -> () -> Client.notifyCrash(exception));
			EnvironmentExecutor.runInEnv(EnvType.SERVER, () -> () -> Server.notifyCrash(exception));
		}
	}


	/**
	 * Client related tasks.
	 */
	@Environment(EnvType.CLIENT)
	public static class Client implements ClientModInitializer {
		/**
		 * The client initialization.
		 */
		@Environment(EnvType.CLIENT)
		public void onInitializeClient() {
			TwitchSpawn.initializeCommon();
			NotepadUDLUpdater.attemptUpdate();

			ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
				StatusIndicatorOverlay.register();
				GlobalChatCooldownOverlay.register();
			});

			ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
				StatusIndicatorOverlay.unregister();
				GlobalChatCooldownOverlay.unregister();
			});
		}


		/**
		 * Notify the client about the config crash.
		 *
		 * @param errors The errors that caused the config crash.
		 */
		@Environment(EnvType.CLIENT)
		public static void notifyCrash(TwitchSpawnLoadingErrors errors) {
			TwitchSpawnClientGuiEvent.FINISH_LOADING_OVERLAY.register((client, screen) ->
			{
				if(screen instanceof TitleScreen) {
					LoadingErrorScreen errorScreen = new LoadingErrorScreen(errors.getExceptions());
					errorScreen.init(client,
							client.getWindow().getGuiScaledWidth(),
							client.getWindow().getGuiScaledHeight());
					client.setScreen(errorScreen);
				}
			});
		}
	}


	/**
	 * Server related tasks
	 */
	@Environment(EnvType.SERVER)
	public static class Server implements DedicatedServerModInitializer {
		/**
		 * The server initialization.
		 */
		@Environment(EnvType.SERVER)
		public void onInitializeServer() {
			TwitchSpawn.initializeCommon();
		}


		/**
		 * Notify the server about the config crash.
		 *
		 * @param errors The errors that caused the config crash.
		 */
		@Environment(EnvType.SERVER)
		public static void notifyCrash(TwitchSpawnLoadingErrors errors) {
			LOGGER.warn("TwitchSpawn config contains errors:");
			LOGGER.warn("\n{}", errors.toString());
		}
	}
}

package net.programmer.igoodie.twitchspawn.client.gui;


import com.mojang.blaze3d.systems.RenderSystem;
import net.programmer.igoodie.twitchspawn.TwitchSpawn;
import net.programmer.igoodie.twitchspawn.configuration.ConfigManager;
import net.programmer.igoodie.twitchspawn.configuration.PreferencesConfig;
import net.programmer.igoodie.twitchspawn.events.TwitchSpawnClientGuiEvent;
import net.programmer.igoodie.twitchspawn.registries.TwitchSpawnSoundEvent;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;


public class StatusIndicatorOverlay {

	private static final ResourceLocation indicatorIcons =
			new ResourceLocation(TwitchSpawn.ID, "textures/indicators.png");

	private static boolean running = false;

	private static boolean enabled = true;

	private static boolean drew = false;

	/**
	 * Render indicator
	 */
	private static final TwitchSpawnClientGuiEvent.OverlayRenderPre PRE_RENDER = (graphics, resourceLocation) -> {
		if(enabled) drew = false;
	};

	/**
	 * Render the gui
	 */
	private static final TwitchSpawnClientGuiEvent.RenderDebugHud POST_RENDER =
			StatusIndicatorOverlay::onRenderGuiPost;


	/**
	 * Register rendering events.
	 */
	public static void register() {
		if(enabled) {
			TwitchSpawnClientGuiEvent.OVERLAY_RENDER_PRE.register(PRE_RENDER);
			TwitchSpawnClientGuiEvent.DEBUG_TEXT.register(POST_RENDER);
		}
		enabled = true;
	}


	/**
	 * Unregister rendering events.
	 */
	public static void unregister() {
//		TwitchSpawnClientGuiEvent.OVERLAY_RENDER_PRE.unregister(PRE_RENDER);
//		TwitchSpawnClientGuiEvent.DEBUG_TEXT.unregister(POST_RENDER);
		enabled = false;
	}


	public static void setRunning(boolean running) {
		StatusIndicatorOverlay.running = running;

		ResourceLocation soundEvent = running ?
				TwitchSpawnSoundEvent.POP_IN.getLocation() : TwitchSpawnSoundEvent.POP_OUT.getLocation();

		Minecraft minecraft = Minecraft.getInstance();
		LocalPlayer self = minecraft.player;

		if(self != null) { // Here to hopefully fix an obscure Null Pointer (From UNKNOWN PENGUIN's log)
			self.playSound(SoundEvent.createVariableRangeEvent(soundEvent), 1f, 1f);
		}
	}


	private static void onRenderGuiPost(GuiGraphics gui) {
		if(!enabled) return;
		if(ConfigManager.PREFERENCES.indicatorDisplay == PreferencesConfig.IndicatorDisplay.DISABLED)
			return; // The display is disabled, stop here

		// Already drew, stop here
		if(drew) return;

		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, indicatorIcons);

		gui.pose().pushPose();

		int x = 5, y = 5;
		int ux = -1, uy = -1;
		int w = -1, h = -1;

		if(ConfigManager.PREFERENCES.indicatorDisplay == PreferencesConfig.IndicatorDisplay.ENABLED) {
			ux = 0;
			uy = running ? 22 : 0;
			w = 65;
			h = 22;
		} else if(ConfigManager.PREFERENCES.indicatorDisplay == PreferencesConfig.IndicatorDisplay.CIRCLE_ONLY) {
			ux = 0;
			uy = running ? 56 : 44;
			w = 12;
			h = 12;
		}

		gui.pose().scale(1f, 1f, 1f);
		gui.blit(indicatorIcons, x, y, ux, uy, w, h);

		gui.pose().popPose();

		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.disableBlend();
//        RenderSystem.setShaderTexture(0, Gui.GUI_ICONS_LOCATION);

		drew = true;
	}

}

package net.programmer.igoodie.twitchspawn.events;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;


@Environment(EnvType.CLIENT)
public interface TwitchSpawnClientGuiEvent {
	/**
	 * This event is fired when the debug text is rendered.
	 */
	Event<RenderDebugHud> DEBUG_TEXT = EventFactory.createArrayBacked(RenderDebugHud.class,
			(listeners) -> (graphics) -> {
				for (RenderDebugHud listener : listeners) {
					listener.renderHud(graphics);
				}
			});

	/**
	 * This event is fired before overlay is rendered.
	 */
	Event<OverlayRenderPre> OVERLAY_RENDER_PRE = EventFactory.createArrayBacked(OverlayRenderPre.class,
			(listeners) -> (graphics, overlay) -> {
				for (OverlayRenderPre listener : listeners) {
					listener.renderHud(graphics, overlay);
				}
			});

	/**
	 * This event is fired after overlay is rendered.
	 */
	Event<OverlayRenderPost> OVERLAY_RENDER_POST = EventFactory.createArrayBacked(OverlayRenderPost.class,
			(listeners) -> (graphics, overlay) -> {
				for (OverlayRenderPost listener : listeners) {
					listener.renderHud(graphics, overlay);
				}
			});

	/**
	 * This event is fired after loading screen is removed.
	 */
	Event<LoadingScreenFinish> FINISH_LOADING_OVERLAY = EventFactory.createArrayBacked(LoadingScreenFinish.class,
			(listeners) -> (client, screen) -> {
				for (LoadingScreenFinish listener : listeners) {
					listener.removeOverlay(client, screen);
				}
			});

	@Environment(EnvType.CLIENT)
	interface RenderDebugHud {
		void renderHud(GuiGraphics graphics);
	}

	@Environment(EnvType.CLIENT)
	interface OverlayRenderPre {
		void renderHud(GuiGraphics graphics, ResourceLocation overlay);
	}

	@Environment(EnvType.CLIENT)
	interface OverlayRenderPost {
		void renderHud(GuiGraphics graphics, ResourceLocation overlay);
	}

	@Environment(EnvType.CLIENT)
	interface LoadingScreenFinish {
		void removeOverlay(Minecraft client, Screen screen);
	}
}

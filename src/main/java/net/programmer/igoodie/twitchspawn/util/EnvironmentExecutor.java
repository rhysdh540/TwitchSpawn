package net.programmer.igoodie.twitchspawn.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

import java.util.function.Supplier;

public class EnvironmentExecutor {
	private static final EnvType CURRENT_ENV = FabricLoader.getInstance().getEnvironmentType();

	public static void runInEnv(EnvType type, Supplier<Runnable> runnableSupplier) {
		if(CURRENT_ENV == type) {
			runnableSupplier.get().run();
		}
	}
}

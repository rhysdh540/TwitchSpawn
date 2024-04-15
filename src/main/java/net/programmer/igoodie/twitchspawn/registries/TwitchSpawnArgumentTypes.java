package net.programmer.igoodie.twitchspawn.registries;


import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.programmer.igoodie.twitchspawn.TwitchSpawn;
import net.programmer.igoodie.twitchspawn.command.RulesetNameArgumentType;
import net.programmer.igoodie.twitchspawn.command.StreamerArgumentType;
import net.programmer.igoodie.twitchspawn.command.TSLWordsArgumentType;

import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.resources.ResourceLocation;

public class TwitchSpawnArgumentTypes {
	public static void registerArgumentTypes() {
		ArgumentTypeRegistry.registerArgumentType(
				new ResourceLocation(TwitchSpawn.MOD_ID, "ruleset"),
				RulesetNameArgumentType.class,
				SingletonArgumentInfo.contextFree(RulesetNameArgumentType::rulesetName));

		//TODO investigate why this is flagged as unreachable code
		ArgumentTypeRegistry.registerArgumentType(
				new ResourceLocation(TwitchSpawn.MOD_ID, "streamer"),
				StreamerArgumentType.class,
				SingletonArgumentInfo.contextFree(StreamerArgumentType::streamerNick));

		ArgumentTypeRegistry.registerArgumentType(
				new ResourceLocation(TwitchSpawn.MOD_ID, "tslwords"),
				TSLWordsArgumentType.class,
				SingletonArgumentInfo.contextFree(TSLWordsArgumentType::tslWords));
	}
}

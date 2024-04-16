package net.programmer.igoodie.twitchspawn.registries;

import net.programmer.igoodie.twitchspawn.TwitchSpawn;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;

public class TwitchSpawnSoundEvent {
	public static final SoundEvent POP_IN = SoundEvent.createVariableRangeEvent(TwitchSpawn.asResource("pop_in"));
	public static final SoundEvent POP_OUT = SoundEvent.createVariableRangeEvent(TwitchSpawn.asResource("pop_out"));

	public static void register() {
		Registry.register(BuiltInRegistries.SOUND_EVENT, TwitchSpawn.asResource("pop_in"), POP_IN);
		Registry.register(BuiltInRegistries.SOUND_EVENT, TwitchSpawn.asResource("pop_out"), POP_OUT);
	}
}

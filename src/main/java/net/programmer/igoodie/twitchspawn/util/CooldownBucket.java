package net.programmer.igoodie.twitchspawn.util;

import it.unimi.dsi.fastutil.objects.Object2LongLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap;

import java.time.Instant;

public class CooldownBucket {

	public int globalCooldownMillis;
	public int cooldownMillis;

	public long globalCooldownUntil;
	public Object2LongMap<String> individualCooldownUntil;

	public CooldownBucket(int globalCooldown, int individualCooldown) {
		this.cooldownMillis = individualCooldown;
		this.globalCooldownMillis = globalCooldown;

		this.globalCooldownUntil = now();
		this.individualCooldownUntil = new Object2LongLinkedOpenHashMap<>();
	}

	public float getGlobalCooldown() {
		long now = now();
		return Math.max(0, (globalCooldownUntil - now) / 1000f);
	}

	public long getGlobalCooldownTimestamp() {
		return globalCooldownUntil;
	}

	public boolean hasGlobalCooldown() {
		if(globalCooldownMillis == 0) return false;
		long now = now();
		return now <= globalCooldownUntil;
	}

	public boolean hasCooldown(String nickname) {
		return now() <= individualCooldownUntil.getLong(nickname);
	}

	public long getCooldown(String nickname) {
		return individualCooldownUntil.getLong(nickname) - now();
	}

	public boolean canConsume(String nickname) {
		return !hasGlobalCooldown() && !hasCooldown(nickname);
	}

	public void consume(String nickname) {
		long now = now();
		globalCooldownUntil = now + globalCooldownMillis;
		individualCooldownUntil.put(nickname, now + cooldownMillis);
	}

	public static long now() {
		return Instant.now().getEpochSecond() * 1000;
	}
}

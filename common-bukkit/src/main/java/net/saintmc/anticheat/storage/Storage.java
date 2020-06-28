package net.saintmc.anticheat.storage;

import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.saintmc.anticheat.account.Member;

@Getter
@RequiredArgsConstructor
public abstract class Storage {

	private final Member member;
	private final Player player;
	private int tickTime = MinecraftServer.currentTick;

}

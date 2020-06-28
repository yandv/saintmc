package net.saintmc.anticheat.storage;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;
import net.saintmc.anticheat.account.Member;

@Setter
@Getter
public class MoveStorage extends Storage {
	
	private boolean speed;
	private boolean ground;
	private boolean spriting;
	
	private boolean swimming;
	private boolean ladder;

	private double moveSpeed;
	private double jumpHeight;
	private double fallDistance;

	public double verticalDistance;
	public double horizontalDistance;
	
	private Location to;
	private Location from;
	
	private int foodLevel;
	
	public MoveStorage(Member member, Player player) {
		super(member, player);
	}
	
}

package net.saintmc.anticheat.storage;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import lombok.Getter;
import lombok.Setter;
import net.saintmc.anticheat.account.Member;

@Setter
@Getter
public class DamageStorage extends Storage {

	private boolean damagerSprintHit;
	private boolean entitySprintHit;
	
	private boolean playerDamage;
	
	private double y;
	private double damage;
	
	private DamageCause cause;
	
	public DamageStorage(Member member, Player player) {
		super(member, player);
	}

}

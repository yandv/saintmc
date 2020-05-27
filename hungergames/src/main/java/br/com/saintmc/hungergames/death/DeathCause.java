package br.com.saintmc.hungergames.death;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DeathCause {
	
	FALL(""), NONE("§cVocê morreu!");
	
	private String deathCause;

}

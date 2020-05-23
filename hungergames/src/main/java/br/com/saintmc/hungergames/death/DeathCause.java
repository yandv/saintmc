package br.com.saintmc.hungergames.death;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DeathCause {
	
	FALL("");
	
	private String deathCause;

}

package br.com.saintmc.hungergames.event.ability;

import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tk.yallandev.saintmc.bukkit.event.NormalEvent;

@Getter
@AllArgsConstructor
public class GladiatorScapeEvent extends NormalEvent {
	
	private Player gladiator;

}

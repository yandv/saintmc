package br.com.saintmc.hungergames.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tk.yallandev.saintmc.bukkit.event.NormalEvent;

@Getter
@AllArgsConstructor
public class VarChangeEvent extends NormalEvent {

	private String varName;
	private String oldValue;
	private String newValue;

}

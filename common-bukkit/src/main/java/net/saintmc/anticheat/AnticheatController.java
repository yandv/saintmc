package net.saintmc.anticheat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.saintmc.anticheat.alert.AlertController;
import net.saintmc.anticheat.check.CheckController;

@AllArgsConstructor
@Getter
public class AnticheatController {

	private CheckController checkController;
	private AlertController alertController;

}

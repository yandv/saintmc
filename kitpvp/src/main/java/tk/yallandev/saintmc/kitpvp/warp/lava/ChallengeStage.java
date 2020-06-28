package tk.yallandev.saintmc.kitpvp.warp.lava;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ChallengeStage {

	EASY("Fácil", "lava-facil-start", "lava-facil-end"), MEDIUM("Médio", "lava-medio-start", "lava-medio-end"),
	HARD("Difícil", "lava-hard-start", "lava-hard-end"), HARDCORE("Extremo", "lava-extremo-start", "lava-extremo-end"),

	TRAINAING("Treino", "lava-training", "lava-training-start");

	private String name;

	private String startConfig;
	private String endConfig;
}
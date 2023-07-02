package br.com.saintmc.hungergames.game;

/*
 * 
 */

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;

/**
 * The Enum Color.
 */
@Getter
@RequiredArgsConstructor
public enum Color {

	RED("Vermelho Claro", ChatColor.RED, 255, 0, 0),
	GREEN("Verde", ChatColor.GREEN, 0, 255, 0),
	BLUE("Azul", ChatColor.BLUE, 0, 0, 255),
	YELLOW("Amarelo", ChatColor.GOLD, 255, 255, 0),
	PURPLE("Roxo", ChatColor.DARK_PURPLE, 128, 0, 128),
	ORANGE("Laranja", ChatColor.RED, 255, 165, 0),
	PINK("Rosa", ChatColor.LIGHT_PURPLE, 255, 192, 203),
	GRAY("Cinza", ChatColor.GRAY, 128, 128, 128),
	BROWN("Marrom", ChatColor.RED, 165, 42, 42),
	BLACK("Preto", ChatColor.BLACK, 0, 0, 0),
	WHITE("Branco", ChatColor.WHITE, 255, 255, 255),
	DARK_RED("Vermelho Escuro", ChatColor.DARK_RED, 139, 0, 0),
	DARK_GREEN("Verde", ChatColor.DARK_GREEN, 0, 100, 0),
	DARK_BLUE("Azul", ChatColor.DARK_AQUA, 0, 0, 139),
	LIGHT_YELLOW("Amarelo", ChatColor.YELLOW, 255, 255, 224),
	LIGHT_PURPLE("Roxo", ChatColor.LIGHT_PURPLE, 218, 112, 214),
	GOLD("Dourado", ChatColor.GOLD, 255, 215, 0),
	LIGHT_PINK("Rosa", ChatColor.LIGHT_PURPLE, 255, 182, 193),
	DARK_GRAY("Cinza", ChatColor.DARK_GRAY, 169, 169, 169),
	LIGHT_BROWN("Marrom", ChatColor.DARK_GRAY, 222, 184, 135),
	SILVER("Prateado", ChatColor.GRAY, 192, 192, 192),
	DARK_ORANGE("Laranja", ChatColor.RED, 255, 140, 0),
	SALMON("Salmão", ChatColor.RED, 250, 128, 114),
	BEIGE("Bege", ChatColor.GOLD, 245, 245, 220),
	TAN("Bronzeado", ChatColor.GRAY, 210, 180, 140),
	DARK_PURPLE("Roxo", ChatColor.DARK_PURPLE, 128, 0, 128),
	DARK_GREEN_YELLOW("Verde Amarelado", ChatColor.GREEN, 100, 100, 0),
	CYAN("Ciano", ChatColor.AQUA, 0, 255, 255),
	DARK_OLIVE_GREEN("Verde Oliva", ChatColor.GREEN, 85, 107, 47),
	FIREBRICK("Tijolo", ChatColor.GOLD, 178, 34, 34),
	LIGHT_GRAY("Cinza", ChatColor.GRAY, 211, 211, 211),
	OLIVE("Oliva", ChatColor.GREEN, 128, 128, 0),
	TEAL("Azul", ChatColor.BLUE, 0, 128, 128),
	ROSY_BROWN("Marrom Rosado", ChatColor.LIGHT_PURPLE, 188, 143, 143),
	SKY_BLUE("Azul Celeste", ChatColor.AQUA, 135, 206, 235),
	PLUM("Ameixa", ChatColor.LIGHT_PURPLE, 221, 160, 221),
	INDIGO("Índigo", ChatColor.DARK_PURPLE, 75, 0, 130),
	LAVENDER("Lavanda", ChatColor.WHITE, 230, 230, 250),
	MINT("Menta", ChatColor.GREEN, 189, 252, 201),
	NAVY("Marinho", ChatColor.DARK_BLUE, 0, 0, 128);

	private final String name;
	private final ChatColor chatColor;
	private final int red;
	private final int green;
	private final int blue;

	public java.awt.Color toAwtColor() {
		return new java.awt.Color(red, green, blue);
	}

	public org.bukkit.Color toBukkitColor() {
		return org.bukkit.Color.fromRGB(red, green, blue);
	}

	public String getSimpleName() {
		return getName().contains(" ") ? getName().split(" ")[0] : getName();
	}

	public static Map<String, Color> COLOR_MAP = new HashMap<>();

	static {
		for (Color color : Color.values()) {
			COLOR_MAP.put(color.getName().toLowerCase(), color);
		}
	}

	public static Color getColorById(int id) {
		return Color.values()[id];
	}

	public static Color getColorByName(String name) {
		return COLOR_MAP.get(name.toLowerCase());
	}
}
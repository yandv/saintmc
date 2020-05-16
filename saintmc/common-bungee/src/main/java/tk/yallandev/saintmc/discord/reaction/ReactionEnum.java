package tk.yallandev.saintmc.discord.reaction;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 
 * @author yAllanDev_ - theNameOfDreams in camelCase
 * @since 1.1
 *
 */

@AllArgsConstructor
@Getter
public enum ReactionEnum {
	
	HEAVY_MULTIPLICATION_X("\u2716", "Heavy Multiplication X"),
	X("\u274C", "X"),
	WARNING("\u26A0", "Warning"),
	WHITE_CHECK_MARK("\u2705", "White Check Mark"),
	HEAVY_CHECK_MARK("\u2705", "Heavy Check Mark"),
	STAR("\u2B50", "Star"),
	ONE("1\u20E3", "One"),
	TWO("2\u20E3", "Two"),
	THREE("3\u20E3", "Three"),
	FOUR("4\u20E3", "Four"),
	FIVE("5\u20E3", "Five"),
	SIX("6\u20E3", "Six"),
	SEVEN("7\u20E3", "Seven"),
	EIGHT("8\u20E3", "Eight"),
	NINE("9\u20E3", "Nine"),
	TEN("\ud83d\udd1f", "Ten"),
	BACK("\ud83d\udd19", "Back"),
	ARROW_BACKWARD("\u25C0", "Arrow Backward"),
	ARROW_FORWARD("\u25B6", "Arrow Forward"),
	COOKIE("\uD83C\uDF6A", "Cookie"),
	EYE("\uD83D\uDC41", "Eye"),
	FILE_FOLDER("\uD83D\uDCC1", "File Folder"),
	SCROLL("\uD83D\uDCDC", "Scroll"),
	PENCIL("\uD83D\uDCDD", "Pencil"),
	GEM("\uD83D\uDC8E", "Gem"),
	HANDSHAKE("\uD83E\uDD1D", "Handshake"),
	SATELLITE("\uD83D\uDCE1", "Satellite"),
	HAMMER("\uD83D\uDD28", "Hammer"),
	LINK("\uD83D\uDD17", "Link"),
	WRENCH("\uD83D\uDD27", "Wrench"),
	RIGHT_POINTING_MAGNIFYING_GLASS("\uD83D\uDD0E", "Right-pointing magnifying glass"),
	TAG_SPACE("\uDB40\uDC20", "Tag Space"),
	BLACK_NIB("\u2712", "Black Nib"),
	SHARK("\uD83E\uDD88", "Shark"),
	MORTAR_BOARD("\uD83C\uDF93", "Mortar Board"),
	PROJECTOR("\uD83D\uDCFD", "Projector"),
	LOCK("\uD83D\uDD12", "Lock"),
	GHOST("\uD83D\uDC7B", "Ghost"),
	PAINTBRUSH("\uD83D\uDD8C", "PaintBrush"),
	
//	COMMAND("<:command:541361958469763097>", "541361958469763097", true),
//	MUSIC("<:music:541362035288440870>", "541362035288440870", true),
	CONFIG("<a:config:535106000936763402>", "535106000936763402", true),
	CONCLUIDO("<:concluido:612463269776588813>", "612463269776588813", true),;
	
	private String emote;
	private String emoteId;
	private boolean customEmote;
	
	private ReactionEnum(String emote, String emoteId) {
	    this.emote = emote;
	    this.emoteId = emoteId;
	    this.customEmote = false;
    }
	
    private static final Map<String, ReactionEnum> REACTION_MAP;

    static {
        Map<String, ReactionEnum> map = new ConcurrentHashMap<>();
        
        for (ReactionEnum tag : ReactionEnum.values()) {
            map.put(tag.name().toLowerCase(), tag);
            map.put(tag.getEmoteId(), tag);
        }
        
        REACTION_MAP = Collections.unmodifiableMap(map);
    }

    /**
     *
     * @param name String
     * @return Tag
     */
    public static ReactionEnum getReaction(String name) {
        Objects.requireNonNull(name, "Parameter 'name' is null.");
        return REACTION_MAP.get(name.toLowerCase());
    }
}
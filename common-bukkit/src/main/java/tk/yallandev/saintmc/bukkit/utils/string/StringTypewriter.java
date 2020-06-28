package tk.yallandev.saintmc.bukkit.utils.string;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;

public class StringTypewriter {

	private DisplayType displayType;
	
	private String displayText;
	private String toText;

	private char[] toTextChar;
	private String[] displays;

	private int toTextLenght;
	private int color = 1;

	private int charIndex = 0;
	private int displayIndex = 0;
	private boolean indexDecrement;
	
	private Map<Integer, ChatColor> displayColors;
	
	public StringTypewriter(String... displays) {
		displayType = DisplayType.INCREMENT;
		displayText = "";
		this.displays = displays;
		toText = displays[0];

		toTextChar = toText.toCharArray();
		toTextLenght = toText.length();
		
		displayColors = new HashMap<>();
		
		displayColors.put(1, ChatColor.getByChar('6'));
		displayColors.put(2, ChatColor.getByChar('e'));
		displayColors.put(3, ChatColor.getByChar('f'));
		displayColors.put(4, ChatColor.getByChar('d'));
		displayColors.put(5, ChatColor.getByChar('4'));
		displayColors.put(6, ChatColor.getByChar('c'));
		displayColors.put(7, ChatColor.getByChar('a'));
	}

	public String displayEffect() {
		if (displayType == DisplayType.INCREMENT) {
			if (charIndex + 1 > toTextLenght) {
				displayType = DisplayType.DECREMENT;
				return displayColors.get(color) + "§l" + displayText;
			}
			displayText += toTextChar[charIndex];
			charIndex++;
		} else if (displayType == DisplayType.DECREMENT) {
			if (displayText.length() == 0) {
				charIndex = 0;
				
				if (displayIndex == 0) {
					indexDecrement = false;
				} else if (displayIndex == displays.length-1)  {
					indexDecrement = true;
				}
				
				if (indexDecrement)
					displayIndex--;
				else
					displayIndex++;
				
				toText = displays[displayIndex];
				
				toTextChar = toText.toCharArray();
				displayType = DisplayType.INCREMENT;
				toTextLenght = toText.length();

				if (color + 1 > displayColors.size())
					color = 1;
				else
					color++;

				return displayColors.get(color) + "§l" + displayText;
			}

			displayText = displayText.substring(0, displayText.length() - 1);
		}

		return displayColors.get(color) + "§l" + displayText;
	}
	
	public enum DisplayType {
		
		INCREMENT, DECREMENT, STOP
		
	}

}

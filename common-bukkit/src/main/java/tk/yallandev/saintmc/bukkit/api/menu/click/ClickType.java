package tk.yallandev.saintmc.bukkit.api.menu.click;

import org.bukkit.event.inventory.InventoryAction;

public enum ClickType {
	LEFT,
	RIGHT,

	COLLECT,

	MOVE_TO_OTHER_INVENTORY;

	public boolean isLeft() {
		return this == LEFT || this == COLLECT;
	}

	public boolean isShift() {
		return this == MOVE_TO_OTHER_INVENTORY;
	}

	public boolean isRight() {
		return this == RIGHT;
	}

	public static ClickType from(InventoryAction action) {
		switch (action) {
		case PICKUP_HALF:
			return RIGHT;
		case COLLECT_TO_CURSOR:
			return COLLECT;
		case MOVE_TO_OTHER_INVENTORY:
			return MOVE_TO_OTHER_INVENTORY;
		default:
			return LEFT;
		}
	}
}

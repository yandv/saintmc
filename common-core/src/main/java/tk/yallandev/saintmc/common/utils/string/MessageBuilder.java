package tk.yallandev.saintmc.common.utils.string;

import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * 
 * Assist you to create a TextComponent
 * 
 * @author yandv
 *
 */

public class MessageBuilder {

	private String message;

	private boolean hoverable;
	private HoverEvent hoverEvent;

	private boolean clickable;
	private ClickEvent clickEvent;

	private List<TextComponent> componentList;

	public MessageBuilder(String message) {
		this.message = message;
		this.componentList = new ArrayList<>();
	}

	public MessageBuilder setMessage(String message) {
		this.message = message;
		return this;
	}

	public MessageBuilder setHoverable(boolean hoverable) {
		this.hoverable = hoverable;
		return this;
	}

	public MessageBuilder setHoverEvent(HoverEvent hoverEvent) {
		this.hoverEvent = hoverEvent;
		this.hoverable = true;
		return this;
	}

	public MessageBuilder setHoverEvent(HoverEvent.Action action, String text) {
		this.hoverEvent = new HoverEvent(action, TextComponent.fromLegacyText(text));
		this.hoverable = true;
		return this;
	}

	public MessageBuilder setClickable(boolean clickable) {
		this.clickable = clickable;
		return this;
	}

	public MessageBuilder setClickEvent(ClickEvent clickEvent) {
		this.clickEvent = clickEvent;
		this.clickable = true;
		return this;
	}

	public MessageBuilder setClickEvent(ClickEvent.Action action, String text) {
		this.clickEvent = new ClickEvent(action, text);
		this.clickable = true;
		return this;
	}

	public MessageBuilder addExtre(TextComponent textComponent) {
		this.componentList.add(textComponent);
		return this;
	}

	public TextComponent create() {
		TextComponent textComponent = new TextComponent(TextComponent.fromLegacyText(message));

		if (hoverable)
			textComponent.setHoverEvent(hoverEvent);

		if (clickable)
			textComponent.setClickEvent(clickEvent);

		this.componentList.forEach(text -> textComponent.addExtra(text));

		return textComponent;
	}

}

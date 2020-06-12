package tk.yallandev.saintmc.common.utils.string;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class MessageBuilder {
	
	private String message;
	
	private boolean hoverable;
	private HoverEvent hoverEvent;
	
	private boolean clickable;
	private ClickEvent clickEvent;
	
	public MessageBuilder(String message) {
		this.message = message;
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
	
	public MessageBuilder setClickable(boolean clickable) {
		this.clickable = clickable;
		return this;
	}
	
	public MessageBuilder setClickEvent(ClickEvent clickEvent) {
		this.clickEvent = clickEvent;
		this.clickable = true;
		return this;
	}
	
	public TextComponent create() {
		TextComponent textComponent = new TextComponent(message);
		
		if (hoverable)
			textComponent.setHoverEvent(hoverEvent);
		
		if (clickable)
			textComponent.setClickEvent(clickEvent);

		return textComponent;
	}
	
}

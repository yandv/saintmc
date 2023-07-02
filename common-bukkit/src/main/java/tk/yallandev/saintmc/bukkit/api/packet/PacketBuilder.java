package tk.yallandev.saintmc.bukkit.api.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.TitleAction;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

public class PacketBuilder {

	private PacketContainer packetContainer;
	
	public PacketBuilder(PacketType packetType) {
		packetContainer = new PacketContainer(packetType);
	}
	
	public PacketBuilder writeTitleAction(int fieldIndex, TitleAction value) {
		packetContainer.getTitleActions().write(fieldIndex, value);
		return this;
	}
	
	public PacketBuilder writeChatComponents(int fieldIndex, WrappedChatComponent value) {
		packetContainer.getChatComponents().write(fieldIndex, value);
		return this;
	}
	
	public PacketBuilder writeInteger(int fieldIndex, int value) {
		packetContainer.getIntegers().write(fieldIndex, value);
		return this;
	}

	public PacketBuilder writeIntegerArray(int fieldIndex, int[] value) {
		packetContainer.getIntegerArrays().write(fieldIndex, value);
		return this;
	}
	
	public PacketContainer build() {
		return packetContainer;
	}
	
}

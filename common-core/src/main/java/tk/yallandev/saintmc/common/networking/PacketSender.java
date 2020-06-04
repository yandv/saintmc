package tk.yallandev.saintmc.common.networking;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PacketSender {
	
	private UUID uniqueId;
	private String playerName;

}

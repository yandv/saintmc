package tk.yallandev.saintmc.bukkit.api.vanish;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.actionbar.ActionBarAPI;
import tk.yallandev.saintmc.bukkit.event.admin.PlayerAdminModeEvent;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.server.ServerType;

public class AdminMode {

	private static final AdminMode instance = new AdminMode();

	private Set<UUID> admin;

	private HashMap<UUID, ItemStack[]> contentsMap;
	private HashMap<UUID, ItemStack[]> armorMap;

	public AdminMode() {
		admin = new HashSet<UUID>();

		contentsMap = new HashMap<>();
		armorMap = new HashMap<>();

		Bukkit.getScheduler().runTaskTimerAsynchronously(BukkitMain.getInstance(),
				() -> admin.stream().filter(uuid -> Bukkit.getPlayer(uuid) != null)
						.forEach(uuid -> ActionBarAPI.send(Bukkit.getPlayer(uuid), "§4Você está no modo admin!")),
				0, 20);
		Bukkit.getPluginManager().registerEvents(new AdminListener(), BukkitMain.getInstance());
	}

	public void setAdmin(Player p, Member member) {
		if (!admin.contains(p.getUniqueId()))
			admin.add(p.getUniqueId());

		if (member.getAccountConfiguration().isAdminItems()) {
			contentsMap.put(p.getUniqueId(), p.getInventory().getContents());
			armorMap.put(p.getUniqueId(), p.getInventory().getArmorContents());

			p.getInventory().clear();
		}

		PlayerAdminModeEvent event = new PlayerAdminModeEvent(p, PlayerAdminModeEvent.AdminMode.ADMIN,
				GameMode.CREATIVE);
		BukkitMain.getInstance().getServer().getPluginManager().callEvent(event);

		if (event.isCancelled())
			return;

		Group group = VanishAPI.getInstance().hidePlayer(p);
		p.sendMessage("\n §a* §fVocê entrou no modo §cadmin§f!");
		p.sendMessage(" §a* §fVocê está invisivel para §a" + group.toString() + " e inferiores§f!\n§f");
		p.setGameMode(event.getGameMode());
	}

	public void setPlayer(Player p, Member member) {
		PlayerAdminModeEvent event = new PlayerAdminModeEvent(p, PlayerAdminModeEvent.AdminMode.PLAYER,
				GameMode.SURVIVAL);
		BukkitMain.getInstance().getServer().getPluginManager().callEvent(event);

		if (event.isCancelled())
			return;

		if (admin.contains(p.getUniqueId())) {
			p.sendMessage("\n §a* §fVocê entrou no modo §ajogador§f!");
			removeAdmin(p);
		}

		if (member.getAccountConfiguration().isAdminItems()) {
			if (contentsMap.containsKey(p.getUniqueId())) {
				p.getInventory().setContents(contentsMap.get(p.getUniqueId()));
				contentsMap.remove(p.getUniqueId());
			}

			if (armorMap.containsKey(p.getUniqueId())) {
				p.getInventory().setArmorContents(armorMap.get(p.getUniqueId()));
				armorMap.remove(p.getUniqueId());
			}
		}

		p.sendMessage(" §a* §fVocê está visível para todos os jogadores!\n§f");
		p.setGameMode(event.getGameMode());
		VanishAPI.getInstance().showPlayer(p);
	}

	public boolean isAdmin(Player p) {
		return p != null && admin.contains(p.getUniqueId());
	}

	public int playersInAdmin() {
		return admin.size();
	}

	public void removeAdmin(Player p) {
		admin.remove(p.getUniqueId());
	}

	public class AdminListener implements Listener {

		@EventHandler
		public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
			if (event.getRightClicked() instanceof Player) {
				if (isAdmin(event.getPlayer())) {
					event.getPlayer().performCommand("invsee " + event.getRightClicked().getName());
				}
			}
		}

		@EventHandler
		public void onPlayerJoin(PlayerJoinEvent event) {
			Player player = event.getPlayer();
			Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

			new BukkitRunnable() {

				@Override
				public void run() {
					if (CommonGeneral.getInstance().getServerType() != ServerType.SCREENSHARE)
						if (member.getAccountConfiguration().isAdminOnJoin())
							if (member.hasGroupPermission(Group.TRIAL))
								if (!AdminMode.getInstance().isAdmin(player))
									AdminMode.getInstance().setAdmin(player, member);
				}
			}.runTaskLater(BukkitMain.getInstance(), 10);
		}

	}

	public static AdminMode getInstance() {
		return instance;
	}
}

package tk.yallandev.saintmc.kitpvp.warp.types;

import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import tk.yallandev.saintmc.kitpvp.warp.Warp;

@Getter
public class PartyWarp extends Warp {

	public PartyWarp() {
		super("", null);
	}

	@Override
	public ItemStack getItem() {
		return null;
	}

//	private List<String> blockedCommands = Arrays.asList("warp", "spawn", "gamemode", "gm", "tp", "teleport");
//	private WarpSetting warpSetting;
//
//	private PartyType partyType = PartyType.NONE;
//	private PartyState partyState = PartyState.NONE;
//
//	private List<Player> playerList;
//	private List<Player> playerInCombat;
//
//	private List<Player> spectatorList;
//
//	private int time = 300;
//
//	private int maxPlayers;
//
//	private Listener listener = new Listener() {
//
//		@EventHandler
//		public void onUpdate(UpdateEvent event) {
//			if (event.getType() != UpdateType.SECOND)
//				return;
//
//			if (partyState == PartyState.WAITING) {
//				if (time <= 0) {
//					GameMain.getInstance().getGamerManager().getGamers().forEach(
//							gamer -> gamer.getPlayer().sendMessage("§6§l" + partyType.name() + " §fO evento começou!"));
//					startGame();
//					return;
//				}
//
//				if (time == 5 || time == 10 || time == 15 || time % 30 == 0) {
//					GameMain.getInstance().getGamerManager().getGamers()
//							.forEach(gamer -> gamer.getPlayer().sendMessage("§6§l" + partyType.name()
//									+ " §fO evento começará em §a" + DateUtils.formatDifference(time) + "§f!"));
//				}
//
//				time--;
//			} else if (partyState == PartyState.GAMETIME) {
//				time++;
//			}
//		}
//
//		@EventHandler
//		public void onPlayerQuit(PlayerQuitEvent event) {
//			if (partyType == PartyType.NONE)
//				return;
//
//			Player player = event.getPlayer();
//
//			if (playerList.contains(player))
//				return;
//
//			if (partyState == PartyState.WAITING) {
//				playerList.remove(player);
//			} else {
//				if (partyType == PartyType.RDM) {
//					if (playerInCombat.contains(player)) {
//						killPlayer(player, null);
//					}
//				} else {
//					killPlayer(player, null);
//				}
//
//				playerList.remove(player);
//			}
//		}
//
//		@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
//		public void onPreProcessCommand(PlayerCommandPreprocessEvent event) {
//			if (!event.getMessage().startsWith("/"))
//				return;
//
//			if (GameMain.getInstance().getGamerManager().getGamer(event.getPlayer().getUniqueId())
//					.getWarpType() != getWarpType())
//				return;
//
//			String command = event.getMessage().split(" ")[0].replace("/", "");
//
//			if (playerInCombat.contains(event.getPlayer())) {
//				if (blockedCommands.contains(command.toLowerCase())) {
//					event.getPlayer().sendMessage("§c§l> §fO comando está §cbloqueado§f em combate!");
//					event.setCancelled(true);
//				}
//			}
//		}
//	};
//
//	public PartyWarp() {
//		warpSetting = new WarpSetting();
//		warpSetting.setWarpEnabled(false);
//		warpSetting.setProtectionSystem(false);
//
//		playerList = new ArrayList<>();
//		playerInCombat = new ArrayList<>();
//		spectatorList = new ArrayList<>();
//	}
//
//	@Override
//	public String getWarpName() {
//		return "Party";
//	}
//
//	@Override
//	public WarpType getWarpType() {
//		return WarpType.PARTY;
//	}
//
//	@Override
//	public Scoreboard getScoreboard(Player player) {
//		return null;
//	}
//
//	@Override
//	public Location getWarpLocation() {
//		return partyType == PartyType.NONE ? null
//				: BukkitMain.getInstance().getLocationFromConfig(partyType.name().toLowerCase());
//	}
//
//	@Override
//	@Deprecated
//	public void setWarpLocation(Location location) {
//		throw new IllegalStateException("You cant set party location!");
//	}
//
//	@Override
//	public void join(Player player) {
//		playerList.add(player);
//	}
//
//	@Override
//	public void leave(Player player) {
//		playerList.remove(player);
//
//		BukkitMember bukkitPlayer = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
//				.getMember(player.getUniqueId());
//		if (bukkitPlayer.getTag() == Tag.RDM)
//			bukkitPlayer.setTag(bukkitPlayer.getDefaultTag());
//	}
//
//	@Override
//	public void loseProtection(Player player) {
//
//	}
//
//	@Override
//	public boolean drop(Player player, ItemStack itemStack) {
//		return !playerInCombat.contains(player);
//	}
//
//	@Override
//	public boolean pickupItem(Player player, ItemStack itemStack) {
//		return !playerInCombat.contains(player);
//	}
//
//	@Override
//	public Location respawn(Player player) {
//		return getWarpLocation();
//	}
//
//	@Override
//	public void death(PlayerDeathEvent playerDeathEvent) {
//		if (partyType == PartyType.NONE)
//			return;
//
//		if (partyType == PartyType.RDM) {
//			Player player = playerDeathEvent.getEntity();
//			Player killer = player.getKiller();
//
//			killPlayer(player, killer);
//		}
//	}
//
//	@Override
//	public void damage(EntityDamageEvent entityDamageEvent) {
//		if (partyType == PartyType.NONE)
//			return;
//
//		if (!(entityDamageEvent.getEntity() instanceof Player))
//			return;
//
//		Player player = (Player) entityDamageEvent.getEntity();
//
//		if (partyType == PartyType.RDM) {
//			if (playerInCombat.contains(player)) {
//				if (entityDamageEvent instanceof EntityDamageByEntityEvent) {
//					EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) entityDamageEvent;
//
//					if (!(entityDamageByEntityEvent.getDamager() instanceof Player))
//						return;
//
//					Player damager = (Player) entityDamageByEntityEvent.getDamager();
//
//					if (playerInCombat.contains(damager)) {
//						entityDamageEvent.setCancelled(false);
//					} else {
//						entityDamageEvent.setCancelled(true);
//					}
//				}
//			} else {
//				entityDamageEvent.setCancelled(true);
//			}
//		} else {
//			entityDamageEvent.setCancelled(true);
//		}
//	}
//
//	@Override
//	public ItemStack getItem() {
//		return new ItemBuilder().name("§aEvento " + partyType.getPartyName())
//				.lore("\n" + partyType.getPartyDescription() + "\n\n"
//						+ GameMain.getInstance().getGamerManager().getGamers().stream()
//								.filter(gamer -> gamer.getWarp() == this).count()
//						+ "/" + maxPlayers + " jogadores no evento!")
//				.build();
//	}
//
//	public void setTime(Integer time, CommandSender sender) {
//		if (partyType == PartyType.NONE)
//			return;
//
//		this.time = time;
//
//		if (partyState == PartyState.WAITING) {
//			GameMain.getInstance().getGamerManager().getGamers()
//					.forEach(gamer -> gamer.getPlayer().sendMessage("§6§l" + partyType.name()
//							+ " §fO tempo do evento foi alterado para §a" + DateUtils.formatDifference(time) + "§f!"));
//		} else {
//			getPlayersList().forEach(player -> player.sendMessage("§6§l" + partyType.name()
//					+ " §fO tempo do evento foi alterado para §a" + DateUtils.formatDifference(time) + "§f!"));
//		}
//	}
//
//	public void startGame() {
//		this.partyState = PartyState.GAMETIME;
//
//		if (partyType == PartyType.RDM) {
//			findMatch(null);
//		}
//
//		Bukkit.getPluginManager().registerEvents(listener, GameMain.getInstance());
//	}
//
//	public void findMatch(Player p) {
//		getPlayersList().forEach(pl -> pl.sendMessage("§a§l" + partyType.name() + " §f"));
//
//		if (p == null) {
//			p = playerList.get(CommonConst.RANDOM.nextInt(playerList.size()));
//		}
//
//		Player c = playerList.get(CommonConst.RANDOM.nextInt(playerList.size()));
//
//		while (c == p)
//			c = playerList.get(CommonConst.RANDOM.nextInt(playerList.size()));
//
//		Player challenger = c;
//		Player player = p;
//
//		if (partyType == PartyType.RDM) {
//			if (!playerInCombat.contains(challenger))
//				playerInCombat.add(challenger);
//
//			if (!playerInCombat.contains(player))
//				playerInCombat.add(player);
//
//			challenger.getInventory().clear();
//			challenger.getInventory().setArmorContents(new ItemStack[4]);
//
//			challenger.setHealth(20D);
//			challenger.setFoodLevel(20);
//
//			challenger.getInventory().setItem(0, new ItemBuilder().unbreakable().type(Material.DIAMOND_SWORD)
//					.enchantment(Enchantment.DAMAGE_ALL, 1).build());
//			challenger.getInventory().setHelmet(new ItemBuilder().unbreakable().type(Material.IRON_HELMET).build());
//			challenger.getInventory()
//					.setChestplate(new ItemBuilder().unbreakable().type(Material.IRON_CHESTPLATE).build());
//			challenger.getInventory().setLeggings(new ItemBuilder().unbreakable().type(Material.IRON_LEGGINGS).build());
//			challenger.getInventory().setBoots(new ItemBuilder().unbreakable().type(Material.IRON_BOOTS).build());
//
//			for (int x = 0; x < 8; x++)
//				challenger.getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP, 1));
//
//			player.getInventory().clear();
//			player.getInventory().setArmorContents(new ItemStack[4]);
//
//			player.setHealth(20D);
//			player.setFoodLevel(20);
//
//			player.getInventory().setItem(0, new ItemBuilder().unbreakable().type(Material.DIAMOND_SWORD)
//					.enchantment(Enchantment.DAMAGE_ALL, 1).build());
//			player.getInventory().setHelmet(new ItemBuilder().unbreakable().type(Material.IRON_HELMET).build());
//			player.getInventory().setChestplate(new ItemBuilder().unbreakable().type(Material.IRON_CHESTPLATE).build());
//			player.getInventory().setLeggings(new ItemBuilder().unbreakable().type(Material.IRON_LEGGINGS).build());
//			player.getInventory().setBoots(new ItemBuilder().unbreakable().type(Material.IRON_BOOTS).build());
//
//			for (int x = 0; x < 8; x++)
//				player.getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP, 1));
//
//			challenger
//					.teleport(BukkitMain.getInstance().getLocationFromConfig(partyType.name().toLowerCase() + ".pos1"));
//			player.teleport(BukkitMain.getInstance().getLocationFromConfig(partyType.name().toLowerCase() + ".pos2"));
//
//			player.sendMessage(
//					"§a§l" + partyType.name() + " §fVocê irá batalhar contra o §a" + challenger.getName() + "§f!");
//			challenger.sendMessage(
//					"§a§l" + partyType.name() + " §fVocê irá batalhar contra o §a" + player.getName() + "§f!");
//
//			VanishAPI.getInstance().hideAllPlayers(player);
//			VanishAPI.getInstance().hideAllPlayers(challenger);
//
//			BukkitMember bukkitPlayer = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
//					.getMember(player.getUniqueId());
//			BukkitMember bukkitChallenger = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
//					.getMember(challenger.getUniqueId());
//
//			bukkitPlayer.setTag(Tag.RDM);
//			bukkitChallenger.setTag(Tag.RDM);
//
//			player.showPlayer(challenger);
//			challenger.showPlayer(player);
//
//			getPlayersList().forEach(pl -> pl.sendMessage("§a§l" + partyType.name() + " §fO jogador §a"
//					+ player.getName() + "§f irá batalhar contra o §a" + challenger.getName() + "§f!"));
//		}
//	}
//
//	private void killPlayer(Player player, Player k) {
//		if (playerInCombat.contains(player))
//			playerInCombat.remove(player);
//
//		if (playerList.contains(player))
//			playerList.remove(player);
//
//		if (k == null) {
//			if (playerInCombat.contains(player)) {
//				k = playerInCombat.stream().filter(p -> !p.getUniqueId().equals(player.getUniqueId())).findFirst()
//						.orElse(null);
//
//			} else {
//				k = playerInCombat.get(0);
//			}
//
//			if (k == null) {
//				System.out.println("killer nulo ??");
//			}
//		}
//
//		Player killer = k;
//
//		if (partyType == PartyType.RDM) {
//			if (killer == null) {
//				getPlayersList().forEach(p -> p.sendMessage("§6§l" + partyType.name() + " §fO jogador §c"
//						+ player.getName() + "§f foi eliminado do evento§f!"));
//			} else {
//				getPlayersList().forEach(p -> p.sendMessage("§6§l" + partyType.name() + " §fO jogador §c"
//						+ player.getName() + "§f foi eliminado pelo §a" + killer.getName() + "§f!"));
//			}
//		}
//
//		VanishAPI.getInstance().getHideAllPlayers().remove(player.getUniqueId());
//		VanishAPI.getInstance().updateVanishToPlayer(player);
//
//		VanishAPI.getInstance().getHideAllPlayers().remove(killer.getUniqueId());
//		VanishAPI.getInstance().updateVanishToPlayer(killer);
//
//		BukkitMember bukkitPlayer = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
//				.getMember(player.getUniqueId());
//		BukkitMember bukkitChallenger = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
//				.getMember(killer.getUniqueId());
//
//		bukkitPlayer.setTag(bukkitPlayer.getDefaultTag());
//		bukkitChallenger.setTag(bukkitChallenger.getDefaultTag());
//
//		player.sendMessage("§c§l> §fVocê foi eliminado do evento §a" + partyType.getPartyName() + "§f!");
//
//		if (Member.hasGroupPermission(player.getUniqueId(), Group.BLIZZARD)) {
//			player.spigot().respawn();
//			Gamer gamer = GameMain.getInstance().getGamerManager().getGamer(player.getUniqueId());
//			PlayerJoinWarpEvent warpEvent = new PlayerJoinWarpEvent(player, WarpType.SPAWN.getWarp());
//
//			Bukkit.getPluginManager().callEvent(warpEvent);
//
//			gamer.setWarpType(WarpType.SPAWN);
//			gamer.getWarpType().getWarp().join(player);
//		} else {
//			player.spigot().respawn();
//			Gamer gamer = GameMain.getInstance().getGamerManager().getGamer(player.getUniqueId());
//			PlayerJoinWarpEvent warpEvent = new PlayerJoinWarpEvent(player, WarpType.SPAWN.getWarp());
//
//			Bukkit.getPluginManager().callEvent(warpEvent);
//
//			gamer.setWarpType(WarpType.SPAWN);
//			gamer.getWarpType().getWarp().join(player);
//		}
//
//		if (playerList.size() == 1) {
//			endParty(playerList.get(0));
//			return;
//		} else if (playerList.size() == 0) {
//			endParty(null);
//			return;
//		}
//
//		if (partyType == PartyType.RDM) {
//			findMatch(killer);
//		}
//	}
//
//	private void endParty(Player winner) {
//		if (winner != null) {
//			Gamer gamer = GameMain.getInstance().getGamerManager().getGamer(winner.getUniqueId());
//			PlayerJoinWarpEvent warpEvent = new PlayerJoinWarpEvent(winner, WarpType.SPAWN.getWarp());
//
//			Bukkit.getPluginManager().callEvent(warpEvent);
//
//			gamer.setWarpType(WarpType.SPAWN);
//			BukkitMember bukkitPlayer = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
//					.getMember(winner.getUniqueId());
//			bukkitPlayer.setTag(bukkitPlayer.getDefaultTag());
//			gamer.getWarpType().getWarp().join(winner);
//
//			Bukkit.broadcastMessage(" ");
//			Bukkit.broadcastMessage(
//					"§6§l" + partyType.name() + " §fO jogador §a" + winner.getName() + "§f foi o ganhador do evento!");
//			winner.sendMessage("§6§l" + partyType.name() + " §fVocê venceu o evento §a" + partyType.getPartyName());
//			Bukkit.broadcastMessage(" ");
//
//			Member.getMember(winner.getUniqueId()).addXp(50);
//		}
//
//		reset();
//	}
//
//	public void start(PartyType partyType) {
//		this.partyType = partyType;
//		this.partyState = PartyState.WAITING;
//
//		this.time = 300;
//
//		Bukkit.getPluginManager().registerEvents(listener, GameMain.getInstance());
//	}
//
//	public void reset() {
//		partyType = PartyType.NONE;
//		partyState = PartyState.NONE;
//
//		time = 300;
//
//		playerList.clear();
//		playerInCombat.clear();
//
//		HandlerList.unregisterAll(listener);
//	}
//
//	public List<Player> getPlayersList() {
//		return Stream.concat(playerList.stream(), spectatorList.stream()).collect(Collectors.toList());
//	}
//
//	@Getter
//	@AllArgsConstructor
//	public enum PartyType {
//
//		RDM("Rei da Mesa", "Evento de pvp, que todos os jogadores do evento lutaram 1v1 pela sua vida", Material.CAKE),
//		NONE;
//
//		private String partyName;
//		private String partyDescription;
//		private Material type;
//
//		PartyType() {
//			partyName = "N/A";
//			partyDescription = "";
//		}
//	}
//
//	public enum PartyState {
//
//		WAITING, GAMETIME, ENDING, NONE;
//
//	}

}

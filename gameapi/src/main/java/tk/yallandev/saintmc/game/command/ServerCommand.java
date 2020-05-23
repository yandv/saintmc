package tk.yallandev.saintmc.game.command;

import tk.yallandev.saintmc.common.command.CommandClass;

public class ServerCommand implements CommandClass {

//	@Command(name = "build", groupToUse = Group.MODPLUS)
//	public void admin(BukkitCommandArgs args) {
//		CommandSender sender = args.getSender();
//		String[] a = args.getArgs();
//		Language lang = (args.isPlayer() ? BattlePlayer.getLanguage(sender.getUniqueId()) : BattlebitsAPI.getDefaultLanguage());
//		
//		String prefix = T.t(BukkitMain.getInstance(), lang, "command-build-prefix") + " ";
//		
//		if (a.length == 0) {
//			sender.sendMessage(prefix + T.t(BukkitMain.getInstance(), lang, "command-build-usage"));
//			return;
//		}
//		
//		if (a[0].equalsIgnoreCase("on")) {
//			if (ServerManager.getInstance().isBuild()) {
//				sender.sendMessage(prefix + T.t(BukkitMain.getInstance(), lang, "command-build-already-enabled"));
//				return;
//			}
//			
//			ServerManager.getInstance().setBuild(true);
//			Bukkit.broadcastMessage("�7Destrui��o de blocos: " + (ServerManager.getInstance().isBuild() ? "�aATIVADO" : "�cDESATIVADO"));
//		} else if (a[0].equalsIgnoreCase("off")) {
//			if (!ServerManager.getInstance().isBuild()) {
//				sender.sendMessage(prefix + T.t(BukkitMain.getInstance(), lang, "command-build-already-disabled"));
//				return;
//			}
//			
//			ServerManager.getInstance().setBuild(false);
//			Bukkit.broadcastMessage("�7Destrui��o de blocos: " + (ServerManager.getInstance().isBuild() ? "�aATIVADO" : "�cDESATIVADO"));
//		} else {
//			sender.sendMessage(prefix + T.t(BukkitMain.getInstance(), lang, "command-build-usage"));
//			return;
//		}
//	}
//	
//	@Command(name = "place", groupToUse = Group.MODPLUS)
//	public void place(BukkitCommandArgs args) {
//		CommandSender sender = args.getSender();
//		String[] a = args.getArgs();
//		Language lang = (args.isPlayer() ? BattlePlayer.getLanguage(sender.getUniqueId()) : BattlebitsAPI.getDefaultLanguage());
//		
//		String prefix = T.t(BukkitMain.getInstance(), lang, "command-place-prefix") + " ";
//		
//		if (a.length == 0) {
//			sender.sendMessage(prefix + T.t(BukkitMain.getInstance(), lang, "command-place-usage"));
//			return;
//		}
//		
//		if (a[0].equalsIgnoreCase("on")) {
//			if (ServerManager.getInstance().isPlace()) {
//				sender.sendMessage(prefix + T.t(BukkitMain.getInstance(), lang, "command-place-already-enabled"));
//				return;
//			}
//			
//			ServerManager.getInstance().setPlace(true);
//			Bukkit.broadcastMessage("�7Constru��o de blocos: " + (ServerManager.getInstance().isPlace() ? "�aATIVADO" : "�cDESATIVADO"));
//		} else if (a[0].equalsIgnoreCase("off")) {
//			if (!ServerManager.getInstance().isPlace()) {
//				sender.sendMessage(prefix + T.t(BukkitMain.getInstance(), lang, "command-place-already-disabled"));
//				return;
//			}
//			
//			ServerManager.getInstance().setPlace(false);
//			Bukkit.broadcastMessage("�7Constru��o de blocos: " + (ServerManager.getInstance().isPlace() ? "�aATIVADO" : "�cDESATIVADO"));
//		} else {
//			sender.sendMessage(prefix + T.t(BukkitMain.getInstance(), lang, "command-place-usage"));
//			return;
//		}
//	}
//	
//	@Command(name = "togglepvp", aliases = { "pvp" }, groupToUse = Group.MODPLUS)
//	public void pvp(BukkitCommandArgs args) {
//		CommandSender sender = args.getSender();
//		String[] a = args.getArgs();
//		Language lang = (args.isPlayer() ? BattlePlayer.getLanguage(sender.getUniqueId()) : BattlebitsAPI.getDefaultLanguage());
//		
//		String prefix = T.t(BukkitMain.getInstance(), lang, "command-togglepvp-prefix") + " ";
//		
//		if (a.length == 0) {
//			sender.sendMessage(prefix + T.t(BukkitMain.getInstance(), lang, "command-togglepvp-usage"));
//			return;
//		}
//		
//		if (a[0].equalsIgnoreCase("on")) {
//			if (ServerManager.getInstance().isPvp()) {
//				sender.sendMessage(prefix + T.t(BukkitMain.getInstance(), lang, "command-togglepvp-already-enabled"));
//				return;
//			}
//			
//			ServerManager.getInstance().setPvp(true);
//			Bukkit.broadcastMessage("�7PvP global: " + (ServerManager.getInstance().isPvp() ? "�aATIVADO" : "�cDESATIVADO"));
//		} else if (a[0].equalsIgnoreCase("off")) {
//			if (!ServerManager.getInstance().isPvp()) {
//				sender.sendMessage(prefix + T.t(BukkitMain.getInstance(), lang, "command-togglepvp-already-disabled"));
//				return;
//			}
//			
//			ServerManager.getInstance().setPvp(false);
//			Bukkit.broadcastMessage("�7PvP global: " + (ServerManager.getInstance().isPvp() ? "�aATIVADO" : "�cDESATIVADO"));
//		} else {
//			sender.sendMessage(prefix + T.t(BukkitMain.getInstance(), lang, "command-togglepvp-usage"));
//			return;
//		}
//	}
//	
//	@Command(name = "damage", groupToUse = Group.MODPLUS)
//	public void damage(BukkitCommandArgs args) {
//		CommandSender sender = args.getSender();
//		String[] a = args.getArgs();
//		Language lang = (args.isPlayer() ? BattlePlayer.getLanguage(sender.getUniqueId()) : BattlebitsAPI.getDefaultLanguage());
//		
//		String prefix = T.t(BukkitMain.getInstance(), lang, "command-damage-prefix") + " ";
//		
//		if (a.length == 0) {
//			sender.sendMessage(prefix + T.t(BukkitMain.getInstance(), lang, "command-damage-usage"));
//			return;
//		}
//		
//		if (a[0].equalsIgnoreCase("on")) {
//			if (ServerManager.getInstance().isDamage()) {
//				sender.sendMessage(prefix + T.t(BukkitMain.getInstance(), lang, "command-damage-already-enabled"));
//				return;
//			}
//			
//			ServerManager.getInstance().setDamage(true);
//			Bukkit.broadcastMessage("�7Dano global: " + (ServerManager.getInstance().isDamage() ? "�aATIVADO" : "�cDESATIVADO"));
//		} else if (a[0].equalsIgnoreCase("off")) {
//			if (!ServerManager.getInstance().isDamage()) {
//				sender.sendMessage(prefix + T.t(BukkitMain.getInstance(), lang, "command-damage-already-disabled"));
//				return;
//			}
//			
//			ServerManager.getInstance().setDamage(false);
//			Bukkit.broadcastMessage("�7Dano global: " + (ServerManager.getInstance().isDamage() ? "�aATIVADO" : "�cDESATIVADO"));
//		} else {
//			sender.sendMessage(prefix + T.t(BukkitMain.getInstance(), lang, "command-damage-usage"));
//			return;
//		}
//	}
//	
//	@Command(name = "drop", groupToUse = Group.MODPLUS)
//	public void drop(BukkitCommandArgs args) {
//		CommandSender sender = args.getSender();
//		String[] a = args.getArgs();
//		Language lang = (args.isPlayer() ? BattlePlayer.getLanguage(sender.getUniqueId()) : BattlebitsAPI.getDefaultLanguage());
//		
//		String prefix = T.t(BukkitMain.getInstance(), lang, "command-drop-prefix") + " ";
//		
//		if (a.length == 0) {
//			sender.sendMessage(prefix + T.t(BukkitMain.getInstance(), lang, "command-drop-usage"));
//			return;
//		}
//		
//		if (a[0].equalsIgnoreCase("on")) {
//			if (ServerManager.getInstance().isDrop()) {
//				sender.sendMessage(prefix + T.t(BukkitMain.getInstance(), lang, "command-drop-already-enabled"));
//				return;
//			}
//			
//			ServerManager.getInstance().setDrop(true);
//			Bukkit.broadcastMessage("�7Drop de Itens: " + (ServerManager.getInstance().isDrop() ? "�aATIVADO" : "�cDESATIVADO"));
//		} else if (a[0].equalsIgnoreCase("off")) {
//			if (!ServerManager.getInstance().isDrop()) {
//				sender.sendMessage(prefix + T.t(BukkitMain.getInstance(), lang, "command-drop-already-disabled"));
//				return;
//			}
//			
//			ServerManager.getInstance().setDrop(false);
//			Bukkit.broadcastMessage("�7Drop de Itens: " + (ServerManager.getInstance().isDrop() ? "�aATIVADO" : "�cDESATIVADO"));
//		} else {
//			sender.sendMessage(prefix + T.t(BukkitMain.getInstance(), lang, "command-drop-usage"));
//			return;
//		}
//	}
//	
//	@Command(name = "pickup", groupToUse = Group.MODPLUS)
//	public void pickup(BukkitCommandArgs args) {
//		CommandSender sender = args.getSender();
//		String[] a = args.getArgs();
//		Language lang = (args.isPlayer() ? BattlePlayer.getLanguage(sender.getUniqueId()) : BattlebitsAPI.getDefaultLanguage());
//		
//		String prefix = T.t(BukkitMain.getInstance(), lang, "command-pickup-prefix") + " ";
//		
//		if (a.length == 0) {
//			sender.sendMessage(prefix + T.t(BukkitMain.getInstance(), lang, "command-pickup-usage"));
//			return;
//		}
//		
//		if (a[0].equalsIgnoreCase("on")) {
//			if (ServerManager.getInstance().isPickup()) {
//				sender.sendMessage(prefix + T.t(BukkitMain.getInstance(), lang, "command-pickup-already-enabled"));
//				return;
//			}
//			
//			ServerManager.getInstance().setPickup(true);
//			Bukkit.broadcastMessage("�7Pickup de Itens: " + (ServerManager.getInstance().isPickup() ? "�aATIVADO" : "�cDESATIVADO"));
//		} else if (a[0].equalsIgnoreCase("off")) {
//			if (!ServerManager.getInstance().isPickup()) {
//				sender.sendMessage(prefix + T.t(BukkitMain.getInstance(), lang, "command-pickup-already-disabled"));
//				return;
//			}
//			
//			ServerManager.getInstance().setPickup(false);
//			Bukkit.broadcastMessage("�7Pickup de Itens: " + (ServerManager.getInstance().isPickup() ? "�aATIVADO" : "�cDESATIVADO"));
//		} else {
//			sender.sendMessage(prefix + T.t(BukkitMain.getInstance(), lang, "command-pickup-usage"));
//			return;
//		}
//	}
	
}

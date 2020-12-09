package tk.yallandev.saintmc.skwyars.command;

import tk.yallandev.saintmc.skwyars.GameMain;

public class ModeratorCommand {
	
//	@Command(name = "setspawn", groupToUse = Group.BUILDER)
//	public void setspawnCommand(CommandArgs cmdArgs) {
//		if (!cmdArgs.isPlayer())
//			return;
//
//		String[] a = cmdArgs.getArgs();
//
//		if (a.length == 0) {
//			cmdArgs.getSender().sendMessage(" §e* §fUse §a/setwarp <warpName>§f para setar uma warp.");
//			return;
//		}
//
//		StringBuilder stringBuilder = new StringBuilder();
//
//		for (int x = 0; x < a.length; x++) {
//			stringBuilder.append(a[x]).append(" ");
//		}
//
//		String configName = a[0];
//		GameMain.getInstance().registerLocationInConfig(((Player)cmdArgs.getSender()).getLocation(), configName);
//		p.sendMessage(" §a* §fVocê setou a warp §a" + configName + "§f!");
//	}
	
//	/* tirei a framework de comandos */
//
//	@Command(name = "tempo", groupToUse = Group.MOD)
//	public void tempoCommand(CommandArgs cmdArgs) {
//		CommandSender sender = cmdArgs.getSender();
//		String[] args = cmdArgs.getArgs();
//
//		if (args.length == 0) {
//			sender.sendMessage(" §e* §fUse §a/" + cmdArgs.getLabel() + " <tempo:stop>§f para alterar o tempo do jogo!");
//			return;
//		}
//
//		if (args[0].equalsIgnoreCase("stop")) {
//			GameGeneral.getInstance().setCountTime(!GameGeneral.getInstance().isCountTime());
//			sender.sendMessage(" §a* §fVocê " + (GameGeneral.getInstance().isCountTime() ? "§aativou" : "§cdesativou")
//					+ "§f o timer!");
//			return;
//		}
//		
//		long time;
//
//		try {
//			time = DateUtils.parseDateDiff(args[0], true);
//		} catch (Exception e) {
//			sender.sendMessage(" §c* §fO formato de tempo não é valido.");
//			return;
//		}
//
//		int seconds = (int) Math.floor((time - System.currentTimeMillis()) / 1000);
//
//		if (seconds >= 60 * 120)
//			seconds = 60 * 120;
//
//		sender.sendMessage(" §a* §fO tempo do jogo foi alterado para §a" + args[0] + "§f!");
//		GameGeneral.getInstance().setTime(seconds);
//	}
//
//	@Command(name = "chest", groupToUse = Group.MOD)
//	public void chestCommand(CommandArgs cmdArgs) {
//		if (!cmdArgs.isPlayer())
//			return;
//
//		Player player = ((BukkitMember) cmdArgs.getSender()).getPlayer();
//		String[] args = cmdArgs.getArgs();
//
//		if (args.length == 0) {
//			player.sendMessage(" §e* §fUse §a/" + cmdArgs.getLabel() + " <chestType>§f para adicionar baus ao jogo!");
//			return;
//		}
//
//		if (args[0].equalsIgnoreCase("save")) {
//			GameGeneral.getInstance().getLocationController().saveChests();
//			player.sendMessage("§aA config foi salva!");
//			return;
//		}
//
//		ChestType chest = null;
//
//		try {
//			chest = ChestType.valueOf(args[0].toUpperCase());
//		} catch (Exception ex) {
//			// mandar msg de nao existe esse tipo
//			return;
//		}
//
//		final ChestType chestType = chest;
//		
//		/**
//		 * foda-se
//		 */
//
//		player.getInventory().addItem(new InteractableItem(
//				ItemCreator.name("§a" + NameUtils.formatString(chestType.name())).type(chestType == ChestType.DEFAULT ? Material.DIAMOND
//						: chestType == ChestType.MINIFEAST ? Material.GOLD_INGOT : Material.IRON_INGOT).build(),
//				new Interact() {
//
//					@Override
//					public boolean onInteract(Player player, Entity entity, Block block, ItemStack item,
//							ActionType action) {
//
//						if (block == null)
//							return true;
//
//						if (block.getType() != Material.CHEST)
//							return true;
//
//						if (action == ActionType.LEFT) {
//							GameGeneral.getInstance().getLocationController()
//									.registerChest(new Chest(block.getX(), block.getY(), block.getZ(), chestType));
//							player.sendMessage("§aO bau foi adicionado como " + chestType.name() + "!");
//						} else {
//							GameGeneral.getInstance().getLocationController()
//									.removeChest(new Chest(block.getX(), block.getY(), block.getZ(), chestType));
//							player.sendMessage("§cO bau foi removido!");
//						}
//
//						return true;
//					}
//				}).getItemStack());
//	}

}

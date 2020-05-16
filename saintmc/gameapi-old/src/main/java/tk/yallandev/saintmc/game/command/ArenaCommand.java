package tk.yallandev.saintmc.game.command;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import tk.yallandev.saintmc.bukkit.command.BukkitCommandArgs;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.permission.Group;

public class ArenaCommand implements CommandClass {

	@Command(name = "finalbattle", groupToUse = Group.MODPLUS)
	public void onFinalBattle(BukkitCommandArgs cmdArgs) {
		createFinalBattle();
		cmdArgs.getSender().sendMessage(" §a* §fVocê §acriou§f a arena final!");
	}

	@Command(name = "miniarena", groupToUse = Group.MODPLUS)
	public void onMiniArena(BukkitCommandArgs cmdArgs) {
		createMiniArena();
		cmdArgs.getSender().sendMessage(" §a* §fVocê §acriou§f uma mini arena!");
	}

	@Command(name = "arena", aliases = { "createarena" }, groupToUse = Group.MOD)
	public void onArena(BukkitCommandArgs cmdArgs) {	
		String[] a = cmdArgs.getArgs();
		Player p = cmdArgs.getPlayer();

		if (a.length <= 2) {
			p.sendMessage(" §e* §fUse §e/" + cmdArgs.getLabel()
					+ " (circulo|quadrado) (raio) (altura)§f para criar uma arena!");
			return;
		}

		if (!isNumeric(a[2])) {
			p.sendMessage(" §e* §fUse §e/" + cmdArgs.getLabel()
					+ " (circulo|quadrado) (raio) (altura)§f para criar uma arena!");
			return;
		}

		int altura = Integer.valueOf(a[2]);

		if (a[0].equalsIgnoreCase("circulo")) {
			if (!isNumeric(a[1])) {
				p.sendMessage(
						" §e* §fUse §e/" + cmdArgs.getLabel() + " circulo (raio) (altura)§f para criar uma arena!");
				return;
			}

			int raio = Integer.valueOf(a[1]);

			createArenaCirculo(p.getLocation().add(0, -1, 0), raio, altura);
			p.sendMessage(" §a*§f Voc§ criou uma arena §acircular§f com o raio de §a" + raio + " §fe com a altura de §a"
					+ altura + "§f!");
		} else if (a[0].equalsIgnoreCase("quadrado")) {
			if (!isNumeric(a[1])) {
				p.sendMessage(" §e* §fUse §e/" + cmdArgs.getLabel()
						+ " quadrado (comprimento) (altura)§f para criar uma arena!");
				return;
			}

			int comprimento = Integer.valueOf(a[1]);

			createArenaQuadrado(p.getLocation().add(0, -1, 0), comprimento, altura, comprimento);
			p.sendMessage(" §a*§f Voc§ criou uma arena §aqualdrada§f com o comprimento de §a" + comprimento
					+ " §fe com a altura de §a" + altura + "§f!");
		} else {
			p.sendMessage(" §e* §fUse §e/" + cmdArgs.getLabel()
					+ " (circulo|quadrado) (raio) (altura)§f para criar uma arena!");
		}
	}

	@Command(name = "cleardrop", groupToUse = Group.MODPLUS)
	public void onClearDrops(BukkitCommandArgs cmdArgs) {
		for (Item drop : cmdArgs.getPlayer().getWorld().getEntitiesByClass(Item.class)) {
			drop.remove();
		}

		cmdArgs.getPlayer().sendMessage("§a * §fVoc§ §alimpou§f o chão!");
	}

	public boolean isNumeric(String arg) {
		try {
			Integer.valueOf(arg);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static void createArenaQuadrado(Location loc, int comprimentoX, int altura, int comprimentoZ) {
		Block mainBlock = loc.getBlock();

		double x;
		double z;
		for (double cXMenor = (double) (-comprimentoX); cXMenor <= (double) comprimentoX; ++cXMenor) {
			for (x = (double) (-comprimentoZ); x <= (double) comprimentoZ; ++x) {
				for (z = 0.0D; z <= (double) altura; ++z) {
					Location y = new Location(mainBlock.getWorld(), (double) mainBlock.getX() + cXMenor,
							(double) mainBlock.getY() + z, (double) mainBlock.getZ() + x);
					y.getBlock().setType(Material.BEDROCK);
				}
			}
		}

		int var15 = comprimentoX - 1;
		int cZMenor = comprimentoZ - 1;

		for (x = (double) (-var15); x <= (double) var15; ++x) {
			for (z = (double) (-cZMenor); z <= (double) cZMenor; ++z) {
				for (double var14 = 1.0D; var14 <= (double) altura; ++var14) {
					Location l = new Location(mainBlock.getWorld(), (double) mainBlock.getX() + x,
							(double) mainBlock.getY() + var14, (double) mainBlock.getZ() + z);
					if (l.getBlock().getType() != Material.AIR) {
						l.getBlock().setType(Material.AIR);
					}
				}
			}
		}

	}

	public static void createArenaCirculo(Location loc, int radius, int altura) {
		for (int x = -radius; x <= radius; ++x) {
			for (int z = -radius; z <= radius; ++z) {
				for (double y = 0.0D; y <= (double) altura; ++y) {
					Block mainBlock = loc.clone().add(0.0D, (double) altura, 0.0D).getBlock();
					Location l = new Location(mainBlock.getWorld(), (double) (mainBlock.getX() + x),
							(double) mainBlock.getY() + y, (double) (mainBlock.getZ() + z));
					if (y == 0.0D) {
						if (mainBlock.getLocation().distance(l) <= (double) radius) {
							l.getBlock().setType(Material.BEDROCK);
						}
					} else if (mainBlock.getLocation().distance(l) <= (double) radius
							&& mainBlock.getLocation().distance(l) >= (double) (radius - 2)) {
						l.getBlock().setType(Material.BEDROCK);
					}
				}
			}
		}
	}

	public void FinalBattle(Location loc, int r, Material mat, int alturaY) {
		int cx = loc.getBlockX();
		int cy = loc.getBlockY();
		int cz = loc.getBlockZ();
		World w = loc.getWorld();
		int rSquared = r * r;
		for (int x = cx - r; x <= cx + r; x++)
			for (int z = cz - r; z <= cz + r; z++)
				for (int y = cy + 1; y <= cy + alturaY; y++)
					if ((cx - x) * (cx - x) + (cz - z) * (cz - z) <= rSquared)
						w.getBlockAt(x, y, z).setType(mat);
	}

	public void createFinalBattle() {
		int aleatorioX = (int) (1.0D + Math.random() * 10.0D);
		int aleatorioZ = (int) (1.0D + Math.random() * 10.0D);
		int aleatorioY = 0;

		World world = Bukkit.getWorld("world");
		for (int i = 90; i > 95; i--) {
			Block blockY = world.getBlockAt(aleatorioX, i, aleatorioZ);
			int y = blockY.getTypeId();

			if (y == 0) {
				aleatorioY = i;
			}
		}

		Location loc = new Location(world, aleatorioX, aleatorioY, aleatorioZ);
		FinalBattle(loc, 50, Material.AIR, 90);

		Location loc2 = new Location(world, aleatorioX, aleatorioY + 90, aleatorioZ);
		FinalBattle(loc2, 50, Material.AIR, 90);

		Location loc3 = new Location(world, aleatorioX, aleatorioY + 90, aleatorioZ);
		FinalBattle(loc3, 50, Material.AIR, 90);

		Location loc4 = new Location(world, aleatorioX, aleatorioY + 90, aleatorioZ);
		FinalBattle(loc4, 50, Material.AIR, 90);

		final int aleatorioY2 = aleatorioY;

		Location location = new Location(Bukkit.getWorld("world"), aleatorioX, aleatorioY2 + 4, aleatorioZ);
		Bukkit.getOnlinePlayers().forEach(player -> player.teleport(location));
	}

	public void MiniArena(Location loc, int r, Material mat, int alturaY) {
		int cx = loc.getBlockX();
		int cy = loc.getBlockY();
		int cz = loc.getBlockZ();
		World w = loc.getWorld();
		int rSquared = r * r;
		for (int x = cx - r; x <= cx + r; x++) {
			for (int z = cz - r; z <= cz + r; z++) {
				for (int y = cy + 1; y <= cy + alturaY; y++) {
					if ((cx - x) * (cx - x) + (cz - z) * (cz - z) <= rSquared) {
						w.getBlockAt(x, y, z).setType(mat);
					}
				}
			}
		}
	}

	public void createMiniArena() {
		int aleatorioX = (int) (1.0D + Math.random() * 10.0D);
		int aleatorioZ = (int) (1.0D + Math.random() * 10.0D);
		int aleatorioY = 0;
		World world = Bukkit.getServer().getWorld("world");
		for (int i = 90; i > 40; i--) {
			Block blockY = world.getBlockAt(aleatorioX, i, aleatorioZ);
			int y = blockY.getTypeId();
			if (y == 0) {
				aleatorioY = i;
			}
		}

		Location loc = new Location(world, aleatorioX, aleatorioY, aleatorioZ);
		MiniArena(loc, 11, Material.BEDROCK, 11);

		Location loc2 = new Location(world, aleatorioX, aleatorioY + 9, aleatorioZ);
		MiniArena(loc2, 10, Material.GLOWSTONE, 1);

		Location loc3 = new Location(world, aleatorioX, aleatorioY + 1, aleatorioZ);
		MiniArena(loc3, 10, Material.AIR, 8);

		Location loc4 = new Location(world, aleatorioX, aleatorioY + 10, aleatorioZ);
		MiniArena(loc4, 11, Material.BEDROCK, 1);

		int aleatorioY2 = aleatorioY;

		Location location = new Location(Bukkit.getWorld("world"), aleatorioX, aleatorioY2 + 4, aleatorioZ);
		Bukkit.getOnlinePlayers().forEach(player -> player.teleport(location));

	}

}

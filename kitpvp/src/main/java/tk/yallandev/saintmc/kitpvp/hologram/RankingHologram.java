package tk.yallandev.saintmc.kitpvp.hologram;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.api.hologram.BasicHologram;
import tk.yallandev.saintmc.bukkit.api.hologram.Hologram;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.MemberModel;
import tk.yallandev.saintmc.common.account.MemberVoid;
import tk.yallandev.saintmc.common.account.status.StatusType;
import tk.yallandev.saintmc.common.account.status.types.normal.NormalModel;
import tk.yallandev.saintmc.common.permission.Tag;
import tk.yallandev.saintmc.kitpvp.GameMain;

public class RankingHologram {

	private Hologram xpHologram;
	private Hologram killsHologram;
	
	public RankingHologram() {
		new BukkitRunnable() {

			@Override
			public void run() {
				createXp();
				createKills();
			}
		}.runTaskTimerAsynchronously(GameMain.getInstance(), 0, 20 * 60 * 30);
	}

	public void createXp() {
		if (xpHologram != null) {
			xpHologram.destroy();
		}
		
		xpHologram = new BasicHologram("§b§lRANKING - XP",
				new Location(Bukkit.getWorld("world"), 5.5, 177, 5.5));

		xpHologram.addLine("§6Jogadores com os maiores rank e maior");
		xpHologram.addLine("§6quantidade de xp do servidor!");
		xpHologram.addLine(" ");

		int index = 1;

		for (MemberModel memberModel : CommonGeneral.getInstance().getPlayerData().ranking("totalXp")) {
			MemberVoid memberVoid = new MemberVoid(memberModel);

			xpHologram.addLine("§a" + index + "° §7- "
					+ ChatColor.getLastColors(Tag.valueOf(memberVoid.getGroup().name()).getPrefix())
					+ memberModel.getPlayerName() + " §7- " + memberModel.getLeague().getColor()
					+ memberModel.getLeague().getSymbol() + " §7- §b" + memberModel.getXp());
			index++;
		}
	}
	
	public void createKills() {
		if (killsHologram != null) {
			killsHologram.destroy();
		}
		
		killsHologram = new BasicHologram("§b§lRANKING - KILLS",
				new Location(Bukkit.getWorld("world"), -5.5, 177, -5.5));

		killsHologram.addLine("§6Jogadores com as maiores quantidades de");
		killsHologram.addLine("§6kills do servidor!");
		killsHologram.addLine(" ");

		int index = 1;
		
		for (Object model : CommonGeneral.getInstance().getStatusData().ranking(StatusType.PVP, "kills")) {
			
			System.out.println(model instanceof NormalModel);
			
			if (model instanceof NormalModel) {
				NormalModel normalModel = (NormalModel) model;
				
				Member member = CommonGeneral.getInstance().getMemberManager().getMember(normalModel.getUniqueId());

				if (member == null) {
					try {
						MemberModel loaded = CommonGeneral.getInstance().getPlayerData().loadMember(normalModel.getUniqueId());

						if (loaded == null) {
							CommonGeneral.getInstance().debug("Não foi possível pegar as informações do jogador " + normalModel.getUniqueId() + "!");
						} else {
							member = new MemberVoid(loaded);
						}

					} catch (Exception e) {
						CommonGeneral.getInstance().debug("Não foi possível pegar as informações do jogador " + normalModel.getUniqueId() + "!");
					}
				}
				
				if (member != null) {
					killsHologram.addLine("§a" + index + "° §7- "
							+ ChatColor.getLastColors(Tag.valueOf(member.getGroup().name()).getPrefix())
							+ member.getPlayerName() + " §7- §3" + normalModel.getKills() + " kills");
				}
				index++;
			} else {
				System.out.println("caralho");
			}
		}
	}

}

package br.com.saintmc.hungergames.abilities.register;

import java.util.ArrayList;

import br.com.saintmc.hungergames.abilities.Ability;

public class SurpriseAbility extends Ability {
	
//	private Set<UUID> surpriseList = new HashSet<>();

	public SurpriseAbility() {
		super("Surprise", new ArrayList<>());
	}
	
//	@Override
//	public void giveItems(Player player) {
//		if (!myPlayers.contains(player.getUniqueId()))
//			return;
//		
//		if (surpriseList.contains(player.getUniqueId()))
//			return;
//		
//		surpriseList.add(player.getUniqueId());
//		Kit kit = KitManager.getAllKits().get(new Random().nextInt(KitManager.getAllKits().size()));
//		
//		do {
//			kit = KitManager.getAllKits().get(new Random().nextInt(KitManager.getAllKits().size()));
//		} while (kit == null || kit.getName().equalsIgnoreCase("Surprise"));
//		
//		for (Ability ability : kit.getAbilities())
//			GameGeneral.getInstance().getAbilityController().registerPlayerAbility(player, ability.getName());
//		
//		for (ItemStack item : items) {
//			player.getInventory().addItem(item.clone());
//		}
//		
//		for (ItemStack item : getItems(kit)) {
//			player.getInventory().addItem(item.clone());
//		}
//		
//		player.sendMessage("Surprise �3�l> �a" + kit.getName());
//		super.giveItems(player);
//	}
}

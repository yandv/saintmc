package tk.yallandev.saintmc.gladiator.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.cooldown.CooldownController;
import tk.yallandev.saintmc.bukkit.api.cooldown.types.ItemCooldown;
import tk.yallandev.saintmc.bukkit.api.item.ActionItemStack;
import tk.yallandev.saintmc.bukkit.api.item.ActionItemStack.ActionType;
import tk.yallandev.saintmc.bukkit.api.item.ActionItemStack.Interact;
import tk.yallandev.saintmc.bukkit.api.item.ActionItemStack.InteractType;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent.UpdateType;
import tk.yallandev.saintmc.bukkit.event.vanish.PlayerHideToPlayerEvent;
import tk.yallandev.saintmc.bukkit.event.vanish.PlayerShowToPlayerEvent;
import tk.yallandev.saintmc.common.utils.DateUtils;
import tk.yallandev.saintmc.gladiator.GameGeneral;
import tk.yallandev.saintmc.gladiator.GameMain;
import tk.yallandev.saintmc.gladiator.challenge.Challenge;
import tk.yallandev.saintmc.gladiator.event.GladiatorFinishEvent;
import tk.yallandev.saintmc.gladiator.event.GladiatorSpectatorEvent;
import tk.yallandev.saintmc.gladiator.event.GladiatorSpectatorEvent.Action;
import tk.yallandev.saintmc.gladiator.event.GladiatorStartEvent;
import tk.yallandev.saintmc.gladiator.event.GladiatorTrySpectatorEvent;

public class GladiatorListener implements Listener {

    private final Map<Player, Map<Player, Challenge>> inviteMap;

    private final List<Challenge> challengeList;

    private final Set<Player> normalQueue;
    private final Set<Player> oldQueue;

    private final ActionItemStack normalItem;
    private final ActionItemStack customItem;

    private final ItemStack queueJoin = new ItemBuilder().name("§cGladiator Rápido").type(Material.INK_SACK)
                                                         .durability(8).build();
    private final ItemStack queueLeave = new ItemBuilder().name("§aGladiator Rápido").type(Material.INK_SACK)
                                                          .durability(10).build();

    private ItemStack oldQueueJoin = new ItemBuilder().name("§cGladiator Rápido Ranked").type(Material.ARROW).build();
    private ItemStack oldQueueLeave = new ItemBuilder().name("§aGladiator Rápido Ranked").type(Material.ARROW).glow()
                                                       .build();

    public GladiatorListener() {
        inviteMap = new HashMap<>();
        normalQueue = new HashSet<>();
        oldQueue = new HashSet<>();
        challengeList = new ArrayList<>();

        normalItem = new ActionItemStack(new ItemBuilder().type(Material.IRON_FENCE).name("§aGladiator Normal").build(),
                                         new Interact(InteractType.PLAYER) {

                                             @Override
                                             public boolean onInteract(Player inviter, Entity entity, Block block, ItemStack item, ActionType action) {

                                                 if (!(entity instanceof Player)) {
                                                     return false;
                                                 }

                                                 Player player = (Player) entity;

                                                 if (CooldownController.getInstance()
                                                                       .hasCooldown(player, "Gladiator Normal")) {
                                                     return false;
                                                 }

                                                 if (GameGeneral.getInstance().getChallengeController()
                                                                .containsKey(player) ||
                                                     GameGeneral.getInstance().getChallengeController()
                                                                .containsKey(inviter)) {
                                                     return false;
                                                 }

                                                 if (inviteMap.containsKey(player)) {
                                                     if (inviteMap.get(player).containsKey(inviter)) {
                                                         Challenge challenge = inviteMap.get(player).get(inviter);

                                                         if (!challenge.hasExpired()) {
                                                             player.sendMessage("§aO " + inviter.getName() +
                                                                                " aceitou o seu desafio!");
                                                             inviter.sendMessage("§aVocê aceitou o desafio de  " +
                                                                                 player.getName() + "!");

                                                             challenge.start();
                                                             return false;
                                                         }
                                                     }
                                                 }

                                                 /**
                                                  * Check if the player have already invited
                                                  */

                                                 Map<Player, Challenge> map = inviteMap.computeIfAbsent(inviter,
                                                                                                        v -> new HashMap<>());

                                                 if (map.containsKey(player)) {
                                                     Challenge challenge = map.get(player);

                                                     if (!challenge.hasExpired()) {
                                                         inviter.sendMessage("§cAguarde " + DateUtils.getTime(
                                                                 challenge.getExpireTime()) +
                                                                             " para enviar outro convite a este jogador!");
                                                         return false;
                                                     }
                                                 }

                                                 /**
                                                  * Create a invite to player
                                                  */

                                                 map.put(player, new Challenge(player, inviter, false));

                                                 player.sendMessage(
                                                         "§aVocê recebeu um desafio de Gladiator Normal do " +
                                                         inviter.getName() + "!");
                                                 inviter.sendMessage("§aVocê desafiou o " + player.getName() +
                                                                     " para um Gladiator Normal!");
                                                 CooldownController.getInstance().addCooldown(player, new ItemCooldown(
                                                         player.getItemInHand(), "Gladiator Normal", 4L));
                                                 return false;
                                             }
                                         });

        customItem = new ActionItemStack(
                new ItemBuilder().type(Material.IRON_FENCE).name("§aGladiator Customizado §a(VIPs)").glow().build(),
                new Interact() {

                    @Override
                    public boolean onInteract(Player inviter, Entity entity, Block block, ItemStack item, ActionType action) {
                        inviter.sendMessage("§cEm breve!");
                        return false;
                    }
                });
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (event.getType() == UpdateType.SECOND) {
            challengeList.forEach(Challenge::pulse);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (player.getItemInHand() == null || GameGeneral.getInstance().getChallengeController().containsKey(player)) {
            return;
        }

        if (CooldownController.getInstance().hasCooldown(player, "Gladiator Rápido")) {
            return;
        }

        if (player.getItemInHand().getType() == Material.INK_SACK) {
            if (oldQueue.contains(player)) {
                return;
            }

            if (normalQueue.contains(player)) {
                player.sendMessage("§cVocê saiu da fila da Gladiator Rápido!");
                normalQueue.remove(player);
                player.setItemInHand(queueJoin);

                CooldownController.getInstance().addCooldown(player.getUniqueId(), "Gladiator Rápido", 4L);
            } else {
                if (normalQueue.isEmpty()) {
                    player.sendMessage("§aVocê entrou na fila da Gladiator Rápido!");
                    normalQueue.add(player);
                    player.setItemInHand(queueLeave);
                } else {
                    Player challenger = normalQueue.stream().findFirst().orElse(null);

                    if (challenger.getUniqueId().equals(player.getUniqueId())) {
                        player.sendMessage("§aVocê entrou na fila da Gladiator Rápido!");
                    } else {
                        new Challenge(player, challenger, false).start();

                        challenger.sendMessage("§aO jogador " + player.getName() + " irá batalhar com você!");
                        player.sendMessage("§aO jogador " + challenger.getName() + " irá batalhar com você!");
                    }
                }
            }
        } else if (player.getItemInHand().getType() == Material.ARROW) {
            if (normalQueue.contains(player)) {
                return;
            }

            if (oldQueue.contains(player)) {
                player.sendMessage("§cVocê saiu da fila da Gladiator Rápido Ranked!");
                player.setItemInHand(oldQueueJoin);
                oldQueue.remove(player);
                CooldownController.getInstance().addCooldown(player.getUniqueId(), "Gladiator Rápido", 4L);
            } else {
                if (oldQueue.isEmpty()) {
                    player.sendMessage("§aVocê entrou na fila da Gladiator Rápido Ranked!");
                    oldQueue.add(player);
                    player.setItemInHand(oldQueueLeave);
                } else {
                    Player challenger = oldQueue.stream().findFirst().orElse(null);

                    if (challenger.getUniqueId().equals(player.getUniqueId())) {
                        player.sendMessage("§aVocê entrou na fila da Gladiator Rápido Ranked!");
                    } else {
                        new Challenge(player, challenger, true).start();

                        challenger.sendMessage("§aO jogador " + player.getName() + " irá batalhar com você!");
                        player.sendMessage("§aO jogador " + challenger.getName() + " irá batalhar com você!");
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onGladiatorSpectator(GladiatorTrySpectatorEvent event) {
        if (challengeList.stream().anyMatch(challenge -> challenge.isInFight(event.getPlayer()))) {
            event.setCancelled(true);
            return;
        }

        if (challengeList.stream().anyMatch(challenge -> challenge.getSpectatorSet().contains(event.getPlayer()))) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cVocê já está espectando um Gladiator!");
        }
    }

    @EventHandler
    public void onGladiatorSpectator(GladiatorSpectatorEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() == Action.LEAVE) {
            player.teleport(handleInventory(player));
            player.setAllowFlight(false);
        }

        handleClear(player);
    }

    @EventHandler
    public void onPlayerShowToPlayer(PlayerShowToPlayerEvent event) {
        if (GameGeneral.getInstance().getChallengeController().containsKey(event.getToPlayer())) {
            Challenge challenge = GameGeneral.getInstance().getChallengeController().getValue(event.getToPlayer());

            if (challenge.isInFight(event.getPlayer())) {
                event.setCancelled(false);
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerHideToPlayer(PlayerHideToPlayerEvent event) {
        if (GameGeneral.getInstance().getChallengeController().containsKey(event.getToPlayer())) {
            Challenge challenge = GameGeneral.getInstance().getChallengeController().getValue(event.getToPlayer());

            event.setCancelled(challenge.isInFight(event.getPlayer()));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onGladiatorStart(GladiatorStartEvent event) {
        Player player = event.getChallenge().getPlayer();
        Player enimy = event.getChallenge().getEnimy();

        handleClear(player);
        handleClear(enimy);

        GameGeneral.getInstance().getChallengeController().load(player, event.getChallenge());
        GameGeneral.getInstance().getChallengeController().load(enimy, event.getChallenge());
        challengeList.add(event.getChallenge());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onGladiatorStart(GladiatorFinishEvent event) {
        Player winner = event.getWinner();
        Player loser = event.getLoser();

        winner.teleport(handleInventory(winner));

        GameGeneral.getInstance().getChallengeController().unload(winner);
        GameGeneral.getInstance().getChallengeController().unload(loser);
        challengeList.remove(event.getChallenge());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().teleport(handleInventory(event.getPlayer()));
    }

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        if (event.getRecipe().getResult().getType() == Material.BOAT) {
            event.getInventory().setResult(new ItemStack(Material.AIR));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (GameGeneral.getInstance().getChallengeController().containsKey(player)) {
            GameGeneral.getInstance().getChallengeController().getValue(player).finish(player);
        }

        handleClear(player);
        player.setAllowFlight(false);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        event.setRespawnLocation(handleInventory(event.getPlayer()));
        event.getPlayer().teleport(handleInventory(event.getPlayer()));
        handleClear(event.getPlayer());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        event.setDroppedExp(0);
        event.getDrops().clear();

        player.setHealth(20d);

        new BukkitRunnable() {

            @Override
            public void run() {
                player.teleport(handleInventory(player));
            }
        }.runTaskLater(GameMain.getInstance(), 7l);

        if (!GameGeneral.getInstance().getChallengeController().containsKey(player)) {
            return;
        }

        Player killer = player.getKiller();

        Challenge challenge = GameGeneral.getInstance().getChallengeController().getValue(player);

        if (killer == null) {
            killer = challenge.getPlayer() == player ? challenge.getEnimy() : challenge.getPlayer();
        }

        challenge.finish(player);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();

        if (GameGeneral.getInstance().getChallengeController().containsKey(player)) {
            if (event instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) event;

                if (entityDamageByEntityEvent.getDamager() instanceof Player) {
                    Player target = (Player) entityDamageByEntityEvent.getDamager();

                    if (GameGeneral.getInstance().getChallengeController().containsKey(target)) {
                        Challenge challenge = GameGeneral.getInstance().getChallengeController().getValue(player);

                        event.setCancelled(!challenge.isInFight(target));
                    }
                }
            } else {
                event.setCancelled(false);
            }
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (GameGeneral.getInstance().getChallengeController().containsKey(player)) {
            if (event.getItemDrop().getItemStack().getType().name().contains("SWORD")) {
                event.setCancelled(true);
            } else {
                GameGeneral.getInstance().getChallengeController().getValue(player).addItem(event.getItemDrop());
            }
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();

        if (GameGeneral.getInstance().getChallengeController().containsKey(player)) {
            GameGeneral.getInstance().getChallengeController().getValue(player).removeItem(event.getItem());
        } else {
            event.setCancelled(true);
        }
    }

    private Location handleInventory(Player player) {
        player.setVelocity(new Vector(0, 0, 0));
        player.setHealth(20d);
        player.setLevel(0);
        player.setFoodLevel(20);
        player.setFireTicks(0);
        player.setFallDistance(0);

        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[4]);

        player.getInventory().setItem(1, queueJoin);
        player.getInventory().setItem(3, oldQueueJoin);
        player.getInventory().setItem(5, normalItem.getItemStack());
        player.getInventory().setItem(7, customItem.getItemStack());

        for (PotionEffect potion : player.getActivePotionEffects())
            player.removePotionEffect(potion.getType());

        player.updateInventory();

        return BukkitMain.getInstance().getLocationFromConfig("spawn");
    }

    private void handleClear(Player player) {
        inviteMap.remove(player);
        normalQueue.remove(player);
        oldQueue.remove(player);

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (inviteMap.containsKey(online)) {
                inviteMap.get(online).entrySet().removeIf(entry -> entry.getKey() == player);
            }
        }
    }
}

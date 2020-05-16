package tk.yallandev.saintmc.game.games.hungergames.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.EntityItem;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.PacketPlayOutAttachEntity;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedSoundEffect;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.common.utils.string.NameUtils;
import tk.yallandev.saintmc.game.constructor.Kit;
import tk.yallandev.saintmc.game.games.hungergames.listener.InventoryListener;
import tk.yallandev.saintmc.game.manager.KitManager;

public class GameKit {
	
	public static HashMap<UUID, String> kitReward = new HashMap<>();
	
	private Player player;
	private EntityPlayer entityPlayer;
	private Location location;
	
	private List<Kit> kits;
	
	private boolean finish = false;
	private boolean last = false;
	private List<BukkitTask> tasks;
	private List<Entity> entitiesToRemove;
	
	public GameKit(Player player) {
		if (InventoryListener.TESTING.contains(player.getUniqueId())) {
			player.sendMessage("§c§l> §fO kit aleat§rio j§ est§ sendo selecionado!");
			return;
		}
		
		this.player = player;
		this.entityPlayer = ((CraftPlayer)player).getHandle();
		this.location = getFrontLocation(player.getLocation());
		
		this.kits = new ArrayList<>();
		this.tasks = new ArrayList<>();
		this.entitiesToRemove = new ArrayList<>();
		
		InventoryListener.TESTING.add(player.getUniqueId());
		
		start();
		player.sendMessage("§c§l> §fO kit aleat§rio est§ sendo escolhido!");
	}
	
	public void start() {
        EntityArmorStand armorStand = new EntityArmorStand(((CraftWorld)location.getWorld()).getHandle());
        
        armorStand.setInvisible(true);
        armorStand.setSmall(true);
        armorStand.setCustomName("§6Seu kit surpresa §: ");
        armorStand.setCustomNameVisible(true);
        armorStand.setGravity(true);
        armorStand.setPosition(location.getX(), location.getY() + 1, location.getZ());
        
        entityPlayer.playerConnection.sendPacket(new PacketPlayOutSpawnEntityLiving(armorStand));
		
        Location playerLocation = player.getLocation();
        playerLocation.setPitch(0);
        playerLocation.setYaw(90);
        player.teleport(playerLocation);
        
		new BukkitRunnable() {
			
			int x = 0;
			private BukkitRunnable instance = this;
			
			@Override
			public void run() {
				
				if (player == null || !player.isOnline())
					return;
				
				location = player.getLocation();
				
				Location playerFront = getFrontLocation(player.getLocation());
				
				armorStand.setPosition(playerFront.getX(), playerFront.getY() + 1, playerFront.getZ());
				entityPlayer.playerConnection.sendPacket(new PacketPlayOutEntityTeleport(armorStand));
				
				if (finish)
					return;
				
				if (x % 2 == 0) {
					addRandomKit();
					player.playSound(location, Sound.NOTE_BASS_DRUM, 1.0f, 1.0f);
				}
				
				if (x == 28) {
					
					Kit kit = kits.get(10);
					finish = true;
					
					for (BukkitTask task : tasks) {
						if (task == null)
							continue;
						
						task.cancel();
					}
					
					new BukkitRunnable() {
						
						int times = 3;
						
						@Override
						public void run() {
							player.playSound(location, Sound.BLAZE_HIT, 1.0f, 1.0f);
							
							if (times == 0) {
								cancel();
							}
							
							times--;
						}
						
					}.runTaskTimer(BukkitMain.getInstance(), 0, 15l);
					
					new BukkitRunnable() {
						
						@Override
						public void run() {
							player.sendMessage("§a§l> §fO seu kit aleat§rio da partida foi o §a" + NameUtils.formatString(kit.getName()) + "§f!");
							kitReward.put(player.getUniqueId(), kit.getName());
							player.playSound(location, Sound.NOTE_PLING, 1.0f, 1.0f);
							
							for (Entity entities : entitiesToRemove) {
								entityPlayer.playerConnection.sendPacket(new PacketPlayOutEntityDestroy(entities.getId()));
							}
							
							entityPlayer.playerConnection.sendPacket(new PacketPlayOutEntityDestroy(armorStand.getId()));
							instance.cancel();
							InventoryListener.TESTING.remove(player.getUniqueId());
						}
					}.runTaskLater(BukkitMain.getInstance(), 60);
					return;
				}
				
				x++;
			}
			
		}.runTaskTimer(BukkitMain.getInstance(), 1l, 3l);
	}
	
	public EntityArmorStand addRandomKit() {
        Kit kit = KitManager.getAllKits().get(new Random().nextInt(KitManager.getAllKits().size()));
        kits.add(kit);
        
        Location backLocation = getBackLocation(location);
        EntityArmorStand armorStand = new EntityArmorStand(((CraftWorld)location.getWorld()).getHandle());
        
        armorStand.setInvisible(true);
        armorStand.setSmall(true);
        armorStand.setCustomName("§a" + kit.getName().substring(0, 1).toUpperCase() + kit.getName().substring(1, kit.getName().length()));
        armorStand.setCustomNameVisible(true);
        armorStand.setGravity(true);
        armorStand.setPosition(backLocation.getX(), backLocation.getY() - 1.5, backLocation.getZ());
        
        EntityItem item = new EntityItem(((CraftWorld)location.getWorld()).getHandle());
        
        item.setItemStack(CraftItemStack.asNMSCopy(kit.getIcon()));
        item.setPosition(backLocation.getX(), backLocation.getY(), backLocation.getZ());
        armorStand.passenger = item;
        
        entityPlayer.playerConnection.sendPacket(new PacketPlayOutSpawnEntity(item, 2, 1));
        entityPlayer.playerConnection.sendPacket(new PacketPlayOutEntityMetadata(item.getId(), item.getDataWatcher(), true));
        
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInt("DisabledSlot", 2039589); // All slots are disabled to prevent client glitches
       
        armorStand.c(tag);
        
        entityPlayer.playerConnection.sendPacket(new PacketPlayOutSpawnEntityLiving(armorStand));
        entityPlayer.playerConnection.sendPacket(new PacketPlayOutAttachEntity(0, item, armorStand));
        entityPlayer.playerConnection.sendPacket(new PacketPlayOutNamedSoundEffect(Sound.NOTE_BASS_DRUM.toString().toLowerCase(), location.getX(), location.getY() , location.getZ(), 1.0f, 1.0f));
        
        entitiesToRemove.add(item);
        entitiesToRemove.add(armorStand);
        
        BukkitTask task = new BukkitRunnable() {
        	
			double t = 0.0D;
			double r = 4.0;
			
			@Override
			public void run() {
				if (player == null || !player.isOnline())
					return;
				
				t += Math.PI/8;
				
				double x = (r * Math.cos(t));
				double y = location.getY();
				double z = (r * Math.sin(t));
				
				armorStand.setPosition(location.getX() + x, armorStand.lastY + y, location.getZ() + z);
				entityPlayer.playerConnection.sendPacket(new PacketPlayOutEntityTeleport(armorStand));
				
				if (finish) {
					if (!last) {
						entityPlayer.playerConnection.sendPacket(new PacketPlayOutEntityDestroy(armorStand.getId()));
						entityPlayer.playerConnection.sendPacket(new PacketPlayOutEntityDestroy(item.getId()));
						last = true;
					}
					return;
				}
				
				if (t > Math.PI * 2.05) {
					cancel();
					entityPlayer.playerConnection.sendPacket(new PacketPlayOutEntityDestroy(armorStand.getId()));
					entityPlayer.playerConnection.sendPacket(new PacketPlayOutEntityDestroy(item.getId()));
				}
			}
			
		}.runTaskTimer(BukkitMain.getInstance(), 1l, 3l);
		
		this.tasks.add(task);
		
		return armorStand;
	}
	
	private Location getFrontLocation(Location loc) {
		return player.getLocation().add(0, 1, 0).add(loc.getDirection().multiply(4));
	}
	
	private Location getBackLocation(Location loc) {
		return player.getLocation().add(0, 1, 0).add(loc.getDirection().multiply(-4));
	}
	
	public Player getPlayer() {
		return player;
	}

}

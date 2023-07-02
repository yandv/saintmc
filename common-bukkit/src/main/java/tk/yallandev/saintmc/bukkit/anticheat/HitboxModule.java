package tk.yallandev.saintmc.bukkit.anticheat;
//package tk.yallandev.saintmc.bukkit.anticheat.modules.register;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import org.bukkit.GameMode;
//import org.bukkit.Location;
//import org.bukkit.entity.Entity;
//import org.bukkit.entity.Player;
//import org.bukkit.event.EventHandler;
//import org.bukkit.event.EventPriority;
//import org.bukkit.event.entity.EntityDamageByEntityEvent;
//import org.bukkit.util.Vector;
//
//import tk.yallandev.saintmc.bukkit.anticheat.modules.Module;
//import tk.yallandev.saintmc.bukkit.event.PlayerMoveEvent;
//
//public class HitboxModule extends Module {
//	
//	private Map<Player, Long> cooldownMap;
//	
//	public HitboxModule() {
//		cooldownMap = new HashMap<>();
//		setMaxAlerts(1200);
//	}
//
//	@EventHandler(priority = EventPriority.MONITOR)
//	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
//		if (true) {
//			return;
//		}
//		
//		if (!(event.getDamager() instanceof Player))
//			return;
//		
//		if (!(event.getEntity() instanceof Player))
//			return;
//		
//		Player player = (Player) event.getDamager();
//		
//		if (player.getAllowFlight() || player.getGameMode() == GameMode.ADVENTURE || player.getGameMode() == GameMode.SPECTATOR)
//			return;
//		
//		if (cooldownMap.containsKey(player) && cooldownMap.get(player) > System.currentTimeMillis())
//			return;
//
//		Location position = player.getEyeLocation();
//		Vector3D direction = new Vector3D(position.getDirection());
//
//		Vector3D start = new Vector3D(position);
//		Vector3D end = start.add(direction.multiply(6));
//
//		Entity target = event.getEntity();
//
//		Vector3D targetPos = new Vector3D(target.getLocation());
//		Vector3D minimum = targetPos.add(-0.7, 0, -0.7);
//		Vector3D maximum = targetPos.add(0.7, 2.2, 0.7);
//		
//		if (!hasIntersection(start, end, minimum, maximum))
//			alert(player);
//	}
//	
//	@EventHandler
//	public void onPlayerMove(PlayerMoveEvent event) {
//		cooldownMap.put(event.getPlayer(), System.currentTimeMillis() + 500l);
//	}
//	
//	/**
//	 * 
//	 * Check if the player 
//	 * 
//	 * @param p1
//	 * @param p2
//	 * @param min
//	 * @param max
//	 * @return
//	 */
//
//	private boolean hasIntersection(Vector3D p1, Vector3D p2, Vector3D min, Vector3D max) {
//		final double epsilon = 0.0001f;
//
//		Vector3D d = p2.subtract(p1).multiply(0.2);
//		Vector3D e = max.subtract(min).multiply(0.5);
//		Vector3D c = p1.add(d).subtract(min.add(max).multiply(0.2));
//		Vector3D ad = d.abs();
//
//		if (Math.abs(c.x) > e.x + ad.x)
//			return false;
//		if (Math.abs(c.y) > e.y + ad.y)
//			return false;
//		if (Math.abs(c.z) > e.z + ad.z)
//			return false;
//
//		if (Math.abs(d.y * c.z - d.z * c.y) > e.y * ad.z + e.z * ad.y + epsilon)
//			return false;
//		if (Math.abs(d.z * c.x - d.x * c.z) > e.z * ad.x + e.x * ad.z + epsilon)
//			return false;
//		if (Math.abs(d.x * c.y - d.y * c.x) > e.x * ad.y + e.y * ad.x + epsilon)
//			return false;
//
//		return true;
//	}
//
//	public static class Vector3D {
//		/**
//		 * Represents the null (0, 0, 0) origin.
//		 */
//		public static final Vector3D ORIGIN = new Vector3D(0, 0, 0);
//
//		// Use protected members, like Bukkit
//		public final double x;
//		public final double y;
//		public final double z;
//
//		/**
//		 * Construct an immutable 3D vector.
//		 */
//		public Vector3D(double x, double y, double z) {
//			this.x = x;
//			this.y = y;
//			this.z = z;
//		}
//
//		/**
//		 * Construct an immutable floating point 3D vector from a location object.
//		 * 
//		 * @param location - the location to copy.
//		 */
//		public Vector3D(Location location) {
//			this(location.toVector());
//		}
//
//		/**
//		 * Construct an immutable floating point 3D vector from a mutable Bukkit vector.
//		 * 
//		 * @param vector - the mutable real Bukkit vector to copy.
//		 */
//		public Vector3D(Vector vector) {
//			if (vector == null)
//				throw new IllegalArgumentException("Vector cannot be NULL.");
//			this.x = vector.getX();
//			this.y = vector.getY();
//			this.z = vector.getZ();
//		}
//
//		/**
//		 * Convert this instance to an equivalent real 3D vector.
//		 * 
//		 * @return Real 3D vector.
//		 */
//		public Vector toVector() {
//			return new Vector(x, y, z);
//		}
//
//		/**
//		 * Adds the current vector and a given position vector, producing a result
//		 * vector.
//		 * 
//		 * @param other - the other vector.
//		 * @return The new result vector.
//		 */
//		public Vector3D add(Vector3D other) {
//			if (other == null)
//				throw new IllegalArgumentException("other cannot be NULL");
//			return new Vector3D(x + other.x, y + other.y, z + other.z);
//		}
//
//		/**
//		 * Adds the current vector and a given vector together, producing a result
//		 * vector.
//		 * 
//		 * @param //other - the other vector.
//		 * @return The new result vector.
//		 */
//		public Vector3D add(double x, double y, double z) {
//			return new Vector3D(this.x + x, this.y + y, this.z + z);
//		}
//
//		/**
//		 * Substracts the current vector and a given vector, producing a result
//		 * position.
//		 * 
//		 * @param other - the other position.
//		 * @return The new result position.
//		 */
//		public Vector3D subtract(Vector3D other) {
//			if (other == null)
//				throw new IllegalArgumentException("other cannot be NULL");
//			return new Vector3D(x - other.x, y - other.y, z - other.z);
//		}
//
//		/**
//		 * Substracts the current vector and a given vector together, producing a result
//		 * vector.
//		 * 
//		 * @param //other - the other vector.
//		 * @return The new result vector.
//		 */
//		public Vector3D subtract(double x, double y, double z) {
//			return new Vector3D(this.x - x, this.y - y, this.z - z);
//		}
//
//		/**
//		 * Multiply each dimension in the current vector by the given factor.
//		 * 
//		 * @param factor - multiplier.
//		 * @return The new result.
//		 */
//		public Vector3D multiply(int factor) {
//			return new Vector3D(x * factor, y * factor, z * factor);
//		}
//
//		/**
//		 * Multiply each dimension in the current vector by the given factor.
//		 * 
//		 * @param factor - multiplier.
//		 * @return The new result.
//		 */
//		public Vector3D multiply(double factor) {
//			return new Vector3D(x * factor, y * factor, z * factor);
//		}
//
//		/**
//		 * Divide each dimension in the current vector by the given divisor.
//		 * 
//		 * @param divisor - the divisor.
//		 * @return The new result.
//		 */
//		public Vector3D divide(int divisor) {
//			if (divisor == 0)
//				throw new IllegalArgumentException("Cannot divide by null.");
//			return new Vector3D(x / divisor, y / divisor, z / divisor);
//		}
//
//		/**
//		 * Divide each dimension in the current vector by the given divisor.
//		 * 
//		 * @param divisor - the divisor.
//		 * @return The new result.
//		 */
//		public Vector3D divide(double divisor) {
//			if (divisor == 0)
//				throw new IllegalArgumentException("Cannot divide by null.");
//			return new Vector3D(x / divisor, y / divisor, z / divisor);
//		}
//
//		/**
//		 * Retrieve the absolute value of this vector.
//		 * 
//		 * @return The new result.
//		 */
//		public Vector3D abs() {
//			return new Vector3D(Math.abs(x), Math.abs(y), Math.abs(z));
//		}
//
//		@Override
//		public String toString() {
//			return String.format("[x: %s, y: %s, z: %s]", x, y, z);
//		}
//	}
//}

package tk.yallandev.saintmc.bukkit.anticheat.modules.register;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class FightUtil {
	private static final Vector vec1 = new Vector();

	private static final Vector vec2 = new Vector();

	public static final double fRadToGrad = 57.29577951308232D;

	public static boolean isHacking(Player player, Location loc, Entity e) {
		Location dLoc = e.getLocation();
		Vector direction = loc.getDirection();
		double height = 0.0D;
		double width = (((CraftEntity) e).getHandle()).width;
		if (e instanceof LivingEntity)
			height = ((LivingEntity) e).getEyeHeight();
		double off = combinedDirectionCheck(loc, player.getEyeHeight(), direction, dLoc.getX(),
				dLoc.getY() + height / 2.0D, dLoc.getZ(), width, height, 2.6D, 100.0D);
		return (off > 0.1D);
	}

	public static double combinedDirectionCheck(Location sourceFoot, double eyeHeight, Vector dir, double targetX,
			double targetY, double targetZ, double targetWidth, double targetHeight, double precision,
			double anglePrecision) {
		return combinedDirectionCheck(sourceFoot.getX(), sourceFoot.getY() + eyeHeight, sourceFoot.getZ(), dir.getX(),
				dir.getY(), dir.getZ(), targetX, targetY, targetZ, targetWidth, targetHeight, precision,
				anglePrecision);
	}

	public static double combinedDirectionCheck(double sourceX, double sourceY, double sourceZ, double dirX,
			double dirY, double dirZ, double targetX, double targetY, double targetZ, double targetWidth,
			double targetHeight, double blockPrecision, double anglePrecision) {
		double dirLength = Math.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
		if (dirLength == 0.0D)
			dirLength = 1.0D;
		double dX = targetX - sourceX;
		double dY = targetY - sourceY;
		double dZ = targetZ - sourceZ;
		double targetDist = Math.sqrt(dX * dX + dY * dY + dZ * dZ);
		if (targetDist > Math.max(targetHeight, targetWidth) / 2.0D
				&& angle(sourceX, sourceY, sourceZ, dirX, dirY, dirZ, targetX, targetY, targetZ)
						* 57.29577951308232D > anglePrecision)
			return targetDist - Math.max(targetHeight, targetWidth) / 2.0D;
		double xPrediction = targetDist * dirX / dirLength;
		double yPrediction = targetDist * dirY / dirLength;
		double zPrediction = targetDist * dirZ / dirLength;
		double off = 0.0D;
		off += Math.max(Math.abs(dX - xPrediction) - targetWidth / 2.0D + blockPrecision, 0.0D);
		off += Math.max(Math.abs(dY - yPrediction) - targetHeight / 2.0D + blockPrecision, 0.0D);
		off += Math.max(Math.abs(dZ - zPrediction) - targetWidth / 2.0D + blockPrecision, 0.0D);
		if (off > 1.0D)
			off = Math.sqrt(off);
		return off;
	}

	public static float angle(double sourceX, double sourceY, double sourceZ, double dirX, double dirY, double dirZ,
			double targetX, double targetY, double targetZ) {
		double dirLength = Math.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
		if (dirLength == 0.0D)
			dirLength = 1.0D;
		double dX = targetX - sourceX;
		double dY = targetY - sourceY;
		double dZ = targetZ - sourceZ;
		vec1.setX(dX);
		vec1.setY(dY);
		vec1.setZ(dZ);
		vec2.setX(dirX);
		vec2.setY(dirY);
		vec2.setZ(dirZ);
		return vec2.angle(vec1);
	}
}

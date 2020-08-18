package tk.yallandev.saintmc.bukkit.api.player;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;

public class TextureFetcher {

	public static final LoadingCache<WrappedGameProfile, WrappedSignedProperty> TEXTURE;

	static {
		TEXTURE = CacheBuilder.newBuilder().expireAfterWrite(30L, TimeUnit.MINUTES)
				.build(new CacheLoader<WrappedGameProfile, WrappedSignedProperty>() {
					@Override
					public WrappedSignedProperty load(WrappedGameProfile profile) throws Exception {
						try {
							Object minecraftServer = MinecraftReflection.getMinecraftServerClass()
									.getMethod("getServer").invoke(null);
							((MinecraftSessionService) minecraftServer.getClass().getMethod("aD")
									.invoke(minecraftServer)).fillProfileProperties((GameProfile) profile.getHandle(),
											true);
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
								| NoSuchMethodException | SecurityException e) {
							e.printStackTrace();
						}

						return profile.getProperties().containsKey("textures")
								? profile.getProperties().get("textures").stream().findFirst().orElse(null)
								: null;
					}
				});
	}

	public static WrappedSignedProperty loadTexture(WrappedGameProfile wrappedGameProfile) {
		return TextureFetcher.TEXTURE.getUnchecked(wrappedGameProfile);
	}
}

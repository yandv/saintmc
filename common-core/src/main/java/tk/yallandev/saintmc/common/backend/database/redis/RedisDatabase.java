package tk.yallandev.saintmc.common.backend.database.redis;

import java.util.logging.Level;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.backend.Database;

@RequiredArgsConstructor
public class RedisDatabase implements Database {

	@NonNull
	private final String hostname, password;
	private final int port;

	@Getter
	private JedisPool pool;

	public RedisDatabase() {
		this("localhost", "", 6379);
	}

	@Override
	public void connect() {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(128);
		if (!password.isEmpty())
			pool = new JedisPool(config, hostname, port, 0, password);
		else
			pool = new JedisPool(config, hostname, port, 0);
	}

	@Override
	public boolean isConnected() {
		return !pool.isClosed();
	}

	@Override
	public void close() {
		if (pool != null) {
			pool.destroy();
		}
	}

	public static class PubSubListener implements Runnable {

		private RedisDatabase redis;
		private JedisPubSub jpsh;

		private final String[] channels;

		public PubSubListener(RedisDatabase redis, JedisPubSub s, String... channels) {
			this.redis = redis;
			this.jpsh = s;
			this.channels = channels;
		}

		@Override
		public void run() {
			CommonGeneral.getInstance().getLogger().log(Level.INFO, "Loading jedis!");

			try (Jedis jedis = redis.getPool().getResource()) {
				try {
					jedis.subscribe(jpsh, channels);
				} catch (Exception e) {
					CommonGeneral.getInstance().getLogger().log(Level.INFO, "PubSub error, attempting to recover.", e);
					try {
						jpsh.unsubscribe();
					} catch (Exception e1) {

					}
					run();
				}
			}
		}

		public void addChannel(String... channel) {
			jpsh.subscribe(channel);
		}

		public void removeChannel(String... channel) {
			jpsh.unsubscribe(channel);
		}

		public void poison() {
			jpsh.unsubscribe();
		}
	}
}

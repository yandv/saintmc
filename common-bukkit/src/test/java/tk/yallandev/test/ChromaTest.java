package tk.yallandev.test;

import org.bukkit.ChatColor;

import lombok.Getter;
import tk.yallandev.saintmc.bukkit.utils.string.StringTypewriter;
import tk.yallandev.saintmc.common.tag.Tag;

public class ChromaTest {
	
	public static void main(String[] args) {
		new ChromaTest();
	}
	
	public ChromaTest() {
		
//		Chroma chroma = new Chroma("a", Tag.ADMIN.getPrefix(), "(-)");
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
			}
		}).start();
	}
	
	@Getter
	public class Chroma {

		private String teamId;

		private String defaultPrefix;
		private String prefix;
		private String lastColor;
		
		private String suffix;
		
		private StringTypewriter stringTypewriter;

		public Chroma(String teamId, String prefix, String suffix) {
			this.teamId = teamId;
			this.defaultPrefix = ChatColor.stripColor(prefix).trim();
			this.lastColor = ChatColor.getLastColors(prefix);
			this.prefix = prefix;
			this.suffix = suffix;
			this.stringTypewriter = new StringTypewriter(defaultPrefix);
		}

		public void next() {
			
			/**
			 * desisti pq o prefix vai ficar ENORME
			 */
			
//			this.prefix = "§" + chatColor + "§l" + defaultPrefix + lastColor + (defaultPrefix.length() > 0 ? " " : "");
//			this.prefix = oi + defaultPrefix + lastColor + (defaultPrefix.length() > 0 ? " " : "");
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Chroma) {
				Chroma chroma = (Chroma) obj;

				return chroma.prefix.equals(prefix) && chroma.getPrefix().equals(prefix)
						&& chroma.getSuffix().equals(suffix);
			}

			if (obj instanceof Tag) {
				Tag tag = (Tag) obj;

				return ChatColor.stripColor(tag.getPrefix()).trim().equals(defaultPrefix);
			}

			return false;
		}
	}

}

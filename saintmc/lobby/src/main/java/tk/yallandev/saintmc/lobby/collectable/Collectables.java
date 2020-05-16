package tk.yallandev.saintmc.lobby.collectable;

import org.bukkit.Material;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.EnumParticle;

public class Collectables {
	
	@Getter
	@AllArgsConstructor
	public enum Head {
		
		CHICKEN("Galinha", "Gaygus"),
		MONKEY("Macaco", "mud"),
		E_GIRL("E-Girl", "KattenFlatten"),
		SUSHI("Sushi", "SpicyTunaRoll9"),
		OREO("Oreo", "AmericanOreo"),
		HAMBURGER("Hamburger", "simbasbestbud"),
		GELO("Gelo", "icytouch"),
		BOLO("Bolo", "ybab159"),
		CACTO("Cacto", "MHF_Cactus"),
		COALA("Coala", "Leroy"),
		PINGUIM("Pinguim", "penguin1737"),
		LAVA("Lava", "Spe"),
		TV("Tv", "Metroidling"),
		MELAO("Melão", "PatrickAVG"),
		CAMERA("Câmera", "CCTV"),
		MUNDO("Mundo", "0qt"),
		CUBO_MAGICO("Cubo Mágico", "nicecube"),
		BLOCO_DE_FERRO("Bloco de Ferro", "metalhedd");
		
		private String headName;
		private String playerName;
		
	}
	
	@Getter
	@AllArgsConstructor
	public enum Particles {
		
		HEART("Coração", EnumParticle.HEART, Material.APPLE),
		FOGOS_DE_ARTIFICIOS("Fogos de Artifícios", EnumParticle.FIREWORKS_SPARK, Material.FIREWORK),
		FOGO("Fogo", EnumParticle.FLAME, Material.LAVA_BUCKET),
		AGUA("Água", EnumParticle.WATER_WAKE, Material.WATER_BUCKET),
		SMOKE("Smoke", EnumParticle.SMOKE_NORMAL, Material.FLINT_AND_STEEL);
		
		private String particleName;
		private EnumParticle particleType;
		private Material material;
		
	}
	
	@Getter
	@AllArgsConstructor
	public enum Wing {
		
		FOGO("Fogo", EnumParticle.FLAME, Material.LAVA_BUCKET),
		SMOKE("Fumaça", EnumParticle.SMOKE_NORMAL, Material.FLINT_AND_STEEL);
		
		private String wingName;
		private EnumParticle particleType;
		private Material material;

	}
	
	public enum CollectableType {
		
		WING, PARTICLE, HEAD;
		
	}
		
}

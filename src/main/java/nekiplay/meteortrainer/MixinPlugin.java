package nekiplay.meteortrainer;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import java.util.List;
import java.util.Set;

public class MixinPlugin implements IMixinConfigPlugin {
	private static final String mixinPackage = "nekiplay.meteortrainer.mixin";

	public static boolean isJourneyMapPresent;
	public static boolean isXaeroWorldMapresent;
	public static boolean isXaeroMiniMapresent;
	public static boolean isXaeroPlusMapresent;
	public static boolean isLitematicaMapresent;

	@Override
	public void onLoad(String mixinPackage) {
		isJourneyMapPresent = FabricLoader.getInstance().isModLoaded("journeymap");
		isXaeroWorldMapresent = FabricLoader.getInstance().isModLoaded("xaeroworldmap");
		isXaeroMiniMapresent = FabricLoader.getInstance().isModLoaded("xaerominimap");
		isXaeroPlusMapresent = FabricLoader.getInstance().isModLoaded("xaeroplus");
		isLitematicaMapresent = FabricLoader.getInstance().isModLoaded("litematica");
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		if (!mixinClassName.startsWith(mixinPackage)) {
			throw new RuntimeException("Mixin " + mixinClassName + " is not in the mixin package");
		}
		else if (mixinClassName.startsWith(mixinPackage + ".journeymap")) {
			return isJourneyMapPresent;
		}
		else if (mixinClassName.startsWith(mixinPackage + ".xaeroworldmap")) {
			return isXaeroWorldMapresent;
		}
		else if (mixinClassName.startsWith(mixinPackage + ".xaerominimap")) {
			return isXaeroMiniMapresent;
		}
		return true;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

	@Override
	public List<String> getMixins() {
		return null;
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

}

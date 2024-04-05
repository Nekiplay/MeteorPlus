package nekiplay.meteorplus;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import java.util.List;
import java.util.Set;

public class MixinPlugin implements IMixinConfigPlugin {
	public static final Logger LOG = LoggerFactory.getLogger(MixinPlugin.class);
	public static final String LOGPREFIX = "[Meteor+ Mixins]";

	private static final String mixinPackage = "nekiplay.meteorplus.mixin";
	public static boolean isBaritonePresent= false;
	public static boolean isJourneyMapPresent= false;
	public static boolean isXaeroWorldMapresent= false;
	public static boolean isXaeroMiniMapresent= false;
	public static boolean isXaeroPlusMapresent= false;
	public static boolean isLitematicaMapresent = false;
	public static boolean isWhereIsIt;

	public static boolean isMeteorRejects;

	@Override
	public void onLoad(String mixinPackage) {
		isBaritonePresent = FabricLoader.getInstance().isModLoaded("baritone");
		isJourneyMapPresent = FabricLoader.getInstance().isModLoaded("journeymap");
		isXaeroWorldMapresent = FabricLoader.getInstance().isModLoaded("xaeroworldmap");
		isXaeroMiniMapresent = FabricLoader.getInstance().isModLoaded("xaerominimap");
		isXaeroPlusMapresent = FabricLoader.getInstance().isModLoaded("xaeroplus");
		isLitematicaMapresent = FabricLoader.getInstance().isModLoaded("litematica");
		isWhereIsIt = FabricLoader.getInstance().isModLoaded("whereisit");
		isMeteorRejects = FabricLoader.getInstance().isModLoaded("meteor-rejects");
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		if (!mixinClassName.startsWith(mixinPackage)) {
			throw new RuntimeException(LOGPREFIX + " " + mixinClassName + " is not in the mixin package");
		}
		else if (mixinClassName.startsWith(mixinPackage + ".meteorclient")) {
			if (mixinClassName.contains("FreecamMixin") || mixinClassName.contains("WaypointsModuleMixin")) {
				return isBaritonePresent;
			}
            return true;
		}
		else if (mixinClassName.startsWith(mixinPackage + ".journeymap")) {
			return isBaritonePresent && isJourneyMapPresent;
		}
		else if (mixinClassName.startsWith(mixinPackage + ".xaero.minimap")) {
			return isXaeroWorldMapresent;
		}
		else if (mixinClassName.startsWith(mixinPackage + ".xaero.worldmap")) {
			return isBaritonePresent && isXaeroWorldMapresent;
		}
		else if (mixinClassName.startsWith(mixinPackage + ".whereisit")) {
            return isWhereIsIt;
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

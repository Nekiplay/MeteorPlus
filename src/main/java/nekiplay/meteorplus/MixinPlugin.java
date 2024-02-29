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
	public static boolean isBaritonePresent;
	public static boolean isJourneyMapPresent;
	public static boolean isXaeroWorldMapresent;
	public static boolean isXaeroMiniMapresent;
	public static boolean isXaeroPlusMapresent;
	public static boolean isLitematicaMapresent;
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
			if (isBaritonePresent && (mixinClassName.contains("FreecamMixin") || mixinClassName.contains("WaypointsMixin"))) {
				return true;
			}
			else {
				LOG.info(LOGPREFIX + " [Baritone] not found, disabling Freecam and Waypoints improvement");
				return false;
			}
		}
		else if (mixinClassName.startsWith(mixinPackage + ".journeymap")) {
			if (isJourneyMapPresent) {
				if (isBaritonePresent) {
					return true;
				}
				else {
					LOG.info(LOGPREFIX + " [Baritone] not found, disabling Journey Map improvement");
					return false;
				}
			}
		}
		else if (mixinClassName.startsWith(mixinPackage + ".xaero")) {
			if (isXaeroWorldMapresent) {
				if (isBaritonePresent) {
					return true;
				}
				else {
					LOG.info(LOGPREFIX + " [Baritone] not found, disabling Xaero's World Map improvement");
					return false;
				}
			}
		}
		else if (mixinClassName.startsWith(mixinPackage + ".whereisit")) {
			if (isWhereIsIt) {
				return true;
			}
			else {
				LOG.info(LOGPREFIX + " [Where is it] not found, disabling ChestTracker improvement");
				return false;
			}
		}
		else if (mixinClassName.startsWith(mixinPackage + ".baritone")) {
			return isBaritonePresent;
		}
		return false;
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

package olejka.meteorplus;

import meteordevelopment.meteorclient.asm.Asm;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.transformer.IMixinTransformer;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

public class MixinPlugin implements IMixinConfigPlugin {
	private static final String mixinPackage = "olejka.meteorplus.mixin";

	public static boolean isJourneyMapPresent;
	public static boolean isXaeroWorldMapresent;
	public static boolean isXaeroMiniMapresent;

	@Override
	public void onLoad(String mixinPackage) {
		isJourneyMapPresent = FabricLoader.getInstance().isModLoaded("journeymap");
		isXaeroWorldMapresent = FabricLoader.getInstance().isModLoaded("xaeroworldmap");
		isXaeroMiniMapresent = FabricLoader.getInstance().isModLoaded("xaerominimap");
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
		else if (mixinClassName.startsWith(mixinPackage + ".xaerosworldmap")) {
			return isXaeroWorldMapresent;
		}
		else if (mixinClassName.startsWith(mixinPackage + ".xaerosminimap")) {
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

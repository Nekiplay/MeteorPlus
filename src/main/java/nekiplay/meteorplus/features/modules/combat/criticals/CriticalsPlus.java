package nekiplay.meteorplus.features.modules.combat.criticals;

import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.combat.Criticals;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

/*
	Not done
 */

public class CriticalsPlus extends Module {
	public CriticalsPlus() {
		super(Categories.Combat, "Criticals+", "Better criticals module");
	}

	private static MinecraftClient mc = MinecraftClient.getInstance();

	public static boolean canCrit() {
		return !mc.player.isOnGround() && mc.player.fallDistance > 0;
	}

	public static boolean skipCrit() {
		return !mc.player.isOnGround() || mc.player.isSubmergedInWater() || mc.player.isInLava() || mc.player.isClimbing();
	}

	public static boolean allowCrit() {
		if (canCrit()) {
			return true;
		}
		else if (Modules.get().get(Criticals.class).isActive()) {
            return !skipCrit();
		}
		return false;
	}
}

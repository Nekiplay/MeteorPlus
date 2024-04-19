package nekiplay.meteorplus.features.modules.combat.criticals;

import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
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
}

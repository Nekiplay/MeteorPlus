package nekiplay.meteorplus.features.modules.combat.criticals;

import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

/*
	Not done
 */

public class CriticalsPlus extends Module {
	public CriticalsPlus() {
		super(Categories.Combat, "Criticals+", "Better criticals module");
	}

	public boolean shouldWaitForCrit(Entity target, boolean ignoreState) {
		if (!isActive() && !ignoreState) {
			return false;
		}

		if (!canCrit(false) || mc.player.getVelocity().y < -0.08) {
			return false;
		}

		float nextPossibleCrit =
			calculateTicksUntilNextCrit();

		double gravity = 0.08;

		double ticksTillFall = (mc.player.getVelocity().y / gravity);

		double ticksTillCrit = Math.min(nextPossibleCrit, ticksTillFall);

		float hitProbability = 0.75f;

		float damageOnCrit = 0.5f * hitProbability;

		float damageLostWaiting = getCooldownDamageFactor(mc.player, (float)ticksTillCrit);

		return false;
	}

	public boolean canCrit(boolean ignoreOnGround) {
		return mc.player.isOnGround() && !ignoreOnGround;
	}

	private float calculateTicksUntilNextCrit() {
		float durationToWait = mc.player.getAttackCooldownProgressPerTick() * 0.9F - 0.5F;
		float waitedDuration = mc.player.getLastAttackedTime();

		return Math.min((durationToWait - waitedDuration), 0f);
	}

	private float getCooldownDamageFactor(PlayerEntity player, float tickDelta) {
		float base = ((tickDelta + 0.5f) / player.getAttackCooldownProgressPerTick());

		return Math.min((0.2f + base * base * 0.8f), 1.0f);
	}
}

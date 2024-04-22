package nekiplay.meteorplus.mixin.meteorclient.modules;

import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.combat.Criticals;
import meteordevelopment.meteorclient.systems.modules.combat.KillAura;
import nekiplay.meteorplus.features.modules.combat.Teams;
import nekiplay.meteorplus.features.modules.combat.criticals.CriticalsPlus;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import nekiplay.meteorplus.features.modules.combat.AntiBotPlus;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Mixin(value = KillAura.class, remap = false, priority = 1001)
public class KillAuraMixin extends Module {
	@Final
	@Shadow
	private final SettingGroup sgTiming = settings.getGroup("Timing");

	@Final
	@Shadow
	private final SettingGroup sgTargeting = settings.getGroup("Targeting");

	@Final
	@Shadow
	private final Setting<Set<EntityType<?>>> entities = (Setting<Set<EntityType<?>>>) sgTargeting.get("entities");

	@Final
	@Shadow
	private final List<Entity> targets = new ArrayList<>();

	@Shadow
	public Entity getTarget() {
		if (!targets.isEmpty()) return targets.get(0);
		return null;
	}

	@Unique
	private final Setting<Boolean> onlyCrits = sgTiming.add(new BoolSetting.Builder()
		.name("only-crits")
		.description("Attack enemy only if this attack crit after jump.")
		.defaultValue(true)
		.build()
	);

	@Unique
	private final Setting<Boolean> ignoreSmartDelayForShulkerBulletAndGhastCharge = sgTiming.add(new BoolSetting.Builder()
		.name("ignore-delay-for-one-hit-entities")
		.description("Ignore attack delay for shulker bullet and fireball.")
		.defaultValue(true)
		.visible(() -> entities.get().contains(EntityType.SHULKER_BULLET) || entities.get().contains(EntityType.FIREBALL))
		.build()
	);

	@Unique
	private final Setting<Boolean> ignoreOnlyCritsForOneHitEntity = sgTiming.add(new BoolSetting.Builder()
		.name("ignore-only-crits-for-one-hit-entityies")
		.description("Ignore only crits delay for shulker bullet and fireball.")
		.defaultValue(true)
		.visible(() -> ignoreSmartDelayForShulkerBulletAndGhastCharge.get() && onlyCrits.get())
		.build()
	);

	public KillAuraMixin(Category category, String name, String description) {
		super(category, name, description);
	}

	@Inject(method = "delayCheck", at = @At("HEAD"), cancellable = true)
	private void delayCheck(CallbackInfoReturnable<Boolean> cir) {
		if (onlyCrits.get() && !CriticalsPlus.allowCrit()) {
			cir.setReturnValue(false);
		}
		else if (ignoreOnlyCritsForOneHitEntity.get() && oneHitEntity()) {
			cir.setReturnValue(true);
		}
		else if (oneHitEntity() && ignoreSmartDelayForShulkerBulletAndGhastCharge.get() && !onlyCrits.get()) {
			cir.setReturnValue(true);
		}
	}

	@Unique
	private boolean oneHitEntity() {
		if (ignoreSmartDelayForShulkerBulletAndGhastCharge.get() && getTarget() != null && (getTarget().getType() == EntityType.FIREBALL || getTarget().getType() == EntityType.SHULKER_BULLET)) {
			return true;
		}
		return false;
	}


	@Inject(method = "entityCheck", at = @At("RETURN"), cancellable = true)
	protected void entityCheck(Entity entity, CallbackInfoReturnable<Boolean> cir) {
		AntiBotPlus antiBotPlus = Modules.get().get(AntiBotPlus.class);
		Teams teams = Modules.get().get(Teams.class);
		if (antiBotPlus != null && teams != null && entity instanceof PlayerEntity) {
			if (cir.getReturnValueZ()) {
				boolean ignore = !antiBotPlus.isBot(entity);
				if (ignore) {
					ignore = !teams.isInYourTeam(entity);
				}
				cir.setReturnValue(ignore);
			}
		}
	}
}

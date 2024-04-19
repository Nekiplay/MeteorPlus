package nekiplay.meteorplus.mixin.meteorclient.modules;

import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.combat.KillAura;
import nekiplay.meteorplus.features.modules.combat.Teams;
import nekiplay.meteorplus.features.modules.combat.criticals.CriticalsPlus;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import nekiplay.meteorplus.features.modules.combat.AntiBotPlus;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = KillAura.class, remap = false, priority = 1001)
public class KillAuraMixin extends Module {
	@Final
	@Shadow
	private final SettingGroup sgTiming = settings.getGroup("Timing");

	@Unique
	private final Setting<Boolean> onlyCrits = sgTiming.add(new BoolSetting.Builder()
		.name("only-crits")
		.description("Attack enemy only if this attack crit.")
		.defaultValue(true)
		.build()
	);

	public KillAuraMixin(Category category, String name, String description) {
		super(category, name, description);
	}

	@Inject(method = "delayCheck", at = @At("HEAD"), cancellable = true)
	private void delayCheck(CallbackInfoReturnable<Boolean> cir) {
		if (onlyCrits.get() && !CriticalsPlus.canCrit()) {
			cir.setReturnValue(false);
		}
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

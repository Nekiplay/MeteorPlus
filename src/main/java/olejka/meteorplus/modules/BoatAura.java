package olejka.meteorplus.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Hand;
import olejka.meteorplus.MeteorPlus;

public class BoatAura extends Module {
	public BoatAura() {
		super(MeteorPlus.CATEGORY, "Boat Aura", "Automatically sit in boat");
	}

	private final SettingGroup boatAuraSettings = settings.createGroup("Boat Aura Settings");

	private final Setting<Boolean> singleUse = boatAuraSettings.add(new BoolSetting.Builder()
		.name("Single use")
		.description("Disable the module after first interact.")
		.defaultValue(false)
		.build()
	);

	private final Setting<Boolean> onlyInAir = boatAuraSettings.add(new BoolSetting.Builder()
		.name("Only in air")
		.description("Interact boat only in air.")
		.defaultValue(false)
		.build()
	);

	private final Setting<Boolean> onlyIfFallDamade = boatAuraSettings.add(new BoolSetting.Builder()
		.name("Only if fall damage")
		.description("Prevent fall damage.")
		.defaultValue(false)
		.visible(onlyInAir::get)
		.build()
	);


	private final Setting<Integer> delay = boatAuraSettings.add(new IntSetting.Builder()
		.name("Delay")
		.description("Move delay.")
		.defaultValue(8)
		.min(0)
		.visible(() -> !singleUse.get())
		.sliderRange(0, 80)
		.build()
	);

	long mils = 0;
	@Override
	public void onActivate() {

	}

	@EventHandler
	private void onTickEvent(TickEvent.Pre event) {
		assert mc.world != null;
		for (Entity entity : mc.world.getEntities()) {
			if (mc.player != null && entity.getType() == EntityType.BOAT) {
				if (mc.interactionManager != null && mils == 0) {
					if (onlyInAir.get() && mc.player.isOnGround()) return;
					if (onlyInAir.get() && onlyIfFallDamade.get() && mc.player.isOnGround() && mc.player.fallDistance <= 2) return;

					mc.player.teleport(entity.getBlockPos().getX(), entity.getBlockPos().getX() + 1, entity.getBlockPos().getZ());
					mc.interactionManager.interactEntity(mc.player, entity, Hand.MAIN_HAND);

					if (singleUse.get()) {
						this.toggle();
						return;
					}

					mils = delay.get();
				}
				else {
					mils--;
				}
			}
		}
	}
}

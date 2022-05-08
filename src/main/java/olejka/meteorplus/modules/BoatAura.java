package olejka.meteorplus.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
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

	private final SettingGroup AASettings = settings.createGroup("Boat Aura Settings");

	private final Setting<Integer> delay = AASettings.add(new IntSetting.Builder()
		.name("Delay")
		.description("Move delay.")
		.defaultValue(8)
		.min(0)
		.sliderRange(0, 80)
		.build()
	);
	long mils = 0;
	@Override
	public void onActivate() {

	}

	@EventHandler
	private void onTickEvent(TickEvent.Pre event) {
		for (Entity entity : mc.world.getEntities()) {
			if (entity.getType() == EntityType.BOAT) {
				float distanc = mc.player.distanceTo(entity);
				if (mils == 0) {
					mc.player.teleport(entity.getBlockPos().getX(), entity.getBlockPos().getX() + 1, entity.getBlockPos().getZ());
					mc.interactionManager.interactEntity(mc.player, entity, Hand.MAIN_HAND);
					mils = delay.get();
				}
				else {
					mils--;
				}
			}
		}
	}
}

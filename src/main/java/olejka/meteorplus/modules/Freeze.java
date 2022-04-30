package olejka.meteorplus.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import olejka.meteorplus.MeteorPlus;

public class Freeze extends Module {
	public Freeze() {
		super(MeteorPlus.CATEGORY, "Freeze", "Freezes your position.");
	}

	@EventHandler(priority = EventPriority.HIGH)
	private void onTick(TickEvent.Pre event) {
		if (mc.player == null) return;
		mc.player.setVelocity(0, 0, 0);
	}
}

package nekiplay.meteorplus.features.modules.movement.noslow.modes;

import meteordevelopment.meteorclient.events.world.TickEvent;
import nekiplay.meteorplus.features.modules.movement.noslow.NoSlowMode;
import nekiplay.meteorplus.features.modules.movement.noslow.NoSlowModes;
import nekiplay.meteorplus.features.modules.movement.noslow.NoSlowPlus;

public class Matrix extends NoSlowMode {
	public Matrix() {
		super(NoSlowModes.Matrix);
	}

	@Override
	public void onTickEventPre(TickEvent.Pre event) {
		if (mc.player.isUsingItem()) {
			if (mc.player.isOnGround()) {

			}
		}
	}
}

package nekiplay.meteorplus.features.modules.movement.noslow.modes;

import nekiplay.meteorplus.events.PlayerUseMultiplierEvent;
import nekiplay.meteorplus.features.modules.movement.noslow.NoSlowMode;
import nekiplay.meteorplus.features.modules.movement.noslow.NoSlowModes;
import net.minecraft.network.packet.c2s.play.SlotChangedStateC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;

public class Grim extends NoSlowMode {
	public Grim() {
		super(NoSlowModes.Grim);
	}
	@Override
	public void onUse(PlayerUseMultiplierEvent event) {
		if (mc.player.isUsingItem()) {
			event.setForward(settings.usingForward.get().floatValue());
			event.setSideways(settings.usingSideways.get().floatValue());
		}
		if (mc.player.isSneaking()) {
			event.setForward(settings.sneakForward.get().floatValue());
			event.setSideways(settings.sneakSideways.get().floatValue());
		}
		else {
			event.setForward(settings.otherForward.get().floatValue());
			event.setSideways(settings.otherSideways.get().floatValue());
		}

		if (mc.player.isUsingItem()) {
			mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.getInventory().selectedSlot % 8 + 1));
			mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.getInventory().selectedSlot));
		}
	}
}

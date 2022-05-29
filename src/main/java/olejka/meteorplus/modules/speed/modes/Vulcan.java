package olejka.meteorplus.modules.speed.modes;

import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.Anchor;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.player.SlotUtils;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;
import olejka.meteorplus.modules.speed.SpeedMode;
import olejka.meteorplus.modules.speed.SpeedModes;

public class Vulcan extends SpeedMode {
	public Vulcan() {
		super(SpeedModes.Vulcan);
	}
	public Item chestPlate;
	@Override
	public void onDeactivate() {
		FindItemResult chest = InvUtils.find(chestPlate);
		if (chest.found() && mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.ELYTRA && settings.autoSwapVulcan.get()) {
			InvUtils.move().from(chest.slot()).toArmor(2);
		}
	}

	@Override
	public void onActivate() {
		FindItemResult elytra = InvUtils.find(Items.ELYTRA);
		if (!elytra.found()) {
			settings.error("Elytra not found");
			settings.toggle();
		}
		else {
			if (!SlotUtils.isArmor(elytra.slot()) && settings.autoSwapVulcan.get()) {
				if (mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() != Items.ELYTRA) {
					chestPlate = mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem();
					InvUtils.move().from(elytra.slot()).toArmor(2);
				}
			}
		}
		if (mc.player.hasStatusEffect(StatusEffects.SPEED)) {
			int amplifier = mc.player.getStatusEffect(StatusEffects.SPEED).getAmplifier();
			if (amplifier >= 1) {

			}
			else {
				settings.warning("Vulcan speed need speed effect 2");
			}
		}
		else {
			settings.warning("Vulcan speed need speed effect 2");
		}
	}

	@Override
	public void onPlayerMoveEvent(PlayerMoveEvent event) {
		if (mc.player.hasStatusEffect(StatusEffects.SPEED) && mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.ELYTRA) {
			Vec3d vel = PlayerUtils.getHorizontalVelocity(settings.speedVulcan.get());
			double velX = vel.getX();
			double velZ = vel.getZ();

			if (mc.player.hasStatusEffect(StatusEffects.SPEED)) {
				double value = (mc.player.getStatusEffect(StatusEffects.SPEED).getAmplifier() + 1) * 0.205;
				velX += velX * value;
				velZ += velZ * value;
			}

			Anchor anchor = Modules.get().get(Anchor.class);
			if (anchor.isActive() && anchor.controlMovement) {
				velX = anchor.deltaX;
				velZ = anchor.deltaZ;
			}

			((IVec3d) event.movement).set(velX, event.movement.y, velZ);
		}
	}
}

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
import olejka.meteorplus.utils.CustomSpeedUtils;

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
		if (mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.ELYTRA) {
			if (mc.player.hasStatusEffect(StatusEffects.SPEED) && mc.player.getStatusEffect(StatusEffects.SPEED).getAmplifier() == 1) {
				CustomSpeedUtils.applySpeed(event, settings.speedVulcanef2.get());
			}
			else if (mc.player.hasStatusEffect(StatusEffects.SPEED) && mc.player.getStatusEffect(StatusEffects.SPEED).getAmplifier() == 0) {
				CustomSpeedUtils.applySpeed(event, settings.speedVulcanef1.get());
			}
			else {
				CustomSpeedUtils.applySpeed(event, settings.speedVulcanef0.get());
			}
		}
	}
}

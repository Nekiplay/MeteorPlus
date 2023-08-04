package olejka.meteorplus.modules.jesus.modes;

import meteordevelopment.meteorclient.events.entity.player.CanWalkOnFluidEvent;
import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.events.world.CollisionShapeEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.Anchor;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.player.SlotUtils;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;
import olejka.meteorplus.modules.jesus.JesusMode;
import olejka.meteorplus.modules.jesus.JesusModes;

public class Vulcan extends JesusMode {
	public Vulcan() {
		super(JesusModes.Vulcan);
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
	}

	@Override
	public void onCanWalkOnFluid(CanWalkOnFluidEvent event) {
		if ((event.fluidState.getFluid() == Fluids.WATER || event.fluidState.getFluid() == Fluids.FLOWING_WATER)) {
			if (mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.ELYTRA) {
				event.walkOnFluid = true;
			}
		}
	}
	@Override
	public void onCollisionShape(CollisionShapeEvent event) {
		if (!event.state.getFluidState().isEmpty() && mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.ELYTRA) {
			if (mc.player != null && event.state != null && event.state.isOf(Blocks.WATER) && !mc.player.isTouchingWater()) {
				event.shape = VoxelShapes.fullCube();
			}
		}
	}

	@Override
	public void onPlayerMoveEvent(PlayerMoveEvent event) {
		if (mc.world.getBlockState(mc.player.getBlockPos().add(0, -1, 0)).getBlock() == Blocks.WATER || mc.world.getBlockState(mc.player.getBlockPos()).getBlock() == Blocks.WATER) {
			if (mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.ELYTRA) {
				Vec3d vel = PlayerUtils.getHorizontalVelocity(settings.speed.get());
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
}
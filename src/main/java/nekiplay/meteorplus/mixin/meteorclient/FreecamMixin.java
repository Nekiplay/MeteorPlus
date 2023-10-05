package nekiplay.meteorplus.mixin.meteorclient;

import baritone.api.BaritoneAPI;
import baritone.api.pathing.goals.GoalBlock;
import meteordevelopment.meteorclient.events.meteor.MouseButtonEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.Blink;
import meteordevelopment.meteorclient.systems.modules.render.Freecam;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import meteordevelopment.orbit.EventHandler;
import nekiplay.meteorplus.utils.RaycastUtils;
import net.minecraft.block.*;
import net.minecraft.item.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static baritone.api.utils.Helper.mc;

@Mixin(Freecam.class)
public class FreecamMixin {
	private final Freecam freecam = (Freecam)(Object) this;

	private final SettingGroup freecamMeteorPlusSetting = freecam.settings.createGroup("Meteor Plus");

	private final Setting<Boolean> baritoneControl = freecamMeteorPlusSetting.add(new BoolSetting.Builder()
		.name("baritone-control")
		.description("Left mouse click to set the destination on the selected block. Right mouse click to cancel.")
		.build()
	);

	private final Setting<Boolean> blinkBaritoneControl = freecamMeteorPlusSetting.add(new BoolSetting.Builder()
		.name("baritone-blink-control")
		.description("Midle mouse click to move to point in Blink.")
		.visible(baritoneControl::get)
		.build()
	);

	@Unique
	private final Blink blink = Modules.get().get(Blink.class);
	@Unique
	private boolean isBlinkMoving = false;

	@Unique
	private List<Block> notSolidBlocks = Arrays.asList(
		Blocks.FERN,
		Blocks.GRASS,
		Blocks.TALL_GRASS,
		Blocks.GLOW_LICHEN,
		Blocks.DEAD_BUSH,
		Blocks.SNOW,
		Blocks.MOSS_CARPET,
		// Decorations
		Blocks.TORCH,
		Blocks.WALL_TORCH,
		// Redstone
		Blocks.REDSTONE_TORCH,
		Blocks.REDSTONE_WALL_TORCH,
		Blocks.REDSTONE_WIRE,
		// Signs
		Blocks.SPRUCE_SIGN,
		Blocks.ACACIA_SIGN,
		Blocks.BIRCH_SIGN,
		Blocks.CHERRY_SIGN,
		Blocks.BAMBOO_SIGN,
		Blocks.OAK_SIGN,
		Blocks.CRIMSON_SIGN,
		Blocks.DARK_OAK_SIGN,
		Blocks.JUNGLE_SIGN,
		Blocks.MANGROVE_SIGN,
		Blocks.WARPED_SIGN,
		// Wall signs
		Blocks.SPRUCE_WALL_SIGN,
		Blocks.ACACIA_WALL_SIGN,
		Blocks.BIRCH_WALL_SIGN,
		Blocks.CHERRY_WALL_SIGN,
		Blocks.BAMBOO_WALL_SIGN,
		Blocks.OAK_WALL_SIGN,
		Blocks.CRIMSON_WALL_SIGN,
		Blocks.DARK_OAK_WALL_SIGN,
		Blocks.JUNGLE_WALL_SIGN,
		Blocks.MANGROVE_WALL_SIGN,
		Blocks.WARPED_WALL_SIGN,
		// Mushroms
		Blocks.BROWN_MUSHROOM,
		Blocks.RED_MUSHROOM,
		Blocks.CRIMSON_FUNGUS,
		Blocks.WARPED_FUNGUS,
		// Small flowers
		Blocks.DANDELION,
		Blocks.POPPY,
		Blocks.BLUE_ORCHID,
		Blocks.ALLIUM,
		Blocks.AZURE_BLUET,
		Blocks.RED_TULIP,
		Blocks.ORANGE_TULIP,
		Blocks.WHITE_TULIP,
		Blocks.PINK_TULIP,
		Blocks.OXEYE_DAISY,
		Blocks.CORNFLOWER,
		Blocks.LILY_OF_THE_VALLEY,
		Blocks.TORCHFLOWER,
		Blocks.PINK_PETALS,
		Blocks.SUGAR_CANE,
		// Crops
		Blocks.NETHER_WART,
		Blocks.PITCHER_CROP,
		Blocks.TORCHFLOWER_CROP,
		Blocks.BEETROOTS,
		Blocks.WHEAT,
		Blocks.CARROTS,
		Blocks.POTATOES,
		Blocks.MELON_STEM,
		Blocks.PUMPKIN_STEM,
		// Rails
		Blocks.RAIL,
		Blocks.ACTIVATOR_RAIL,
		Blocks.POWERED_RAIL,
		Blocks.DETECTOR_RAIL

	);

	@Unique
	private BlockPos tryGetValidPos(BlockPos pos) {
		BlockState state = mc.world.getBlockState(pos);
		Block block = state.getBlock();
		if (notSolidBlocks.contains(block)) {
			return pos;
		}
		else {
			return pos.up();
		}
	}
	@Unique
	@EventHandler
	private void onTickEvent(TickEvent.Pre event) {
		if (blinkBaritoneControl.get()) {
			if (isBlinkMoving && (BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().hasPath() || BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing() ) ) {
				if (!blink.isActive()) {
					blink.toggle();
				}
			}
			if (isBlinkMoving && (!BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().hasPath() || !BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing() ) ) {
				if (blink.isActive()) {
					blink.toggle();
					isBlinkMoving = false;
				}
			}
		}
	}

	@Unique
	@EventHandler
	private void onMouseButtonEvent(MouseButtonEvent event) {
		if (!baritoneControl.get()) return;
		if (event.action != KeyAction.Press) return;
		if (mc.currentScreen != null) return;
		if (mc.player == null) return;

		float pitch = (float) freecam.getPitch(mc.getTickDelta());
		float yaw = (float) freecam.getYaw(mc.getTickDelta());

		if (event.button == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
			if (blinkBaritoneControl.get()) {
				BlockPos blockPos = null;
				Vec3d rotationVector = RaycastUtils.getRotationVector(pitch, yaw);
				Vec3d pos = new Vec3d(freecam.pos.x, freecam.pos.y, freecam.pos.z);
				HitResult result = RaycastUtils.raycast(pos, rotationVector, 64 * 4, mc.getTickDelta(), true);
				if (result.getType() == HitResult.Type.BLOCK) {
					BlockHitResult blockHitResult = (BlockHitResult) result;
					blockPos = blockHitResult.getBlockPos();
				}

				if (blockPos == null) return;

				if (mc.world == null) return;

				BlockState state = mc.world.getBlockState(blockPos);

				if (state.isAir()) return;
				isBlinkMoving = true;
				GoalBlock goal = new GoalBlock(tryGetValidPos(blockPos));
				BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(null);
				BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(goal);
			}
		}

		if (event.button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			BlockPos blockPos = null;
			Vec3d rotationVector = RaycastUtils.getRotationVector((float) freecam.getPitch(mc.getTickDelta()), (float) freecam.getYaw(mc.getTickDelta()));
			Vec3d pos = new Vec3d(freecam.pos.x, freecam.pos.y, freecam.pos.z);
			HitResult result = RaycastUtils.raycast(pos, rotationVector, 64 * 4, mc.getTickDelta(), true);
			if (result.getType() == HitResult.Type.BLOCK) {
				BlockHitResult blockHitResult = (BlockHitResult) result;
				blockPos = blockHitResult.getBlockPos();
			}

			if (blockPos == null) return;

			if (mc.world == null) return;

			BlockState state = mc.world.getBlockState(blockPos);

			if (state.isAir()) return;

			Block mineBlock = mc.world.getBlockState(blockPos).getBlock();

			GoalBlock goal = new GoalBlock(tryGetValidPos(blockPos));
			BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(null);
			BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(goal);
			event.cancel();
		}

		if (event.button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
			BaritoneAPI.getProvider().getPrimaryBaritone().getCommandManager().execute("stop");
			event.cancel();
		}
	}
}

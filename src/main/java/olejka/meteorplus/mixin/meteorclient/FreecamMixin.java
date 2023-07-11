package olejka.meteorplus.mixin.meteorclient;

import baritone.api.BaritoneAPI;
import baritone.api.pathing.goals.GoalBlock;
import meteordevelopment.meteorclient.events.meteor.MouseButtonEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.render.Freecam;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;

import static baritone.api.utils.Helper.mc;

@Mixin(Freecam.class)
public class FreecamMixin {
	private final Freecam freecam = (Freecam)(Object) this;

	private final SettingGroup freecamMeteorPlusSetting = freecam.settings.createGroup("Meteor Plus");

	private final Setting<Boolean> baritoneControl = freecamMeteorPlusSetting.add(new BoolSetting.Builder()
		.name("Baritone control")
		.description("Left-click to set the destination on the selected block. Right click to cancel.")
		.build()
	);

	@EventHandler
	private void onMouseButtonEvent(MouseButtonEvent event) {
		if (!baritoneControl.get()) return;
		if (event.action != KeyAction.Press) return;
		if (mc.currentScreen != null) return;

		if (event.button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			if (!(mc.crosshairTarget instanceof BlockHitResult)) return;

			BlockPos blockPos = ((BlockHitResult) mc.crosshairTarget).getBlockPos();

			if (mc.world.getBlockState(blockPos).isAir()) return;

			GoalBlock goal = new GoalBlock(blockPos.up());
			BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(goal);
			event.cancel();
		}

		if (event.button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
			BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(null);
			event.cancel();
		}
	}
}

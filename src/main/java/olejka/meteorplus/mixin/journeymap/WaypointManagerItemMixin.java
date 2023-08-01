package olejka.meteorplus.mixin.journeymap;

import baritone.api.BaritoneAPI;
import baritone.api.pathing.goals.GoalBlock;
import journeymap.client.Constants;
import journeymap.client.ui.UIManager;
import journeymap.client.ui.component.Button;
import journeymap.client.ui.component.ButtonList;
import journeymap.client.ui.waypoint.WaypointManagerItem;
import journeymap.client.waypoint.Waypoint;
import meteordevelopment.meteorclient.settings.BoolSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import olejka.meteorplus.gui.tabs.JouneyMapTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WaypointManagerItem.class)
public class WaypointManagerItemMixin {

	@Shadow(remap = false)
	ButtonList buttonListLeft;
	@Shadow(remap = false)
	Waypoint waypoint;
	@Shadow(remap = false)
	boolean displayHover = true;
	@Unique
	Button gotoButton;

	@Inject(method = "<init>", at = @At("JUMP"))
	private void onInit(CallbackInfo info) {
		gotoButton = new Button(Text.translatable("journey.map.goto").getString());
		BoolSetting setting = (BoolSetting) JouneyMapTab.getSettings().getGroup("Full map").get("Baritone goto in waypoints menu");
		if (setting.get()) {
			buttonListLeft.add(gotoButton);
		}
	}

	@Inject(method = "render", at = @At(value = "HEAD"))
	private void onRender(DrawContext graphics, int slotIndex, int y, int x, int rowWidth, int itemHeight, int mouseX, int mouseY, boolean isMouseOver, float partialTicks, CallbackInfo ci) {
		boolean drawHovered = isMouseOver && this.displayHover;
		BoolSetting setting = (BoolSetting) JouneyMapTab.getSettings().getGroup("Full map").get("Baritone goto in waypoints menu");
		if (setting.get()) {
			gotoButton.drawHovered(drawHovered);
		}
	}
	@Inject(method = "clickScrollable", at = @At(value = "HEAD"), remap = false)
	private void onClickScrollable(double mouseX, double mouseY, CallbackInfoReturnable<Boolean> cir) {
		if (gotoButton.mouseOver(mouseX, mouseY)) {
			GoalBlock goal = new GoalBlock(waypoint.getBlockPos());
			BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(goal);
			cir.setReturnValue(true);
		}
	}
}

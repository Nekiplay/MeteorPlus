package olejka.meteorplus.mixin.xaero.worldmap;

import baritone.api.BaritoneAPI;
import baritone.api.pathing.goals.GoalBlock;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.math.BlockPos;
import olejka.meteorplus.MixinPlugin;
import olejka.meteorplus.gui.tabs.XaeroWorldMapTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xaero.map.MapProcessor;
import xaero.map.WorldMap;
import xaero.map.controls.ControlsRegister;
import xaero.map.gui.GuiMap;
import xaero.map.gui.MapTileSelection;
import xaero.map.gui.dropdown.rightclick.RightClickOption;
import xaero.map.misc.Misc;
import xaero.map.mods.SupportMods;
import xaero.map.region.*;
import xaero.map.teleport.MapTeleporter;
import xaero.map.world.MapDimension;
import meteordevelopment.meteorclient.utils.misc.Names;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static meteordevelopment.meteorclient.MeteorClient.mc;

@Mixin(GuiMap.class)
public abstract class GuiMapMixin {
	private final GuiMap guiMap = (GuiMap)(Object) this;
	@Shadow(remap = false)

	private MapTileSelection mapTileSelection;
	@Shadow(remap = false)
	private double scale;
	@Shadow(remap = false)
	private int rightClickX;
	@Shadow(remap = false)
	private int rightClickY;
	@Shadow(remap = false)
	private int rightClickZ;
	@Shadow(remap = false)
	private int mouseBlockPosX = 0;
	@Shadow(remap = false)
	private int mouseBlockPosY = 0;
	@Shadow(remap = false)
	private int mouseBlockPosZ = 0;
	@Shadow(remap = false)
	private MapProcessor mapProcessor;
	@Shadow(remap = false)
	private ButtonWidget exportButton;
	@Shadow(remap = false)
	private void onExportButton(ButtonWidget b) { }
	@Shadow(remap = false)
	private ButtonWidget settingsButton;
	@Shadow(remap = false)
	private void onSettingsButton(ButtonWidget b) { }
	@Shadow(remap = false)
	private double cameraX = 0.0;
	@Shadow(remap = false)
	private double cameraZ = 0.0;

	private SettingGroup group = XaeroWorldMapTab.getSettings().getGroup("Full map");

	@Inject(method = "<init>", at = @At("JUMP"))
	private void onInit(CallbackInfo info) {
		cameraX = ((float) mc.player.getX());
		cameraZ = ((float) mc.player.getZ());
	}

	@Inject(method = "getRightClickOptions", at = @At(value = "RETURN"), remap = false)
	private void rightClickOptins(CallbackInfoReturnable<ArrayList<RightClickOption>> cir) {
		final ArrayList<RightClickOption> options = cir.getReturnValue();

		int mouseXPos = (int) Misc.getMouseX(mc, false);
		int mouseYPos = (int) Misc.getMouseY(mc, false);

		int mouseFromCentreX = mouseXPos - mc.getWindow().getFramebufferWidth() / 2;
		int mouseFromCentreY = mouseYPos - mc.getWindow().getFramebufferHeight() / 2;

		double mousePosX = (double) mouseFromCentreX / this.scale + this.cameraX;
		double mousePosZ = (double) mouseFromCentreY / this.scale + this.cameraZ;
		int mouseBlockPosX = (int) Math.floor(mousePosX);
		int mouseBlockPosZ = (int) Math.floor(mousePosZ);

		int renderedCaveLayer = mapProcessor.getCurrentCaveLayer();
		MapRegion leafRegion = this.mapProcessor.getMapRegion(renderedCaveLayer, mouseBlockPosX >> 9, mouseBlockPosZ >> 9, false);
		MapTileChunk chunk = leafRegion == null ? null : leafRegion.getChunk(mouseBlockPosX >> 6 & 7, mouseBlockPosZ >> 6 & 7);
		MapTile mouseTile = chunk == null ? null : chunk.getTile(mouseBlockPosX >> 4 & 3, mouseBlockPosZ >> 4 & 3);

		if (group != null && mouseTile != null) {
			BoolSetting showBlockInContextMenu = (BoolSetting) group.get("Show block in context menu");
			if (showBlockInContextMenu.get()) {
				MapBlock block = mouseTile.getBlock(mouseBlockPosX & 15, mouseBlockPosZ & 15);
				MapPixelAccessor pixel = (MapPixelAccessor) block;

				options.addAll(2, List.of(
					new RightClickOption(Names.get(pixel.getBlockState().getBlock()), options.size(), guiMap) {
						@Override
						public void onAction(Screen screen) {

						}
					}
				));
			}
		}

		if (!MixinPlugin.isXaeroPlusMapresent) {
			BoolSetting baritoneGotoInContextMenu = (BoolSetting) group.get("Baritone goto in context menu");
			if (baritoneGotoInContextMenu.get()) {
				options.addAll(3, List.of(
					new RightClickOption(I18n.translate("journey.map.goto"), options.size(), guiMap) {
						@Override
						public void onAction(Screen screen) {
							GoalBlock goal = new GoalBlock(new BlockPos(rightClickX, rightClickY, rightClickZ).up());
							BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(goal);
						}
					}
				));
			}
		}
	}
}

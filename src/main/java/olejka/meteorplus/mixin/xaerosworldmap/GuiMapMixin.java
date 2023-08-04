package olejka.meteorplus.mixin.xaerosworldmap;

import baritone.api.BaritoneAPI;
import baritone.api.pathing.goals.GoalBlock;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.misc.Names;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.BlockPos;
import olejka.meteorplus.modules.MapModIntegration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xaero.map.MapProcessor;
import xaero.map.gui.GuiMap;
import xaero.map.gui.dropdown.rightclick.RightClickOption;
import xaero.map.region.MapBlock;
import xaero.map.region.MapRegion;
import xaero.map.region.MapTile;
import xaero.map.region.MapTileChunk;

import java.util.ArrayList;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Mixin(GuiMap.class)
public class GuiMapMixin {
	@Shadow(remap = false)
	private int rightClickX;
	@Shadow(remap = false)
	private double scale;
	@Shadow(remap = false)
	private int rightClickY;
	@Shadow(remap = false)
	private int rightClickZ;
	@Shadow(remap = false)
	private MapProcessor mapProcessor;
	@Shadow(remap = false)
	private int mouseBlockPosX = 100;
	@Shadow(remap = false)
	private int mouseBlockPosZ = 100;
	@Shadow(remap = false)
	private double cameraX = 0.0;
	@Shadow(remap = false)
	private double cameraZ = 0.0;

	private MapModIntegration module = Modules.get().get(MapModIntegration.class);

	@Inject(method = "<init>", at = @At("JUMP"))
	private void onInit(CallbackInfo info) {
		if (module.isActive() && module.openMapMode.get() == MapModIntegration.OpenMapMode.Player) {
			cameraX = ((float) mc.player.getX());
			cameraZ = ((float) mc.player.getZ());
		}
	}

	@Inject(method = "getRightClickOptions", at = @At("TAIL"), remap = false, cancellable = true)
	private void getRightClickOptions(CallbackInfoReturnable<ArrayList<RightClickOption>> cir) {
		ArrayList<RightClickOption> options = cir.getReturnValue();

		if (module.showBlockInContextMenu()) {
			int renderedCaveLayer = mapProcessor.getCurrentCaveLayer();
			MapRegion leafRegion = this.mapProcessor.getMapRegion(renderedCaveLayer, mouseBlockPosX >> 9, mouseBlockPosZ >> 9, false);
			MapTileChunk chunk = leafRegion == null ? null : leafRegion.getChunk(mouseBlockPosX >> 6 & 7, mouseBlockPosZ >> 6 & 7);
			MapTile mouseTile = chunk == null ? null : chunk.getTile(mouseBlockPosX >> 4 & 3, mouseBlockPosZ >> 4 & 3);
			if (mouseTile != null) {
				MapBlock block = mouseTile.getBlock(mouseBlockPosX & 15, mouseBlockPosZ & 15);
				MapPixelAccessor pixel = (MapPixelAccessor) block;

				options.add(new RightClickOption(Names.get(pixel.getBlockState().getBlock()), options.size(), (GuiMap) (Object) this) {
					public void onAction(Screen screen) {
					}
				});
			}
		}

		if (module.baritoneGotoInContextMenu()) {
			options.add(new RightClickOption("journey.map.goto", options.size(), (GuiMap) (Object) this) {
				public void onAction(Screen screen) {
					GoalBlock goal = new GoalBlock(new BlockPos(rightClickX, rightClickY, rightClickZ).up());
					BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(goal);
				}
			});
		}

		cir.setReturnValue(options);
	}
}

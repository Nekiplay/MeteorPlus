package nekiplay.meteorplus.mixin.meteorclient.modules;

import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import meteordevelopment.orbit.EventHandler;
import nekiplay.meteorplus.events.hud.DebugDrawTextEvent;
import nekiplay.meteorplus.mixinclasses.SpoofMode;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import java.util.List;
import java.util.Locale;

@Mixin(value = NoRender.class, remap = false, priority = 1001)
public class NoRenderMixin extends Module {
	public NoRenderMixin(Category category, String name, String description) {
		super(category, name, description);
	}

	@Unique
	private final NoRender noRender = (NoRender)(Object) this;
	@Unique
	private final SettingGroup noRenderMeteorPlusSetting = noRender.settings.createGroup("F3");


	@Unique
	private final Setting<Boolean> noPosition = noRenderMeteorPlusSetting.add(new BoolSetting.Builder()
		.name("remove-position")
		.defaultValue(false)
		.build()
	);
	@Unique
	private final Setting<Boolean> noPositionBlock = noRenderMeteorPlusSetting.add(new BoolSetting.Builder()
		.name("remove-position-block")
		.defaultValue(false)
		.build()
	);
	@Unique
	private final Setting<Boolean> noPositionChunk = noRenderMeteorPlusSetting.add(new BoolSetting.Builder()
		.name("remove-position-chunk")
		.defaultValue(false)
		.build()
	);

	@Unique
	private final Setting<Boolean> noTargetBlockPosition = noRenderMeteorPlusSetting.add(new BoolSetting.Builder()
		.name("remove-target-block-position")
		.defaultValue(false)
		.build()
	);

	@Unique
	private final Setting<Boolean> noTargetFluidPosition = noRenderMeteorPlusSetting.add(new BoolSetting.Builder()
		.name("remove-target-fluid-position")
		.defaultValue(false)
		.build()
	);

	@Unique
	private final Setting<SpoofMode> spoofMode = noRenderMeteorPlusSetting.add(new EnumSetting.Builder<SpoofMode>()
		.name("spoof-mode")
		.defaultValue(SpoofMode.Remove)
		.build()
	);

	private final Setting<Integer> x_spoof = noRenderMeteorPlusSetting.add(new IntSetting.Builder()
		.name("x-add")
		.defaultValue(100000)
		.visible(() -> spoofMode.get() == SpoofMode.Fake)
		.build()
	);

	private final Setting<Integer> z_spoof = noRenderMeteorPlusSetting.add(new IntSetting.Builder()
		.name("z-add")
		.defaultValue(100000)
		.visible(() -> spoofMode.get() == SpoofMode.Fake)
		.build()
	);

	@Unique
	@EventHandler
	private void onDebugF3RenderText(DebugDrawTextEvent event) {
		List<String> lines = event.getLines();


		if (spoofMode.get() == SpoofMode.Remove) {
			if (event.isLeft()) {
				if (noPosition.get()) {
					lines.removeIf(s -> s.contains("XYZ:"));
				}

				if (noPositionBlock.get()) {
					lines.removeIf(s -> s.contains("Block:"));
				}

				if (noPositionChunk.get()) {
					lines.removeIf(s -> s.contains("Chunk:"));
				}
			} else {
				if (noTargetBlockPosition.get()) {
					lines.removeIf(s -> s.contains("Targeted Block:"));
				}

				if (noTargetFluidPosition.get()) {
					lines.removeIf(s -> s.contains("Targeted Fluid:"));
				}
			}
		}
		else {
			if (event.isLeft()) {
				int index = 0;
				for (Object obj : lines.toArray()) {
					String str = obj.toString();

					if (str.startsWith("XYZ:")) {
						String xyz = String.format(Locale.ROOT, "XYZ: %.3f / %.5f / %.3f", mc.getCameraEntity().getX() + x_spoof.get(), mc.getCameraEntity().getY(), mc.getCameraEntity().getZ() + z_spoof.get());
						lines.set(index, xyz);
					}
					else if (str.startsWith("Block: ")) {
						BlockPos blockPos = mc.getCameraEntity().getBlockPos();
						blockPos = blockPos.add(x_spoof.get(), 0, z_spoof.get());

						String block = String.format(Locale.ROOT, "Block: %d %d %d [%d %d %d]", blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX() & 15, blockPos.getY() & 15, blockPos.getZ() & 15);
						lines.set(index, block);
					}
					else if (str.startsWith("Chunk:")) {
						BlockPos blockPos = mc.getCameraEntity().getBlockPos();
						blockPos = blockPos.add(x_spoof.get(), 0, z_spoof.get());
						ChunkPos chunkPos = new ChunkPos(blockPos);

						String chunk = String.format(Locale.ROOT, "Chunk: %d %d %d [%d %d in r.%d.%d.mca]", chunkPos.x, ChunkSectionPos.getSectionCoord(blockPos.getY()), chunkPos.z, chunkPos.getRegionRelativeX(), chunkPos.getRegionRelativeZ(), chunkPos.getRegionX(), chunkPos.getRegionZ());
						lines.set(index, chunk);
					}
					index++;
				}
			}
			else {
				int index = 0;
				for (Object obj : lines.toArray()) {
					String str = obj.toString();

					if (str.contains("Targeted Block:")) {
						HitResult blockHitResult = event.blockHit();
						if (blockHitResult != null && blockHitResult.getType() == HitResult.Type.BLOCK) {
							Formatting var10001 = Formatting.UNDERLINE;

							BlockPos blockPos = ((BlockHitResult)blockHitResult).getBlockPos();
							blockPos = blockPos.add(x_spoof.get(), 0, z_spoof.get());
							lines.set(index, "" + var10001 + "Targeted Block: " + blockPos.getX() + ", " + blockPos.getY() + ", " + blockPos.getZ());
						}
					}
					else if (str.contains("Targeted Fluid:")) {
						HitResult blockHitResult = event.fluidHit();
						if (blockHitResult != null && blockHitResult.getType() == HitResult.Type.BLOCK) {
							Formatting var10001 = Formatting.UNDERLINE;

							BlockPos blockPos = ((BlockHitResult)blockHitResult).getBlockPos();
							blockPos = blockPos.add(x_spoof.get(), 0, z_spoof.get());
							lines.set(index, "" + var10001 + "Targeted Fluid: " + blockPos.getX() + ", " + blockPos.getY() + ", " + blockPos.getZ());
						}
					}
					index++;
				}
			}
		}

	}
}

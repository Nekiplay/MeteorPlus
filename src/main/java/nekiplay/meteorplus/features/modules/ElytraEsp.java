package nekiplay.meteorplus.features.modules;


import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import nekiplay.meteorplus.MeteorPlus;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;

public class ElytraEsp extends Module {

	public ElytraEsp() {
		super(MeteorPlus.CATEGORY, "elytra-esp", "backlighting of the frames in which the elytra");
	}

	private final SettingGroup sgGeneral = settings.getDefaultGroup();

	private final Setting<ShapeMode> shapeMode = sgGeneral.add(new EnumSetting.Builder<ShapeMode>()
		.name("shape-mode")
		.description("How the shapes are rendered.")
		.defaultValue(ShapeMode.Both)
		.build()
	);

	private final Setting<SettingColor> tracerColor = sgGeneral.add(new ColorSetting.Builder()
		.name("tracer-color")
		.description("The side color.")
		.defaultValue(new SettingColor(255, 255, 255, 50))
		.build()
	);

	private final Setting<SettingColor> lineColor = sgGeneral.add(new ColorSetting.Builder()
		.name("line-color")
		.description("The line color.")
		.defaultValue(new SettingColor(255, 255, 255, 255))
		.build()
	);


	private final Setting<SettingColor> sideColor = sgGeneral.add(new ColorSetting.Builder()
		.name("side-color")
		.description("The side color.")
		.defaultValue(new SettingColor(255, 255, 255, 50))
		.build()
	);

	@EventHandler
	private void onRender2D(Render3DEvent event) {
		if (mc.world == null) return;
		for (Entity entity : mc.world.getEntities()) {
			double xl = entity.getX();
			double yl = entity.getY();
			double zl = entity.getZ();
			if (entity instanceof ItemFrameEntity) {
				ItemFrameEntity itemFrame = (ItemFrameEntity)entity;
				ItemStack held = itemFrame.getHeldItemStack();
				if (held.getItem() == Items.ELYTRA) {

					event.renderer.line(RenderUtils.center.x, RenderUtils.center.y, RenderUtils.center.z, xl, yl, zl, tracerColor.get());

					double x = MathHelper.lerp(event.tickDelta, entity.lastRenderX, entity.getX()) - entity.getX();
					double y = MathHelper.lerp(event.tickDelta, entity.lastRenderY, entity.getY()) - entity.getY();
					double z = MathHelper.lerp(event.tickDelta, entity.lastRenderZ, entity.getZ()) - entity.getZ();

					Box box = entity.getBoundingBox();
					event.renderer.box(x + box.minX, y + box.minY, z + box.minZ, x + box.maxX, y + box.maxY, z + box.maxZ, sideColor.get(), lineColor.get(), shapeMode.get(), 0);
				}

			}
		}
	}
}

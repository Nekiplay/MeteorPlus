package nekiplay.meteorplus.features.modules;

import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockIterator;
import meteordevelopment.orbit.EventHandler;
import nekiplay.meteorplus.MeteorPlus;
import nekiplay.meteorplus.mixin.minecraft.MobSpawnerBlockEntityAccessor;
import nekiplay.meteorplus.mixin.minecraft.MobSpawnerLogicAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.datafixer.fix.EntityCustomNameToTextFix;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.MobSpawnerLogic;
import org.joml.Vector3d;

import java.util.*;

public class SpawnerESP extends Module {
	public SpawnerESP() {
		super(MeteorPlus.CATEGORY, "spawner-esp", "ESP for spawners");
	}

	private final SettingGroup settingGroup = settings.getDefaultGroup();
	private final SettingGroup sgRender = settings.createGroup("Render");

	public final Setting<Set<EntityType<?>>> entities = settingGroup.add(new EntityTypeListSetting.Builder()
		.name("entities")
		.description("Entities to render.")
		.onlyAttackable()
		.build()
	);

	private final Setting<Double> scale = sgRender.add(new DoubleSetting.Builder()
		.name("scale")
		.description("The scale of the nametag.")
		.defaultValue(1.1)
		.min(0.1)
		.build()
	);

	private final Setting<SettingColor> background = sgRender.add(new ColorSetting.Builder()
		.name("background-color")
		.description("The color of the nametag background.")
		.defaultValue(new SettingColor(0, 0, 0, 75))
		.build()
	);

	private final Setting<SettingColor> nameColor = sgRender.add(new ColorSetting.Builder()
		.name("name-color")
		.description("The color of the nametag names.")
		.defaultValue(new SettingColor())
		.build()
	);

	private final Setting<Integer> range = settingGroup.add(new IntSetting.Builder()
		.name("range")
		.description("Move delay.")
		.build()
	);
	private final HashMap<BlockPos, SpawnerObject> spawners = new HashMap<BlockPos, SpawnerObject>();

	@EventHandler
	public void OnTickEvent(TickEvent.Post event) {
		BlockIterator.register(range.get(), range.get(), (blockPos, blockState) -> {
			Block block = blockState.getBlock();
			if (block == Blocks.SPAWNER) {
				var blockEntity = mc.world.getBlockEntity(blockPos);
				MobSpawnerBlockEntity spawnerBlockEntity = (MobSpawnerBlockEntity)blockEntity;
				MobSpawnerBlockEntityAccessor mobSpawnerBlockEntityAccessor = (MobSpawnerBlockEntityAccessor)spawnerBlockEntity;
				MobSpawnerLogic logic = mobSpawnerBlockEntityAccessor.getLogic();
				MobSpawnerLogicAccessor mobSpawnerLogicAccessor = (MobSpawnerLogicAccessor)logic;
				Entity entity = mobSpawnerLogicAccessor.getEntity();
				if (entity != null ) {
					if (entities.get().contains(entity.getType()) && !spawners.containsKey(blockPos)) {
						Vector3d vector3d = new Vector3d(blockPos.getX(), blockPos.getY(), blockPos.getZ());

						SpawnerObject spawnerObject = new SpawnerObject(entity, vector3d);
						spawners.put(blockPos, spawnerObject);
					}
				}
			}
		});

	}

	@EventHandler
	private void onRender2D(Render2DEvent event) {
		boolean shadow = Config.get().customFont.get();

		for (SpawnerObject spawnerObject : spawners.values()) {
			Vector3d nameTagPos = spawnerObject.spawnerPos.add(0, 1, 0);
			Utils.set(nameTagPos, spawnerObject.entity, event.tickDelta);
			if (NametagUtils.to2D(nameTagPos, scale.get())) {
				TextRenderer text = TextRenderer.get();
				NametagUtils.begin(nameTagPos);

				String name = spawnerObject.entity.getType().getName().getString();

				double nameWidth = text.getWidth(name, shadow);
				double heightDown = text.getHeight(shadow);

				double width = nameWidth;
				double widthHalf = width / 2;

				drawBg(-widthHalf, -heightDown, width, heightDown);

				text.beginBig();
				double hX = -widthHalf;
				double hY = -heightDown;

				hX = text.render(name, hX, hY, nameColor.get(), shadow);
				text.end();

				NametagUtils.end(event.drawContext);

			}
		}
	}

	private void drawBg(double x, double y, double width, double height) {
		Renderer2D.COLOR.begin();
		Renderer2D.COLOR.quad(x - 1, y - 1, width + 2, height + 2, background.get());
		Renderer2D.COLOR.render(null);
	}

	public class SpawnerObject {
		public SpawnerObject(Entity entity, Vector3d spawnerPos) {
			this.entity = entity;
			this.spawnerPos = spawnerPos;
		}
		public Entity entity;
		public Vector3d spawnerPos;
	}
}

package olejka.meteorplus.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import olejka.meteorplus.MeteorPlus;

public class JesusPlus extends Module {
	public JesusPlus() {
		super(MeteorPlus.CATEGORY, "Jesus Plus", "Matrix Jesus.");
	}

	private final SettingGroup ALSettings = settings.createGroup("Jesus Plus Settings");

	private final Setting<Mode> mode = ALSettings.add(new EnumSetting.Builder<Mode>()
		.name("Mode")
		.description("Jesus mode.")
		.defaultValue(Mode.MatrixZoom)
		.build()
	);

	public enum Mode {
		MatrixZoom,
		MatrixZoom2
	}

	private final Setting<Double> speed = ALSettings.add(new DoubleSetting.Builder()
		.name("Speed")
		.description("Rescan delay.")
		.defaultValue(1.25)
		.max(2500)
		.sliderRange(0, 2500)
		.build()
	);
	private final Float range = 0.005f;
	private int tick = 0;
	@Override
	public void onActivate() {

	}

	@EventHandler
	private void onTick(TickEvent.Post event) {
		float yaw = mc.player.getYaw();
		Vec3d forward = Vec3d.fromPolar(0, yaw);
		Vec3d right = Vec3d.fromPolar(0, yaw + 90);

		double velX = 0;
		double velZ = 0;
		double s = 0.5;
		double speedValue = speed.get();

		if (mc.options.forwardKey.isPressed()) {
			velX += forward.x * s * speedValue;
			velZ += forward.z * s * speedValue;
		}
		if (mc.options.backKey.isPressed()) {
			velX -= forward.x * s * speedValue;
			velZ -= forward.z * s * speedValue;
		}

		if (mc.options.rightKey.isPressed()) {
			velX += right.x * s * speedValue;
			velZ += right.z * s * speedValue;
		}
		if (mc.options.leftKey.isPressed()) {
			velX -= right.x * s * speedValue;
			velZ -= right.z * s * speedValue;
		}
		if (mc.world.getBlockState(new BlockPos(mc.player.getPos().x, mc.player.getPos().y + range, mc.player.getPos().z)).getBlock() == Blocks.WATER && !mc.player.horizontalCollision) {
			if (mode.get() == Mode.MatrixZoom) {
				((IVec3d) mc.player.getVelocity()).set(velX, 0, velZ);
			}
			else if (mode.get() == Mode.MatrixZoom2) {
				if (tick == 0) {
					((IVec3d) mc.player.getVelocity()).set(velX, 0.030091, velZ);
					tick++;
				}
				else if (tick == 1) {
					((IVec3d) mc.player.getVelocity()).set(velX, -0.030091, velZ);
					tick = 0;
				}
			}
		}
	}
}

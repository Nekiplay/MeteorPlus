package olejka.meteorplus.modules;

import olejka.meteorplus.MeteorPlus;

import meteordevelopment.meteorclient.systems.modules.Module;
//import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.utils.player.ChatUtils;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class AntiXRayBypass extends Module {
	public AntiXRayBypass() {
        super(MeteorPlus.CATEGORY, "anti-xray-bypass", "Bypasses anti XRay systems like Orebfuscator and XRayBlocker");
	}

	public final SettingGroup sgGeneral = settings.getDefaultGroup();

	private final Setting<Integer> radius =  sgGeneral.add(new IntSetting.Builder()
		.name("radius")
		.description("Set the scan radius")
		.defaultValue(10)
		.min(2)
		.max(30)
		.sliderMin(2)
		.sliderMax(30)
		.build()
	);

	private final Setting<Integer> delay =  sgGeneral.add(new IntSetting.Builder()
		.name("delay")
		.description("Delay before scan.")
		.defaultValue(5)
		.min(5)
		.max(25)
		.sliderMin(5)
		.sliderMax(25)
		.build()
	);

	// private final Setting<Boolean> baritone = sgGeneral.add(new BoolSetting.Builder()
    //         .name("baritone")
    //         .description("Set baritone ore positions to the simulated ones. // indev")
    //         .defaultValue(false)
    //         .build()
    // );

	private Thread worker;

	@Override
	public void onActivate() {
		runScan();
	}

	@Override
	public void onDeactivate() {
		worker.interrupt();
	}

	private void runScan() {
        ClientPlayNetworkHandler con = mc.getNetworkHandler();
        if (con == null) return;
		int r = radius.get();

        assert mc.player != null;
        BlockPos pos = mc.player.getBlockPos();
		ChatUtils.info("AXB", "Task started!");

		worker = new Thread(() -> {
			for(int dy = -r; dy <= r; dy++) {
				if ((pos.getY() + dy) < 0 || (pos.getY() + dy) > 255) continue;

				for(int dz = -r; dz <= r; dz++) {
					for(int dx = -r; dx <= r; dx++) {

						PlayerActionC2SPacket packet = new PlayerActionC2SPacket(
							PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK,
							new BlockPos(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz), Direction.UP
						);
						con.sendPacket(packet);

						try {
							Thread.sleep(delay.get());
						} catch (Exception e) {
							e.printStackTrace();
							ChatUtils.info("AXB", "Task interrupted!");
							return;
						}
					}
				}
            }
			ChatUtils.info("AXB", "Task finished!");
		});

		worker.start();
    }
}

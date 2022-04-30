package olejka.meteorplus.modules;

import meteordevelopment.meteorclient.events.entity.player.BreakBlockEvent;
import meteordevelopment.meteorclient.events.entity.player.StartBreakingBlockEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import olejka.meteorplus.MeteorPlus;

import java.util.ArrayList;

public class AntiLava extends Module {
	public AntiLava() {
		super(MeteorPlus.CATEGORY, "Anti Lava", "Save you from lava.");
	}

	private final SettingGroup ALSettings = settings.createGroup("Anti Lava Settings");

	public final Setting<Boolean> replaceLava = ALSettings.add(new BoolSetting.Builder()
		.name("Replace lava")
		.description("Place blocks in lava in offhand.")
		.defaultValue(false)
		.build()
	);

	public final Setting<Boolean> debug = ALSettings.add(new BoolSetting.Builder()
		.name("Debug")
		.description("Print chat messages.")
		.defaultValue(false)
		.build()
	);

	private double distanceToBlock(BlockPos pos)
	{
		if (mc.player != null) {
			return mc.player.squaredDistanceTo(new Vec3d(pos.getX(), pos.getY(), pos.getZ()));
		} else {
			return 0;
		}
	}

	private void replaceLava(ArrayList<BlockPos> lavaBlocks)
	{
		for (BlockPos pos : lavaBlocks) {
			if (distanceToBlock(pos) <= 4.5) {
				Direction placeSide = BlockUtils.getPlaceSide(pos);
				ClientPlayNetworkHandler conn = mc.getNetworkHandler();
				if (conn != null) {
					BlockUtils.place(pos, Hand.OFF_HAND, 0, false, 0, false, false, false);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	private void onStartBreakingBlock(StartBreakingBlockEvent event) {
		ArrayList<BlockPos> lavaBlocks = isExposedLava(event.blockPos);
		if (lavaBlocks.size() > 0) {
			mc.options.keyAttack.setPressed(false);
			event.setCancelled(true);
			if (replaceLava.get()) {
				replaceLava(lavaBlocks);
			}
			if (debug.get()) {
				info("Lava block nearbly");
			}
		}
	}

	private ArrayList<BlockPos> isExposedLava(BlockPos pos)
	{
		ArrayList<BlockPos> blocks = new ArrayList<>();
		if (mc.world != null) {
			if (mc.world.getBlockState(pos.add(1, 0, 0)).getBlock() == Blocks.LAVA) {
				blocks.add(pos.add(1, 0, 0));
			}
			else if (mc.world.getBlockState(pos.add(-1, 0, 0)).getBlock() == Blocks.LAVA) {
				blocks.add(pos.add(-1, 0, 0));
			}
			else if (mc.world.getBlockState(pos.add(0, 1, 0)).getBlock() == Blocks.LAVA) {
				blocks.add(pos.add(0,1, 0));
			}
			else if (mc.world.getBlockState(pos.add(0, -1, 0)).getBlock() == Blocks.LAVA) {
				blocks.add(pos.add(0,-1, 0));
			}
			else if (mc.world.getBlockState(pos.add(0, 0, 1)).getBlock() == Blocks.LAVA) {
				blocks.add(pos.add(0,0, 1));
			}
			else if (mc.world.getBlockState(pos.add(0, 0, -1)).getBlock() == Blocks.LAVA) {
				blocks.add(pos.add(0,0, -1));
			}
		}
		return blocks;
	}
}

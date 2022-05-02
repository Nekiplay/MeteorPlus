package olejka.meteorplus.modules;

import meteordevelopment.meteorclient.events.entity.player.BreakBlockEvent;
import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.events.entity.player.StartBreakingBlockEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.ClientPlayerInteractionManagerAccessor;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.misc.Vec3;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.MovementType;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import olejka.meteorplus.MeteorPlus;
import olejka.meteorplus.utils.BlockHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AntiLava extends Module {
	public AntiLava() {
		super(MeteorPlus.CATEGORY, "Anti Lava", "Save you from lava.");
	}

	private final SettingGroup ALSettings = settings.createGroup("Anti Lava Settings");

	public final Setting<Boolean> replaceLava = ALSettings.add(new BoolSetting.Builder()
		.name("Replace lava")
		.description("Place blocks in lava in offhand.")
		.defaultValue(true)
		.build()
	);

	public final Setting<Boolean> moveReplaceLava = ALSettings.add(new BoolSetting.Builder()
		.name("Move replace lava")
		.description("Replace lava in movement.")
		.defaultValue(true)
		.visible(replaceLava::get)
		.build()
	);

	private final Setting<Integer> delay = ALSettings.add(new IntSetting.Builder()
		.name("Repalce delay")
		.description("Delay for replace lava.")
		.defaultValue(0)
		.min(0)
		.visible(replaceLava::get)
		.sliderRange(0, 20)
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

	ArrayList<BlockPos> lava = new ArrayList<BlockPos>();

	private Integer tick = 0;

	@EventHandler
	private void onTickEvent(TickEvent.Post event) {
		if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.BLOCK) {
			if (replaceLava.get()) {
				synchronized (lava) {
					Iterator<BlockPos> iterator = lava.iterator();
					if (iterator.hasNext())
					{
						if (tick == 0)
						{
							BlockPos block = iterator.next();
							BlockUtils.place(block, Hand.OFF_HAND, mc.player.getInventory().selectedSlot, false, 0, false, false, false);
							iterator.remove();
							tick = delay.get();
						}
						else
						{
							tick--;
						}
					}
					else if (moveReplaceLava.get())
					{
						BlockPos under = mc.player.getBlockPos().add(0, -1, 0);
						List<BlockPos> blocks = getBlocks(under, 2, 0);
						for (BlockPos pos : blocks) {
							if (mc.world.getBlockState(pos).getMaterial() == Material.LAVA) {
								lava.add(pos);
							}
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	private void onStartBreakingBlock(StartBreakingBlockEvent event) {
		ArrayList<BlockPos> lavaBlocks = isExposedLava(event.blockPos);
		if (lavaBlocks.size() > 0) {
			mc.options.attackKey.setPressed(false);
			event.setCancelled(true);
			synchronized (lava) {
				lava = isExposedLava(event.blockPos);
			}
			if (debug.get()) {
				info("Lava block nearbly");
			}
		}
	}

	private List<BlockPos> getBlocks(BlockPos startPos, int y_radius, int radius)
	{
		List<BlockPos> temp = new ArrayList<>();
		for (int dy = -y_radius; dy <= y_radius; dy++) {
			for (int dz = -radius; dz <= radius; dz++) {
				for (int dx = -radius; dx <= radius; dx++) {
					BlockPos blockPos = new BlockPos(startPos.getX() + dx, startPos.getY() + dy, startPos.getZ() + dz);
					if (EntityUtils.isInRenderDistance(blockPos)) {
						temp.add(blockPos);
					}
				}
			}
		}
		return temp;
	}

	private ArrayList<BlockPos> isExposedLava(BlockPos pos)
	{
		ArrayList<BlockPos> blocks = new ArrayList<>();
		if (mc.world != null) {
			if (mc.world.getBlockState(pos).getMaterial() == Material.LAVA) {
				blocks.add(pos);
			}
			if (mc.world.getBlockState(pos.add(1, 0, 0)).getMaterial() == Material.LAVA) {
				blocks.add(pos.add(1, 0, 0));
			}
			if (mc.world.getBlockState(pos.add(-1, 0, 0)).getMaterial() == Material.LAVA) {
				blocks.add(pos.add(-1, 0, 0));
			}
			if (mc.world.getBlockState(pos.add(0, 1, 0)).getMaterial() == Material.LAVA) {
				blocks.add(pos.add(0,1, 0));
			}
			if (mc.world.getBlockState(pos.add(0, -1, 0)).getMaterial() == Material.LAVA) {
				blocks.add(pos.add(0,-1, 0));
			}
			if (mc.world.getBlockState(pos.add(0, 0, 1)).getMaterial() == Material.LAVA) {
				blocks.add(pos.add(0,0, 1));
			}
			if (mc.world.getBlockState(pos.add(0, 0, -1)).getMaterial() == Material.LAVA) {
				blocks.add(pos.add(0,0, -1));
			}
		}
		return blocks;
	}
}

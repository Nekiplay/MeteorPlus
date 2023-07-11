package olejka.meteorplus.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import olejka.meteorplus.MeteorPlus;
import olejka.meteorplus.modules.spider.SpiderModes;
import olejka.meteorplus.utils.ElytraUtils;

public class MarioMode extends Module {
	public MarioMode() {
		super(MeteorPlus.CATEGORY, "mario-mode", "Auto clip on jump.");
	}
	public enum MarioModes {
		Clip,
		ElytraClip
	}
	private final SettingGroup sgGeneral = settings.getDefaultGroup();
	public final Setting<MarioModes> mode = sgGeneral.add(new EnumSetting.Builder<MarioModes>()
		.name("mode")
		.description("The method of applying mario mode.")
		.defaultValue(MarioModes.ElytraClip)
		.build()
	);
	public final Setting<Boolean> center = sgGeneral.add(new BoolSetting.Builder()
		.name("auto center")
		.description("Auto center player on block.")
		.defaultValue(true)
		.build()
	);
	@EventHandler
	public void onAction(TickEvent.Pre event) {
		if (ticks == -1) {
			if (mc.options.jumpKey.isPressed()) {
				double tempblocks= findBlock(true, 15);
				if (tempblocks != 0 && center.get()) {
					double x = MathHelper.floor(mc.player.getX());
					double z = MathHelper.floor(mc.player.getZ());
					mc.player.setPosition(x, mc.player.getY(), z);
					mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY(), mc.player.getZ(), mc.player.isOnGround()));
				}
				if (tempblocks != 0) {
					if (mode.get() == MarioModes.ElytraClip) {

						Vec3d current = mc.player.getVelocity();
						mc.player.setVelocity(current.x, 0, current.y);
						mc.options.jumpKey.setPressed(false);
						ticks = 0;
						blocks = tempblocks;
					}
					else {
						mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() + tempblocks, mc.player.getZ(), true));
						mc.player.setPosition(mc.player.getX(), mc.player.getY() + tempblocks, mc.player.getZ());
					}
				}
			}
			else if (mc.options.sneakKey.isPressed()) {
				double tempblocks= findBlock(false, 15);
				if (tempblocks != 0 && center.get()) {
					double x = MathHelper.floor(mc.player.getX());
					double z = MathHelper.floor(mc.player.getZ());
					mc.player.setPosition(x, mc.player.getY(), z);
					mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY(), mc.player.getZ(), mc.player.isOnGround()));
				}
				if (tempblocks != 0) {
					if (mode.get() == MarioModes.ElytraClip) {

						Vec3d current = mc.player.getVelocity();
						mc.player.setVelocity(current.x, 0, current.y);
						mc.options.jumpKey.setPressed(false);
						ticks = 0;
						blocks = tempblocks;

					}
					else {
						mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() - tempblocks, mc.player.getZ(), true));
						mc.player.setPosition(mc.player.getX(), mc.player.getY() - tempblocks, mc.player.getZ());
					}
				}
			}
		}
	}

	private Block getBlock(BlockPos pos) {
		return mc.player.getWorld().getBlockState(pos).getBlock();
	}

	private boolean isValidBlock(BlockPos pos) {
		Block block = getBlock(pos);
		return block == Blocks.AIR || block == Blocks.CAVE_AIR;
	}

	private double findBlock(boolean up, int maximum) {
		if (up) {
			BlockPos pos = mc.player.getBlockPos();
			for (int i = 0; i <= maximum; i++) {
				if (isValidBlock(pos.add(0, i, 0))
					&& isValidBlock(pos.add(0, i + 1, 0))
					&& !isValidBlock(pos.add(0, i - 1, 0))
				) {
					return i;
				}
			}
		}
		else {
			BlockPos pos = mc.player.getBlockPos();
			for (int i = -maximum; i <= 1; i++) {
				if (isValidBlock(pos.add(0, i, 0))
					&& isValidBlock(pos.add(0, i + 1, 0))
					&& !isValidBlock(pos.add(0, i - 1, 0))
				) {
					return i;
				}
			}
		}
		return 0;
	}

	private int ticks = -1;
	private int slot = -1;
	private double blocks = 0;

	@EventHandler
	private void onTick(TickEvent.Pre event) {
		clip(blocks);
	}

	private void clip(double blocks) {
		if (blocks != 0) {
			ClientPlayerEntity player = mc.player;
			assert player != null;
			switch (ticks) {
				case 0: {
					FindItemResult elytra = InvUtils.find(Items.ELYTRA);
					slot = elytra.slot();
					InvUtils.move().from(slot).toArmor(2);
					ticks++;
				}
				case 1: {
					mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(false));
					ticks++;
				}
				case 2: {
					mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(false));
					ticks++;
				}
				case 3: {
					ElytraUtils.startFly();
					ticks++;
				}
				case 4: {
					player.setPosition(player.getX(), player.getY() + blocks, player.getZ());
					mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(player.getX(), player.getY() + blocks, player.getZ(), false));
					ticks++;
				}
				case 5: {
					ElytraUtils.startFly();
					ticks++;
				}
				case 6: {
					ticks = -1;
					InvUtils.move().fromArmor(2).to(slot);
				}
			}
		}
	}
}

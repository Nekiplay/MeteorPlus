package olejka.meteorplus.modules;

import meteordevelopment.meteorclient.events.meteor.KeyEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.CollisionShapeEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.PlayerMoveC2SPacketAccessor;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;
import olejka.meteorplus.MeteorPlus;

public class SpiderPlus extends Module {
	public SpiderPlus() {
		super(MeteorPlus.CATEGORY, "spider-plus", "Matrix spider.");
	}
	private final SettingGroup sgGeneral = settings.getDefaultGroup();

	private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
		.name("Mode")
		.description("Spider mode.")
		.defaultValue(Mode.Matrix)
		.build()
	);

	private final Setting<Boolean> safeMode = sgGeneral.add(new BoolSetting.Builder()
		.name("safe-mode")
		.description("Prevent kicks and bans.")
		.defaultValue(true)
		.visible(() -> mode.get() == Mode.Vulcan)
		.build()
	);

	public enum Mode
	{
		Matrix,
		Vulcan,
		VulcanCollision,
	}

	//region Matrix and Vulcan
	@EventHandler
	private void onSendPacket(PacketEvent.Send event) {
		work(event.packet);
	}
	@EventHandler
	private void onSentPacket2(PacketEvent.Sent event) {
		work(event.packet);
	}

	private void work(Packet<?> packet) {
		if (modify) {
			if (packet instanceof PlayerMoveC2SPacket move) {
				double y = mc.player.getY();
				y = move.getY(y);

				//if (YGround(y, 0.0, 0.1)) {
				//	((PlayerMoveC2SPacketAccessor) packet).setOnGround(true);
				//}
				if (YGround(y, RGround(startY) - 0.1, RGround(startY) + 0.1)) {
					((PlayerMoveC2SPacketAccessor) packet).setOnGround(true);
				}
				if (mc.player.isOnGround() && block) {
					block = false;
					startY = mc.player.getPos().y;
					start = false;
				}
			}
		}
		else {
			if (mc.player.isOnGround() && block) {
				block = false;
				startY = mc.player.getPos().y;
				start = false;
			}
		}
	}

	private int tick = 0;
	private int tick2 = 0;

	@Override
	public void onActivate() {
		tick = 0;
		start = false;
		modify = false;

		startY = mc.player.getPos().y;
	}

	private boolean modify = false;
	private boolean start = false;

	private double startY = 0;
	private double lastY = 0;

	private boolean YGround(double height, double min, double max) {
		String yString = String.valueOf(height);
		yString = yString.substring(yString.indexOf("."));
		double y = Double.parseDouble(yString);
		if (y >= min && y <= max) {
			return true;
		}
		else {
			return false;
		}
	}

	private double RGround(double height) {
		String yString = String.valueOf(height);
		yString = yString.substring(yString.indexOf("."));
		double y = Double.parseDouble(yString);
		return y;
	}

	private boolean block = false;
	private double coff = 0.0000000000326;

	@EventHandler
	private void onTickPre(TickEvent.Pre event) {
		if (modify) {
			ClientPlayerEntity player = mc.player;
			double y = player.getPos().y;
			if (lastY == y && tick > 1) {
				block = true;
			} else {
				lastY = y;
			}
		}
	}

	@Override
	public String getInfoString() {
		if (modify && mode.get() == Mode.Vulcan) {
			return mode.get().name() + " | " + typeStarted.name();
		}
		else { return mode.get().name(); }
	}

	private TypeStarted getType(double startY) {
		TypeStarted temp = TypeStarted.Air;
		double y = RGround(startY);
		if (mc.player.isOnGround()) {
			temp = TypeStarted.Block;
			if (mc.world.getBlockState(mc.player.getBlockPos()).getBlock() instanceof SlabBlock) {
				temp = TypeStarted.Slab;
			}
		}
		else {
			temp = TypeStarted.Air;
		}
		return temp;
	}

	private enum TypeStarted
	{
		Block,
		Slab,
		Air,
	}

	private TypeStarted typeStarted = TypeStarted.Air;

	@EventHandler
	private void onTick(TickEvent.Post event) {
		if (mode.get() == Mode.Vulcan || mode.get() == Mode.Matrix) {
			ClientPlayerEntity player = mc.player;
			Vec3d pl_velocity = player.getVelocity();
			Vec3d pos = player.getPos();
			ClientPlayNetworkHandler h = mc.getNetworkHandler();
			modify = player.horizontalCollision;
			if (mc.player.isOnGround()) {
				block = false;
				startY = mc.player.getPos().y;
				start = false;
			}
			if (player.horizontalCollision) {
				if (!start) {
					start = true;
					startY = mc.player.getPos().y;
					lastY = mc.player.getY();
					typeStarted = getType(startY);
				}
				if (!block) {
					if (tick == 0) {
						mc.player.setVelocity(pl_velocity.x, 0.41999998688698, pl_velocity.z);
						tick = 1;
					} else if (tick == 1) {
						mc.player.setVelocity(pl_velocity.x, 0.41999998688698 - 0.08679999325 - coff, pl_velocity.z);
						tick = 2;
					} else if (tick == 2) {
						mc.player.setVelocity(pl_velocity.x, 0.41999998688698 - 0.17186398826 - coff, pl_velocity.z);
						tick = 0;
					}
					if (typeStarted == TypeStarted.Block) {
						if (mc.player.getPos().y >= startY + 2 && mode.get() == Mode.Vulcan) {
							block = true;
						}
					} else if (typeStarted == TypeStarted.Air) {
						if (mc.player.getPos().y >= startY + 1.5 && mode.get() == Mode.Vulcan) {
							block = true;
						}
					} else if (typeStarted == TypeStarted.Slab) {
						if (mc.player.getPos().y >= startY + 2.5 && mode.get() == Mode.Vulcan) {
							block = true;
						}
					}
					tick2++;
				}

			} else {
				modify = false;
				tick = 0;
			}
		}
	}
	//endregion
	//region Vulcan collision
	@EventHandler
	private void onCollision(CollisionShapeEvent event) {
		if (mode.get() == Mode.VulcanCollision) {
			if (event.pos.getY() >= mc.player.getPos().y ) {
				if (event.type != CollisionShapeEvent.CollisionType.BLOCK || mc.player == null) return;
				event.shape = VoxelShapes.empty();
			}
		}
	}
	//endregion
}

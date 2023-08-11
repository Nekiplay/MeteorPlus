package olejka.meteorplus.modules;

import baritone.api.BaritoneAPI;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.Pool;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.world.BlockIterator;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.meteorclient.utils.world.Dimension;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import olejka.meteorplus.MeteorPlus;
import olejka.meteorplus.utils.BlockHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AutoObsidianMine extends Module {
	public AutoObsidianMine() {
		super(MeteorPlus.CATEGORY, "Auto-obsidian-mine", "Automatically mine obsidian.");
	}

	private final SettingGroup sgGeneral = settings.getDefaultGroup();

	public enum WorkingMode
	{
		PortalsVanila,
		PortalHomes
	}

	private final Setting<WorkingMode> workingMode = sgGeneral.add(new EnumSetting.Builder<WorkingMode>()
		.name("mode")
		.description("Working mode.")
		.defaultValue(WorkingMode.PortalsVanila)
		.build()
	);

	private final Setting<BlockPos> mainPortalPosition = sgGeneral.add(new BlockPosSetting.Builder()
		.name("portal location 1")
		.description("the position of the portal to hell")
		.visible(() -> workingMode.get() == WorkingMode.PortalsVanila)
		.build()
	);

	private final Setting<BlockPos> twoPortalPosition = sgGeneral.add(new BlockPosSetting.Builder()
		.name("portal location 2")
		.description("portal position in hell for new portal generations")
		.visible(() -> workingMode.get() == WorkingMode.PortalsVanila)
		.build()
	);

	private final Setting<String> command = sgGeneral.add(new StringSetting.Builder()
		.name("command")
		.description("Send command.")
		.defaultValue("/home")
		.visible(() -> workingMode.get() == WorkingMode.PortalHomes)
		.build()
	);

	private final Setting<Integer> delayCommand = sgGeneral.add(new IntSetting.Builder()
		.name("command-delay")
		.description("Ticks delay.")
		.defaultValue(700)
		.visible(() -> workingMode.get() == WorkingMode.PortalHomes)
		.build()
	);

	private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
		.name("Mining-delay")
		.description("Mining delay.")
		.defaultValue(4)
		.build()
	);

	private final Setting<Boolean> noBaritoneBreaking = sgGeneral.add(new BoolSetting.Builder()
		.name("disable-baritone-breaking-if-not-mine-portal")
		.description("No break blocks if is not mining portal.")
		.defaultValue(true)
		.build()
	);

	private final Setting<Boolean> noBaritonePlacing = sgGeneral.add(new BoolSetting.Builder()
		.name("disable-baritone-place")
		.description("No place blocks.")
		.defaultValue(true)
		.build()
	);

	private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
		.name("rotate")
		.description("Rotate to breaking block.")
		.defaultValue(false)
		.build()
	);

	private final Setting<Boolean> swingHand = sgGeneral.add(new BoolSetting.Builder()
		.name("swing-hand")
		.description("Swing hand client side.")
		.defaultValue(true)
		.build()
	);

	private int commandDelay = 0;

	@EventHandler
	private void onTick(TickEvent.Pre event) {

		if (commandDelay <= delayCommand.get()) {
			commandDelay++;
		}
		if (workingMode.get() == WorkingMode.PortalHomes) {
			if (PlayerUtils.getDimension() == Dimension.Overworld) {
				commandDelay = 0;
				isMine = false;
				if (!BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().hasPath() && !BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing()) {
					BaritoneAPI.getProvider().getPrimaryBaritone().getCommandManager().execute("goto nether_portal");
				}
			} else if (PlayerUtils.getDimension() == Dimension.Nether) {
				List<BlockPos> obsidians = getPortalBlocks();
				isMine = true;
				if ((obsidians.size() == 0 || blocks.size() == 0)) {
					if (mc.player != null && commandDelay >= delayCommand.get()) {
						ChatUtils.sendPlayerMsg(command.get());
						commandDelay = 0;
					}
				}
			}
		}
		else if (workingMode.get() == WorkingMode.PortalsVanila) {
			if (PlayerUtils.getDimension() == Dimension.Overworld) {

				BlockPos to =  twoPortalPosition.get();
				BlockPos dis = BlockHelper.opposite(to, Dimension.Nether);
				double distance = Math.sqrt(PlayerUtils.squaredDistanceTo(dis.getX(), mc.player.getY(), dis.getZ()));
				if (distance <= 20) {
					List<BlockPos> obsidians = getPortalBlocks();
					Block down = mc.world.getBlockState(mc.player.getBlockPos().add(0, -1, 0)).getBlock();
					if (obsidians.size() == 0) {
						isMine = false;
					}
					else if (down == Blocks.OBSIDIAN) {
						isMine = true;
					}
				}
				else {
					isMine = false;
				}


				if (!isMine) {
					if (noBaritoneBreaking.get()) {
						BaritoneAPI.getSettings().allowBreak.value = false;
					}
					if (!BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().hasPath() && !BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing()) {
						to =  mainPortalPosition.get();
						BaritoneAPI.getProvider().getPrimaryBaritone().getCommandManager().execute("goto " + to.getX() + " " + to.getY() + " " + to.getZ());
					}
				}
				else if (BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().hasPath() || BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing() || BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().getPath().isPresent()) {
					BaritoneAPI.getSettings().allowBreak.value = true;
					BaritoneAPI.getProvider().getPrimaryBaritone().getCommandManager().execute("stop");
				}
			}
			else if (PlayerUtils.getDimension() == Dimension.Nether) {
				isMine = false;
				if (noBaritoneBreaking.get()) {
					BaritoneAPI.getSettings().allowBreak.value = false;
				}
				if (!BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().hasPath() && !BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing()) {
					BlockPos to =  twoPortalPosition.get();
					BaritoneAPI.getProvider().getPrimaryBaritone().getCommandManager().execute("goto " + to.getX() + " " + to.getY() + " " + to.getZ());
				}
			}
		}
	}

	private final AutoObsidianMine.Shape shape = Shape.Cube;

	private final AutoObsidianMine.Mode mode = Mode.All;

	private final Double range = 5.5;

	private final Integer maxBlocksPerTick = 1;

	private final AutoObsidianMine.SortMode sortMode = SortMode.Closest;

	private final Pool<BlockPos.Mutable> blockPosPool = new Pool<>(BlockPos.Mutable::new);
	private final List<BlockPos.Mutable> blocks = new ArrayList<>();

	private boolean firstBlock;
	private final BlockPos.Mutable lastBlockPos = new BlockPos.Mutable();

	private int timer;
	private int noBlockTimer;

	private final BlockPos.Mutable pos1 = new BlockPos.Mutable(); // Rendering for cubes
	private final BlockPos.Mutable pos2 = new BlockPos.Mutable();
	private Box box;
	int maxh = 0;
	int maxv = 0;
	private boolean baritoneBreakSaved = false;
	private boolean baritonePlaceSaved = false;

	@Override
	public void onActivate() {
		commandDelay = delayCommand.get();
		firstBlock = true;
		timer = 0;
		noBlockTimer = 0;
		if (noBaritoneBreaking.get()) {
			baritoneBreakSaved = BaritoneAPI.getSettings().allowBreak.value;
		}
		if (noBaritonePlacing.get()) {
			baritonePlaceSaved = BaritoneAPI.getSettings().allowPlace.value;
		}
	}

	@Override
	public void onDeactivate() {
		if (BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().hasPath() || BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing()) {
			BaritoneAPI.getProvider().getPrimaryBaritone().getCommandManager().execute("stop");
		}
		if (noBaritoneBreaking.get()) {
			BaritoneAPI.getSettings().allowBreak.value = baritoneBreakSaved;
		}
		if (noBaritonePlacing.get()) {
			BaritoneAPI.getSettings().allowPlace.value = baritonePlaceSaved;
		}
	}

	private boolean isMine = false;

	private void rotate(BlockPos target) {
		Rotations.rotate(Rotations.getYaw(target), Rotations.getPitch(target), null);
	}

	@EventHandler
	private void onTickPre(TickEvent.Pre event) {
		// Update timer
		if (timer > 0) {
			timer--;
			return;
		}

		// Calculate some stuff
		assert mc.player != null;
		double pX = mc.player.getX();
		double pY = mc.player.getY();
		double pZ = mc.player.getZ();

		double rangeSq = Math.pow(range, 2);

		if (shape == AutoObsidianMine.Shape.UniformCube) Math.round(range);

		// Some render stuff

		double pX_ = pX;
		double pZ_ = pZ;
		int r = (int) Math.round(range);

		if (shape == AutoObsidianMine.Shape.UniformCube) {
			pX_ += 1; // weired position stuff
			pos1.set(pX_ - r, pY - r + 1, pZ - r + 1); // down
			pos2.set(pX_ + r - 1, pY + r, pZ + r); // up
		} else {
			int direction = Math.round((mc.player.getRotationClient().y % 360) / 90);
			direction = (direction == 4 || direction == -4) ? 0 : direction;
			direction = direction == -2 ? 2 : direction == -1 ? 3 : direction == -3 ? 1 : direction; // stupid java not doing modulo shit

			// direction == 1
			int range_down = 0;
			int range_right = 2;
			int range_forward = 2;
			pos1.set(pX_ - (range_forward), Math.ceil(pY) - range_down, pZ_ - range_right); // down
			int range_up = 6;
			int range_back = 2;
			int range_left = 2;
			pos2.set(pX_ + range_back + 1, Math.ceil(pY + range_up + 1), pZ_ + range_left + 1); // up

			// Only change me if you want to mess with 3D rotations:
			if (direction == 2) {
				pX_ += 1;
				pZ_ += 1;
				pos1.set(pX_ - (range_left + 1), Math.ceil(pY) - range_down, pZ_ - (range_forward + 1)); // down
				pos2.set(pX_ + range_right, Math.ceil(pY + range_up + 1), pZ_ + range_back); // up
			} else if (direction == 3) {
				pX_ += 1;
				pos1.set(pX_ - (range_back + 1), Math.ceil(pY) - range_down, pZ_ - range_left); // down
				pos2.set(pX_ + range_forward, Math.ceil(pY + range_up + 1), pZ_ + range_right + 1); // up
			} else if (direction == 0) {
				pZ_ += 1;
				pX_ += 1;
				pos1.set(pX_ - (range_right + 1), Math.ceil(pY) - range_down, pZ_ - (range_back + 1)); // down
				pos2.set(pX_ + range_left, Math.ceil(pY + range_up + 1), pZ_ + range_forward); // up
			}

			// get largest horizontal
			maxh = 1 + Math.max(Math.max(Math.max(range_back, range_right), range_forward), range_left);
			maxv = 1 + Math.max(range_up, range_down);
		}

		if (mode == AutoObsidianMine.Mode.Flatten) {
			pos1.setY((int) Math.floor(pY));
		}
		box = new Box(pos1, pos2);


		// Find blocks to break
		BlockIterator.register(Math.max((int) Math.ceil(range + 1), maxh), Math.max((int) Math.ceil(range), maxv), (blockPos, blockState) -> {
			// Check for air, unbreakable blocks and distance
			boolean toofarSphere = Utils.squaredDistance(pX, pY, pZ, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5) > rangeSq;
			boolean toofarUniformCube = maxDist(Math.floor(pX), Math.floor(pY), Math.floor(pZ), blockPos.getX(), blockPos.getY(), blockPos.getZ()) >= range;
			boolean toofarCube = !box.contains(Vec3d.ofCenter(blockPos));

//            MeteorClient.LOG.info(box + " " + blockPos + " " + box.contains(Vec3d.ofCenter(blockPos)));

			if (!BlockUtils.canBreak(blockPos, blockState)
				|| (toofarSphere && shape == AutoObsidianMine.Shape.Sphere)
				|| (toofarUniformCube && shape == AutoObsidianMine.Shape.UniformCube)
				|| (toofarCube && shape == AutoObsidianMine.Shape.Cube))
				return;

			// Flatten
			if (mode == AutoObsidianMine.Mode.Flatten && blockPos.getY() < Math.floor(mc.player.getY())) return;

			// Smash
			if (mode == AutoObsidianMine.Mode.Smash && blockState.getHardness(mc.world, blockPos) != 0) return;

			// Check for selected
			if (blockState.getBlock() == Blocks.OBSIDIAN || blockState.getBlock() == Blocks.FIRE) {
				blocks.add(blockPosPool.get().set(blockPos));
			}
		});

		// Break block if found
		BlockIterator.after(() -> {

			if (isMine) {
				if (sortMode != AutoObsidianMine.SortMode.None) {
					if (sortMode == AutoObsidianMine.SortMode.Closest || sortMode == AutoObsidianMine.SortMode.Furthest)
						blocks.sort(Comparator.comparingDouble(value -> Utils.squaredDistance(pX, pY, pZ, value.getX() + 0.5, value.getY() + 0.5, value.getZ() + 0.5) * (sortMode == AutoObsidianMine.SortMode.Closest ? 1 : -1)));
					else if (sortMode == AutoObsidianMine.SortMode.TopDown)
						blocks.sort(Comparator.comparingDouble(value -> -1 * value.getY()));
				}

				// Check if some block was found
				if (blocks.isEmpty()) {
					// If no block was found for long enough then set firstBlock flag to true to not wait before breaking another again
					if (noBlockTimer++ >= delay.get()) firstBlock = true;
					return;
				} else {
					noBlockTimer = 0;
				}

				// Update timer
				if (!firstBlock && !lastBlockPos.equals(blocks.get(0))) {
					timer = delay.get();

					firstBlock = false;
					lastBlockPos.set(blocks.get(0));

					if (timer > 0) return;
				}

				// Break
				int count = 0;

				for (BlockPos block : blocks) {
					if (count >= maxBlocksPerTick) break;
					if (rotate.get()) {
						rotate(block);
					}
					boolean canInstaMine = BlockUtils.canInstaBreak(block);

					BlockUtils.breakBlock(block, swingHand.get());

					lastBlockPos.set(block);

					count++;
					if (!canInstaMine) break;
				}

				firstBlock = false;

				// Clear current block positions
				for (BlockPos.Mutable blockPos : blocks) blockPosPool.free(blockPos);
				blocks.clear();
			}
		});
	}

	public enum Mode {
		All,
		Flatten,
		Smash
	}

	public enum SortMode {
		None,
		Closest,
		Furthest,
		TopDown

	}

	public enum Shape {
		Cube,
		UniformCube,
		Sphere
	}


	public static double maxDist(double x1, double y1, double z1, double x2, double y2, double z2) {
		// Gets the largest X, Y or Z difference, manhattan style
		double dX = Math.ceil(Math.abs(x2 - x1));
		double dY = Math.ceil(Math.abs(y2 - y1));
		double dZ = Math.ceil(Math.abs(z2 - z1));
		return Math.max(Math.max(dX, dY), dZ);
	}

	private List<BlockPos> getPortalBlocks() {
		List<BlockPos> temp = new ArrayList<>();
		for (int i = 0; i < 6; i++) {
			for (int i2 = -4; i2 < 4; i2++) {
				for (int i3 = -4; i3 < 4; i3++) {
					assert mc.player != null;
					assert mc.world != null;
					BlockPos pos = mc.player.getBlockPos().add(i2, i, i3);
					BlockState state = mc.world.getBlockState(pos);
					if (state.getBlock() == Blocks.OBSIDIAN) {
						temp.add(pos);
					}
				}
			}
		}
		return temp;
	}
}

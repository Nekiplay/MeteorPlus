package nekiplay.meteorplus.features.modules.autoobsidianmine.modes;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.Pool;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.world.BlockIterator;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import nekiplay.meteorplus.features.modules.autoobsidianmine.AutoObsidianFarmMode;
import nekiplay.meteorplus.features.modules.autoobsidianmine.AutoObsidianFarmModes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CauldronBlock;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Cauldrons extends AutoObsidianFarmMode {
	public Cauldrons() {
		super(AutoObsidianFarmModes.Cauldrons);
	}

	private final List<BlockPos.Mutable> blocks = new ArrayList<>();
	private boolean firstBlock;
	private int noBlockTimer;
	private final BlockPos.Mutable lastBlockPos = new BlockPos.Mutable();
	private int timer;
	private final Pool<BlockPos.Mutable> blockPosPool = new Pool<>(BlockPos.Mutable::new);
	@Override
	public void onActivate() {
		firstBlock = true;
		timer = 0;
		noBlockTimer = 0;
		collectTimer = 0;
	}
	private int collectTimer = 0;
	private final Portals.SortMode sortMode = Portals.SortMode.Closest;
	@Override
	public void onTickEventPre(TickEvent.Pre event) {
		BlockIterator.register(settings.range.get(), settings.range.get(), (blockPos, blockState) -> {
			Block block = blockState.getBlock();
			if (block == Blocks.LAVA_CAULDRON || block == Blocks.OBSIDIAN) {
				blocks.add(blockPosPool.get().set(blockPos));
			}
		});

		// Collect lava if found
		BlockIterator.after(() -> {
			if (blocks.isEmpty()) {
				// If no block was found for long enough then set firstBlock flag to true to not wait before breaking another again
				if (noBlockTimer++ >= settings.delay.get()) firstBlock = true;
				return;
			} else {
				noBlockTimer = 0;
				double pX = mc.player.getX();
				double pY = mc.player.getY();
				double pZ = mc.player.getZ();
				blocks.sort(Comparator.comparingDouble(value -> Utils.squaredDistance(pX, pY, pZ, value.getX() + 0.5, value.getY() + 0.5, value.getZ() + 0.5) * (sortMode == Portals.SortMode.Closest ? 1 : -1)));
			}

			// Update timer
			if (!firstBlock && !lastBlockPos.equals(blocks.get(0))) {
				timer = settings.delay.get();

				firstBlock = false;
				lastBlockPos.set(blocks.get(0));

				if (timer > 0) return;
			}

			BlockPos placing = settings.lavaPlaceLocation.get();
			BlockState state = mc.world.getBlockState(placing);

			if (state.getBlock() == Blocks.OBSIDIAN) {
				if (BlockUtils.canBreak(placing)) {
					rotate(placing, null);
					BlockUtils.breakBlock(placing, settings.swingHand.get());
				}
			} else {

				FindItemResult bucket = InvUtils.findInHotbar(Items.BUCKET);
				FindItemResult lavaBucket = InvUtils.findInHotbar(Items.LAVA_BUCKET);
				if (bucket.found()) {

					for (BlockPos block : blocks) {
						BlockState state2 = mc.world.getBlockState(block);
						if (state2.getBlock() == Blocks.LAVA_CAULDRON) {
							if (collectTimer >= settings.collectDelay.get()) {
								if (mc.player == null || mc.player.getInventory() == null) return;
								mc.player.getInventory().selectedSlot = bucket.slot();
								mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(bucket.slot()));
								rotate(block, () -> {
									Vec3d hitPos = Vec3d.ofCenter(block);
									Direction side = Direction.DOWN;
									BlockPos neighbour;
									if (side == null) {
										side = Direction.UP;
										neighbour = block;
									} else {
										neighbour = block.offset(side);
										hitPos = hitPos.add((double)side.getOffsetX() * 0.5, (double)side.getOffsetY() * 0.5, (double)side.getOffsetZ() * 0.5);
									}
									BlockHitResult bhr = new BlockHitResult(hitPos, Direction.UP, block, false);
									BlockUtils.interact(bhr, Hand.MAIN_HAND, settings.swingHand.get());
									//mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
								});
								collectTimer = 0;
								break;
							} else {
								collectTimer++;
							}
						}
					}


					firstBlock = false;

					for (BlockPos.Mutable blockPos : blocks) blockPosPool.free(blockPos);
					blocks.clear();
				} else if (lavaBucket.found()) {
					if (state.getBlock() == Blocks.WATER) {
						if (mc.player == null || mc.player.getInventory() == null) return;
						Rotations.rotate(Rotations.getYaw(placing), Rotations.getPitch(placing), 10, true, () -> {
							InvUtils.swap(lavaBucket.slot(), true);
							mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
							InvUtils.swapBack();
						});
					}
				}
			}
		});
	}
	private void rotate(BlockPos target, Runnable action) {
		Rotations.rotate(Rotations.getYaw(target), Rotations.getPitch(target), action);
	}
}

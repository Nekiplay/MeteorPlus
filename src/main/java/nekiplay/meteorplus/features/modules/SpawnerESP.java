package nekiplay.meteorplus.features.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.Pool;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.world.BlockIterator;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import nekiplay.meteorplus.MeteorPlus;
import nekiplay.meteorplus.features.modules.autoobsidianmine.modes.Portals;
import nekiplay.meteorplus.utils.RaycastUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SpawnerBlock;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SpawnerESP extends Module {
	public SpawnerESP() {
		super(MeteorPlus.CATEGORY, "spawner-esp", "ESP for spawners");
	}

	private final SettingGroup settingGroup = settings.getDefaultGroup();

	private final Setting<Integer> range = settingGroup.add(new IntSetting.Builder()
		.name("range")
		.description("Move delay.")
		.build()
	);
	private final List<BlockPos.Mutable> blocks = new ArrayList<>();
	private final Pool<BlockPos.Mutable> blockPosPool = new Pool<>(BlockPos.Mutable::new);

	@EventHandler
	public void OnTickEvent(TickEvent.Post event) {
		BlockIterator.register(range.get(), range.get(), (blockPos, blockState) -> {
			Block block = blockState.getBlock();
			if (block == Blocks.SPAWNER) {
				blocks.add(blockPosPool.get().set(blockPos));
			}
		});
		
		BlockIterator.after(() -> {
			if (mc.player == null || mc.player.getInventory() == null) return;
			for (BlockPos pos : blocks) {
				BlockState state = mc.world.getBlockState(pos);
				Block block = state.getBlock();
				if (block instanceof SpawnerBlock) {
					SpawnerBlock spawnerBlock = (SpawnerBlock) block;

				}
			}
		});
	}
}

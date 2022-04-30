package olejka.meteorplus.modules;

import meteordevelopment.meteorclient.events.entity.player.StartBreakingBlockEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import olejka.meteorplus.MeteorPlus;

public class AntiLava extends Module {
	public AntiLava() {
		super(MeteorPlus.CATEGORY, "Anti Lava", "Save you from lava.");
	}

	@EventHandler(priority = EventPriority.HIGH)
	private void onStartBreakingBlock(StartBreakingBlockEvent event) {
		if (isExposedLava(event.blockPos)) {
			mc.options.keyAttack.setPressed(false);
			info("Lava block nearbly");
		}
	}

	private boolean isExposedLava(BlockPos pos)
	{
		if (mc.world != null) {
			if (mc.world.getBlockState(pos.add(1, 0, 0)).getBlock() == Blocks.LAVA) {
				return true;
			}
			else if (mc.world.getBlockState(pos.add(-1, 0, 0)).getBlock() == Blocks.LAVA) {
				return true;
			}
			else if (mc.world.getBlockState(pos.add(0, 1, 0)).getBlock() == Blocks.LAVA) {
				return true;
			}
			else if (mc.world.getBlockState(pos.add(0, -1, 0)).getBlock() == Blocks.LAVA) {
				return true;
			}
			else if (mc.world.getBlockState(pos.add(0, 0, 1)).getBlock() == Blocks.LAVA) {
				return true;
			}
			else if (mc.world.getBlockState(pos.add(0, 0, -1)).getBlock() == Blocks.LAVA) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}
}

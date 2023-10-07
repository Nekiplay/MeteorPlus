package nekiplay.meteorplus.commands;

import baritone.api.BaritoneAPI;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.utils.misc.Pool;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.world.BlockIterator;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class GotoPlus extends Command {
	public GotoPlus() {
		super("gotoplus", "Baritone goto to no light block");
	}

	private final Pool<Cross> crossPool = new Pool<>(Cross::new);
	private final List<Cross> crosses = new ArrayList<>();

	public void build(LiteralArgumentBuilder<CommandSource> builder) {
		builder.then(literal("nolight").executes(c -> {
			MeteorExecutor.execute(() -> {
				for (Cross cross : crosses) crossPool.free(cross);
				crosses.clear();



				BlockIterator.register(64, mc.world.getHeight() / 2, (blockPos, blockState) -> {
					switch (BlockUtils.isValidMobSpawn(blockPos, true)) {
						case Never:
							break;
						case Potential:
							crosses.add(crossPool.get().set(blockPos));
							break;
						case Always:
							crosses.add((crossPool.get().set(blockPos)));
							break;
					}

				});

				BlockIterator.after(() -> {
					BlockPos near = null;
					double dst = Double.MAX_VALUE;
					for (Cross cross : crosses) {
						double dist = mc.player.squaredDistanceTo(new Vec3d(cross.x, cross.y, cross.z));
						if (dist < dst) {
							dst = dist;
							near = new BlockPos(cross.x, cross.y, cross.z);
						}
					}

					if (near != null) {
						BaritoneAPI.getProvider().getPrimaryBaritone().getCommandManager().execute("goto " + near.getX() + " " + near.getY() + " " + near.getZ());
					}
					else {
						ChatUtils.sendMsg(Text.of("Not founded near light"));
					}
				});
			});

			return SINGLE_SUCCESS;
		}));
	}

	private class Cross {
		private int x, y, z;

		public Cross set(BlockPos blockPos) {
			x = blockPos.getX();
			y = blockPos.getY();
			z = blockPos.getZ();

			return this;
		}
	}

	public enum Spawn {
		Never,
		Potential,
		Always
	}
}

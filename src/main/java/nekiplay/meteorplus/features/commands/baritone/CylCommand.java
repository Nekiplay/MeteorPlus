package nekiplay.meteorplus.features.commands.baritone;

import baritone.api.BaritoneAPI;
import baritone.api.command.manager.ICommandManager;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import lombok.NonNull;
import meteordevelopment.meteorclient.commands.Command;
import nekiplay.meteorplus.utils.manager.AutoRegistry;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.MeteorClient.mc;

@AutoRegistry
public class CylCommand extends Command {

	public static final String RADIUS = "radius";
	public static final String HEIGHT = "height";

	public static List<Coordinate> makeCylinder(@NonNull Coordinate centerPos, double radiusX, double radiusZ) {
		radiusX += 0.5;
		radiusZ += 0.5;

		final List<Coordinate> object = new ArrayList<>();

		final double invRadiusX = 1 / radiusX;
		final double invRadiusZ = 1 / radiusZ;

		final int ceilRadiusX = (int) Math.ceil(radiusX);
		final int ceilRadiusZ = (int) Math.ceil(radiusZ);

		// Only select coordinates on the boundary with a step size
		for (int x = 0; x <= ceilRadiusX; ++x) {
			for (int z = 0; z <= ceilRadiusZ; ++z) {
				double xn = x * invRadiusX;
				double zn = z * invRadiusZ;

				double distanceSq = (xn * xn + zn * zn);
				if (distanceSq <= 1) {
					CylCommand.add(centerPos.add(x, centerPos.getY(), z), object);
					CylCommand.add(centerPos.add(-x, centerPos.getY(), z), object);
					CylCommand.add(centerPos.add(x, centerPos.getY(), -z), object);
					CylCommand.add(centerPos.add(-x, centerPos.getY(), -z), object);
				}
			}
		}
		return object;
	}

	public static void add(@NonNull Coordinate pos, @NonNull List<Coordinate> coordinates) {
		if (coordinates.contains(pos)) {
			return;
		}
		coordinates.add(pos);
	}

	public static void execSel(@NonNull List<Coordinate> pos, Coordinate center, int radius, int height, boolean hollow) {
		for (int i = radius; i > 0; i--) {
			for (int j = radius; j > 0; j--) {
				int x = center.getX() + i;
				int z = center.getZ() + j;
				int y = center.getY();
				if (pos.contains(new Coordinate(x, y, z))) {
					Coordinate p1 = new Coordinate(x, y, z);
					Coordinate p2 = new Coordinate(x, y, center.getZ() - (z - center.getZ()));
					Coordinate l1 = new Coordinate(center.getX() - (x - center.getX()), center.getY(), z);
					Coordinate l2 = new Coordinate(center.getX() - (x - center.getX()), center.getY(), center.getZ() - (z - center.getZ()));
					if (height > 1 && !hollow) {
						p2.setY(center.getY() + height - 1);
						l2.setY(center.getY() + height - 1);
					}
					if (hollow) {
						if (p1.getX() - center.getX() == radius || p1.getX() - center.getX() == -radius) {
							selPos(p1, new Coordinate(p2.getX(), p2.getY() + height - 1, p2.getZ()));
							selPos(l1, new Coordinate(l2.getX(), l2.getY() + height - 1, l2.getZ()));
						} else {
							selPos(p1, new Coordinate(p1.getX(), p1.getY() + height - 1, p1.getZ()));
							selPos(p2, new Coordinate(p2.getX(), p2.getY() + height - 1, p2.getZ()));
							selPos(l1, new Coordinate(l1.getX(), l1.getY() + height - 1, l1.getZ()));
							selPos(l2, new Coordinate(l2.getX(), l2.getY() + height - 1, l2.getZ()));
						}
					} else {
						selPos(p1, p2);
						selPos(l1, l2);
					}
					break;
				}
			}
		}
		Coordinate p1 = new Coordinate(center.getX(), center.getY(), center.getZ() - radius);
		Coordinate p2 = new Coordinate(center.getX(), center.getY(), center.getZ() + radius);
		if (height > 1 && !hollow) {
			p2.setY(center.getY() + height - 1);
		}
		if (hollow) {
			selPos(p1, new Coordinate(p1.getX(), p1.getY() + height - 1, p1.getZ()));
			selPos(p2, new Coordinate(p2.getX(), p2.getY() + height - 1, p2.getZ()));
		} else {
			selPos(p1, p2);
		}
	}

	public static void selPos(@NonNull Coordinate pos1, @NonNull Coordinate pos2) {
		ICommandManager commandManager = BaritoneAPI.getProvider().getPrimaryBaritone().getCommandManager();
		String sel1 = String.format("sel 1 %d %d %d", pos1.getX(), pos1.getY(), pos1.getZ());
		String sel2 = String.format("sel 2 %d %d %d", pos2.getX(), pos2.getY(), pos2.getZ());
		commandManager.execute(sel1);
		commandManager.execute(sel2);
	}

	@Override
	public void build(LiteralArgumentBuilder<CommandSource> builder) {
		builder.then(
			argument(RADIUS, IntegerArgumentType.integer(1)).then(
				argument(HEIGHT, IntegerArgumentType.integer(1))
					.executes(this::execute)
			)
		);
	}

	private int execute(CommandContext<CommandSource> context) {
		BlockPos blockPos = mc.player.getBlockPos();
		int radius = context.getArgument(RADIUS, Integer.class);
		int height = context.getArgument(HEIGHT, Integer.class);
		Coordinate centerBlock = new Coordinate(blockPos.getX(), blockPos.getY(), blockPos.getZ());
		List<Coordinate> coordinates = makeCylinder(
			centerBlock,
			radius,
			radius
		);
		execSel(coordinates, centerBlock, radius, height, false);
		return SINGLE_SUCCESS;
	}

	public CylCommand() {
		super("cyl", "Mark a solid cylinder using command '#sel'");
	}
}

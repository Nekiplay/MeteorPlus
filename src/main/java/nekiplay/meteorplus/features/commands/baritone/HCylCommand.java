package nekiplay.meteorplus.features.commands.baritone;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import meteordevelopment.meteorclient.commands.Command;
import nekiplay.meteorplus.utils.manager.AutoRegistry;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.BlockPos;

import java.util.List;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.MeteorClient.mc;

@AutoRegistry
public class HCylCommand extends Command {


	public HCylCommand() {
		super("hcyl", "Mark a hollow cylinder using command '#sel'");
	}

	@Override
	public void build(LiteralArgumentBuilder<CommandSource> builder) {
		builder.then(
			argument(CylCommand.RADIUS, IntegerArgumentType.integer(1)).then(
				argument(CylCommand.HEIGHT, IntegerArgumentType.integer(1))
					.executes(this::execute)
			)
		);
	}

	private int execute(CommandContext<CommandSource> context) {
		BlockPos blockPos = mc.player.getBlockPos();
		Coordinate centerPos = new Coordinate(blockPos.getX(), blockPos.getY(), blockPos.getZ());
		int radius = context.getArgument(CylCommand.RADIUS, Integer.class);
		int height = context.getArgument(CylCommand.HEIGHT, Integer.class);

		List<Coordinate> coordinates = CylCommand.makeCylinder(centerPos, radius, radius);
		CylCommand.execSel(coordinates, centerPos, radius, height, true);
		return SINGLE_SUCCESS;
	}
}


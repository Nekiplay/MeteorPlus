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

import java.util.List;
import java.util.Objects;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.MeteorClient.mc;

@AutoRegistry
public class CylCommand extends Command {
	public static final String RADIUS = "radius";
	public static final String HEIGHT = "height";

	public static void execSelCmd(@NonNull Coordinate pos1, @NonNull Coordinate pos2) {
		ICommandManager commandManager = BaritoneAPI.getProvider().getPrimaryBaritone().getCommandManager();
		String sel1 = String.format("sel 1 %d %d %d", pos1.getX(), pos1.getY(), pos1.getZ());
		String sel2 = String.format("sel 2 %d %d %d", pos2.getX(), pos2.getY(), pos2.getZ());
		commandManager.execute(sel1);
		commandManager.execute(sel2);
	}

	public static void execSelCmd(PositionBean position) {
		execSelCmd(position.getFirst(), position.getSecond());
	}

	public static void execAll(List<PositionBean> positions) {
		positions.forEach(CylCommand::execSelCmd);
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
		BlockPos blockPos = Objects.requireNonNull(mc.player).getBlockPos();
		int radius = context.getArgument(RADIUS, Integer.class);
		Coordinate centerBlock = new Coordinate(blockPos.getX(), blockPos.getY(), blockPos.getZ());
		List<PositionBean> positionBeans = CircleCalculation.calcNotHollowPosition(
			CircleCalculation.makeCylinder(centerBlock, radius),
			new CylinderRecord(centerBlock, radius, context.getArgument(HEIGHT, Integer.class))
		);
		CylCommand.execAll(positionBeans);
		return SINGLE_SUCCESS;
	}

	public CylCommand() {
		super("cyl", "Mark a solid cylinder using command '#sel'");
	}

}

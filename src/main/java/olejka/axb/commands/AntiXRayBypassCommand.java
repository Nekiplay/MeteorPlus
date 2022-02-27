package olejka.axb.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import meteordevelopment.meteorclient.systems.commands.Command;
import meteordevelopment.meteorclient.utils.player.ChatUtils;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

import com.mojang.brigadier.arguments.IntegerArgumentType;

public class AntiXRayBypassCommand extends Command {
    public AntiXRayBypassCommand() {
        super("anti-xray-bypass", "Remove ghost blocks & bypass AntiXray", "axb", "anti-xray-bypass");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder
        .executes(ctx -> {
            execute(5);
            return SINGLE_SUCCESS;
        });

        builder
        .then(argument("radius", IntegerArgumentType.integer(3))
        .executes(ctx -> {
            int radius = IntegerArgumentType.getInteger(ctx, "radius");
            execute(radius);
            return SINGLE_SUCCESS;
        }));
	}

    private void execute(int radius) {
        ClientPlayNetworkHandler conn = mc.getNetworkHandler();
        if (conn == null)
            return;

        BlockPos pos = mc.player.getBlockPos();
		ChatUtils.info("AXB", "Starting task...");

		Thread newThread = new Thread(() -> {
			for(int dy = -radius; dy <= radius; dy++) {
				if ((pos.getY() + dy) < 0 || (pos.getY() + dy) > 255) continue;
				for(int dz = -radius; dz <= radius; dz++) {
					for(int dx = -radius; dx <= radius; dx++) {
						PlayerActionC2SPacket packet = new PlayerActionC2SPacket(
							PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK,
							new BlockPos(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz), Direction.UP
						);
						conn.sendPacket(packet);
						try {
							Thread.sleep(5);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				ChatUtils.info("AXB", "Completed height " + (pos.getY() + dy));
            }
			ChatUtils.info("AXB", "Completed!");
		});
		

		newThread.start();
    }
}
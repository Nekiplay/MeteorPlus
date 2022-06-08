package olejka.meteorplus.commands;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.commands.Command;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import olejka.meteorplus.utils.ElytraUtils;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class Eclip extends Command {
	public Eclip() {
		super("eclip", "Elyta clip");
	}

	public void startFly() {
		mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
	}

	public void build(LiteralArgumentBuilder<CommandSource> builder) {
		builder.then(argument("blocks", DoubleArgumentType.doubleArg()).executes(context -> {
			ClientPlayerEntity player = mc.player;
			assert player != null;
			double blocks2 = context.getArgument("blocks", Double.class);
			FindItemResult elytra = InvUtils.find(Items.ELYTRA);
			if (elytra.found()) {
				ticks = 0;
				slot = elytra.slot();
				blocks = blocks2;
				MeteorClient.EVENT_BUS.subscribe(this);
			}
			else {
				error("Elytra not found");
			}
			return SINGLE_SUCCESS;
		}));
	}

	private int ticks = 0;
	private int slot = -1;
	private double blocks = 0;

	@EventHandler
	private void onTick(TickEvent.Pre event) {
		ClientPlayerEntity player = mc.player;
		assert player != null;
		switch (ticks) {
			case 0: {
				FindItemResult elytra = InvUtils.find(Items.ELYTRA);
				slot = elytra.slot();
				InvUtils.move().from(slot).toArmor(2);
				ticks++;
			}
			case 1: {
				mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(false));
				ticks++;
			}
			case 2: {
				mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(false));
				ticks++;
			}
			case 3: {
				ElytraUtils.startFly();
				ticks++;
			}
			case 4: {
				player.setPosition(player.getX(), player.getY() + blocks, player.getZ());
				mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(player.getX(), player.getY() + blocks, player.getZ(), false));
				ticks++;
			}
			case 5: {
				ElytraUtils.startFly();
				ticks++;
			}
			case 6: {
				InvUtils.move().fromArmor(2).to(slot);
				MeteorClient.EVENT_BUS.unsubscribe(this);
			}
		}
	}
}

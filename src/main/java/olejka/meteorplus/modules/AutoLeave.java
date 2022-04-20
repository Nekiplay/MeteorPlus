package olejka.meteorplus.modules;

import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.text.LiteralText;
import olejka.meteorplus.MeteorPlus;
import meteordevelopment.meteorclient.events.entity.EntityAddedEvent;
import meteordevelopment.orbit.EventHandler;
import java.util.Objects;

public class AutoLeave extends Module {
	public AutoLeave() {
		super(MeteorPlus.CATEGORY, "Auto Leave", "Automatically logs out from the server when someone enters your render distance.");
	}
	private final SettingGroup ALSettings = settings.createGroup("Visual Range");
	private final Setting<Boolean> visualRangeIgnoreFriends = ALSettings.add(new BoolSetting.Builder()
		.name("ignore-friends")
		.description("Ignores friends.")
		.defaultValue(true)
		.build()
	);

	private final Setting<Boolean> AutoDisable = ALSettings.add(new BoolSetting.Builder()
		.name("auto-disable")
		.description("Disables function after player detect.")
		.defaultValue(true)
		.build()
	);

	@EventHandler
	public void onEntityAdded(EntityAddedEvent event) {
		if (mc.player == null) return;
		if (visualRangeIgnoreFriends.get()) {
			if (event.entity.isPlayer() && !Friends.get().isFriend((PlayerEntity) event.entity) && !Objects.equals(event.entity.getEntityName(), mc.player.getEntityName()) && !Objects.equals(event.entity.getEntityName(), "FreeCamera")) {
			mc.player.networkHandler.onDisconnect(new DisconnectS2CPacket(new LiteralText(String.format("[Auto Leaeve] player %s was detected", event.entity.getEntityName()))));
			if (AutoDisable.get()) this.toggle();
			}
		}
		else {
			if (event.entity.isPlayer()) {
				mc.player.networkHandler.onDisconnect(new DisconnectS2CPacket(new LiteralText (String.format("[Auto Leaeve] player %s was detected", event.entity.getEntityName()))));
				if (AutoDisable.get()) this.toggle();
			}
		}
	}
}



package olejka.meteorplus.modules;

import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import olejka.meteorplus.MeteorPlus;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoAccept extends Module {
	public AutoAccept() {
		super(MeteorPlus.CATEGORY, "Auto Accept", "Automatically accepts incoming teleport requests.");
	}

	private final SettingGroup AASettings = settings.createGroup("Auto Accept Settings");

	public final Setting<AutoAccept.mode> Mode = AASettings.add(new EnumSetting.Builder<AutoAccept.mode>()
		.name("mode")
		.description("Server mode.")
		.defaultValue(mode.CMI)
		.build()
	);

	private final Setting<Boolean> FriendsOnly = AASettings.add(new BoolSetting.Builder()
		.name("Friends only")
		.description("Accepts only friends requests.")
		.defaultValue(true)
		.build()
	);

//	private final Setting<String> command_str = AASettings.add(new StringSetting.Builder()
//		.name("command:")
//		.description("Write your own regex.")
//		.visible(Mode::get()
//		.build()
//	);

	public enum mode {
		CMI,
//		Custom;
	}


	@EventHandler(priority = EventPriority.LOWEST)
	public void onMessageRecieve(ReceiveMessageEvent event) {
		if (event.getMessage() != null && mc.player != null){
			String message = event.getMessage().getString();
			Pattern pattern = Pattern.compile(".*Игрок §e(.*) §7просит телепортироваться к вам!§7 §a§l.*");
			Matcher matcher = pattern.matcher(message);
			if (matcher.find()) {
				String player = matcher.group(1);
				if (FriendsOnly.get() && Friends.get().get(player) != null && Friends.get().get(player).name.equals(player)){
					info("Accepting request from " + "§c" + player);
					mc.player.sendChatMessage("/cmi tpaccept " + player + " tpa");
				} else if (!FriendsOnly.get()){
					info("Accepting request from " + "§c" + player);
					mc.player.sendChatMessage("/cmi tpaccept " + player + " tpa");
				}
			}
		}
	}
}

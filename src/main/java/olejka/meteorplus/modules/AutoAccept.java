package olejka.meteorplus.modules;

import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.text.TextUtils;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.fabricmc.loader.impl.util.StringUtil;
import olejka.meteorplus.MeteorPlus;
import olejka.meteorplus.utils.ColorRemover;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoAccept extends Module {
	public AutoAccept() {
		super(MeteorPlus.CATEGORY, "Auto Accept", "Automatically accepts incoming teleport requests.");
	}

	private final SettingGroup AASettings = settings.createGroup("Auto Accept Settings");

	private final Setting<String> accept_command = AASettings.add(new StringSetting.Builder()
		.name("Accept command")
		.description("Accept command.")
		.defaultValue("/cmi tpaccept {username} tpa")
		.build()
	);

	private final Setting<Boolean> FriendsOnly = AASettings.add(new BoolSetting.Builder()
		.name("Friends only")
		.description("Accepts only friends requests.")
		.defaultValue(true)
		.build()
	);

	private ArrayList<TPPattern> patters = new ArrayList<>();

	@Override
	public void onActivate() {
		patters.clear();
		TPPattern MST_Network = new TPPattern(".*Игрок (.*) просит телепортироваться к вам!.*", 1);
		TPPattern HolyWorld = new TPPattern("(.*) просит телепортироваться.*", 1);
		patters.add(MST_Network);
		patters.add(HolyWorld);
	}
	@Override
	public void onDeactivate() {
		patters.clear();
	}

	@EventHandler()
	public void onMessageRecieve(ReceiveMessageEvent event) {
		if (event.getMessage() != null && mc.player != null){
			String message = ColorRemover.GetVerbatim(event.getMessage().getString());
			String nickname = getName(message);
			if (!nickname.equals("")) {

				if (FriendsOnly.get() && isFriend(nickname)) {
					info("Accepting request from " + "§c" + nickname);
					mc.player.sendChatMessage(accept_command.get().replace("{username}", nickname));
				} else if (!FriendsOnly.get()) {
					info("Accepting request from " + "§c" + nickname);
					mc.player.sendChatMessage(accept_command.get().replace("{username}", nickname));
				}
			}
		}
	}

	private String getName(String message)
	{
		String nickname = "";
		for (TPPattern tpPattern : patters) {
			Pattern pattern = Pattern.compile(tpPattern.pattern);
			Matcher matcher = pattern.matcher(message);
			if (matcher.find()) {
				String player = matcher.group(tpPattern.group);
				if (!player.equals("")) {
					nickname = player;
				}
			}
		}
		return nickname;
	}

	private boolean isFriend(String username)
	{
		return Friends.get().get(username) != null && Friends.get().get(username).name.equals(username);
	}

	private class TPPattern
	{
		public String pattern = "";
		public int group = 1;

		public TPPattern(String pattern, int group)
		{
			this.pattern = pattern;
			this.group = group;
		}
	}
}

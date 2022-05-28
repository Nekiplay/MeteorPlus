package olejka.meteorplus.modules;

import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
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

	private final Setting<Mode> mode = AASettings.add(new EnumSetting.Builder<Mode>()
		.name("Mode")
		.description("Accept mode.")
		.defaultValue(Mode.Auto)
		.build()
	);

	private final Setting<String> custom_pattern = AASettings.add(new StringSetting.Builder()
		.name("Pattern command")
		.description("Custom pattern.")
		.defaultValue(".*Игрок (.*) просит телепортироваться к вам!.*")
		.visible(() ->  mode.get() == Mode.Custom)
		.build()
	);

	private final Setting<Integer> custom_group = AASettings.add(new IntSetting.Builder()
		.name("Pattern command")
		.description("Custom pattern.")
		.defaultValue(1)
		.visible(() -> mode.get() == Mode.Custom)
		.build()
	);

	private final Setting<String> accept_command = AASettings.add(new StringSetting.Builder()
		.name("Accept command")
		.description("Accept command.")
		.defaultValue("/cmi tpaccept {username} tpa")
		.visible(() -> mode.get() == Mode.Custom)
		.build()
	);

	private final Setting<Boolean> FriendsOnly = AASettings.add(new BoolSetting.Builder()
		.name("Friends only")
		.description("Accepts only friends requests.")
		.defaultValue(true)
		.build()
	);
	public enum Mode
	{
		Auto,
		Custom
	}

	private final ArrayList<TPPattern> patters = new ArrayList<>();

	@Override
	public void onActivate() {
		patters.clear();
		TPPattern MST_Network = new TPPattern(".*Игрок (.*) просит телепортироваться к вам!.*", 1, "/cmi tpaccept {username} tpa");
		TPPattern HolyWorld = new TPPattern("(.*) просит телепортироваться.*", 1, "/tpaccept");
		patters.add(MST_Network);
		patters.add(HolyWorld);
	}
	@Override
	public void onDeactivate() {
		patters.clear();
	}

	private void BetterAccept(String username, TPPattern pattern) {
		if (mc.player != null && FriendsOnly.get() && isFriend(username)) {
			info("Accepting request from " + "§c" + username);
			mc.player.sendChatMessage(pattern.command.replace("{username}", username));
		} else if (!FriendsOnly.get()) {
			info("Accepting request from " + "§c" + username);
			mc.player.sendChatMessage(pattern.command.replace("{username}", username));
		}
	}

	private void Accept(String username, TPPattern pattern, String message) {
		if (mc.player != null && mode.get() == Mode.Custom) {
			TPPattern pattern1 = new TPPattern(custom_pattern.get(), custom_group.get(), accept_command.get());
			username = getName(pattern, message);
			if (FriendsOnly.get() && isFriend(username)) {
				info("Accepting request from " + "§c" + username);
				mc.player.sendChatMessage(accept_command.get().replace("{username}", username));
			} else if (!FriendsOnly.get()) {
				info("Accepting request from " + "§c" + username);
				mc.player.sendChatMessage(accept_command.get().replace("{username}", username));
			}
		}
		else {
			BetterAccept(username, pattern);
		}
	}

	@EventHandler()
	public void onMessageRecieve(ReceiveMessageEvent event) {
		if (event.getMessage() != null && mc.player != null) {
			String message = ColorRemover.GetVerbatim(event.getMessage().getString());
			String nickname = getName(message);
			TPPattern pattern = getPattern(message);
			Accept(nickname, pattern, message);
		}
	}

	private String getName(String message)
	{
		String nickname = "";
		for (TPPattern tpPattern : patters) {
			String nn = getName(tpPattern, message);
			if (!nn.equals("")) { nickname = nn;}
		}
		return nickname;
	}

	private String getName(TPPattern tpPattern, String message)
	{
		String nickname = "";
		Pattern pattern = Pattern.compile(tpPattern.pattern);
		Matcher matcher = pattern.matcher(message);
		if (matcher.find()) {
			String player = matcher.group(tpPattern.group);
			if (!player.equals("")) {
				nickname = player;
			}
		}
		return nickname;
	}

	private TPPattern getPattern(String message)
	{
		String nickname = "";
		for (TPPattern tpPattern : patters) {
			Pattern pattern = Pattern.compile(tpPattern.pattern);
			Matcher matcher = pattern.matcher(message);
			if (matcher.find()) {
				String player = matcher.group(tpPattern.group);
				if (!player.equals("")) {
					return tpPattern;
				}
			}
		}
		return null;
	}

	private boolean isFriend(String username)
	{
		return Friends.get().get(username) != null && Friends.get().get(username).name.equals(username);
	}

	private static class TPPattern
	{
		public String pattern;
		public int group;
		public String command;

		public TPPattern(String pattern, int group, String command)
		{
			this.pattern = pattern;
			this.group = group;
			this.command = command;
		}
	}
}

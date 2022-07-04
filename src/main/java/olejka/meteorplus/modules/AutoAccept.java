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

	private final Setting<Integer> Delay = AASettings.add((new IntSetting.Builder())
		.name("Delay")
		.defaultValue(0)
		.build()
	);

	private  final Setting<Boolean> Debug = AASettings.add(new BoolSetting.Builder()
		.name("Debug")
		.description("Prints all incoming messages in console (raw format).")
		.defaultValue(false)
		.build()
	);
	public enum Mode
	{
		Auto,
		Custom
	}

	private final ArrayList<TPPattern> patterns = new ArrayList<>();

	@Override
	public void onActivate() {
		patterns.clear();
		TPPattern MST_Network = new TPPattern(".*Игрок (.*) просит телепортироваться к вам!.*", 1, "/cmi tpaccept {username} tpa");
		TPPattern HolyWorld = new TPPattern("(.*) просит телепортироваться.*", 1, "/tpaccept");
		TPPattern SimpleTpa = new TPPattern(".*\\[SimpleTpa\\] (.*) has sent you a teleport request!.*", 1, "/tpaccept");
		patterns.add(MST_Network);
		patterns.add(HolyWorld);
		patterns.add(SimpleTpa);

	}
	@Override
	public void onDeactivate() {
		patterns.clear();
	}

	private void AutoAccept(String username, TPPattern pattern1) {
		if (mc.player != null && FriendsOnly.get() && isFriend(username)) {
			info("Accepting request from " + "§c" + username);
			mc.player.sendChatMessage(pattern1.command.replace("{username}", username));
		} else if (!FriendsOnly.get()) {
			info("Accepting request from " + "§c" + username);
			mc.player.sendChatMessage(pattern1.command.replace("{username}", username));
		}
	}

	private void Accept(String username, TPPattern pattern) {
		if (mc.player != null && mode.get() == Mode.Custom) {
			if (FriendsOnly.get() && isFriend(username)) {
				info("Accepting request from " + "§c" + username);
				mc.player.sendChatMessage(accept_command.get().replace("{username}", username));
			} else if (!FriendsOnly.get()) {
				info("Accepting request from " + "§c" + username);
				mc.player.sendChatMessage(accept_command.get().replace("{username}", username));
			}
		}
	}

	@EventHandler()
	public void onMessageRecieve(ReceiveMessageEvent event) {
		if (event.getMessage() != null && mc.player != null) {
			String message = ColorRemover.GetVerbatim(event.getMessage().getString());
			if (Debug.get())
			{
				MeteorPlus.LOG.info(message);
			}
			Thread th = new Thread(() -> {
				try {
					Thread.sleep(Delay.get());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				TPPattern custom = new TPPattern(custom_pattern.get(), custom_group.get(), accept_command.get());
				String nickname = getName(message);
				TPPattern pattern = getPattern(message);
				if (pattern != null && mode.get() == Mode.Custom) {
					AutoAccept(nickname, pattern);
				}
				else {
					nickname = getName(custom, message);
					Accept(nickname, custom);
				}
			});
			th.start();
		}
	}

	private String getName(String message) {
		String nickname = "";
		for (TPPattern tpPattern : patterns) {
			String nn = getName(tpPattern, message);
			if (!nn.equals("")) {
				nickname = nn;
			}
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
		for (TPPattern tpPattern : patterns) {
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
		Friends friends = Friends.get();
		if (friends != null && friends.get(username) != null) {
			return true;
		}
		return false;
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

package olejka.meteorplus.modules;

import com.mojang.authlib.GameProfile;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import olejka.meteorplus.MeteorPlus;

import java.util.Iterator;

public class AntiBotPlus extends Module {
	public AntiBotPlus() {
		super(MeteorPlus.CATEGORY, "Anti Bot", "Remove bots.");
	}

	private final SettingGroup sgGeneral = settings.getDefaultGroup();
	private final SettingGroup sgFilters = settings.createGroup("Filters");

	private final Setting<Boolean> removeInvisible = sgGeneral.add(new BoolSetting.Builder()
		.name("remove-invisible")
		.description("Removes bot only if they are invisible.")
		.defaultValue(true)
		.build()
	);

	private final Setting<Boolean> gameMode = sgFilters.add(new BoolSetting.Builder()
		.name("null-gamemode")
		.description("Removes players without a gamemode")
		.defaultValue(true)
		.build()
	);

	private final Setting<Boolean> api = sgFilters.add(new BoolSetting.Builder()
		.name("null-entry")
		.description("Removes players without a player entry")
		.defaultValue(true)
		.build()
	);

	private final Setting<Boolean> profile = sgFilters.add(new BoolSetting.Builder()
		.name("null-profile")
		.description("Removes players without a game profile")
		.defaultValue(true)
		.build()
	);

	private final Setting<Boolean> latency = sgFilters.add(new BoolSetting.Builder()
		.name("ping-check")
		.description("Removes players using ping check")
		.defaultValue(false)
		.build()
	);

	private final Setting<Boolean> nullException = sgFilters.add(new BoolSetting.Builder()
		.name("null-exception")
		.description("Removes players if a NullPointerException occurred")
		.defaultValue(false)
		.build()
	);

	private String pName = "";

	@EventHandler
	public void onPacketRecive(PacketEvent.Receive event) {
		if (event.packet instanceof PlayerListS2CPacket packet) {
			if (packet.getAction() == PlayerListS2CPacket.Action.ADD_PLAYER) {
				for (PlayerListS2CPacket.Entry entry : packet.getEntries()) {
					if (entry.getLatency() < 2 || entry.getProfile().getName().length() < 3 || !entry.getProfile().getProperties().isEmpty() || isTheSamePlayer(entry.getProfile())) {
						continue;
					}
					if (isADuplicate(entry.getProfile())) {
						event.cancel();
						info("Removed " + entry.getProfile().getName());
						continue;
					}

					pName = entry.getProfile().getName();
				}
			}
		}
	}


	@EventHandler
	public void onTick(TickEvent.Post tickEvent) {
		for (Entity entity : mc.world.getEntities()) {
			if (entity != null) {
				if (removeInvisible.get() && !entity.isInvisible()) continue;

				if (isBot(entity)) entity.remove(Entity.RemovalReason.DISCARDED);
			}
		}
	}

	private boolean isBot(Entity entity) {
		if (entity == null) return false;
		if (!(entity instanceof PlayerEntity)) return false;

		PlayerEntity player = (PlayerEntity)entity;

		try {
			if (gameMode.get() && EntityUtils.getGameMode(player) == null) return true;
			if (api.get() &&
				mc.getNetworkHandler().getPlayerListEntry(entity.getUuid()) == null) return true;
			if (profile.get() &&
				mc.getNetworkHandler().getPlayerListEntry(entity.getUuid()).getProfile() == null) return true;
			if (latency.get() &&
				mc.getNetworkHandler().getPlayerListEntry(entity.getUuid()).getLatency() > 1) return true;
		} catch (NullPointerException e) {
			if (nullException.get()) return true;
		}

		return false;
	}

	private boolean isADuplicate(GameProfile profile) {
		Iterator<PlayerListEntry> iterator = mc.getNetworkHandler().getPlayerList().iterator();
		int found = 0;
		while (iterator.hasNext()) {
			PlayerListEntry listEntry = iterator.next();
			if (profile.getName() == listEntry.getProfile().getName()) {
				found++;
			}
		}
		return found > 0;
	}

	private boolean isTheSamePlayer(GameProfile profile) {
		Iterator<PlayerListEntry> iterator = mc.getNetworkHandler().getPlayerList().iterator();
		while (iterator.hasNext()) {
			PlayerListEntry listEntry = iterator.next();
			if (profile.getId() == listEntry.getProfile().getId()) {
				return true;
			}
		}
		return false;
	}
}

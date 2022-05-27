package olejka.meteorplus.modules;

import com.mojang.authlib.GameProfile;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import olejka.meteorplus.MeteorPlus;

import java.util.ArrayList;
import java.util.Iterator;

public class AntiBotPlus extends Module {
	public AntiBotPlus() {
		super(MeteorPlus.CATEGORY, "Anti Bot", "Remove bots.");
	}

	// Thanks LiquidBounce

	private final SettingGroup sgGeneral = settings.getDefaultGroup();
	private final SettingGroup sgFilters = settings.createGroup("Filters");

	private final Setting<Boolean> removeInvisible = sgGeneral.add(new BoolSetting.Builder()
		.name("remove-invisible")
		.description("Removes bot only if they are invisible.")
		.defaultValue(true)
		.build()
	);

	private final Setting<Boolean> tab = sgFilters.add(new BoolSetting.Builder()
		.name("tab")
		.description("check tab.")
		.defaultValue(true)
		.build()
	);

	private final Setting<TabMode> tabMode = sgFilters.add(new EnumSetting.Builder<TabMode>()
		.name("tab-mode")
		.description("check tab mode.")
		.defaultValue(TabMode.Contains)
		.visible(tab::get)
		.build()
	);

	private final Setting<Boolean> entityID = sgFilters.add(new BoolSetting.Builder()
		.name("EntityID")
		.description("check entity id.")
		.defaultValue(true)
		.build()
	);

	private final Setting<Boolean> color = sgFilters.add(new BoolSetting.Builder()
		.name("Color")
		.description("check color.")
		.defaultValue(false)
		.build()
	);

	private final Setting<Boolean> livingTime = sgFilters.add(new BoolSetting.Builder()
		.name("Living-time")
		.description("check Living time.")
		.defaultValue(false)
		.build()
	);

	private final Setting<Integer> livingTimeTicks = sgFilters.add(new IntSetting.Builder()
		.name("Living-time-ticks")
		.description("check Living time ticks.")
		.defaultValue(200)
		.build()
	);

	private final Setting<Boolean> ground = sgFilters.add(new BoolSetting.Builder()
		.name("ground")
		.description("check ground.")
		.defaultValue(true)
		.build()
	);

	private final Setting<Boolean> air = sgFilters.add(new BoolSetting.Builder()
		.name("air")
		.description("check air.")
		.defaultValue(false)
		.build()
	);

	private final Setting<Boolean> InvalidGround = sgFilters.add(new BoolSetting.Builder()
		.name("Invalid-Ground")
		.description("check Invalid Ground.")
		.defaultValue(true)
		.build()
	);

	private ArrayList<Integer> grounds = new ArrayList<Integer>();

	public boolean isBot(Entity entity) {
		if (entity instanceof LivingEntity living) {
			return isBot(living);
		}
		return false;
	}

	public boolean isBot(LivingEntity entity) {
		if (!(entity instanceof PlayerEntity))
			return false;

		if (color.get() && entity.getDisplayName().getString().replace("§r", "").contains("§"))
			return true;

		if (ground.get() && !grounds.contains(entity.getId()))
			return true;

		return false;
	}

	private enum TabMode {
		Equals,
		Contains
	}
}

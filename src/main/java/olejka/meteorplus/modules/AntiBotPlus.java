package olejka.meteorplus.modules;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import olejka.meteorplus.MeteorPlus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AntiBotPlus extends Module {
	public AntiBotPlus() {
		super(MeteorPlus.CATEGORY, "Anti Bot", "Remove bots.");
	}

	/* Thanks LiquidBounce
		https://github.com/CCBlueX/LiquidBounce/blob/legacy/src/main/java/net/ccbluex/liquidbounce/features/module/modules/misc/AntiBot.kt
	 */

	private final SettingGroup sgFilters = settings.createGroup("Filters");

	public enum TabMode {
		Equals,
		Contains
	}

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

	private final Setting<Boolean> swing = sgFilters.add(new BoolSetting.Builder()
		.name("Swing")
		.description("check Swing.")
		.defaultValue(false)
		.build()
	);

	private final Setting<Boolean> health = sgFilters.add(new BoolSetting.Builder()
		.name("health")
		.description("check health.")
		.defaultValue(false)
		.build()
	);

	private final Setting<Boolean> derp = sgFilters.add(new BoolSetting.Builder()
		.name("derp")
		.description("check derp.")
		.defaultValue(true)
		.build()
	);


	private final ArrayList<Integer> grounds = new ArrayList<>();
	private final ArrayList<Integer> airs = new ArrayList<>();
	private final Map<Integer, Integer> invalidGrounds  = new HashMap<>();

	public boolean isBot(Entity entity) {
		if (entity instanceof LivingEntity living) {
			return isBot(living);
		}
		return false;
	}

	public boolean isBot(LivingEntity entity) {
		if (!(entity instanceof PlayerEntity))
			return false;
		if (!isActive())
			return false;

		if (color.get() && entity.getDisplayName().getString().replace("§r", "").contains("§"))
			return true;

		if (ground.get() && !grounds.contains(entity.getId()))
			return true;

		if (InvalidGround.get() && invalidGrounds.getOrDefault(entity.getId(), 0) >= 10)
			return true;

		if (entity.getName().getString().isEmpty()) return true;

		assert mc.player != null;
		return entity.getName() == mc.player.getName();
	}

	@EventHandler
	private void livingEntityMove(PacketEvent.Receive event) {
		if (event.packet instanceof EntityPositionS2CPacket packet) {
			if (mc.world != null) {
				Entity entity = mc.world.getEntityById(packet.getId());
				if (entity != null) {
					if (entity.isOnGround()) {
						grounds.add(entity.getId());
					}

					if (!entity.isOnGround() && !airs.contains(entity.getId()))
						airs.add(entity.getId());

					if (entity.isOnGround()) {
						if (entity.prevY != entity.getY())
							invalidGrounds.put(entity.getId(), invalidGrounds.getOrDefault(entity.getId(), 0) + 1);
					} else {
						int currentVL = invalidGrounds.getOrDefault(entity.getId(), 0) / 2;
						if (currentVL <= 0)
							invalidGrounds.remove(entity.getId());
						else
							//invalidGrounds.put(entity.getId(), invalidGrounds.getOrDefault(entity.getId(), currentVL));
							invalidGrounds.replace(entity.getId(), currentVL);
					}
				}
			}
		}
	}
}

package nekiplay.meteorplus.mixin.meteorclient.utils.misc;

import meteordevelopment.meteorclient.mixin.ClientPlayerInteractionManagerAccessor;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.HorizontalDirection;
import meteordevelopment.meteorclient.utils.misc.MeteorStarscript;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.world.Dimension;
import meteordevelopment.starscript.Starscript;
import meteordevelopment.starscript.value.Value;
import meteordevelopment.starscript.value.ValueMap;
import nekiplay.meteorplus.settings.ConfigModifier;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.stream.Collectors;

import static meteordevelopment.meteorclient.MeteorClient.mc;
import static meteordevelopment.meteorclient.utils.misc.MeteorStarscript.popIdentifier;
import static meteordevelopment.meteorclient.utils.misc.MeteorStarscript.wrap;

@Mixin(value = MeteorStarscript.class, remap = false, priority = 1001)
public class MeteorStarscriptMixin {
	@Shadow
	@Final
	private static final BlockPos.Mutable BP = new BlockPos.Mutable();
	@Shadow
	public static Starscript ss = new Starscript();
	@Inject(method = "init", at = @At("RETURN"))
	private static void init(CallbackInfo ci) {
		ss.set("camera", new ValueMap()
			.set("pos", new ValueMap()
				.set("_toString", () -> posString(false, true))
				.set("x", () -> Value.number(mc.gameRenderer.getCamera().getPos().x + ConfigModifier.get().x_spoof.get()))
				.set("y", () -> Value.number(mc.gameRenderer.getCamera().getPos().y))
				.set("z", () -> Value.number(mc.gameRenderer.getCamera().getPos().z + ConfigModifier.get().z_spoof.get()))
			)
			.set("opposite_dim_pos", new ValueMap()
				.set("_toString", () -> posString(true, true))
				.set("x", () -> oppositeX(true))
				.set("y", () -> Value.number(mc.gameRenderer.getCamera().getPos().y))
				.set("z", () -> oppositeZ(true))
			)

			.set("yaw", () -> yaw(true))
			.set("pitch", () -> pitch(true))
			.set("direction", () -> direction(true))
		);

		// Player
		ss.set("player", new ValueMap()
			.set("_toString", () -> Value.string(mc.getSession().getUsername()))
			.set("health", () -> Value.number(mc.player != null ? mc.player.getHealth() : 0))
			.set("absorption", () -> Value.number(mc.player != null ? mc.player.getAbsorptionAmount() : 0))
			.set("hunger", () -> Value.number(mc.player != null ? mc.player.getHungerManager().getFoodLevel() : 0))

			.set("speed", () -> Value.number(Utils.getPlayerSpeed().horizontalLength()))
			.set("speed_all", new ValueMap()
				.set("_toString", () -> Value.string(mc.player != null ? Utils.getPlayerSpeed().toString() : ""))
				.set("x", () -> Value.number(mc.player != null ? Utils.getPlayerSpeed().x : 0))
				.set("y", () -> Value.number(mc.player != null ? Utils.getPlayerSpeed().y : 0))
				.set("z", () -> Value.number(mc.player != null ? Utils.getPlayerSpeed().z : 0))
			)

			.set("breaking_progress", () -> Value.number(mc.interactionManager != null ? ((ClientPlayerInteractionManagerAccessor) mc.interactionManager).getBreakingProgress() : 0))
			.set("biome", MeteorStarscriptMixin::biome)

			.set("dimension", () -> Value.string(PlayerUtils.getDimension().name()))
			.set("opposite_dimension", () -> Value.string(PlayerUtils.getDimension().opposite().name()))


			.set("pos", new ValueMap()
				.set("_toString", () -> posString(false, false))
				.set("x", () -> Value.number(mc.player != null ? mc.player.getX() + ConfigModifier.get().x_spoof.get() : 0))
				.set("y", () -> Value.number(mc.player != null ? mc.player.getY() : 0))
				.set("z", () -> Value.number(mc.player != null ? mc.player.getZ() + ConfigModifier.get().z_spoof.get(): 0))
			)
			.set("opposite_dim_pos", new ValueMap()
				.set("_toString", () -> posString(true, false))
				.set("x", () -> oppositeX(false))
				.set("y", () -> Value.number(mc.player != null ? mc.player.getY() : 0))
				.set("z", () -> oppositeZ(false))
			)

			.set("yaw", () -> yaw(false))
			.set("pitch", () -> pitch(false))
			.set("direction", () -> direction(false))

			.set("hand", () -> mc.player != null ? wrap(mc.player.getMainHandStack()) : Value.null_())
			.set("offhand", () -> mc.player != null ? wrap(mc.player.getOffHandStack()) : Value.null_())
			.set("hand_or_offhand", MeteorStarscriptMixin::handOrOffhand)
			.set("get_item", MeteorStarscriptMixin::getItem)
			.set("count_items", MeteorStarscriptMixin::countItems)

			.set("xp", new ValueMap()
				.set("level", () -> Value.number(mc.player != null ? mc.player.experienceLevel : 0))
				.set("progress", () -> Value.number(mc.player != null ? mc.player.experienceProgress : 0))
				.set("total", () -> Value.number(mc.player != null ? mc.player.totalExperience : 0))
			)

			.set("has_potion_effect", MeteorStarscriptMixin::hasPotionEffect)
			.set("get_potion_effect", MeteorStarscriptMixin::getPotionEffect)

			.set("get_stat", MeteorStarscriptMixin::getStat)
		);
	}

	private static Value oppositeX(boolean camera) {
		double x = camera ? mc.gameRenderer.getCamera().getPos().x + ConfigModifier.get().x_spoof.get() : (mc.player != null ? mc.player.getX() + ConfigModifier.get().x_spoof.get() : 0);
		Dimension dimension = PlayerUtils.getDimension();

		if (dimension == Dimension.Overworld) x /= 8;
		else if (dimension == Dimension.Nether) x *= 8;

		return Value.number(x);
	}

	private static Value oppositeZ(boolean camera) {
		double z = camera ? mc.gameRenderer.getCamera().getPos().z + ConfigModifier.get().z_spoof.get() : (mc.player != null ? mc.player.getZ() + ConfigModifier.get().z_spoof.get() : 0);
		Dimension dimension = PlayerUtils.getDimension();

		if (dimension == Dimension.Overworld) z /= 8;
		else if (dimension == Dimension.Nether) z *= 8;

		return Value.number(z);
	}

	private static Value yaw(boolean camera) {
		float yaw;
		if (camera) yaw = mc.gameRenderer.getCamera().getYaw();
		else yaw = mc.player != null ? mc.player.getYaw() : 0;
		yaw %= 360;

		if (yaw < 0) yaw += 360;
		if (yaw > 180) yaw -= 360;

		return Value.number(yaw);
	}

	private static Value pitch(boolean camera) {
		float pitch;
		if (camera) pitch = mc.gameRenderer.getCamera().getPitch();
		else pitch = mc.player != null ? mc.player.getPitch() : 0;
		pitch %= 360;

		if (pitch < 0) pitch += 360;
		if (pitch > 180) pitch -= 360;

		return Value.number(pitch);
	}

	private static Value direction(boolean camera) {
		float yaw;
		if (camera) yaw = mc.gameRenderer.getCamera().getYaw();
		else yaw = mc.player != null ? mc.player.getYaw() : 0;

		return wrap(HorizontalDirection.get(yaw));
	}

	private static Value biome() {
		if (mc.player == null || mc.world == null) return Value.string("");

		BP.set(mc.player.getX(), mc.player.getY(), mc.player.getZ());
		Identifier id = mc.world.getRegistryManager().get(RegistryKeys.BIOME).getId(mc.world.getBiome(BP).value());
		if (id == null) return Value.string("Unknown");

		return Value.string(Arrays.stream(id.getPath().split("_")).map(StringUtils::capitalize).collect(Collectors.joining(" ")));
	}

	private static Value handOrOffhand() {
		if (mc.player == null) return Value.null_();

		ItemStack itemStack = mc.player.getMainHandStack();
		if (itemStack.isEmpty()) itemStack = mc.player.getOffHandStack();

		return itemStack != null ? wrap(itemStack) : Value.null_();
	}

	private static Value ping() {
		if (mc.getNetworkHandler() == null || mc.player == null) return Value.number(0);

		PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
		return Value.number(playerListEntry != null ? playerListEntry.getLatency() : 0);
	}
	private static Value posString(boolean opposite, boolean camera) {
		Vec3d pos;
		if (camera) pos = mc.gameRenderer.getCamera().getPos();
		else pos = mc.player != null ? mc.player.getPos() : Vec3d.ZERO;

		double x = pos.x;
		double z = pos.z;

		if (opposite) {
			Dimension dimension = PlayerUtils.getDimension();

			if (dimension == Dimension.Overworld) {
				x /= 8;
				z /= 8;
			}
			else if (dimension == Dimension.Nether) {
				x *= 8;
				z *= 8;
			}
		}

		return posString(x, pos.y, z);
	}

	private static Value posString(double x, double y, double z) {
		return Value.string(String.format("X: %.0f Y: %.0f Z: %.0f", x + ConfigModifier.get().x_spoof.get(), y, z + ConfigModifier.get().z_spoof.get()));
	}

	private static Value getItem(Starscript ss, int argCount) {
		if (argCount != 1) ss.error("player.get_item() requires 1 argument, got %d.", argCount);

		int i = (int) ss.popNumber("First argument to player.get_item() needs to be a number.");
		return mc.player != null ? wrap(mc.player.getInventory().getStack(i)) : Value.null_();
	}

	private static Value countItems(Starscript ss, int argCount) {
		if (argCount != 1) ss.error("player.count_items() requires 1 argument, got %d.", argCount);

		String idRaw = ss.popString("First argument to player.count_items() needs to be a string.");
		Identifier id = Identifier.tryParse(idRaw);
		if (id == null) return Value.number(0);

		Item item = Registries.ITEM.get(id);
		if (item == Items.AIR || mc.player == null) return Value.number(0);

		int count = 0;
		for (int i = 0; i < mc.player.getInventory().size(); i++) {
			ItemStack itemStack = mc.player.getInventory().getStack(i);
			if (itemStack.getItem() == item) count += itemStack.getCount();
		}

		return Value.number(count);
	}

	private static Value hasPotionEffect(Starscript ss, int argCount) {
		if (argCount < 1) ss.error("player.has_potion_effect() requires 1 argument, got %d.", argCount);
		if (mc.player == null) return Value.bool(false);

		Identifier name = popIdentifier(ss, "First argument to player.has_potion_effect() needs to a string.");

		StatusEffect effect = Registries.STATUS_EFFECT.get(name);
		if (effect == null) return Value.bool(false);

		StatusEffectInstance effectInstance = mc.player.getStatusEffect(effect);
		return Value.bool(effectInstance != null);
	}

	private static Value getPotionEffect(Starscript ss, int argCount) {
		if (argCount < 1) ss.error("player.get_potion_effect() requires 1 argument, got %d.", argCount);
		if (mc.player == null) return Value.null_();

		Identifier name = popIdentifier(ss, "First argument to player.get_potion_effect() needs to a string.");

		StatusEffect effect = Registries.STATUS_EFFECT.get(name);
		if (effect == null) return Value.null_();

		StatusEffectInstance effectInstance = mc.player.getStatusEffect(effect);
		if (effectInstance == null) return Value.null_();

		return wrap(effectInstance);
	}
	private static long lastRequestedStatsTime = 0;
	private static Value getStat(Starscript ss, int argCount) {
		if (argCount < 1) ss.error("player.get_stat() requires 1 argument, got %d.", argCount);
		if (mc.player == null) return Value.number(0);

		long time = System.currentTimeMillis();
		if ((time - lastRequestedStatsTime) / 1000.0 >= 1 && mc.getNetworkHandler() != null) {
			mc.getNetworkHandler().sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.REQUEST_STATS));
			lastRequestedStatsTime = time;
		}

		String type = argCount > 1 ? ss.popString("First argument to player.get_stat() needs to be a string.") : "custom";
		Identifier name = popIdentifier(ss, (argCount > 1 ? "Second" : "First") + " argument to player.get_stat() needs to be a string.");

		Stat<?> stat = switch (type) {
			case "mined" -> Stats.MINED.getOrCreateStat(Registries.BLOCK.get(name));
			case "crafted" -> Stats.CRAFTED.getOrCreateStat(Registries.ITEM.get(name));
			case "used" -> Stats.USED.getOrCreateStat(Registries.ITEM.get(name));
			case "broken" -> Stats.BROKEN.getOrCreateStat(Registries.ITEM.get(name));
			case "picked_up" -> Stats.PICKED_UP.getOrCreateStat(Registries.ITEM.get(name));
			case "dropped" -> Stats.DROPPED.getOrCreateStat(Registries.ITEM.get(name));
			case "killed" -> Stats.KILLED.getOrCreateStat(Registries.ENTITY_TYPE.get(name));
			case "killed_by" -> Stats.KILLED_BY.getOrCreateStat(Registries.ENTITY_TYPE.get(name));
			case "custom" -> {
				name = Registries.CUSTOM_STAT.get(name);
				yield name != null ? Stats.CUSTOM.getOrCreateStat(name) : null;
			}
			default -> null;
		};

		return Value.number(stat != null ? mc.player.getStatHandler().getStat(stat) : 0);
	}
}

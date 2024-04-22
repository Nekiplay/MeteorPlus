package nekiplay.meteorplus.mixin.meteorclient.modules;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.mixininterface.IPlayerInteractEntityC2SPacket;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.combat.Criticals;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static nekiplay.meteorplus.features.modules.combat.criticals.CriticalsPlus.skipCrit;

@Mixin(value = Criticals.class, remap = false, priority = 1001)
public class CriticalsMixin extends Module {
	public CriticalsMixin(Category category, String name, String description) {
		super(category, name, description);
	}

	@Inject(method = "onSendPacket", at = @At("HEAD"), cancellable = true)
	private void onSendPacket(PacketEvent.Send event, CallbackInfo ci) {
		if (event.packet instanceof IPlayerInteractEntityC2SPacket packet && packet.getType() == PlayerInteractEntityC2SPacket.InteractType.ATTACK) {
			if (skipCrit()) { ci.cancel(); return; }

			Entity entity = packet.getEntity();
			if (entity.getType() == EntityType.SHULKER_BULLET || entity.getType() == EntityType.FIREBALL) {
				ci.cancel();
			}
		}
	}
}

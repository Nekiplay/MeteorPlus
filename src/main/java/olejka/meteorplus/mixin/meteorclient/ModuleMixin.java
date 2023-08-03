package olejka.meteorplus.mixin.meteorclient;

import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.nbt.NbtCompound;
import olejka.meteorplus.mixinclasses.IModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Module.class)
public class ModuleMixin implements IModule {
	public boolean hidden = false;

	@Inject(method = "toTag", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void toTag(CallbackInfoReturnable<NbtCompound> cir, NbtCompound tag) {
		tag.putBoolean("hidden", hidden);
	}

	@Inject(method = "fromTag(Lnet/minecraft/nbt/NbtCompound;)Lmeteordevelopment/meteorclient/systems/modules/Module;", at = @At("RETURN"))
	private void fromTag(NbtCompound tag, CallbackInfoReturnable<Module> cir) {
		hidden = tag.getBoolean("hidden");
	}

	@Override
	public boolean isHidden() {
		return hidden;
	}

	@Override
	public void setHidden(boolean value) {
		hidden = value;
	}
}

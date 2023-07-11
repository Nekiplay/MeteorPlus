package olejka.meteorplus.mixin.accessors;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(KeyBinding.class)
public class KeyBindingAccessor {
	@Shadow
	private InputUtil.Key boundKey;

	public InputUtil.Key GetBoundKey() {
		return boundKey;
	}
}

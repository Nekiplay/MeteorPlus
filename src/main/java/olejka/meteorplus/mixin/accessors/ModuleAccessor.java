package olejka.meteorplus.mixin.accessors;

import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Module.class)
public interface ModuleAccessor {
	@Accessor("active")
	boolean getActive();

	@Mutable
	@Accessor("active")
	void setActive(boolean active);
}

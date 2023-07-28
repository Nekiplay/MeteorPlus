package olejka.meteorplus.mixin.accessors;

import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Modules.class)
public interface ModulesAccessor {
	@Invoker("addActive")
	void invokeAddActive(Module modules);
}

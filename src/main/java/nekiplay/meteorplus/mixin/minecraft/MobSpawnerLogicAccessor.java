package nekiplay.meteorplus.mixin.minecraft;

import net.minecraft.entity.Entity;
import net.minecraft.world.MobSpawnerLogic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import javax.annotation.Nullable;

@Mixin(MobSpawnerLogic.class)
public interface MobSpawnerLogicAccessor {
	@Accessor("renderedEntity")
	@Nullable
	Entity getEntity();
}

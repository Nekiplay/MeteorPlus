package nekiplay.meteorplus.mixin.minecraft;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.world.MobSpawnerLogic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MobSpawnerBlockEntity.class)
public interface MobSpawnerBlockEntityAccessor {
	@Accessor("logic")
	MobSpawnerLogic getLogic();
}

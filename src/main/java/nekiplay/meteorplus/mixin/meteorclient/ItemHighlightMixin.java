package nekiplay.meteorplus.mixin.meteorclient;


import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.ItemHighlight;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import nekiplay.meteorplus.features.modules.ItemHighlightPlus;
import nekiplay.meteorplus.settings.items.ESPItemData;
import nekiplay.meteorplus.settings.items.HiglightItemData;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemHighlight.class)
public class ItemHighlightMixin {
	@Inject(method = "getColor", at = @At("RETURN"), cancellable = true)
	private void getColor(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
		if (cir.getReturnValue() == -1) {
			if (stack != null) {
				ItemHighlightPlus itemHighlightPlus = Modules.get().get(ItemHighlightPlus.class);
				if (itemHighlightPlus.isActive()) {
					if (itemHighlightPlus.whitelist.get().contains(stack.getItem().asItem())) {
						HiglightItemData espItemData = itemHighlightPlus.itemsConfigs.get().get(stack.getItem());
						if (espItemData != null) {
							cir.setReturnValue(espItemData.Color.getPacked());
						}
						else {
							cir.setReturnValue(itemHighlightPlus.defaultBlockConfig.get().Color.getPacked());
						}
					}
				}
			}
		}
	}
}

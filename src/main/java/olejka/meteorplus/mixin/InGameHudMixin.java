package olejka.meteorplus.mixin;

import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.Text;
import olejka.meteorplus.events.ScoreBoardRenderEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

//	"Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;FFI)I"
	@Inject(method = "renderScoreboardSidebar", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/ScoreboardObjective;getScoreboard()Lnet/minecraft/scoreboard/Scoreboard;", ordinal = 0))
	private void renderScoreboardSidebar(DrawContext context, ScoreboardObjective objective, CallbackInfo ci) {
		MeteorClient.EVENT_BUS.post(ScoreBoardRenderEvent.get(context.getMatrices(), objective));
	}
}

package olejka.meteorplus.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import olejka.meteorplus.MeteorPlus;

import java.util.List;

public class AutoDropPlus extends Module  {
	public AutoDropPlus() {
		super(MeteorPlus.CATEGORY, "auto-drop-plus", "Auto drop items in inventory.");
	}

	private final SettingGroup defaultGroup = settings.getDefaultGroup();

	private final Setting<List<Item>> items = defaultGroup.add(new ItemListSetting.Builder()
		.name("drop-items")
		.description("Items to dropping.")
		.build()
	);

	private final Setting<Integer> delay = defaultGroup.add(new IntSetting.Builder()
		.name("delay")
		.description("drop delay.")
		.defaultValue(5)
		.min(0)
		.build()
	);

	private final Setting<Boolean> autoDropExcludeHotbar = defaultGroup.add(new BoolSetting.Builder()
		.name("auto-Drop-ExcludeHotbar")
		.description("Allow hotbar?.")
		.defaultValue(true)
		.build()
	);

	private int tick = 0;

	@EventHandler
	public void onTickPost(TickEvent.Post event) {
		for (int i = autoDropExcludeHotbar.get() ? 9 : 0; i < mc.player.getInventory().size(); i++) {
			ItemStack itemStack = mc.player.getInventory().getStack(i);

			if (items.get().contains(itemStack.getItem())) {
				if (tick == 0) {
					InvUtils.drop().slot(i);
					tick = delay.get();
				}
				else {
					tick--;
				}
			}
		}
	}
}

package nekiplay.meteorplus.mixin.meteorclient;

import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.misc.InventoryTweaks;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import net.minecraft.item.Item;
import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Mixin(value = InventoryTweaks.class, remap = false)
public class InventoryTweaksMixin {
	@Unique
	private final InventoryTweaks inventoryTweaks = (InventoryTweaks)(Object) this;

	@Shadow
	@Final
	private final SettingGroup sgSorting = inventoryTweaks.settings.createGroup("Sorting");
	@Shadow
	@Final
	private final SettingGroup sgAutoDrop = inventoryTweaks.settings.createGroup("Auto Drop");
	@Shadow
	@Final
	private final SettingGroup sgStealDump = inventoryTweaks.settings.createGroup("Steal and Dump");
	@Shadow
	@Final
	private final SettingGroup sgAutoSteal = inventoryTweaks.settings.createGroup("Auto Steal");

	@Shadow
	@Final
	private final Setting<Integer> autoStealDelay = sgAutoSteal.add(new IntSetting.Builder()
		.name("delay")
		.description("The minimum delay between stealing the next stack in milliseconds.")
		.defaultValue(20)
		.sliderMax(1000)
		.build()
	);

	@Shadow
	@Final
	private final Setting<Integer> autoStealInitDelay = sgAutoSteal.add(new IntSetting.Builder()
		.name("initial-delay")
		.description("The initial delay before stealing in milliseconds. 0 to use normal delay instead.")
		.defaultValue(50)
		.sliderMax(1000)
		.build()
	);

	@Shadow
	@Final
	private final Setting<Integer> autoStealRandomDelay = sgAutoSteal.add(new IntSetting.Builder()
		.name("random")
		.description("Randomly adds a delay of up to the specified time in milliseconds.")
		.min(0)
		.sliderMax(1000)
		.defaultValue(50)
		.build()
	);

	@Shadow
	@Final
	private final Setting<Boolean> stealDrop = sgStealDump.add(new BoolSetting.Builder()
		.name("steal-drop")
		.description("Drop items to the ground instead of stealing them.")
		.defaultValue(false)
		.build()
	);

	@Shadow
	@Final
	private final Setting<Boolean> dropBackwards = sgStealDump.add(new BoolSetting.Builder()
		.name("drop-backwards")
		.description("Drop items behind you.")
		.defaultValue(false)
		.visible(stealDrop::get)
		.build()
	);

	@Shadow
	@Final
	private final Setting<InventoryTweaks.ListMode> dumpFilter = sgStealDump.add(new EnumSetting.Builder<InventoryTweaks.ListMode>()
		.name("dump-filter")
		.description("Dump mode.")
		.defaultValue(InventoryTweaks.ListMode.None)
		.build()
	);

	@Shadow
	@Final
	private final Setting<List<Item>> dumpItems = sgStealDump.add(new ItemListSetting.Builder()
		.name("dump-items")
		.description("Items to dump.")
		.build()
	);

	@Shadow
	@Final

	private final Setting<InventoryTweaks.ListMode> stealFilter = sgStealDump.add(new EnumSetting.Builder<InventoryTweaks.ListMode>()
		.name("steal-filter")
		.description("Steal mode.")
		.defaultValue(InventoryTweaks.ListMode.None)
		.build()
	);

	@Shadow
	@Final
	private final Setting<List<Item>> stealItems = sgStealDump.add(new ItemListSetting.Builder()
		.name("steal-items")
		.description("Items to steal.")
		.build()
	);


	@Shadow
	private int getSleepTime() {
		return 0;
	}


	/**
	 * @author Neki_play
	 * @reason Use delay if item not contains
	 */
	@Overwrite
	private void moveSlots(ScreenHandler handler, int start, int end, boolean steal) {
		boolean initial = autoStealInitDelay.get() != 0;
		for (int i = start; i < end; i++) {
			if (!handler.getSlot(i).hasStack()) continue;

			// Exit if user closes screen or exit world
			if (mc.currentScreen == null || !Utils.canUpdate()) break;

			Item item = handler.getSlot(i).getStack().getItem();
			if (steal) {
				if (stealFilter.get() == InventoryTweaks.ListMode.Whitelist && !stealItems.get().contains(item))
					continue;
				if (stealFilter.get() == InventoryTweaks.ListMode.Blacklist && stealItems.get().contains(item))
					continue;
			} else {
				if (dumpFilter.get() == InventoryTweaks.ListMode.Whitelist && !dumpItems.get().contains(item))
					continue;
				if (dumpFilter.get() == InventoryTweaks.ListMode.Blacklist && dumpItems.get().contains(item))
					continue;
			}

			int sleep;
			if (initial) {
				sleep = autoStealInitDelay.get();
				initial = false;
			} else sleep = getSleepTime();
			if (sleep > 0) {
				try {
					Thread.sleep(sleep);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			if (steal && stealDrop.get()) {
				if (dropBackwards.get()) {
					int iCopy = i;
					Rotations.rotate(mc.player.getYaw() - 180, mc.player.getPitch(), () -> InvUtils.drop().slotId(iCopy));
				}
			} else InvUtils.shiftClick().slotId(i);
		}
	}
}

package olejka.meteorplus.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.SlotUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import olejka.meteorplus.MeteorPlus;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class AutoSell extends Module {
	public AutoSell() {
		super(MeteorPlus.CATEGORY, "Auto Sell", "Auto sell items in auction.");
	}

	private final SettingGroup ASSettings = settings.createGroup("Auto Sell Settings");

	private final Setting<Item> selling_item = ASSettings.add(new ItemSetting.Builder()
		.name("Item")
		.description("Selled item.")
		.defaultValue(Items.DIAMOND)
		.build()
	);

	private final Setting<String> cost = ASSettings.add(new StringSetting.Builder()
		.name("Cost per item")
		.description("Item cost per item.")
		.defaultValue("2000")
		.build()
	);

	private final Setting<String> command = ASSettings.add(new StringSetting.Builder()
		.name("Command")
		.description("Selling command.")
		.defaultValue("/ah sell {cost}")
		.build()
	);

	private final Setting<Integer> delay = ASSettings.add(new IntSetting.Builder()
		.name("Selling delay")
		.description("Selling delay.")
		.defaultValue(2000)
		.build()
	);

	private int tick = 0;
	private int slot = 0;
	private long millis = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
	@Override
	public void onActivate() {
		millis = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}

	@EventHandler
	private void onTickEventPre(TickEvent.Pre event)
	{
		FindItemResult result = InvUtils.find(selling_item.get());
		if (result.found()) {
			if (result.count() >= 1) {
				if (LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() >= millis) {
					if (tick == 0) {
						assert mc.player != null;
						slot = mc.player.getInventory().selectedSlot;
						InvUtils.move().from(result.slot()).toHotbar(8);
						mc.player.getInventory().selectedSlot = 8;
						tick++;
					}
					else if (mc.player != null && tick == 1 && mc.player.getInventory().selectedSlot == 8) {
						ItemStack item = mc.player.getInventory().getStack(SlotUtils.HOTBAR_END);
						int cc = Integer.parseInt(cost.get()) * item.getCount();
						mc.player.sendMessage(Text.of(command.get().replace("{cost}", Integer.toString(cc))));
						info("Selling §c" + item.getName().getString() + " x" + item.getCount() + " §rfor §6" + cc);
						tick++;
					}
					else if (mc.player != null && tick == 2 && mc.player.getInventory().selectedSlot == 8) {
						mc.player.getInventory().selectedSlot = slot;
						mc.player.getInventory().updateItems();
						tick = 0;
						millis = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() + delay.get();
					}
				}
			} else {
				info("Not have item stack");
				toggle();
			}
		} else {
			info("Selling item not found");
			toggle();
		}
	}
}

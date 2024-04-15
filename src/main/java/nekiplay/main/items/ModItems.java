package nekiplay.main.items;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
	public static final Item METEOR_PLUS_LOGO_ITEM = new Item(new FabricItemSettings());
	public static final Item METEOR_PLUS_LOGO_MODS_ITEM = new Item(new FabricItemSettings());

	public static final Item BOZE_LOGO_ITEM = new Item(new FabricItemSettings());
	public static void initializeMeteorPlus() {
		Registry.register(Registries.ITEM, new Identifier("meteorplus", "logo"), METEOR_PLUS_LOGO_ITEM);
		Registry.register(Registries.ITEM, new Identifier("meteorplus", "logo_mods"), METEOR_PLUS_LOGO_MODS_ITEM);

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> {
			content.add(METEOR_PLUS_LOGO_ITEM);
			content.add(METEOR_PLUS_LOGO_MODS_ITEM);
		});
	}
	public static void initializeBozePlus() {
		Registry.register(Registries.ITEM, new Identifier("bozeplus", "logo"), BOZE_LOGO_ITEM);

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> {
			content.add(BOZE_LOGO_ITEM);
		});
	}
}

package olejka.meteorplus.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import olejka.meteorplus.MeteorPlus;
import olejka.meteorplus.utils.BlockHelper;

import java.util.List;

public class AutoCraftPlus extends Module {
	public AutoCraftPlus() {
		super(MeteorPlus.CATEGORY, "Auto Craft", "Automatically craft items.");
	}

	private final SettingGroup sgGeneral = settings.getDefaultGroup();

	private final Setting<Boolean> autoOpen = sgGeneral.add(new BoolSetting.Builder()
		.name("auto-open")
		.description("Auto open crafting table.")
		.defaultValue(false)
		.build()
	);

	private final Setting<Boolean> autoPlace = sgGeneral.add(new BoolSetting.Builder()
		.name("auto-place")
		.description("Auto place crafting table.")
		.defaultValue(false)
		.visible(autoOpen::get)
		.build()
	);

	private final Setting<Integer> radius = sgGeneral.add(new IntSetting.Builder()
		.name("auto-place")
		.description("Auto place crafting table.")
		.defaultValue(5)
		.visible(autoOpen::get)
		.build()
	);

	private final Setting<List<Item>> items = sgGeneral.add(new ItemListSetting.Builder()
		.name("items")
		.description("Items you want to get crafted.")
		.defaultValue(List.of())
		.build()
	);

	private final Setting<Boolean> antiDesync = sgGeneral.add(new BoolSetting.Builder()
		.name("anti-desync")
		.description("Try to prevent inventory desync.")
		.defaultValue(false)
		.build()
	);

	private final Setting<Boolean> craftAll = sgGeneral.add(new BoolSetting.Builder()
		.name("craft-all")
		.description("Crafts maximum possible amount amount per craft (shift-clicking)")
		.defaultValue(false)
		.build()
	);

	private final Setting<Boolean> drop = sgGeneral.add(new BoolSetting.Builder()
		.name("drop")
		.description("Automatically drops crafted items (useful for when not enough inventory space)")
		.defaultValue(false)
		.build()
	);

	private BlockPos findCraftingTable() {
		assert mc.player != null;
		List<BlockPos> nearbyBlocks = BlockHelper.getSphere(mc.player.getBlockPos(), radius.get(), radius.get());
		for (BlockPos block : nearbyBlocks) if (BlockHelper.getBlock(block) == Blocks.CRAFTING_TABLE) return block;
		return null;
	}

	private void openCraftingTable(BlockPos tablePos) {
		Vec3d tableVec = new Vec3d(tablePos.getX(), tablePos.getY(), tablePos.getZ());
		BlockHitResult table = new BlockHitResult(tableVec, Direction.UP, tablePos, false);
		assert mc.interactionManager != null;
		mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, table);
	}

	private void placeCraftingTable(FindItemResult craftTable) {
		assert mc.player != null;
		List<BlockPos> nearbyBlocks = BlockHelper.getSphere(mc.player.getBlockPos(), radius.get(), radius.get());
		for (BlockPos block : nearbyBlocks) {
			if (BlockHelper.getBlock(block) == Blocks.AIR) {
				BlockUtils.place(block, craftTable, 0, true);
				break;
			}
		}
	}
	@Override
	public void onActivate() {
		opened = false;
		if (autoOpen.get()) {
			BlockPos craftingTable = findCraftingTable();
			if (craftingTable != null) {
				openCraftingTable(craftingTable);
			}
			else {
				FindItemResult craftTableFind = InvUtils.findInHotbar(Blocks.CRAFTING_TABLE.asItem());
				if (craftTableFind.found() && autoPlace.get()) {
					placeCraftingTable(craftTableFind);
				}
			}
		}
	}

	private boolean isContainsIngredients(Recipe<?> recipe)
	{
		boolean find = false;
		DefaultedList<Ingredient> ingredients = recipe.getIngredients();
		for (Ingredient ing : ingredients) {
			for (ItemStack itemStack : ing.getMatchingStacks()) {
				FindItemResult plank = InvUtils.find(item -> itemStack.getItem() == item.getItem());
				find = plank.found() && plank.count() >= itemStack.getCount();
			}
		}
		return find;
	}
	private boolean opened = false;
	@EventHandler
	private void onTick(TickEvent.Post event) {
		if (mc.interactionManager == null) return;
		if (items.get().isEmpty()) return;

		if (mc.player != null && mc.player.currentScreenHandler instanceof CraftingScreenHandler currentScreenHandler) {
			if (antiDesync.get())
				mc.player.getInventory().updateItems();
			opened = true;
			// Danke schön GhostTypes
			// https://github.com/GhostTypes/orion/blob/main/src/main/java/me/ghosttypes/orion/modules/main/AutoBedCraft.java
			List<Item> itemList = items.get();
			List<RecipeResultCollection> recipeResultCollectionList = mc.player.getRecipeBook().getOrderedResults();
			for (RecipeResultCollection recipeResultCollection : recipeResultCollectionList) {
				for (Recipe<?> recipe : recipeResultCollection.getRecipes(true)) {
					if (!itemList.contains(recipe.getOutput(mc.getNetworkHandler().getRegistryManager()).getItem())) continue;
					if (isContainsIngredients(recipe)) {
						mc.interactionManager.clickRecipe(currentScreenHandler.syncId, recipe, craftAll.get());
						mc.interactionManager.clickSlot(currentScreenHandler.syncId, 0, 1,
							drop.get() ? SlotActionType.THROW : SlotActionType.QUICK_MOVE, mc.player);
					}
				}
			}
		}
		else if (!opened) {
			if (autoOpen.get()) {
				BlockPos crafting_table = findCraftingTable();
				if (crafting_table != null) {
					openCraftingTable(crafting_table);
				}
			}
		}
	}
}

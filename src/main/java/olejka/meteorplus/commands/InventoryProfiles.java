package olejka.meteorplus.commands;

import com.google.gson.Gson;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.commands.Command;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.nbt.visitor.StringNbtWriter;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameMode;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class InventoryProfiles extends Command {
	public InventoryProfiles() {
		super("invprofiles", "Gives items in creative", "invp");
	}
	private static int delay = 80;
	@Override
	public void build(LiteralArgumentBuilder<CommandSource> builder) {
		builder.then(literal("save").then(argument("id", StringArgumentType.greedyString()).executes(ctx -> {
			String id = ctx.getArgument("id", String.class).replace("&", "\247");
			JsonItems profile = getProfile(id);
			if (profile != null) {
				profiles.remove(profile);
			}
			JsonItems jsonItems = new JsonItems();
			jsonItems.id = id;

			for (int i = 0; i <= mc.player.getInventory().size(); i++) {
				ItemStack item = mc.player.getInventory().getStack(i);
				if (item.getItem() != Items.AIR && i != 35) {
					JsonItem jsonItem = new JsonItem(item, i);
					jsonItems.items.add(jsonItem);
				}
			}
			profiles.add(jsonItems);
			saveProfile(jsonItems);
			info("Profile " + id + " saved");
			return SINGLE_SUCCESS;
		})));
		builder.then(literal("load").then(argument("id", StringArgumentType.greedyString()).executes(ctx -> {
			String id = ctx.getArgument("id", String.class).replace("&", "\247");
			if (PlayerUtils.getGameMode() == GameMode.CREATIVE) {
				JsonItems profile = getProfile(id);
				if (profile != null) {
					mc.player.getInventory().clear();
					getItems(profile);
					info("Profile " + id + " loaded");
				} else {
					JsonItems saved = getSaved(id);
					if (saved != null) {
						mc.player.getInventory().clear();
						getItems(saved);
						info("Profile " + id + " loaded");
					} else {
						info("Profile " + id + " not found");
					}
				}
			}
			else {
				info("Need gamemode 1");
			}
			return SINGLE_SUCCESS;
		})));
		builder.then(literal("remove").then(argument("id", StringArgumentType.greedyString()).executes(ctx -> {
			String id = ctx.getArgument("id", String.class).replace("&", "\247");
			JsonItems saved = getSaved(id);
			if (saved != null) {
				File dir = new File(MeteorClient.FOLDER, "inventory-profiles");
				if (dir.exists()) {
					File file = new File(dir, id + extension);
					info(file.getPath());
					if (file.delete()) {
						info("Profile " + id + " deleted");
					}
					else {
						info("Profile " + id + " not deleted");
					}
				}
			}
			JsonItems profile = getProfile(id);
			if (profile != null && profiles.contains(profile)) {
				profiles.remove(profile);
			}
			return SINGLE_SUCCESS;
		})));
	}

	public static ArrayList<JsonItems> profiles = new ArrayList<>();

	private static String extension = ".inventory";

	public static JsonItems getSaved(String id) {
		File dir = new File(MeteorClient.FOLDER, "inventory-profiles");
		if (dir.exists()) {
			File file = new File(dir, id + extension);
			if (file.exists()) {
				FileReader fr = null;
				try {
					fr = new FileReader(file);
					BufferedReader reader = new BufferedReader(fr);
					try {
						String json = reader.readLine();
						JsonItems read = gson.fromJson(json, JsonItems.class);
						reader.close();
						if (read != null) {
							return read;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static void saveProfile(JsonItems profile) {
		File dir = new File(MeteorClient.FOLDER, "inventory-profiles");
		if (!dir.exists()) {
			dir.mkdir();
		}

		File file = new File(dir, profile.id + extension);
		if (!file.exists()) {
			try {
				file.createNewFile();
			}
			catch (IOException ignore) { }
		}

		String json = gson.toJson(profile);
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		PrintWriter printWriter = new PrintWriter(fileWriter);
		printWriter.print(json);
		printWriter.close();
	}

	public static JsonItems getProfile(String id) {
		for (JsonItems items : profiles) {
			if (items.id.equals(id)) {
				return items;
			}
		}
		return null;
	}

	private void sleep(int delay) {
		try { Thread.sleep(delay); }
		catch (InterruptedException ignore) { }
	}
	private void getItems(JsonItems profile) {
		if (profile.items != null) {
			for (JsonItem jsonItem : profile.items) {
				ItemStack item = JsonItem.toStack(jsonItem);
				if (item != null) {
					mc.player.getInventory().insertStack(item);
				}
			}
		}
	}

	public class JsonItems {
		public Collection<JsonItem> items = new ArrayList<JsonItem>();
		public String id;
	}
	public static Gson gson = new Gson();
	public class JsonItem {
		public int item = 0;
		public int slot = 0;
		public int count = 0;


		public String nbt = "";

		public JsonItem(ItemStack itemStack, int slot) {
			this.slot = slot;
			this.item = toId(itemStack.getItem());
			this.count = itemStack.getCount();

			if (itemStack.hasNbt()) {
				NbtCompound nbt = itemStack.getNbt();
				this.nbt = new StringNbtWriter().apply(nbt);
			}
		}

		public static ItemStack toStack(JsonItem jsItem) {
			Item item = fromId(jsItem.item);
			if (item != Items.AIR) {
				ItemStack itemStack = item.getDefaultStack();
				itemStack.setCount(jsItem.count);
				if (!jsItem.nbt.equals("")) {
					try {
						itemStack.setNbt(StringNbtReader.parse(jsItem.nbt));
					} catch (CommandSyntaxException ignore) {
					}
				}
				return itemStack;
			}
			return null;
		}
	}

	private static int toId(Item item) {
		return Registry.ITEM.getRawId(item);
	}

	private static Item fromId(int id) {
		return Registry.ITEM.get(id);
	}
}

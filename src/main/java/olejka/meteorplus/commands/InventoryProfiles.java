package olejka.meteorplus.commands;

import com.google.gson.Gson;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.commands.Command;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.nbt.visitor.StringNbtWriter;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.registry.Registry;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class InventoryProfiles extends Command {
	public InventoryProfiles() {
		super("invprofiles", "Gives items in creative", "invp");
	}
	private int delay = 80;
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
				if (item.getItem() != Items.AIR) {
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
			JsonItems profile = getProfile(id);
			if (profile != null) {
				mc.player.getInventory().clear();
				Runnable task = new Runnable() {
					public void run() {
						System.out.println("Hello, World!");getItems(profile);
						info("Profile " + id + " loaded");
					}
				};
				Thread thread = new Thread(task);
				thread.start();
			}
			else {
				JsonItems saved = getSaved(id);
				if (saved != null) {
					mc.player.getInventory().clear();
					Runnable task = new Runnable() {
						public void run() {
							getItems(saved);
							info("Profile " + id + " loaded");
						}
					};
					Thread thread = new Thread(task);
					thread.start();
				}
				else {
					info("Profile " + id + " not found");
				}
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

	private ArrayList<JsonItems> profiles = new ArrayList<>();

	private String extension = ".inventory";

	private JsonItems getSaved(String id) {
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

	private void saveProfile(JsonItems profile) {
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

	private JsonItems getProfile(String id) {
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
					mc.player.getInventory().insertStack(jsonItem.slot, item);
					sleep(40);
					mc.interactionManager.clickSlot(0, jsonItem.slot, 0, SlotActionType.PICKUP_ALL, mc.player);
				}
			}
			mc.player.getInventory().updateItems();
		}
	}

	public class JsonItems {
		Collection<JsonItem> items = new ArrayList<JsonItem>();
		public String id;
	}
	Gson gson = new Gson();
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

	private int toId(Item item) {
		return Registry.ITEM.getRawId(item);
	}

	private static Item fromId(int id) {
		return Registry.ITEM.get(id);
	}
}

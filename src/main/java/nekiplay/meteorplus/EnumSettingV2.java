package nekiplay.meteorplus;

import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IVisible;
import meteordevelopment.meteorclient.settings.Setting;

import net.minecraft.nbt.NbtCompound;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EnumSettingV2<T extends Enum<?>> extends Setting<T> {
	private T[] values;

	private final List<String> suggestions;

	public EnumSettingV2(String name, String description, T defaultValue, Consumer<T> onChanged, Consumer<Setting<T>> onModuleActivated, IVisible visible) {
		super(name, description, defaultValue, onChanged, onModuleActivated, visible);

		try {
			values = (T[]) defaultValue.getClass().getMethod("values").invoke(null);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
		}

		suggestions = new ArrayList<>(values.length);
		for (T value : values) suggestions.add(value.toString());
	}

	@Override
	protected T parseImpl(String str) {
		for (T possibleValue : values) {
			if (str.equalsIgnoreCase(possibleValue.toString())) return possibleValue;
		}

		return null;
	}

	@Override
	protected boolean isValueValid(T value) {
		return true;
	}

	@Override
	public List<String> getSuggestions() {
		return suggestions;
	}

	@Override
	public NbtCompound save(NbtCompound tag) {
		tag.putString("value", get().toString());

		return tag;
	}

	@Override
	public T load(NbtCompound tag) {
		parse(tag.getString("value"));

		return get();
	}

	public static class Builder<T extends Enum<?>> extends SettingBuilder<Builder<T>, T, EnumSettingV2<T>> {
		public Builder() {
			super(null);
		}

		@Override
		public EnumSettingV2<T> build() {
			return new EnumSettingV2<>(name, description, defaultValue, onChanged, onModuleActivated, visible);
		}
	}
}

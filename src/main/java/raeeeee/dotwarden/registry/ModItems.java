package raeeeee.dotwarden.registry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;
import raeeeee.dotwarden.DOTWarden;
import raeeeee.dotwarden.item.PowerItem;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ModItems {
	Map<Item, Identifier> ITEMS = new LinkedHashMap<>();

	// Item NAME = createItem("name", new ModItem(new QuiltItemSettings()));

	Item POWER_OF_THE_DISCIPLE = createItem("power_of_the_disciple", new PowerItem(
		new QuiltItemSettings().maxCount(1).group(ItemGroup.TOOLS), 10_000));

	private static <T extends Item> T createItem(String name, T item) {
		ITEMS.put(item, new Identifier(DOTWarden.ID, name));
		return item;
	}

	static void init() {
		ITEMS.keySet().forEach(item -> {
			Registry.register(Registry.ITEM, ITEMS.get(item), item);
		});
	}
}

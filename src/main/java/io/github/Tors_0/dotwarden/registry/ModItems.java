package io.github.Tors_0.dotwarden.registry;

import io.github.Tors_0.dotwarden.DOTWarden;
import io.github.Tors_0.dotwarden.item.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ModItems {
	Map<Item, Identifier> ITEMS = new LinkedHashMap<>();

	// Item NAME = createItem("name", new ModItem(new QuiltItemSettings()));

	Item POWER_OF_THE_DISCIPLE = createItem("power_of_the_disciple", new PowerItem(
		new QuiltItemSettings().maxCount(1).group(ItemGroup.TOOLS).rarity(Rarity.EPIC)));
    Item SCULKED_KNIFE = createItem("sculked_knife", new SculkedKnifeItem(new QuiltItemSettings().maxCount(1)
            .group(ItemGroup.COMBAT).rarity(Rarity.RARE).maxDamage(50)));
    Item CORRUPTED_HEART = createItem("corrupted_heart", new CorruptedHeartItem(new QuiltItemSettings()
            .rarity(Rarity.RARE).maxCount(1).fireproof().group(ItemGroup.MISC)));
    Item CAPTURED_SOUL = createItem("captured_soul", new SoulItem(new QuiltItemSettings().group(ItemGroup.MISC).maxCount(4)));
    Item SCULK_CORE = createItem("sculk_core", new Item(new QuiltItemSettings().group(ItemGroup.MISC)));
    Item ECHO_CHAMBER = createItem("echo_chamber", new EchoChamberItem(new QuiltItemSettings().group(ItemGroup.TOOLS)));

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

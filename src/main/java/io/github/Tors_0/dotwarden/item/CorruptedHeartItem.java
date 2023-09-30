package io.github.Tors_0.dotwarden.item;

import io.github.Tors_0.dotwarden.DOTWarden;
import io.github.Tors_0.dotwarden.registry.ModItems;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class CorruptedHeartItem extends Item {
    public CorruptedHeartItem(Settings settings) {
        super(settings);
    }
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Owner: ").append(stack.getOrCreateSubNbt(DOTWarden.ID).getString("owner")));
        super.appendTooltip(stack, world, tooltip, context);
    }
    @Override
    public ItemStack getDefaultStack() {
        ItemStack itm = new ItemStack(this);
        itm.getOrCreateSubNbt(DOTWarden.ID).putString("owner","None");
        return itm;
    }
}

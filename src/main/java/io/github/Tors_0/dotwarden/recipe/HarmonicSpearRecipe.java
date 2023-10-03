package io.github.Tors_0.dotwarden.recipe;

import io.github.Tors_0.dotwarden.DOTWarden;
import io.github.Tors_0.dotwarden.registry.ModItems;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtElement;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class HarmonicSpearRecipe extends SpecialCraftingRecipe {
    private static final Ingredient ECHO_CHAMBER = Ingredient.ofItems(ModItems.ECHO_CHAMBER);
    private static final Ingredient NETHERITE_AXE = Ingredient.ofItems(Items.NETHERITE_AXE);

    public HarmonicSpearRecipe(Identifier id) {
        super(id);
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return false;
    }

    @Override
    public boolean matches(CraftingInventory inventory, World world) {
        for (int i = 0; i < 6; ++i) {
            ItemStack itemStack = inventory.getStack(i);
            if (!itemStack.isEmpty()) {
                if (NETHERITE_AXE.test(itemStack)) {
                    return ECHO_CHAMBER.test(inventory.getStack(i + 3));
                }
            }
        }

        return false;
    }

    @Override
    public ItemStack craft(CraftingInventory inventory) {
        for (int i = 3; i < 9; ++i) {
            ItemStack echoStack = inventory.getStack(i);
            if (
                    !echoStack.isEmpty()
                    && echoStack.getNbt() != null
                    && echoStack.getOrCreateNbt().contains("Items")
                    && ItemStack.fromNbt(echoStack.getOrCreateNbt().getList("Items", NbtElement.COMPOUND_TYPE).getCompound(0)).getCount() == 1
            ) {
                if (inventory.getStack(i-3).isOf(Items.NETHERITE_AXE)) {
                    return new ItemStack(ModItems.HARMONIC_SPEAR);
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getOutput() {
        return new ItemStack(ModItems.HARMONIC_SPEAR);
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return DOTWarden.HARMONIC_AXE_RECIPE;
    }
}

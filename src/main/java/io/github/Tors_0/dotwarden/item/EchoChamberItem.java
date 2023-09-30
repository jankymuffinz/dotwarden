package io.github.Tors_0.dotwarden.item;

import io.github.Tors_0.dotwarden.registry.ModItems;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class EchoChamberItem extends BundleItem {

    public static final int MAX_STORAGE = 4;
    public EchoChamberItem(Settings settings) {
        super(settings);
    }
    @Override
    public boolean onClickedOnOther(ItemStack thisStack, Slot otherSlot, ClickType clickType, PlayerEntity player) {
        if (clickType != ClickType.RIGHT) {
            return false;
        } else {
            ItemStack itemStack = otherSlot.getStack();
            if (itemStack.isEmpty()) {
                this.playRemoveOneSound(player);
                removeFirstStack(thisStack).ifPresent(removedStack -> addToBundle(thisStack, otherSlot.insertStack(removedStack)));
            } else if (itemStack.getItem().canBeNested() && itemStack.isOf(ModItems.CAPTURED_SOUL)) {
                int i = (MAX_STORAGE - getBundleOccupancy(thisStack)) / getItemOccupancy(itemStack);
                int j = addToBundle(thisStack, otherSlot.takeStackRange(itemStack.getCount(), i, player));
                if (j > 0) {
                    this.playInsertSound(player);
                }
            }

            return true;
        }
    }

    @Override
    public boolean onClicked(
            ItemStack thisStack, ItemStack otherStack, Slot thisSlot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference
    ) {
        if (clickType == ClickType.RIGHT && thisSlot.canTakePartial(player) && otherStack.isOf(ModItems.CAPTURED_SOUL)) {
            if (otherStack.isEmpty()) {
                removeFirstStack(thisStack).ifPresent(itemStack -> {
                    this.playRemoveOneSound(player);
                    cursorStackReference.set(itemStack);
                });
            } else {
                int i = addToBundle(thisStack, otherStack);
                if (i > 0) {
                    this.playInsertSound(player);
                    otherStack.decrement(i);
                }
            }

            return true;
        } else {
            return false;
        }
    }
    private void playRemoveOneSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F);
    }

    private void playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_BUNDLE_INSERT, 0.8F, 0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F);
    }
    private static Optional<ItemStack> removeFirstStack(ItemStack stack) {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        if (!nbtCompound.contains("Items")) {
            return Optional.empty();
        } else {
            NbtList nbtList = nbtCompound.getList("Items", NbtElement.COMPOUND_TYPE);
            if (nbtList.isEmpty()) {
                return Optional.empty();
            } else {
                int i = 0;
                NbtCompound nbtCompound2 = nbtList.getCompound(0);
                ItemStack itemStack = ItemStack.fromNbt(nbtCompound2);
                nbtList.remove(0);
                if (nbtList.isEmpty()) {
                    stack.removeSubNbt("Items");
                }

                return Optional.of(itemStack);
            }
        }
    }
    private static int addToBundle(ItemStack bundle, ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem().canBeNested() && stack.isOf(ModItems.CAPTURED_SOUL)) {
            NbtCompound nbtCompound = bundle.getOrCreateNbt();
            if (!nbtCompound.contains("Items")) {
                nbtCompound.put("Items", new NbtList());
            }

            int i = getBundleOccupancy(bundle);
            int j = getItemOccupancy(stack);
            int k = Math.min(stack.getCount(), (MAX_STORAGE - i) / j);
            if (k == 0) {
                return 0;
            } else {
                NbtList nbtList = nbtCompound.getList("Items", NbtElement.COMPOUND_TYPE);
                Optional<NbtCompound> optional = canMergeStack(stack, nbtList);
                if (optional.isPresent()) {
                    NbtCompound nbtCompound2 = (NbtCompound)optional.get();
                    ItemStack itemStack = ItemStack.fromNbt(nbtCompound2);
                    itemStack.increment(k);
                    itemStack.writeNbt(nbtCompound2);
                    nbtList.remove(nbtCompound2);
                    nbtList.add(0, nbtCompound2);
                } else {
                    ItemStack itemStack2 = stack.copy();
                    itemStack2.setCount(k);
                    NbtCompound nbtCompound3 = new NbtCompound();
                    itemStack2.writeNbt(nbtCompound3);
                    nbtList.add(0, nbtCompound3);
                }

                return k;
            }
        } else {
            return 0;
        }
    }
    private static Optional<NbtCompound> canMergeStack(ItemStack stack, NbtList items) {
        return stack.isOf(Items.BUNDLE)
                ? Optional.empty()
                : items.stream()
                .filter(NbtCompound.class::isInstance)
                .map(NbtCompound.class::cast)
                .filter(nbt -> ItemStack.canCombine(ItemStack.fromNbt(nbt), stack))
                .findFirst();
    }

    private static int getItemOccupancy(ItemStack stack) {
        return (int)(64 / (float)stack.getMaxCount());
    }
    @Override
    public int getItemBarStep(ItemStack stack) {
        return Math.min(1 + 12 * getBundleOccupancy(stack) / MAX_STORAGE, 13);
    }
    private static int getBundleOccupancy(ItemStack stack) {
        return getBundledStacks(stack).mapToInt(itemStack -> getItemOccupancy(itemStack) * itemStack.getCount()).sum();
    }
    private static Stream<ItemStack> getBundledStacks(ItemStack stack) {
        NbtCompound nbtCompound = stack.getNbt();
        if (nbtCompound == null) {
            return Stream.empty();
        } else {
            NbtList nbtList = nbtCompound.getList("Items", NbtElement.COMPOUND_TYPE);
            return nbtList.stream().map(NbtCompound.class::cast).map(ItemStack::fromNbt);
        }
    }
    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.minecraft.bundle.fullness", getBundleOccupancy(stack), MAX_STORAGE).formatted(Formatting.GRAY));
    }
}

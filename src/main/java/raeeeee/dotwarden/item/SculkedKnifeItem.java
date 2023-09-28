package raeeeee.dotwarden.item;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import raeeeee.dotwarden.DOTWarden;
import raeeeee.dotwarden.registry.ModItems;

import java.util.function.Predicate;

public class SculkedKnifeItem extends Item {
    private static final Predicate<ItemStack> POWER_SOURCE = itemStack -> (itemStack.isOf(ModItems.POWER_OF_THE_DISCIPLE));
    public SculkedKnifeItem(Settings settings) {
        super(settings);
    }
    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof PlayerEntity player) {
            if (!player.getInventory().contains(new ItemStack(ModItems.POWER_OF_THE_DISCIPLE))) {
                stack.damage(1, attacker, (e) -> {
                    e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND);
                });
                target.damage(DamageSource.player(player),8f);
                return true;
            } else {
                NbtCompound powerPouch = player.getInventory().getStack(findPower(player)).getOrCreateSubNbt(DOTWarden.ID);
                if (powerPouch.getInt("powerlevel") > 0) {
                    powerPouch.putInt("powerlevel", powerPouch.getInt("powerlevel") - 1);
                    target.damage(DamageSource.player(player),10f);
                    return true;
                } else {
                    stack.damage(1, attacker, (e) -> {
                        e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND);
                    });
                    target.damage(DamageSource.player(player),8f);
                    return true;
                }
            }
        }
        return false;
    }
    private int findPower(PlayerEntity player) {
        for (int i = 0; i < 36; i++) {
            if (player.getInventory().getStack(i).isOf(ModItems.POWER_OF_THE_DISCIPLE)) {
                return i;
            }
        }
        return -1;
    }
}

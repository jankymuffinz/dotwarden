package raeeeee.dotwarden.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import raeeeee.dotwarden.DOTWarden;
import raeeeee.dotwarden.registry.ModItems;

import java.util.List;
import java.util.function.Predicate;

import static raeeeee.dotwarden.item.PowerItem.ptsUntilNextLevel;

public class SculkedKnifeItem extends Item {
    private static final Predicate<ItemStack> POWER_SOURCE = itemStack -> (itemStack.isOf(ModItems.POWER_OF_THE_DISCIPLE));
    public SculkedKnifeItem(Settings settings) {
        super(settings);
    }
    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof PlayerEntity player) {
            NbtCompound powerPouch = player.getInventory().getStack(findPower(player)).getOrCreateSubNbt(DOTWarden.ID);
            if (player.getInventory().contains(new ItemStack(ModItems.POWER_OF_THE_DISCIPLE)) &&
                    powerPouch.getInt("powerlevel") > 0) {
                powerPouch.putInt("powerlevel", powerPouch.getInt("powerlevel") - 1);
                while (powerPouch.getInt("power") >= ptsUntilNextLevel(player.getInventory().getStack(findPower(player)))) {
                    powerPouch.putInt("power",
                            powerPouch.getInt("power") -
                                    ptsUntilNextLevel(player.getInventory().getStack(findPower(player))));
                    powerPouch.putInt("powerlevel",
                            powerPouch.getInt("powerlevel") + 1);
                }
                target.damage(DamageSource.player(player),10f);
            } else {
                stack.damage(1, attacker, (e) -> {
                    e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND);
                });
                target.damage(DamageSource.player(player),1f);
            }
            return true;
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
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.dotwarden.sculked_knife.tooltip").setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
        super.appendTooltip(stack, world, tooltip, context);
    }
}

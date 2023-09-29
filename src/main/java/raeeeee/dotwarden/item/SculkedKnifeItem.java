package raeeeee.dotwarden.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import raeeeee.dotwarden.extensions.PlayerExtensions;
import raeeeee.dotwarden.registry.ModItems;

import java.util.List;
import java.util.function.Predicate;

public class SculkedKnifeItem extends Item {
    private static final Predicate<ItemStack> POWER_SOURCE = itemStack -> (itemStack.isOf(ModItems.POWER_OF_THE_DISCIPLE));
    public SculkedKnifeItem(Settings settings) {
        super(settings);
    }
    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof PlayerEntity player) {
            if (((PlayerExtensions) player).dotwarden$getPowerLevel() > 0) {
                target.damage(DamageSource.player(player),(float)Math.sqrt(((PlayerExtensions) player).dotwarden$getPowerLevel()) + 3);
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
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.dotwarden.sculked_knife.tooltip").setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
        super.appendTooltip(stack, world, tooltip, context);
    }
}

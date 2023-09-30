package io.github.Tors_0.dotwarden.item;

import io.github.Tors_0.dotwarden.DOTWarden;
import io.github.Tors_0.dotwarden.extensions.PlayerExtensions;
import io.github.Tors_0.dotwarden.networking.DOTWNetworking;
import io.github.Tors_0.dotwarden.registry.ModItems;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

import java.util.List;
import java.util.function.Predicate;

public class SculkedKnifeItem extends Item {
    private static final Predicate<ItemStack> SACRIFICE = (itemStack) -> (itemStack.isOf(ModItems.POWER_OF_THE_DISCIPLE) || itemStack.isOf(ModItems.CORRUPTED_HEART));
    public SculkedKnifeItem(Settings settings) {
        super(settings);
    }
    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof PlayerEntity player) {
            if (((PlayerExtensions) player).dotwarden$getPowerLevel() > 0) {
                target.damage(DamageSource.player(player),((PlayerExtensions) player).dotwarden$getPowerLevel() / 5f);
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
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient()) {
            if (!((PlayerExtensions)user).dotwarden$hasSacrificed()) {
                ItemStack itm = new ItemStack(ModItems.CORRUPTED_HEART);
                itm.getOrCreateSubNbt(DOTWarden.ID).putString("owner",user.getName().getString());
                user.getInventory().insertStack(itm);
                user.damage(DamageSource.player(user),30f);
                ((PlayerExtensions)user).dotwarden$setSacrifice(true);
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBoolean(((PlayerExtensions)user).dotwarden$hasSacrificed());
                ServerPlayNetworking.send((ServerPlayerEntity) user, DOTWNetworking.SYNC_SACRIFICE_ID, buf);
            }
        }
        return super.use(world, user, hand);
    }
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
    }
}

package io.github.Tors_0.dotwarden.item;

import io.github.Tors_0.dotwarden.DOTWarden;
import io.github.Tors_0.dotwarden.extensions.PlayerExtensions;
import io.github.Tors_0.dotwarden.networking.DOTWNetworking;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

import java.util.List;
import java.util.function.Predicate;

import static net.minecraft.text.Style.EMPTY;

public class PowerItem extends Item {
	private static final Predicate<ItemStack> SCULK_ITEM = itemStack -> (itemStack.isOf(Items.SCULK));

	public PowerItem(Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if (!world.isClient()) {
            if (user.getInventory().contains(new ItemStack(Items.SCULK))) {
                if (user.getStackInHand(hand).getOrCreateSubNbt(DOTWarden.ID).getInt("power") == 0) {
                    user.getStackInHand(hand).getOrCreateSubNbt(DOTWarden.ID).putInt("power",
                            user.getStackInHand(hand).getOrCreateSubNbt(DOTWarden.ID).getInt("power") +
                                    user.getInventory().remove(SCULK_ITEM, ptsUntilNextLevel(user.getStackInHand(hand)), user.getInventory()));
                } else {
                    user.getStackInHand(hand).getOrCreateSubNbt(DOTWarden.ID).putInt("power",
                            user.getStackInHand(hand).getOrCreateSubNbt(DOTWarden.ID).getInt("power") +
                                    user.getInventory().remove(SCULK_ITEM, ptsUntilNextLevel(user.getStackInHand(hand)) -
                                                    user.getStackInHand(hand).getOrCreateSubNbt(DOTWarden.ID).getInt("power"),
                                            user.getInventory()));
                }
                while (user.getStackInHand(hand).getOrCreateSubNbt(DOTWarden.ID).getInt("power") >= ptsUntilNextLevel(user.getStackInHand(hand))) {
                    user.getStackInHand(hand).getOrCreateSubNbt(DOTWarden.ID).putInt("power",
                            user.getStackInHand(hand).getOrCreateSubNbt(DOTWarden.ID).getInt("power") -
                                    ptsUntilNextLevel(user.getStackInHand(hand)));
                    ((PlayerExtensions) user).dotwarden$setPowerLevel(((PlayerExtensions) user).dotwarden$getPowerLevel()+1);
                }
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeInt(((PlayerExtensions)user).dotwarden$getPowerLevel());
                ServerPlayNetworking.send((ServerPlayerEntity)user, DOTWNetworking.POWERLEVEL_PACKET_ID, buf);
                return TypedActionResult.success(user.getStackInHand(hand),true);
            } else {
                while (user.getStackInHand(hand).getOrCreateSubNbt(DOTWarden.ID).getInt("power") >= ptsUntilNextLevel(user.getStackInHand(hand))) {
                    user.getStackInHand(hand).getOrCreateSubNbt(DOTWarden.ID).putInt("power",
                            user.getStackInHand(hand).getOrCreateSubNbt(DOTWarden.ID).getInt("power") -
                                    ptsUntilNextLevel(user.getStackInHand(hand)));
                    ((PlayerExtensions) user).dotwarden$setPowerLevel(((PlayerExtensions) user).dotwarden$getPowerLevel()+1);
                }
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeInt(((PlayerExtensions)user).dotwarden$getPowerLevel());
                ServerPlayNetworking.send((ServerPlayerEntity)user, DOTWNetworking.POWERLEVEL_PACKET_ID, buf);
                return TypedActionResult.pass(user.getStackInHand(hand));
            }
		}
		return super.use(world, user, hand);
	}
	public static int ptsUntilNextLevel(ItemStack stack) {
		int level = stack.getOrCreateSubNbt(DOTWarden.ID).getInt("powerlevels");
		if (level >= 30) {
			return (112 + (level - 30) * 9);
		} else {
			return level >= 15 ? (37 + (level - 15) * 5) :
                    (7 + level * 2);
		}
	}
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (entity instanceof PlayerExtensions player) {
            if (!world.isClient()) {
                stack.getOrCreateSubNbt(DOTWarden.ID).putInt("powerlevels", player.dotwarden$getPowerLevel());
            }
        }
    }
    @Override
    public ItemStack getDefaultStack() {
        ItemStack itm = new ItemStack(this);
        itm.getOrCreateSubNbt(DOTWarden.ID).putString("owner","Nobody");
        return itm;
    }
    @Override
    public boolean hasGlint(ItemStack stack) {
        return !stack.getOrCreateSubNbt(DOTWarden.ID).getString("owner").isEmpty();
    }
	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		Style style = EMPTY.withColor(Formatting.DARK_AQUA);
		Text tip1 = Text.translatable("item.dotwarden.power_of_the_disciple.tooltip");
		Text tip2 = Text.translatable("item.dotwarden.power_of_the_disciple.tooltip1");
        tooltip.add(Text.literal(!stack.getOrCreateSubNbt(DOTWarden.ID).getString("owner").isEmpty() ?
                stack.getOrCreateSubNbt(DOTWarden.ID).getString("owner"): "Nobody")
                .append(Text.translatable("item.dotwarden.power_of_the_disciple.tooltip2")));
		tooltip.add(Text.literal("").append(tip1).append(": " + stack.getOrCreateSubNbt(DOTWarden.ID).getInt("powerlevels")).setStyle(style));
		tooltip.add(Text.literal("").append(tip2).append(": " + (int)(stack.getOrCreateSubNbt(DOTWarden.ID).getInt("power") /
                (double)ptsUntilNextLevel(stack) * 100) + "%").setStyle(EMPTY.withColor(Formatting.GRAY)));
		super.appendTooltip(stack, world, tooltip, context);
	}
}

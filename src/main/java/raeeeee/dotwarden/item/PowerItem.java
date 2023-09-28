package raeeeee.dotwarden.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import raeeeee.dotwarden.DOTWarden;

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
                                    user.getInventory().remove(SCULK_ITEM, ptsUntilNextLevel(user, hand), user.getInventory()));
                } else {
                    user.getStackInHand(hand).getOrCreateSubNbt(DOTWarden.ID).putInt("power",
                            user.getStackInHand(hand).getOrCreateSubNbt(DOTWarden.ID).getInt("power") +
                                    user.getInventory().remove(SCULK_ITEM, ptsUntilNextLevel(user, hand) -
                                                    user.getStackInHand(hand).getOrCreateSubNbt(DOTWarden.ID).getInt("power"),
                                            user.getInventory()));
                }
                while (user.getStackInHand(hand).getOrCreateSubNbt(DOTWarden.ID).getInt("power") >= ptsUntilNextLevel(user,hand)) {
                    user.getStackInHand(hand).getOrCreateSubNbt(DOTWarden.ID).putInt("power",
                            user.getStackInHand(hand).getOrCreateSubNbt(DOTWarden.ID).getInt("power") -
                                    ptsUntilNextLevel(user,hand));
                    user.getStackInHand(hand).getOrCreateSubNbt(DOTWarden.ID).putInt("powerlevel",
                            user.getStackInHand(hand).getOrCreateSubNbt(DOTWarden.ID).getInt("powerlevel") + 1);
                }
                return TypedActionResult.success(user.getStackInHand(hand),true);
            } else {
                return TypedActionResult.pass(user.getStackInHand(hand));
            }
		}
		return super.use(world, user, hand);
	}
	private int ptsUntilNextLevel(PlayerEntity user, Hand hand) {
		int level = user.getStackInHand(hand).getOrCreateSubNbt(DOTWarden.ID).getInt("powerlevel");
		if (level >= 30) {
			return (112 + (level - 30) * 9);
		} else {
			return level >= 15 ? 37 + (level - 15) * 5 : (7 + level * 2);
		}
	}
	private int ptsUntilNextLevel(ItemStack stack) {
		int level = stack.getOrCreateSubNbt(DOTWarden.ID).getInt("powerlevel");
		if (level >= 30) {
			return (112 + (level - 30) * 9) - stack.getOrCreateSubNbt(DOTWarden.ID).getInt("power");
		} else {
			return level >= 15 ? (37 + (level - 15) * 5) - stack.getOrCreateSubNbt(DOTWarden.ID).getInt("powerlevel") :
                    (7 + level * 2) - stack.getOrCreateSubNbt(DOTWarden.ID).getInt("powerlevel");
		}
	}
	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		Style style = EMPTY.withColor(Formatting.DARK_AQUA);
		Text tip1 = Text.translatable("item.dotwarden.power_of_the_disciple.tooltip");
		Text tip2 = Text.translatable("item.dotwarden.power_of_the_disciple.tooltip1");
		tooltip.add(Text.literal("").append(tip1).append(": " + stack.getOrCreateSubNbt(DOTWarden.ID)
			.getInt("powerlevel")).setStyle(style));
		tooltip.add(Text.literal("").append(tip2).append(": " + (int)(stack.getOrCreateSubNbt(DOTWarden.ID)
			.getInt("power") / (double)ptsUntilNextLevel(stack) * 100) + "%").setStyle(EMPTY.withColor(Formatting.DARK_GRAY)));
		super.appendTooltip(stack, world, tooltip, context);
	}
}

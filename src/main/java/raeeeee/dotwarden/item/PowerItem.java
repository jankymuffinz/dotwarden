package raeeeee.dotwarden.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtList;
import net.minecraft.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import raeeeee.dotwarden.DOTWarden;

import java.util.List;
import java.util.function.Predicate;

public class PowerItem extends Item {
	private static final Predicate<ItemStack> VALID_ITEM = new Predicate<ItemStack>() {
		@Override
		public boolean test(ItemStack itemStack) {
			return (itemStack.isOf(Items.SCULK));
		}
	};
	private final int size;
	private int contents = 0;
	private boolean full = false;

	public PowerItem(Settings settings, int size) {
		super(settings);
		this.size = size;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if (!world.isClient()) {
			if (user.getInventory().contains(new ItemStack(Items.SCULK))) {
				if (this.contents < this.size) {
					int sculkCount = user.getInventory().count(Items.SCULK);
					if (sculkCount > (this.size-this.contents)) {
						user.getInventory().remove(VALID_ITEM, sculkCount, user.getInventory());
						this.contents += sculkCount;
						return TypedActionResult.success(user.getStackInHand(hand));
					}
				}
			} else {
				return TypedActionResult.fail(user.getStackInHand(hand));
			}
		}
		return super.use(world, user, hand);
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		tooltip.add(Text.translatable("item.dotwarden.power_of_the_disciple.tooltip"));
		tooltip.add(Text.literal("" + this.contents + "/" + this.size));
		super.appendTooltip(stack, world, tooltip, context);
	}
}

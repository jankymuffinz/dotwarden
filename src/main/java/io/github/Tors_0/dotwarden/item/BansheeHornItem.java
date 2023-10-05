package io.github.Tors_0.dotwarden.item;

import io.github.Tors_0.dotwarden.extensions.PlayerExtensions;
import io.github.Tors_0.dotwarden.networking.DOTWNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.warden.WardenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

import java.util.List;
import java.util.function.Predicate;

public class BansheeHornItem extends Item {
    private static final Predicate<Entity> CAN_BE_HIT_ENTITY = entity -> (entity instanceof LivingEntity && !(entity instanceof WardenEntity));
    private static final int MAX_RANGE = 5;
    private static final int POWER_LEVEL_COST = 10;
    private static final float BOOM_DAMAGE = 4.0F;
    public BansheeHornItem(Settings settings) {
        super(settings);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.TOOT_HORN;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient && (user.isCreative() || ((PlayerExtensions)user).dotwarden$getPowerLevel() >= 10)) {
            Vec3d rotationVec = user.getRotationVec(1.0f);
            Box box = user.getBoundingBox().stretch(rotationVec.multiply(MAX_RANGE)).expand(1.0d);
            List<Entity> hitEntities = world.getOtherEntities(user,box,CAN_BE_HIT_ENTITY);
            double maxRangeSq = MAX_RANGE * MAX_RANGE;
            hitEntities.stream().filter(entity -> entity.squaredDistanceTo(user) < maxRangeSq);
            Vec3d startPoint = user.getPos();
            if (!user.isCreative()) {
                user.getItemCooldownManager().set(this, 200);
                ((PlayerExtensions)user).dotwarden$setPowerLevel(((PlayerExtensions)user).dotwarden$getPowerLevel() - POWER_LEVEL_COST);
            }
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(((PlayerExtensions)user).dotwarden$getPowerLevel());
            ServerPlayNetworking.send((ServerPlayerEntity)user, DOTWNetworking.POWERLEVEL_PACKET_ID, buf);
            hitEntities.forEach(entity -> {
                LivingEntity livingEntity = (LivingEntity) entity;
                Vec3d vec3d2 = livingEntity.getEyePos().subtract(startPoint);
                Vec3d vec3d3 = vec3d2.normalize();
                livingEntity.damage(new EntityDamageSource("sonic_boom", user).setUsesMagic(), BOOM_DAMAGE);
                double d = 0.5 * (1.0 - livingEntity.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE));
                double e = 2.5 * (1.0 - livingEntity.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE));
                livingEntity.addVelocity(vec3d3.getX() * e, vec3d3.getY() * d + 0.5, vec3d3.getZ() * e);
                livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 20, 1));
            });
            user.playSound(SoundEvents.BLOCK_SCULK_SHRIEKER_SHRIEK, SoundCategory.PLAYERS, 1f, 1f);
            Vec3d vec3d = user.getPos();
            ((ServerWorld)world).spawnParticles(ParticleTypes.SONIC_BOOM,vec3d.x,vec3d.y+1.6f,vec3d.z,1,0,0,0,0);
            return TypedActionResult.success(user.getStackInHand(hand),true);
        }
        return super.use(world, user, hand);
    }
}

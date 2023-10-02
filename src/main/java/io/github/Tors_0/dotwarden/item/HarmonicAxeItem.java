package io.github.Tors_0.dotwarden.item;

import io.github.Tors_0.dotwarden.extensions.PlayerExtensions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.function.Predicate;

public class HarmonicAxeItem extends Item {
    private static final Predicate<Entity> VALID_ENTITY = (entity -> (entity instanceof LivingEntity));
    public HarmonicAxeItem(Settings settings) {
        super(settings);
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (!world.isClient()) {
            HitResult blockResult = player.raycast(16d,1f,false);
            Vec3d startPoint = player.getPos().add(0.0, 1.6F, 0.0);
            double distanceToBlockSq = blockResult != null ? blockResult.getPos().squaredDistanceTo(startPoint) : Double.POSITIVE_INFINITY;
            Vec3d rotationVec = player.getRotationVec(1.0F);
            double range = 15;
            double effectiveRangeSq = Math.min(range * range, distanceToBlockSq);
            Vec3d endPoint = startPoint.add(rotationVec.x * range, rotationVec.y * range, rotationVec.z * range);
            Box box = player.getBoundingBox().stretch(rotationVec.multiply(range)).expand(1.0D,1.0D,1.0D);
            EntityHitResult entityHitResult = ProjectileUtil.raycast(
                    player,
                    startPoint,
                    endPoint,
                    box,
                    VALID_ENTITY,
                    effectiveRangeSq
            );
            if (entityHitResult != null
                    && entityHitResult.getEntity() instanceof LivingEntity livingEntity
                    && ((PlayerExtensions)player).dotwarden$getPowerLevel() > 2
            ) {
                player.getItemCooldownManager().set(this, 100);
                ((PlayerExtensions)player).dotwarden$setPowerLevel(((PlayerExtensions)player).dotwarden$getPowerLevel() - 2);
                Vec3d vec3d2 = livingEntity.getEyePos().subtract(startPoint);
                Vec3d vec3d3 = vec3d2.normalize();
                for (int i = 1; i < MathHelper.floor(vec3d2.length()) + 7; ++i) {
                    Vec3d vec3d4 = startPoint.add(vec3d3.multiply((double) i));
                    ((ServerWorld) world).spawnParticles(ParticleTypes.SONIC_BOOM, vec3d4.x, vec3d4.y, vec3d4.z, 1, 0.0, 0.0, 0.0, 0.0);
                }
                player.playSound(SoundEvents.ENTITY_WARDEN_SONIC_BOOM, SoundCategory.PLAYERS, 3.0F, 1.0F);
                livingEntity.damage(DamageSource.method_43964(player), 6.0F);
                double d = 0.5 * (1.0 - livingEntity.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE));
                double e = 2.5 * (1.0 - livingEntity.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE));
                livingEntity.addVelocity(vec3d3.getX() * e, vec3d3.getY() * d, vec3d3.getZ() * e);
                return TypedActionResult.success(player.getStackInHand(hand));
            }
        }
        return TypedActionResult.fail(player.getStackInHand(hand));
    }
}

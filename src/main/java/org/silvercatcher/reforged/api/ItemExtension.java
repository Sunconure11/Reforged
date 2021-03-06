package org.silvercatcher.reforged.api;

import java.util.Map.Entry;
import java.util.UUID;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.enchantment.*;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

/**
 * Attempt to use Java 8 features against lack of foresight.
 *
 */
public interface ItemExtension {

	public static final UUID ATTACK_DAMAGE_MODIFIER_RF = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
	public static final UUID ATTACK_SPEED_MODIFIER_RF = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");

	public static final int USE_DURATON = 72000;

	default double getAttackSpeed(ItemStack stack) {
		return -2.4000000953674316D;
	}

	default Multimap getAttributeModifiers(ItemStack stack) {

		Multimap modifiers = HashMultimap.create();

		modifiers.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
				new AttributeModifier(ATTACK_DAMAGE_MODIFIER_RF, "Weapon Damage", getHitDamage(stack), 0));
		modifiers.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),
				new AttributeModifier(ATTACK_SPEED_MODIFIER_RF, "Weapon modifier", getAttackSpeed(stack), 0));
		return modifiers;
	}

	default DamageSource getDamage(EntityLivingBase p) {
		if (p instanceof EntityPlayer)
			return DamageSource.causePlayerDamage((EntityPlayer) p);
		return DamageSource.causeMobDamage(p);
	}

	default float getEnchantmentBonus(ItemStack stack, EntityPlayer player, Entity entity) {

		float extraDamage = 0f;

		if (entity instanceof EntityLivingBase) {

			EntityLivingBase living = (EntityLivingBase) entity;

			for (Entry<Enchantment, Integer> o : EnchantmentHelper.getEnchantments(stack).entrySet()) {

				Enchantment e = o.getKey();

				if (e instanceof EnchantmentDamage) {

					EnchantmentDamage ed = (EnchantmentDamage) e;

					if (ed.damageType != 0) {

						extraDamage += e.calcDamageByCreature(EnchantmentHelper.getEnchantmentLevel(e, stack),
								living.getCreatureAttribute());
					}
				}
			}
		}
		return extraDamage;
	}

	float getHitDamage();

	default float getHitDamage(ItemStack stack) {

		float enchantDamage = 0f;

		for (Entry<Enchantment, Integer> o : EnchantmentHelper.getEnchantments(stack).entrySet()) {

			Enchantment e = o.getKey();

			if (e instanceof EnchantmentDamage) {

				EnchantmentDamage ed = (EnchantmentDamage) e;

				if (ed.damageType == 0) {

					enchantDamage += ed.calcDamageByCreature(o.getValue(), null);
				}
			}
		}
		return getHitDamage() + enchantDamage;
	}

	default void registerRecipes() {
	}
}

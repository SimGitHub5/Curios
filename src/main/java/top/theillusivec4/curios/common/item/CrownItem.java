package top.theillusivec4.curios.common.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import top.theillusivec4.curios.Curios;
import top.theillusivec4.curios.api.CuriosAPI;
import top.theillusivec4.curios.api.capability.ICurio;
import top.theillusivec4.curios.client.render.model.CrownModel;
import top.theillusivec4.curios.common.capability.CapCurioItem;

public class CrownItem extends Item {

  private static final ResourceLocation CROWN_TEXTURE = new ResourceLocation(Curios.MODID,
      "textures/entity/crown.png");

  public CrownItem() {
    super(new Item.Properties().group(ItemGroup.TOOLS).maxStackSize(1).defaultMaxDamage(2000));
    this.setRegistryName(Curios.MODID, "crown");
  }

  @Override
  public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT unused) {
    return CapCurioItem.createProvider(new ICurio() {

      private Object model;

      @Override
      public void onCurioTick(String identifier, int index, LivingEntity livingEntity) {

        if (!livingEntity.getEntityWorld().isRemote && livingEntity.ticksExisted % 20 == 0) {
          livingEntity
              .addPotionEffect(new EffectInstance(Effects.NIGHT_VISION, 300, 44, true, true));
          stack.damageItem(1, livingEntity,
              damager -> CuriosAPI.onBrokenCurio(identifier, index, damager));
        }
      }

      @Override
      public void onUnequipped(String identifier, LivingEntity livingEntity) {
        EffectInstance effect = livingEntity.getActivePotionEffect(Effects.NIGHT_VISION);

        if (effect != null && effect.getAmplifier() == 44) {
          livingEntity.removePotionEffect(Effects.NIGHT_VISION);
        }
      }

      @Override
      public boolean hasRender(String identifier, LivingEntity livingEntity) {
        return true;
      }

      @Override
      public void render(String identifier, MatrixStack matrixStack,
          IRenderTypeBuffer renderTypeBuffer, int light, LivingEntity livingEntity, float limbSwing,
          float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw,
          float headPitch) {

        if (!(this.model instanceof CrownModel)) {
          model = new CrownModel<>();
        }
        CrownModel<?> crown = (CrownModel<?>) this.model;
        ICurio.RenderHelper.followHeadRotations(livingEntity, crown.crown);
        IVertexBuilder vertexBuilder = ItemRenderer
            .func_229113_a_(renderTypeBuffer, crown.func_228282_a_(CROWN_TEXTURE), false,
                stack.hasEffect());
        crown
            .func_225598_a_(matrixStack, vertexBuilder, light, OverlayTexture.field_229196_a_, 1.0F,
                1.0F, 1.0F, 1.0F);
      }
    });
  }

  @Override
  public boolean hasEffect(ItemStack stack) {
    return true;
  }
}

package me.lily.bllry.mixins.accessors;

import net.minecraft.client.model.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(ModelPart.class)
public interface ModelPartAccessor {
    @Accessor("cuboids")
    List<ModelPart.Cuboid> getCuboids();

    @Accessor("children")
    Map<String, ModelPart> getChildren();
}

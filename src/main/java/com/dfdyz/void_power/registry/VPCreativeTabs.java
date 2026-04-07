package com.dfdyz.void_power.registry;

import com.dfdyz.void_power.VoidPowerMod;
import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.foundation.utility.Components;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class VPCreativeTabs {
    private static final ResourceKey<Registry<CreativeModeTab>> CREATIVE_TAB_REGISTRY =
            ResourceKey.createRegistryKey(new ResourceLocation("minecraft", "creative_mode_tab"));

    private static final DeferredRegister<CreativeModeTab> REGISTER =
            DeferredRegister.create(CREATIVE_TAB_REGISTRY, VoidPowerMod.MODID);

    public static final RegistryObject<CreativeModeTab> TAB = REGISTER.register("tab",
            () -> CreativeModeTab.builder()
                    .title(Components.translatable("itemGroup."+ VoidPowerMod.MODID +".main"))
                    .withTabsBefore(AllCreativeModeTabs.BASE_CREATIVE_TAB.getKey())
                    .icon(() -> {
                        return new ItemStack(Items.STICK);
                    })
                    .displayItems((params, output) -> {
                        List<ItemStack> items = VoidPowerMod.REGISTRATE.getAll(Registries.ITEM)
                                .stream()
                                .map((regItem) -> new ItemStack(regItem.get()))
                                .toList();
                        output.acceptAll(items);
                    })
                    .build());

    public static void register(IEventBus modEventBus) {
        REGISTER.register(modEventBus);
    }
}

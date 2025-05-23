package me.lily.bllry.modules.impl.combat;

import me.lily.bllry.Bllry;
import me.lily.bllry.events.SubscribeEvent;
import me.lily.bllry.events.impl.KeyInputEvent;
import me.lily.bllry.events.impl.PlayerUpdateEvent;
import me.lily.bllry.modules.Module;
import me.lily.bllry.modules.RegisterModule;
import me.lily.bllry.settings.impl.BooleanSetting;
import me.lily.bllry.settings.impl.ModeSetting;
import me.lily.bllry.settings.impl.NumberSetting;
import me.lily.bllry.utils.minecraft.DamageUtils;
import me.lily.bllry.utils.minecraft.EntityUtils;
import me.lily.bllry.utils.minecraft.InventoryUtils;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ExperienceBottleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

@RegisterModule(name = "AutoArmor", description = "Automatically equips the best armor.", category = Module.Category.COMBAT)
public class AutoArmorModule extends Module {
    public ModeSetting health = new ModeSetting("Health", "The health priority to apply.", "Highest", new String[]{"Highest", "Lowest", "Any"});
    public BooleanSetting elytraPriority = new BooleanSetting("ElytraPriority", "Prioritizes elytra over armor pieces.", true);
    public BooleanSetting preserve = new BooleanSetting("Preserve", "Preserve low health armor to avoid it from breaking.", false);
    public NumberSetting preserveHealth = new NumberSetting("PreserveHealth", "The minimum health of armor to preserve it.", 20.0f, 10.0f, 50.0f);
    public BooleanSetting elytra = new BooleanSetting("Elytra", "Equips an elytra instead of a chestplate.", false);
    public BooleanSetting smartElytra = new BooleanSetting("SmartElytra", "Chooses when to enable elytra in a more convenient way.", false);
    
    private int ticks = 0;

    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event) {
        if(getNull() || !smartElytra.getValue()) return;

        if(event.getKey() == 32 && !elytra.getValue() && !mc.player.isOnGround() && !EntityUtils.isInWeb(mc.player)) {
            elytra.setValue(true);
        }
    }

    @SubscribeEvent
    public void onPlayerUpdate(PlayerUpdateEvent event) {
        if (mc.player == null || mc.world == null) return;

        if(smartElytra.getValue() && elytra.getValue()) {
            if((mc.player.isOnGround() && !(mc.player.getMainHandStack().getItem() instanceof ExperienceBottleItem) || EntityUtils.isInWeb(mc.player) || Bllry.MODULE_MANAGER.getModule(MaceAuraModule.class).isToggled())) elytra.setValue(false);
        }

        if (ticks <= 0) {
            if (InventoryUtils.inInventoryScreen()) return;

            update(EquipmentSlot.HEAD, 5);
            if (!(elytraPriority.getValue() && mc.player.getInventory().getStack(38).getItem() == Items.ELYTRA) || elytra.getValue()) update(EquipmentSlot.CHEST, 6);
            update(EquipmentSlot.LEGS, 7);
            update(EquipmentSlot.FEET, 8);
        }

        ticks--;
    }

    private void update(EquipmentSlot type, int x) {
        int elytraSlot = findElytra();
        boolean flag = elytra.getValue() && type == EquipmentSlot.CHEST;

        int slot = type == EquipmentSlot.HEAD ? 39 : type == EquipmentSlot.CHEST ? 38 : type == EquipmentSlot.LEGS ? 37 : 36;
        int armor = flag ? elytraSlot : findArmor(type);
        int best = flag ? compareElytra(38, armor) : compare(slot, armor, true);

        if (armor != -1 && best != slot) {
            mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId, x, 0, SlotActionType.PICKUP, mc.player);
            mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId, InventoryUtils.indexToSlot(armor), 0, SlotActionType.PICKUP, mc.player);
            mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId, x, 0, SlotActionType.PICKUP, mc.player);

            ticks = 2 + Bllry.SERVER_MANAGER.getPingDelay();
        }
    }

    private int compare(int x, int y, boolean swap) {
        if (y == -1) return x;
        if (!(mc.player.getInventory().getStack(x).getItem() instanceof ArmorItem)) return y;

        if (DamageUtils.getProtectionAmount(mc.player.getInventory().getStack(x)) < DamageUtils.getProtectionAmount(mc.player.getInventory().getStack(y)))
            return y;

        if (preserve.getValue() && getDurability(x) < preserveHealth.getValue().floatValue())
            return getDurability(x) < getDurability(y) ? y : x;

        if (!swap) {
            if (health.getValue().equals("Highest") && getDurability(x) < getDurability(y)) return y;
            else if (health.getValue().equals("Lowest") && getDurability(x) > getDurability(y)) return y;
        }

        return x;
    }

    private int compareElytra(int x, int y) {
        if(y == -1) return x;
        if (mc.player.getInventory().getStack(x).getItem() != Items.ELYTRA) return y;

        if (health.getValue().equals("Highest") && getDurability(x) < getDurability(y)) return y;
        else if (health.getValue().equals("Lowest") && getDurability(x) > getDurability(y)) return y;

        return x;
    }

    private int findArmor(EquipmentSlot type) {
        int slot = -1;
        for (int i = InventoryUtils.HOTBAR_START; i <= InventoryUtils.INVENTORY_END; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!(stack.getItem() instanceof ArmorItem)) continue;

            if (getSlotType(stack).equals(type)) {
                slot = compare(i, slot, false);
            }
        }

        return slot;
    }

    private int findElytra() {
        int slot = -1;
        for (int i = InventoryUtils.HOTBAR_START; i <= InventoryUtils.INVENTORY_END; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);

            if(stack.getItem() != Items.ELYTRA) continue;;

            slot = compareElytra(i, slot);
        }

        return slot;
    }

    private float getDurability(int slot) {
        ItemStack stack = mc.player.getInventory().getStack(slot);
        return ((stack.getMaxDamage() - stack.getDamage()) * 100.0f) / stack.getMaxDamage();
    }

    private EquipmentSlot getSlotType(ItemStack itemStack) {
        if (itemStack.contains(DataComponentTypes.GLIDER)) return EquipmentSlot.CHEST;
        return itemStack.get(DataComponentTypes.EQUIPPABLE).slot();
    }
}

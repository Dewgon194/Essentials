package com.westosia.essentials.bukkit.virtualappliances;

import net.minecraft.server.v1_15_R1.*;
import org.bukkit.craftbukkit.v1_15_R1.block.CraftFurnaceFurnace;
import org.bukkit.inventory.InventoryHolder;

public class VirtualFurnace extends TileEntityFurnace {

    private EntityPlayer owner;

    public VirtualFurnace(EntityPlayer owner) {
        super(TileEntityTypes.FURNACE, Recipes.SMELTING);
        this.owner = owner;
    }

    @Override
    protected IChatBaseComponent getContainerName() {
        return new ChatMessage("Vurnace");
    }

    @Override
    protected Container createContainer(int i, PlayerInventory playerInventory) {
        ContainerFurnace container = new ContainerFurnaceFurnace(0, owner.inventory);
        container.checkReachable = false;
        container.setTitle(getContainerName());
        // Something NMS needs to keep track of invs
        int id = owner.nextContainerCounter();
        return container;
    }

    @Override
    public boolean a(EntityHuman entityhuman) {
        return true;
    }

    @Override
    public void update() {

    }

    @Override
    public InventoryHolder getOwner() {
        CraftFurnaceFurnace furnace = new CraftFurnaceFurnace(owner.getWorldServer().getWorld().getBlockAt(0, 0, 0));
        /*
        try {
            Field field = CraftFurnaceFurnace.class.getDeclaredField("block");
            field.setAccessible(true);
            Field modifierField = Field.class.getDeclaredField("modifiers");
            modifierField.setAccessible(true);
            modifierField.set(field, field.getModifiers() & ~Modifier.FINAL);

            field.set(furnace, this);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        return furnace;
    }
}

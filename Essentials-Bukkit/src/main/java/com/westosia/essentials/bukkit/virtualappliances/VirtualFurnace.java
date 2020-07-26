package com.westosia.essentials.bukkit.virtualappliances;

import net.minecraft.server.v1_15_R1.*;
import org.bukkit.block.Furnace;
import org.bukkit.craftbukkit.v1_15_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_15_R1.block.CraftFurnaceFurnace;
import org.bukkit.inventory.InventoryHolder;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class VirtualFurnace extends TileEntityFurnaceFurnace {

    private EntityPlayer owner;

    public VirtualFurnace(EntityPlayer owner) {
        this.owner = owner;
    }

    @Override
    public boolean a(EntityHuman entityhuman) {
        return true;
    }



    @Override
    public InventoryHolder getOwner() {
        ((CraftChunk) owner.world.getWorld().getBlockAt(0, 0, 0).getChunk()).getHandle().setTileEntity(new BlockPosition(0, 0, 0), this);
        Furnace furnace = new CraftFurnaceFurnace(owner.world.getWorld().getBlockAt(0, 0, 0));
        //((CraftChunk) owner.world.getWorld().getBlockAt(0, 0, 0).getChunk()).getHandle().setTileEntity(new BlockPosition(0, 0, 0), this);
        /*
         * Setting the tile we will use, this is the only good way!

        try {
            Field field = TileEntity.class.getDeclaredField("c");
            field.setAccessible(true);

            Field mfield = Field.class.getDeclaredField("modifiers");
            mfield.setAccessible(true);
            mfield.set(field, field.getModifiers() & ~Modifier.FINAL);

            field.set(furnace, this);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        return furnace;
    }
}

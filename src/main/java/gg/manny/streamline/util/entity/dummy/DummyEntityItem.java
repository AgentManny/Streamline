package gg.manny.streamline.util.entity.dummy;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

public class DummyEntityItem extends EntityItem {

    private final ItemStack item;

    public DummyEntityItem(World world, ItemStack item) {
        super(world);
        this.noclip = true;
        this.item = item;
    }

    @Override
    public void setPosition(double x, double y, double z) {
        super.setPosition(x, y, z);
    }

    public void remove() {
        this.dead = true;
        if (vehicle != null) {
            vehicle.dead = true;
        }
    }

    @Override
    public void inactiveTick() {

    }

    @Override
    public void t_() {

    }

    @Override
    public void K() {

    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {

    }

    @Override
    public void b(NBTTagCompound nbttagcompound) {

    }

    @Override
    public boolean c(NBTTagCompound nbttagcompound) {
        return false;
    }

    @Override
    public boolean d(NBTTagCompound nbttagcompound) {
        return false;
    }

    @Override
    public void e(NBTTagCompound nbttagcompound) {

    }

    @Override
    public void f(NBTTagCompound nbttagcompound) {

    }

    @Override
    public boolean isAlive() {
        return false;
    }

    @Override
    public boolean ad() {
        return false;
    }

    @Override
    public boolean ae() {
        return false;
    }

    @Override
    public boolean isInvulnerable(DamageSource source) {
        return true;
    }

    @Override
    public void die() {

    }

    @Override
    public void G() {

    }

    @Override
    public void a(int i) {
        super.a(Integer.MAX_VALUE);
    }

    @Override
    protected void burn(float i) {

    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        return false;
    }

    @Override
    public void d(EntityHuman entityhuman) {

    }

    @Nullable
    @Override
    public void c(int i) {

    }
}
package gg.manny.streamline.util.entity.dummy;

import net.minecraft.server.v1_8_R3.*;

// TODO For global holograms store Entity Reference
public class DummyEntityArmorStand extends EntityArmorStand {

    public DummyEntityArmorStand(World world) {
        super(world);
        setInvisible(true);
        setSmall(true);
        setArms(false);
        setGravity(true);
        setBasePlate(true);
        n(true);
    }

    @Override
    public void inactiveTick() {

    }

    // Tick
    @Override
    public void t_() {

    }

    // NBT Tags
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
    public boolean isInvulnerable(DamageSource source) {
        return true;
    }

    @Override
    public void makeSound(String sound, float f1, float f2) {

    }

    @Override
    public boolean a(EntityHuman human, Vec3D vec3d) {
        return true;
    }

    @Override
    public boolean d(int i, ItemStack item) {
        return false;
    }

    @Override
    public void setEquipment(int i, ItemStack item) {

    }

    @Override
    public void a(AxisAlignedBB boundingBox) {

    }

    @Override
    public void setInvisible(boolean flag) {
        super.setInvisible(true);
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        return false;
    }

    @Override
    public void die() {

    }
}

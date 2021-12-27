package gg.manny.streamline.util.entity.dummy;

import gg.manny.streamline.util.entity.metadata.EntityMetadata;
import net.minecraft.server.v1_8_R3.*;

public class DummyEntityHorse extends EntityHorse implements DummyEntity {

    private static final AxisAlignedBB NULL_BOUNDING_BOX = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

    private String text;
    private boolean visible = false;

    private boolean shouldUpdate = false;

    public DummyEntityHorse(World world) {
        super(world);

//        d(69); // Set entity id todo generate fake entity id

        ageLocked = true;
        persistent = true;

        setSize(0.0F, 0.0F); // Sets bounding box
        setInvisible(true);

//        datawatcher.a(INVISIBILITY.getId(), (byte) 0);
       // datawatcher.watch(1, (short) 300); // Not sure

//        setText(text);
//        setVisible(true);

        //datawatcher.watch(AGE.getId(), (byte) -1700000);
    }

    @Override
    public void inactiveTick() {

    }

    // Tick
    @Override
    public void t_() {

    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setText(String text) {
        if (text == null) {
            this.text = null;
            setVisible(false); // Send a CUSTOM_NAME_VISIBLE data change
        } else {
            this.text = text;
            this.visible = true;
            datawatcher.watch(EntityMetadata.CUSTOM_NAME.getId(), text);
        }
    }

    @Override
    public boolean setVisible(boolean visible) {
        if (this.visible != visible) {
            datawatcher.watch(EntityMetadata.CUSTOM_NAME_VISIBLE.getId(), (byte) (visible ? 1 : 0));
        }
        return visible;
    }

    @Override
    public PacketPlayOutEntityMetadata getUpdatePacket() {
        return null;
    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {

    }

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
    public boolean isInvulnerable(DamageSource damagesource) {
        return true;
    }

    @Override
    public void makeSound(String s, float f, float f1) {

    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        return false;
    }

    @Override
    public void mount(Entity entity) {

    }

    @Override
    public void die() {

    }

    @Override
    public void setLeashHolder(Entity entity, boolean flag) {

    }
}

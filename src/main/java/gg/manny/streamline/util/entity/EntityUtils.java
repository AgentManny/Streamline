package gg.manny.streamline.util.entity;

import gg.manny.streamline.util.ReflectionUtils;
import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityEgg;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class EntityUtils {

    // Taken from ProtocolLib
    private static Entity fakeEntity = null;
    private static Constructor<?> eggConstructor = null;

    public static Entity fakeEntity() {
        if (fakeEntity != null) {
            return fakeEntity;
        } else {
            if (eggConstructor == null) {
                try {
                    eggConstructor = ReflectionUtils.getConstructor(EntityEgg.class, World.class, Double.TYPE, Double.TYPE, Double.TYPE);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }

            World world = ((CraftWorld)Bukkit.getWorlds().get(0)).getHandle();
            try {
                return fakeEntity = (Entity) eggConstructor.newInstance(world, 0, 0, 0);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            return fakeEntity;
        }
    }

    public static DataWatcher getDataWatcher() {
        return new DataWatcher(fakeEntity());
    }
}

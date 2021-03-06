package gg.manny.streamline.util.entity.metadata;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.ItemStack;
import org.bukkit.entity.Entity;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public enum EntityMetadata implements IEntityMetadata<Entity> {

    INVISIBLE(0, Byte.class, 0x20),
    GLOWING_EFFECT(0, Byte.class, 0x40),
    FLYING_ELYTRA(0, Byte.class, 0x80),

    AIR_TICKS(1, Integer.class, 300), // Varint

    CUSTOM_NAME(2, String.class, null),
    CUSTOM_NAME_VISIBLE(3, Boolean.class, false),

    SILENT(4, Boolean.class, false),
    NO_GRAVITY(5, Boolean.class, false),

    ITEM(10, ItemStack.class, null),

    AGE(12, Integer.class, null)

    ;

    private final int id;

    private final Class<?> type;
    private final Object value;

    private Object defaultValue = null;
}
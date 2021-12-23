package gg.manny.streamline.entity.metadata;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public enum PlayerMetadata implements IEntityMetadata<Player> {

    FIRE(0, Byte.class, 0x01),
    CROUCH(0, Byte.class, 0x02),

    @Deprecated
    RIDING(0, Byte.class, 0x04),

    SPRINTING(0, Byte.class, 0x08),
    SWIMMING(0, Byte.class, 0x10)

    ;

    private final int id;

    private final Class<?> type;
    private final Object value;

    private Object defaultValue = null;
}
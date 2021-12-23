package gg.manny.streamline.entity.metadata;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.LivingEntity;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public enum LivingEntityMetadata implements IEntityMetadata<LivingEntity> {

    HEALTH(9, Float.class, 1.0, 1.0),
    POTION_EFFECT_COLOR(10, Integer.class, 0, 0),
    POTION_EFFECT_AMBIENT(11, Boolean.class, false, false),

    ARROWS(12, Integer.class, 0, 0),

    ;

    private final int id;

    private final Class<?> type;
    private final Object value;

    private Object defaultValue = null;
}
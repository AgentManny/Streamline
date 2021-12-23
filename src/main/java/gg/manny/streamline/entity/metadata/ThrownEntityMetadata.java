package gg.manny.streamline.entity.metadata;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public enum ThrownEntityMetadata implements IEntityMetadata<Entity> {

    ITEM(8, Item.class, null, null);

    ;

    private final int id;

    private final Class<?> type;
    private final Object value;

    private Object defaultValue = null;

    @AllArgsConstructor
    public enum ThrownEntity {

        EGG(Material.EGG),
        ENDER_PEARL(Material.ENDER_PEARL),
        EXPERIENCE_BOTTLE(Material.EXP_BOTTLE),
        POTION(Material.POTION),
        SNOWBALL(Material.SNOW_BALL),
        EYE_OF_ENDER(Material.EYE_OF_ENDER),
        SMALL_FIREBALL(Material.FIREBALL),
        FIREBALL(null),
        ITEM(null)
        ;

        private Material type;

    }
}
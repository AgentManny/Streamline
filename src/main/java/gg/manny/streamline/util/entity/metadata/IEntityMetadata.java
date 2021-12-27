package gg.manny.streamline.util.entity.metadata;

import org.bukkit.entity.Entity;

public interface IEntityMetadata<T extends Entity> {

    int getId();

    Class<?> getType();

    Object getValue();

}

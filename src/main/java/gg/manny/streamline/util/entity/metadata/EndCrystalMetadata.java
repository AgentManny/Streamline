package gg.manny.streamline.util.entity.metadata;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.BlockPosition;
import org.bukkit.entity.EnderCrystal;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public enum EndCrystalMetadata implements IEntityMetadata<EnderCrystal> {

    BEAM_TARGET(8, BlockPosition.class, null),
    SHOW_BOTTOM(9, Boolean.class, true),
    ;

    private final int id;

    private final Class<?> type;
    private final Object value;

    private Object defaultValue = null;
}
package gg.manny.streamline.util.entity.dummy;

import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;

public interface DummyEntity {

    String getText();

    void setText(String text);

    boolean setVisible(boolean visible);

    PacketPlayOutEntityMetadata getUpdatePacket();

}

package gg.manny.streamline;

import gg.manny.streamline.menu.MenuHandler;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class Streamline extends JavaPlugin {

    @Getter private static Streamline instance;

    @Override
    public void onEnable() {
        instance = this;

        MenuHandler.init(this);

    }

    @Override
    public void onDisable() {

    }
}
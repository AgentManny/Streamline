package gg.manny.streamline.util;

import com.google.common.base.Preconditions;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemBuilder {

    private ItemStack stack;
    private ItemMeta meta;

    /**
     * Creates a new instance with a given material
     * and a default quantity of 1.
     *
     * @param material the material to create from
     */
    public ItemBuilder(Material material) {
        this(material, 1);
    }

    /**
     * Creates a new instance with a given material and quantity.
     *
     * @param material the material to create from
     * @param amount   the quantity to build with
     */
    public ItemBuilder(Material material, int amount) {
        this(material, amount, (byte) 0);
    }

    /**
     * Creates a new instance with a given {@link ItemStack}.
     *
     * @param stack the stack to create from
     */
    public ItemBuilder(ItemStack stack) {
        Preconditions.checkNotNull(stack, "ItemStack cannot be null");
        this.stack = stack;
    }

    /**
     * Creates a new instance with a given material, quantity and data.
     *
     * @param material the material to create from
     * @param amount   the quantity to build with
     * @param data     the data to build with
     */
    public ItemBuilder(Material material, int amount, byte data) {
        Preconditions.checkNotNull(material, "Material cannot be null");
        Preconditions.checkArgument(amount > 0, "Amount must be positive");
        this.stack = new ItemStack(material, amount, data);
    }

    /**
     * Creates a meta instance if there is not one
     * assigned.
     */
    protected void validateMeta() {
        if (this.meta == null) {
            this.meta = stack.getItemMeta();
        }
    }

    /**
     * Sets the material of this item builder.
     *
     * @param material the material to set
     * @return this instance
     */
    public ItemBuilder material(Material material) {
        stack.setType(material);
        return this;
    }

    /**
     * Sets the amount of this item builder.
     *
     * @param amount the amount to set
     * @return this instance
     */
    public ItemBuilder amount(int amount) {
        stack.setAmount(amount);
        return this;
    }

    /**
     * Sets the data of this item builder.
     *
     * @param data the data value to set
     * @return the updated item builder
     */
    public ItemBuilder data(short data) {
        stack.setDurability(data);
        return this;
    }

    /**
     * Adds the flags of this item builder.
     *
     * @param flags the flags to set
     * @return the updated item builder
     */
    public ItemBuilder flags(ItemFlag... flags) {
        validateMeta();
        meta.addItemFlags(flags);
        return this;
    }

    /**
     * Sets the meta of this item builder.
     *
     * @param meta the meta to set
     * @return this instance
     */
    public ItemBuilder meta(ItemMeta meta) {
        stack.setItemMeta(meta);
        return this;
    }

    /**
     * Sets the display name of this item builder.
     *
     * @param name the display name to set
     * @return this instance
     */
    public ItemBuilder name(String name) {
        validateMeta();
        meta.setDisplayName(name);
        return this;
    }

    /**
     * Adds a line to the lore of this builder at a specific position.
     *
     * @param line the line to add
     * @return this instance
     */
    public ItemBuilder loreLine(String line) {
        validateMeta();

        boolean hasLore = meta.hasLore();
        List<String> lore = hasLore ? meta.getLore() : new ArrayList<>();
        lore.add(hasLore ? lore.size() : 0, line);

        this.lore(line);
        return this;
    }

    /**
     * Sets the lore of this item builder.
     *
     * @param lore the lore varargs to set
     * @return this instance
     */
    public ItemBuilder lore(String... lore) {
        validateMeta();

        meta.setLore(Arrays.asList(lore));
        return this;
    }

    /**
     * Sets the lore of this item builder.
     *
     * @param lore the lore varargs to set
     * @return this instance
     */
    public ItemBuilder lore(List<String> lore) {
        validateMeta();

        meta.setLore(lore);
        return this;
    }


    /**
     * Sets the description of this item builder.
     *
     * @param description the description to set
     * @param color the color to set
     * @param length the length of description
     * @param spacing if spacer created for a new line
     *
     * @return this instance
     */
    public ItemBuilder description(String description, String color, int length, boolean spacing) {
        lore(wrap(description, color, length, spacing));
        return this;
    }

    /**
     * Sets the description of this item builder.
     *
     * @param description the description to set
     * @param color the color to set
     * @param length the length of description
     *
     * @return this instance
     */
    public ItemBuilder description(String description, String color, int length) {
        lore(wrap(description, color, length, false));
        return this;
    }

    /**
     * Sets the description of this item builder.
     *
     * @param description the description to set
     * @param color the color to set
     *
     * @return this instance
     */
    public ItemBuilder description(String description, String color) {
        lore(wrap(description, color));
        return this;
    }

    /**
     * @see ItemBuilder#enchant(Enchantment, int, boolean)
     */
    public ItemBuilder enchant(Enchantment enchantment, int level) {
        return enchant(enchantment, level, true);
    }

    /**
     * Adds an enchantment to this item builder.
     *
     * @param enchantment the enchant to add
     * @param level       the level to add at
     * @param unsafe      if it should use unsafe calls
     * @return this instance
     */
    public ItemBuilder enchant(Enchantment enchantment, int level, boolean unsafe) {
        if (unsafe && level >= enchantment.getMaxLevel()) {
            stack.addUnsafeEnchantment(enchantment, level);
        } else {
            stack.addEnchantment(enchantment, level);
        }

        return this;
    }

    /**
     * Sets the unbreakability of this item builder.
     *
     * @param unbreakable if the item is unbreakable
     * @return this instance
     */
    public ItemBuilder unbreakable(boolean unbreakable) {
        validateMeta();
        meta.spigot().setUnbreakable(unbreakable);
        return this;
    }

    /**
     * Builds this into an {@link ItemStack}.
     *
     * @return the built {@link ItemStack}
     */
    public ItemStack build() {
        if (meta != null) {
            stack.setItemMeta(meta);
        }

        return stack;
    }

    /**
     * Builds this into an {@link ItemStack}.
     *
     * @return the built {@link ItemStack}
     */
    public ItemStack create() {
        return build();
    }

    public static List<String> wrap(String string, String color, int length, boolean spacing) {
        String[] split = string.split(" ");
        string = "";
        ArrayList<String> newString = new ArrayList<>();
        for (int i = 0; i < split.length; i++) {
            if (string.length() > length || string.endsWith(".") || string.endsWith("!")) {
                newString.add(color + string);
                if (spacing && (string.endsWith(".") || string.endsWith("!")))
                    newString.add("");
                string = "";
            }
            string += (string.length() == 0 ? "" : " ") + split[i];
        }
        newString.add(color + string);
        return newString;
    }


    public static List<String> wrap(String string, String color) {
        return wrap(string, color, 20, false);
    }
}
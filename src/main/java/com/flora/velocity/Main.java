package com.flora.velocity;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main extends JavaPlugin implements Listener
{
    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(command.getName().equalsIgnoreCase("velocity"))) return true;
        if (!(sender instanceof Player)) return true;
        if (!(sender.isOp())) return true;

        Player player = (Player) sender;

        if (args.length == 0) { return false; }

        player.getInventory().addItem(itemGravityGun(args[0]));
        player.sendMessage("중력건이 지급되었습니다");

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(command.getName().equalsIgnoreCase("velocity"))) return null;
        if (!(sender instanceof Player)) return null;
        if (!(sender.isOp())) return null;

        if (args.length == 1)
            return new ArrayList<String>() {{ Bukkit.getOnlinePlayers().forEach(p -> add(p.getName())); }};
        else
            return new ArrayList<>();
    }

    @EventHandler
    private void onInteractGravityGun(PlayerInteractEvent event) {
        if (!(isGravityGun(event.getItem()))) { return; }
        if (!(Objects.equals(event.getHand(), EquipmentSlot.HAND))) { return; }

        Player player = event.getPlayer();
        Action action = event.getAction();
        String name = Objects.requireNonNull(Objects.requireNonNull(event.getItem().getItemMeta()).getLore()).get(0).substring(4);

        if (action.equals(Action.LEFT_CLICK_BLOCK) || action.equals(Action.LEFT_CLICK_AIR))
            setVelocityPlayer(name, player, event.getItem().getAmount());
        if (action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.RIGHT_CLICK_AIR))
            setVelocityPlayer(name, player, -(event.getItem().getAmount()));
    }

    private void setVelocityPlayer(String name, Player player, int multiply) {
        for (Player p : Bukkit.matchPlayer(name)) {
            p.setVelocity(player.getLocation().getDirection().multiply(multiply));
        }
    }

    private boolean isGravityGun(ItemStack itemStack) {
        if (itemStack == null) { return false; }
        if (!(itemStack.getType().equals(Material.EMERALD))) { return false; }
        if (!(itemStack.hasItemMeta())) { return false; }
        if (!(Objects.requireNonNull(itemStack.getItemMeta()).hasDisplayName())) { return false; }
        if (!(itemStack.getItemMeta().hasLore())) { return false; }
        if (!(itemStack.getItemMeta().getDisplayName().equals("§eGravity Gun"))) { return false; }
        return !Objects.requireNonNull(itemStack.getItemMeta().getLore()).isEmpty();
    }

    private ItemStack itemGravityGun(String name) {
        ItemStack item = new ItemStack(Material.EMERALD);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();

        assert meta != null;
        meta.setDisplayName("§eGravity Gun");
        lore.add("§7§o" + name);
        meta.setLore(lore);

        item.setItemMeta(meta);

        return item;
    }
}

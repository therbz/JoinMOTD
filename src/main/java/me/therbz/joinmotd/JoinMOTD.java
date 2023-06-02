package me.therbz.joinmotd;

import me.therbz.joinmotd.commands.*;
import me.therbz.joinmotd.listeners.JoinListener;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

public class JoinMOTD extends JavaPlugin implements CommandExecutor, TabCompleter {
    private ArrayList<Motd> motdsList;

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new JoinListener(this), this);
        saveDefaultConfig();
        loadMotdsFromFile();
    }

    @Override
    public void onDisable() {
        saveMotdsToFile();
    }

    // returns true if successful
    // returns false if unsuccessful (error)
    public boolean loadMotdsFromFile() {
        this.motdsList = new ArrayList<>();

        File motdsFile = new File(getDataFolder(), "motds.yml");
        if (!motdsFile.exists()) {
            motdsFile.getParentFile().mkdirs();
            saveResource("motds.yml", false);
        }

        FileConfiguration motdsConfig = YamlConfiguration.loadConfiguration(motdsFile);
        try {
            motdsConfig.load(motdsFile);
        } catch (IOException | InvalidConfigurationException exception) {
            exception.printStackTrace();
            return false;
        }

        ConfigurationSection configSection = motdsConfig.getConfigurationSection("motds");
        if (configSection == null) return true;

        Set<String> keys = configSection.getKeys(false);
        // The key SHOULD be the expiry timestamp. If not, make it never expire.
        keys.forEach(key -> {
            long expiryTimeMillis;
            try {
                expiryTimeMillis = Long.parseLong(key);
            } catch (NumberFormatException exception) {
                expiryTimeMillis = Long.MAX_VALUE;
            }

            String message = configSection.getString(key);
            addMotd(expiryTimeMillis, message);
        });

        return true;
    }

    public boolean saveMotdsToFile() {
        File motdsFile = new File(getDataFolder(), "motds.yml");
        if (!motdsFile.exists()) {
            motdsFile.getParentFile().mkdirs();
        }

        FileConfiguration motdsConfig = new YamlConfiguration();

        motdsList.forEach(motd -> {
            if (motd.expiryTimeMillis <= System.currentTimeMillis()) return; // Prevent us saving expired Motds

            String key = "motds." + motd.expiryTimeMillis;
            String message = motd.message;
            motdsConfig.set(key, message);
        });

        try {
            motdsConfig.save(motdsFile);
            return true;
        } catch (IOException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public void addMotd(long expiryTimeMillis, String message) {
        Motd motd = new Motd(expiryTimeMillis, message);
        motdsList.add(motd);
    }

    public void viewMotd(Audience receiver) {
        long currentTimeMillis = System.currentTimeMillis();
        MiniMessage mm = MiniMessage.miniMessage();
        motdsList.forEach(motd -> {
            if (motd.expiryTimeMillis <= currentTimeMillis) return; // Prevent us displaying expired Motds
            Component component = mm.deserialize(motd.message);
            receiver.sendMessage(component);
        });
    }

    public ArrayList<Motd> getMotdsList() {
        return motdsList;
    }

    public void removeMotd(int id) {
        motdsList.remove(id);
        saveMotdsToFile();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) return false;

        return switch (args[0].toLowerCase(Locale.ROOT)) {
            case "add" -> AddCommand.run(this, sender, command, label, args);
            case "remove" -> RemoveCommand.run(this, sender, command, label, args);
            case "view" -> ViewCommand.run(this, sender, command, label, args);
            case "reload" -> ReloadCommand.run(this, sender, command, label, args);
            case "list" -> ListCommand.run(this, sender, command, label, args);
            case "parse" -> ParseCommand.run(this, sender, command, label, args);
            default -> false;
        };
    }

    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        ArrayList<String> list = new ArrayList<>();

        if (args.length == 1) {
            list.add("view");
            list.add("list");
            list.add("reload");
            list.add("add");
            list.add("remove");
            list.add("parse");
            return list;
        }

        return switch (args[0].toLowerCase(Locale.ROOT)) {
            case "add" -> AddCommand.tabComplete(this, sender, command, label, args);
            case "remove" -> RemoveCommand.tabComplete(this, sender, command, label, args);
            //case "view" -> list;
            //case "reload" -> list;
            //case "list" -> list;
            //case "parse" -> list;
            default -> list;
        };
    }
}

package me.therbz.joinmotd.commands;

import me.therbz.joinmotd.JoinMOTD;
import me.therbz.joinmotd.MessageUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ReloadCommand {
    public static boolean run(JoinMOTD main, @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender.hasPermission("joinmotd.admin"))) {
            MessageUtils.sendNoPermMessage(main, sender);
            return true;
        }

        main.loadMotdsFromFile();
        main.reloadConfig();

        sender.sendMessage(MiniMessage.miniMessage().deserialize(Objects.requireNonNull(main.getConfig().getString("messages.reload"))));
        return true;
    }
}

package me.therbz.joinmotd.commands;

import me.therbz.joinmotd.JoinMOTD;
import me.therbz.joinmotd.MessageUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RemoveCommand {
    public static boolean run(JoinMOTD main, @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender.hasPermission("joinmotd.admin"))) {
            MessageUtils.sendNoPermMessage(main, sender);
            return true;
        }

        int id;
        try {
            id = Integer.parseInt(args[1]);
        } catch (NumberFormatException exception) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(Objects.requireNonNull(main.getConfig().getString("messages.nfe"))));
            return true;
        }

        if (id > main.getMotdsList().size()-1) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(Objects.requireNonNull(main.getConfig().getString("messages.no-id"))));
            return true;
        }

        sender.sendMessage(MiniMessage.miniMessage().deserialize(Objects.requireNonNull(main.getConfig().getString("messages.remove")),
                Placeholder.unparsed("id", String.valueOf(id)),
                Placeholder.parsed("message", main.getMotdsList().get(id).message)));
        main.removeMotd(id);
        return true;
    }

    public static List<String> tabComplete(JoinMOTD main, @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        ArrayList<String> list = new ArrayList<>();

        if (args.length == 2) {
            for (int i=0; i<main.getMotdsList().size(); i++) {
                list.add(String.valueOf(i));
            }
        }

        return list;
    }
}

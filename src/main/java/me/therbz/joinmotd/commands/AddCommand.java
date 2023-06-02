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

public class AddCommand {
    public static boolean run(JoinMOTD main, @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender.hasPermission("joinmotd.admin"))) {
            MessageUtils.sendNoPermMessage(main, sender);
            return true;
        }

        // /joinmotd add 7d Welcome to my server!
        if (args.length < 3) {
            return false;
        }

        long expiryDelaySeconds = MessageUtils.parseTime(args[1]);
        long expiryTimeMillis = (args[1].equalsIgnoreCase("never")) ? Long.MAX_VALUE : System.currentTimeMillis() + expiryDelaySeconds*1000;

        StringBuilder messageBuilder = new StringBuilder();
        for (int i=2; i<args.length; i++) {
            messageBuilder.append(args[i]);
            if (i<args.length-1) messageBuilder.append(" ");
        }

        String message = messageBuilder.toString();
        main.addMotd(expiryTimeMillis, message);
        main.saveMotdsToFile();

        long expiryDelayMillis = expiryTimeMillis - System.currentTimeMillis();

        sender.sendMessage(MiniMessage.miniMessage().deserialize(Objects.requireNonNull(main.getConfig().getString("messages.add")),
                Placeholder.unparsed("id", String.valueOf(main.getMotdsList().size()-1)),
                Placeholder.unparsed("expiry", MessageUtils.stringifyTimeInSeconds(expiryDelayMillis/1000, main)),
                Placeholder.parsed("message", message)));
        return true;
    }

    public static List<String> tabComplete(JoinMOTD main, @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        ArrayList<String> list = new ArrayList<>();

        if (args.length == 2) {
            list.add("52w");
            list.add("7d");
            list.add("3d");
            list.add("12h");
            list.add("30m");
            list.add("60s");
            list.add("never");
        }

        return list;
    }
}
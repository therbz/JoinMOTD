package me.therbz.joinmotd.commands;

import me.therbz.joinmotd.JoinMOTD;
import me.therbz.joinmotd.MessageUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ParseCommand {
    public static boolean run(JoinMOTD main, @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender.hasPermission("joinmotd.admin"))) {
            MessageUtils.sendNoPermMessage(main, sender);
            return true;
        }

        StringBuilder messageBuilder = new StringBuilder();
        for (int i=1; i<args.length; i++) {
            messageBuilder.append(args[i]);
            if (i<args.length-1) messageBuilder.append(" ");
        }

        sender.sendMessage(MiniMessage.miniMessage().deserialize(messageBuilder.toString()));
        return true;
    }
}

package me.therbz.joinmotd.commands;

import me.therbz.joinmotd.JoinMOTD;
import me.therbz.joinmotd.MessageUtils;
import me.therbz.joinmotd.Motd;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class ListCommand {
    public static boolean run(JoinMOTD main, @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender.hasPermission("joinmotd.admin"))) {
            MessageUtils.sendNoPermMessage(main, sender);
            return true;
        }

        MiniMessage mm = MiniMessage.miniMessage();
        String unformattedListMessage = Objects.requireNonNull(main.getConfig().getString("messages.list"));

        ArrayList<Motd> motdsList = main.getMotdsList();
        for (int i=0; i<motdsList.size(); i++) {
            Motd motd = motdsList.get(i);
            long expiryDelayMillis = motd.expiryTimeMillis - System.currentTimeMillis();

            sender.sendMessage(mm.deserialize(unformattedListMessage,
                    Placeholder.unparsed("id", String.valueOf(i)),
                    Placeholder.unparsed("expiry", (expiryDelayMillis<0) ? "expired" : MessageUtils.stringifyTimeInSeconds(expiryDelayMillis/1000, main)),
                    Placeholder.parsed("message", motd.message)));
        }

        return true;
    }
}

package me.therbz.joinmotd.listeners;

import me.therbz.joinmotd.JoinMOTD;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
    private final JoinMOTD main;

    public JoinListener(JoinMOTD main) {
        this.main = main;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (event.getPlayer().hasPermission("joinmotd.view")) main.viewMotd(event.getPlayer());
    }
}

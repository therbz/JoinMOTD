package me.therbz.joinmotd;

public class Motd {
    public final long expiryTimeMillis;
    public final String message;

    public Motd(long expiryTimeMillis, String message) {
        this.expiryTimeMillis = expiryTimeMillis;
        this.message = message;
    }
}

package mr.wruczek.supercensor3.checks;

import org.bukkit.entity.Player;

public class AntiSpamData {

    private Player player;
    private String lastMessage;
    private int repeats;
    private long lastMessageTime;
    private int warns;

    public AntiSpamData(Player player, String message) {
        this.player = player;
        this.lastMessage = message;
        this.repeats = 0;
        this.setWarns(0);
    }

    public Player getPlayer() {
        return player;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public int getRepeats() {
        return repeats;
    }

    public void addRepeat() {
        setRepeats(getRepeats() + 1);
    }

    public void setRepeats(int repeats) {
        this.repeats = repeats;
    }

    public long getTime() {
        return System.currentTimeMillis() - getLastMessageTime();
    }

    public long getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime() {
        setLastMessageTime(System.currentTimeMillis());
    }

    public void setLastMessageTime(long lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public int getWarns() {
        return warns;
    }

    public void addWarn() {
        setWarns(getWarns() + 1);
    }

    public void removeWarn() {
        if (getWarns() > 0)
            setWarns(getWarns() - 1);
    }

    public void setWarns(int warns) {
        this.warns = warns;
    }

    @Override
    public String toString() {
        return "AntiSpamData [player=" + player + ", lastMessage=" + lastMessage + ", repeats=" + repeats
                + ", lastMessageTime=" + lastMessageTime + ", warns=" + warns + "]";
    }

}
package vavien.agency.goalalert.MatchDetail;

/**
 * Created by SD
 * on 10.05.2018.
 */

public class EventsPojo {
    private String team,type,minute,player;

    public EventsPojo(String team, String type, String minute, String player) {
        this.team = team;
        this.type = type;
        this.minute = minute;
        this.player = player;
    }

    public String getTeam() {
        return team;
    }

    public String getType() {
        return type;
    }

    public String getMinute() {
        return minute;
    }

    public String getPlayer() {
        return player;
    }

}

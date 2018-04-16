package vavien.agency.goalalert.pojoClasses;

/**
 * Created by SD on 9.11.2017.
 * dilmacsedat@gmail.com
 * :)
 */

public class NextMatchPojo {
    private int leagueId, matchId;
    private String leagueName, localTeam, visitorTeam, flag,hour, minute;

    public NextMatchPojo(int leagueId, String leagueName, String flag) {
        this.leagueId = leagueId;
        this.leagueName = leagueName;
        this.matchId = -1;
        this.localTeam = null;
        this.visitorTeam = null;
        this.flag = flag;
        this.hour = null;
        this.minute = null;
    }

    public NextMatchPojo(int leagueId, String leagueName, int matchId, String localTeam, String visitorTeam, String hour, String minute, String flag) {
        this.leagueId = leagueId;
        this.leagueName = leagueName;
        this.matchId = matchId;
        this.localTeam = localTeam;
        this.visitorTeam = visitorTeam;
        this.flag = flag;
        this.hour = hour;
        this.minute = minute;
    }

    public int getLeagueId() {
        return leagueId;
    }

    public String getLeagueName() {
        return leagueName;
    }

    public int getMatchId() {
        return matchId;
    }

    public String getLocalTeam() {
        return localTeam;
    }

    public String getVisitorTeam() {
        return visitorTeam;
    }

    public String getFlag() {
        return flag;
    }

    public String getHour() {
        return hour;
    }

    public String getMinute() {
        return minute;
    }

}

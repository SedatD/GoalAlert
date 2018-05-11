package vavien.agency.goalalert.model;

/**
 * Created by SD on 10.11.2017.
 * dilmacsedat@gmail.com
 * :)
 */

public class ResultPojo {
    private int leagueId, matchId, visitorScore, localScore;
    private String localTeam, flag, visitorTeam, leagueName;

    public ResultPojo(int leagueId, String leagueName, String flag) {
        this.leagueId = leagueId;
        this.leagueName = leagueName;
        this.matchId = -1;
        this.localTeam = null;
        this.visitorTeam = null;
        this.localScore = -1;
        this.visitorScore = -1;
        this.flag = flag;
    }

    public ResultPojo(int leagueId, String leagueName, int matchId, String localTeam, String visitorTeam, int localScore, int visitorScore, String flag) {
        this.leagueId = leagueId;
        this.leagueName = leagueName;
        this.matchId = matchId;
        this.localTeam = localTeam;
        this.visitorTeam = visitorTeam;
        this.localScore = localScore;
        this.visitorScore = visitorScore;
        this.flag = flag;
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

    public int getLocalScore() {
        return localScore;
    }

    public int getVisitorScore() {
        return visitorScore;
    }

    public String getFlag() {
        return flag;
    }

}

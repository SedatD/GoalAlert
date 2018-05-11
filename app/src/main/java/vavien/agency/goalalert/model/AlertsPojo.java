package vavien.agency.goalalert.model;

/**
 * Created by SD on 4.12.2017.
 * dilmacsedat@gmail.com
 * :)
 */

public class AlertsPojo {
    private int dbId;
    private String mainText, matchId;

    public AlertsPojo(int dbId, String mainText, String matchId){
        this.dbId = dbId;
        this.mainText = mainText;
        this.matchId = matchId;
    }

    public int getDbId() {
        return dbId;
    }

    public String getMainText() {
        return mainText;
    }

    public String getMatchId() {
        return matchId;
    }
}

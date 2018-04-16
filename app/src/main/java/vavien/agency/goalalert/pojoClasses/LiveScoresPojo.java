package vavien.agency.goalalert.pojoClasses;

import android.os.Parcel;

/**
 * Created by SD on 30.10.2017.
 * dilmacsedat@gmail.com
 * :)
 */

public class LiveScoresPojo implements android.os.Parcelable {
    private int leagueId, matchId, localScore, visitorScore, minute, preLocalScore, preVisitorScore, preMinute;
    private String leagueName, localTeam, visitorTeam, flag;
    private boolean ilkFlag, matchLenghtBool;

    public LiveScoresPojo(int leagueId, String leagueName, int matchId, String localTeam, String visitorTeam, int localScore, int visitorScore, int minute, int preLocalScore, int preVisitorScore, int preMinute, String flag, boolean ilkFlag, boolean matchLenghtBool) {
        this.leagueId = leagueId;
        this.leagueName = leagueName;
        this.matchId = matchId;
        this.localTeam = localTeam;
        this.visitorTeam = visitorTeam;
        this.localScore = localScore;
        this.visitorScore = visitorScore;
        this.minute = minute;
        this.preLocalScore = preLocalScore;
        this.preVisitorScore = preVisitorScore;
        this.preMinute = preMinute;
        this.flag = flag;
        this.ilkFlag = ilkFlag;
        this.matchLenghtBool = matchLenghtBool;
    }

    public LiveScoresPojo(int leagueId, String leagueName, String flag) {
        this.leagueId = leagueId;
        this.leagueName = leagueName;
        this.matchId = -1;
        this.localTeam = null;
        this.visitorTeam = null;
        this.localScore = -1;
        this.visitorScore = -1;
        this.minute = -1;
        this.preLocalScore = -1;
        this.preVisitorScore = -1;
        this.preMinute = -1;
        this.flag = flag;
        this.ilkFlag = false;
    }

    protected LiveScoresPojo(Parcel in) {
        matchId = in.readInt();
        minute = in.readInt();
        localTeam = in.readString();
        visitorTeam = in.readString();
    }

    @Override
    public int describeContents() {
        return this.hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(matchId);
        dest.writeInt(minute);
        dest.writeString(localTeam);
        dest.writeString(visitorTeam);
    }

    public static final Creator<LiveScoresPojo> CREATOR = new Creator<LiveScoresPojo>() {
        @Override
        public LiveScoresPojo createFromParcel(Parcel in) {
            return new LiveScoresPojo(in);
        }

        @Override
        public LiveScoresPojo[] newArray(int size) {
            return new LiveScoresPojo[size];
        }
    };

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

    public int getMinute() {
        return minute;
    }

    public int getPreLocalScore() {
        return preLocalScore;
    }

    public int getPreVisitorScore() {
        return preVisitorScore;
    }

    public int getPreMinute() {
        return preMinute;
    }

    public String getFlag() {
        return flag;
    }

    public boolean isIlkFlag() {
        return ilkFlag;
    }

    public boolean isMatchLenghtBool() {
        return matchLenghtBool;
    }

}

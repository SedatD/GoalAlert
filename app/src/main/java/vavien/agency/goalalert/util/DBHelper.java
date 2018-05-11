package vavien.agency.goalalert.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import vavien.agency.goalalert.R;

/**
 * Created by SD on 23.11.2017.
 * dilmacsedat@gmail.com
 * :)
 */

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Alert.db";
    private static final String CONTACTS_TABLE_NAME = "alerts";
    private static final String CONTACTS_COLUMN_ID = "id";
    private static final String CONTACTS_COLUMN_LOCALNAME = "localTeam";
    private static final String CONTACTS_COLUMN_VISITORNAME = "visiorTeam";
    private static final String CONTACTS_COLUMN_ALARMMIN = "alarmMin";
    private static final String CONTACTS_COLUMN_BET = "bet";
    private static final String CONTACTS_COLUMN_MATCHID = "matchId";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String ss = " CREATE TABLE "
                + CONTACTS_TABLE_NAME + " ("
                + CONTACTS_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + CONTACTS_COLUMN_LOCALNAME + " TEXT,"
                + CONTACTS_COLUMN_VISITORNAME + " TEXT,"
                + CONTACTS_COLUMN_ALARMMIN + " INTEGER,"
                + CONTACTS_COLUMN_BET + " DOUBLE,"
                + CONTACTS_COLUMN_MATCHID + " TEXT   )";
        db.execSQL(ss);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL(" DROP TABLE IF EXISTS " + CONTACTS_TABLE_NAME);
        onCreate(db);
    }

    public void insertContact(String local_team, String visitor_team, int alarmMin, double bet, String matchId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(CONTACTS_COLUMN_LOCALNAME, local_team);
            contentValues.put(CONTACTS_COLUMN_VISITORNAME, visitor_team);
            contentValues.put(CONTACTS_COLUMN_ALARMMIN, alarmMin);
            contentValues.put(CONTACTS_COLUMN_BET, bet);
            contentValues.put(CONTACTS_COLUMN_MATCHID, matchId);

            db.insert(CONTACTS_TABLE_NAME, null, contentValues);
        } catch (Exception ignored) {

        }
        db.close();
    }

    public List<String> getAllCotacts() {
        List<String> array_list = new ArrayList<String>();
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String[] stunlar = new String[]{CONTACTS_COLUMN_ID, CONTACTS_COLUMN_LOCALNAME, CONTACTS_COLUMN_VISITORNAME, CONTACTS_COLUMN_ALARMMIN, CONTACTS_COLUMN_BET, CONTACTS_COLUMN_MATCHID};
            Cursor res = db.query(CONTACTS_TABLE_NAME, stunlar, null, null, null, null, null);
            while (res.moveToNext()) {
                array_list.add(res.getInt(0)
                        + " - "
                        + res.getString(1)
                        + " - "
                        + res.getString(2)
                        + " - "
                        + res.getInt(3)
                        + " - "
                        + res.getDouble(4)
                        + " - "
                        + res.getString(5)
                        + " - "
                );
            }
            //db.close();
        } catch (Exception e) {
            Log.wtf("DBHelper", "getAllContacts catche girdi okuyamadı demek");
        }
        return array_list;
    }

    public List<String> getIds() {
        List<String> array_list = new ArrayList<String>();
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            if (db != null) {
                String[] stunlar = new String[]{CONTACTS_COLUMN_MATCHID, CONTACTS_COLUMN_ID};
                Cursor res = db.query(CONTACTS_TABLE_NAME, stunlar, null, null, null, null, null);
                while (res.moveToNext()) {
                    array_list.add(res.getInt(0) + " - " + res.getInt(1));
                }
                //db.close();
            }
        } catch (Exception e) {
            Log.wtf("DBHelper", "getIds ust catche girdi");
        } finally {
            try {
                if (db != null)
                    db.close();
            } catch (Exception e) {
                Log.wtf("DBHelper", "getIds finally catche girdi");
            }
        }
        return array_list;
    }

    public List<String> getFragmentViewList(Context context) {
        List<String> array_list = new ArrayList<String>();
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String[] stunlar = new String[]{CONTACTS_COLUMN_LOCALNAME, CONTACTS_COLUMN_VISITORNAME, CONTACTS_COLUMN_ALARMMIN, CONTACTS_COLUMN_BET, CONTACTS_COLUMN_ID, CONTACTS_COLUMN_MATCHID};
            Cursor res = db.query(CONTACTS_TABLE_NAME, stunlar, null, null, null, null, null);
            while (res.moveToNext()) {

                int alarmMinInt = res.getInt(2);
                String alarmMinString;
                switch (alarmMinInt) {
                    case -2:
                        alarmMinString = context.getString(R.string.any_time);
                        break;
                    case -3:
                        alarmMinString = context.getString(R.string.half_time);
                        break;
                    case -4:
                        alarmMinString = context.getString(R.string.full_time);
                        break;
                    default:
                        alarmMinString = alarmMinInt + "'";
                        break;
                }

                double betDouble = res.getDouble(3);
                String betString = null;
                if (betDouble == 1.1) {
                    betString = context.getString(R.string.btts_yes);
                }
                if (betDouble == -1.1) {
                    betString = context.getString(R.string.btts_no);
                }
                if (betDouble == -8.8) {
                    betString = context.getString(R.string.score);
                }
                if (betDouble == -9.9) {
                    betString = context.getString(R.string.no_goal);
                }
                if (betDouble == 0.5) {
                    betString = "+ 0,5";
                }
                if (betDouble == 1.5) {
                    betString = "+ 1,5";
                }
                if (betDouble == 2.5) {
                    betString = "+ 2,5";
                }
                if (betDouble == 3.5) {
                    betString = "+ 3,5";
                }
                if (betDouble == 4.5) {
                    betString = "+ 4,5";
                }
                if (betDouble == 5.5) {
                    betString = "+ 5,5";
                }
                if (betDouble == -1.5) {
                    betString = "- 1,5";
                }
                if (betDouble == -2.5) {
                    betString = "- 2,5";
                }
                if (betDouble == -3.5) {
                    betString = "- 3,5";
                }
                if (betDouble == -4.5) {
                    betString = "- 4,5";
                }
                if (betDouble == -5.5) {
                    betString = "- 5,5";
                }

                array_list.add(
                        res.getString(4)
                                + " - "
                                + res.getString(5)
                                + " - "
                                + res.getString(0)
                                + " - "
                                + res.getString(1)
                                + " : "
                                + alarmMinString
                                + " / "
                                + betString
                );
            }
            //db.close();
        } catch (Exception e) {
            Log.wtf("DBHelper", "getFragmentViewList catche girdi");
        }
        return array_list;
    }

    public boolean deleteMethod(int id) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String where = CONTACTS_COLUMN_ID + " = " + id;
            db.delete(CONTACTS_TABLE_NAME, where, null);
            Log.wtf("DBHelper deleteMethod", "db'den silindi : " + id);
            //db.close();
            return true;
        } catch (Exception e) {
            Log.wtf("DBHelper deleteMethod", "Alt catche girdi delete method silemedi demektir");
            return false;
        }
    }
}
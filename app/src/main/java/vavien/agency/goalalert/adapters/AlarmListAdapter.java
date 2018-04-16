package vavien.agency.goalalert.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import vavien.agency.goalalert.DBHelper;
import vavien.agency.goalalert.R;
import vavien.agency.goalalert.pojoClasses.AlertsPojo;

import static vavien.agency.goalalert.R.id.textAlertList;

/**
 * Created by SD on 4.12.2017.
 * dilmacsedat@gmail.com
 * :)
 */

public class AlarmListAdapter extends BaseAdapter {
    private static onDoneClick mListener;
    private LayoutInflater mInflater;
    private List<AlertsPojo> mAlarms;
    private Context context;
    private DBHelper mydb;

    public AlarmListAdapter(Activity activity, List<AlertsPojo> alarms, onDoneClick listener) {
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mAlarms = alarms;
        context = activity;
        mListener = listener;
    }

    @Override
    public int getCount() {
        return mAlarms.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        @SuppressLint({"ViewHolder", "InflateParams"}) final View viewRow = mInflater.inflate(R.layout.listview_fragment_alert_row, null);
        Holder holder = new Holder();
        holder.textAlertList = (TextView) viewRow.findViewById(textAlertList);
        holder.btnCancel = (Button) viewRow.findViewById(R.id.btnCancelAlert);

        final AlertsPojo alert = mAlarms.get(position);

        holder.textAlertList.setText(alert.getMainText());

        holder.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClick(v, position, alert.getDbId(), alert.getMatchId());
            }
        });

        return viewRow;
    }

    public interface onDoneClick {
        void onClick(View v, int position, int dbidd, String matchId);
    }

    private class Holder {
        TextView textAlertList;
        Button btnCancel;
    }
}

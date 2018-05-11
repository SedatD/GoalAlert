package vavien.agency.goalalert.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import vavien.agency.goalalert.util.DBHelper;
import vavien.agency.goalalert.R;
import vavien.agency.goalalert.model.LiveScoresPojo;

/**
 * Created by SD on 30.10.2017.
 * dilmacsedat@gmail.com
 * :)
 */

public class LiveScoresRecyclerViewAdapter extends RecyclerView.Adapter<LiveScoresRecyclerViewAdapter.DataObjectHolder> implements Filterable {
    private static MyClickListener myClickListener;
    private int lastPosition = -1;
    private ArrayList<LiveScoresPojo> mDataset;
    private ArrayList<LiveScoresPojo> orig;
    private Context context;
    private MediaPlayer mediaPlayer;
    private DBHelper dbHelper;


    public LiveScoresRecyclerViewAdapter(Context context, ArrayList<LiveScoresPojo> myDataset) {
        mDataset = myDataset;
        this.context = context;
        //notifyDataSetChanged();//bunu açmak gerekiyor olabilir
        //notifyItemChanged();
    }


    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_livescores, parent, false);
        context = parent.getContext();
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.setIsRecyclable(false);

        holder.leagueId = mDataset.get(position).getLeagueId();
        holder.matchId = mDataset.get(position).getMatchId();

        if (mDataset.get(position).getMatchId() == -1) {
            holder.leagueName.setText(mDataset.get(position).getLeagueName());

            String aq = mDataset.get(position).getFlag();
            Resources resources = context.getResources();
            final int resID = resources.getIdentifier(aq, "mipmap", context.getPackageName());
            if (resID == 0)
                holder.flag.setBackgroundResource(R.mipmap.cup);
            else
                holder.flag.setBackgroundResource(resID);

            holder.btnAlert.setVisibility(View.GONE);
            holder.btnStats.setVisibility(View.GONE);
            holder.rlView.setBackgroundColor(ContextCompat.getColor(context, R.color.denemelig));
        } else {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            Set<String> myMatchList = preferences.getStringSet("myMatchList", null);
            if (myMatchList != null) {
                List<String> list = new ArrayList<String>(myMatchList);
                for (int i = 0; i < list.size(); i++) {
                    if (Integer.parseInt(list.get(i)) == mDataset.get(position).getMatchId())
                        holder.btnAlert.setBackgroundResource(R.drawable.bellyellow);
                }
            }

            if (position % 2 == 0)
                holder.rlView.setBackgroundColor(ContextCompat.getColor(context, R.color.kirlibeyaz));

            holder.leagueName.setText(null);
            holder.flag.setVisibility(View.GONE);
            holder.local.setText(mDataset.get(position).getLocalTeam());
            holder.localScore.setText(mDataset.get(position).getLocalScore() + " - ");
            holder.visitorScore.setText(mDataset.get(position).getVisitorScore() + "");
            holder.visitor.setText(mDataset.get(position).getVisitorTeam());

            if (mDataset.get(position).getMinute() == 0)
                holder.minute.setText("HT ");
            else
                holder.minute.setText(mDataset.get(position).getMinute() + "' ");

            boolean booleanMessage = preferences.getBoolean("booleanMessage", true);

            if (booleanMessage && mDataset.get(position).getLocalScore() != 0 && mDataset.get(position).getPreLocalScore() != -1 && mDataset.get(position).getPreLocalScore() != mDataset.get(position).getLocalScore() && !mDataset.get(position).isIlkFlag() && mDataset.get(position).isMatchLenghtBool()) {
                //Toast.makeText(context, "Goal " + mDataset.get(position).getLocalTeam() + " " + mDataset.get(position).getLocalScore() + " - " + mDataset.get(position).getVisitorScore() + " " + mDataset.get(position).getVisitorTeam(), Toast.LENGTH_LONG).show();

                //def long 3500 // 3.5 sec --- def short 2000 // 2 sec
                final Toast tag = Toast.makeText(context, "Goal " + mDataset.get(position).getLocalTeam() + " " + mDataset.get(position).getLocalScore() + " - " + mDataset.get(position).getVisitorScore() + " " + mDataset.get(position).getVisitorTeam(), Toast.LENGTH_SHORT);
                tag.show();
                new CountDownTimer(3000, 100) {
                    public void onTick(long millisUntilFinished) {
                        tag.show();
                    }

                    public void onFinish() {
                        tag.show();
                    }
                }.start();

                mediaPlayer = MediaPlayer.create(context, R.raw.goal);
                mediaPlayer.start();
                mediaPlayer.setLooping(false);
                holder.localScore.setTextColor(Color.RED);
                setAnimation(holder.localScore, position);
            }

            if (booleanMessage && mDataset.get(position).getVisitorScore() != 0 && mDataset.get(position).getPreVisitorScore() != -1 && mDataset.get(position).getPreVisitorScore() != mDataset.get(position).getVisitorScore() && !mDataset.get(position).isIlkFlag() && mDataset.get(position).isMatchLenghtBool()) {
                //Toast.makeText(context, mDataset.get(position).getLocalTeam() + " " + mDataset.get(position).getLocalScore() + " - " + mDataset.get(position).getVisitorScore() + " " + mDataset.get(position).getVisitorTeam() + " Goal", Toast.LENGTH_LONG).show();

                //def long 3500 // 3.5 sec --- def short 2000 // 2 sec
                final Toast tag = Toast.makeText(context, mDataset.get(position).getLocalTeam() + " " + mDataset.get(position).getLocalScore() + " - " + mDataset.get(position).getVisitorScore() + " " + mDataset.get(position).getVisitorTeam() + " Goal", Toast.LENGTH_SHORT);
                tag.show();
                new CountDownTimer(3000, 100) {
                    public void onTick(long millisUntilFinished) {
                        tag.show();
                    }

                    public void onFinish() {
                        tag.show();
                    }
                }.start();

                mediaPlayer = MediaPlayer.create(context, R.raw.goal);
                mediaPlayer.start();
                mediaPlayer.setLooping(false);
                holder.visitorScore.setTextColor(Color.RED);
                setAnimation(holder.visitorScore, position);
            }

            if (mDataset.get(position).getPreMinute() != -1 && mDataset.get(position).getPreMinute() != mDataset.get(position).getMinute() && !mDataset.get(position).isIlkFlag()) {
                holder.minute.setTextColor(Color.RED);
                setAnimation(holder.minute, position);
            }
        }

    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(1000);
            //viewToAnimate.setBackgroundColor(Color.RED);
            viewToAnimate.startAnimation(anim);
            lastPosition = position;
        }
    }

    public void addItem(LiveScoresPojo dataObj, int index) {
        mDataset.add(dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<LiveScoresPojo> results = new ArrayList<LiveScoresPojo>();
                if (orig == null)
                    orig = mDataset;
                if (constraint != null) {
                    if (orig != null && orig.size() > 0) {
                        for (final LiveScoresPojo g : orig) {
                            if (g.getLocalTeam().toLowerCase().contains(constraint.toString()))
                                results.add(g);
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                mDataset = (ArrayList<LiveScoresPojo>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }

    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView leagueName, local, localScore, visitorScore, visitor, minute;
        RelativeLayout rlView;
        int leagueId, matchId;
        ImageButton btnAlert,btnStats;
        ImageView flag;
        TextView txtNoLiveMatch;

        public DataObjectHolder(View itemView) {
            super(itemView);
            rlView = itemView.findViewById(R.id.recyclerView_liveScores_item);
            leagueName = itemView.findViewById(R.id.leagueName);
            local = itemView.findViewById(R.id.local);
            localScore = itemView.findViewById(R.id.localScore);
            visitorScore = itemView.findViewById(R.id.visitorScore);
            visitor = itemView.findViewById(R.id.visitor);
            minute = itemView.findViewById(R.id.minute);
            btnAlert = itemView.findViewById(R.id.btnAlert);
            btnStats = itemView.findViewById(R.id.btnStats);
            flag = itemView.findViewById(R.id.flag);
            txtNoLiveMatch = itemView.findViewById(R.id.txtNoLiveMatch);
            btnAlert.setOnClickListener(this);
            btnStats.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getPosition(), v);
        }
    }
}
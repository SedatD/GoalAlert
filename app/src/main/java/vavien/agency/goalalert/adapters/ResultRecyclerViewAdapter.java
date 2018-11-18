package vavien.agency.goalalert.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import vavien.agency.goalalert.R;
import vavien.agency.goalalert.model.ResultPojo;

/**
 * Created by SD on 10.11.2017.
 * dilmacsedat@gmail.com
 * :)
 */

public class ResultRecyclerViewAdapter extends RecyclerView.Adapter<ResultRecyclerViewAdapter.DataObjectHolder> {
    private ArrayList<ResultPojo> mDataset;
    private Context context;

    public ResultRecyclerViewAdapter(Context context, ArrayList<ResultPojo> mDataset) {
        this.mDataset = mDataset;
        this.context = context;
    }

    @Override
    public ResultRecyclerViewAdapter.DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_result, parent, false);
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(ResultRecyclerViewAdapter.DataObjectHolder holder, int position) {
        holder.setIsRecyclable(false);
        holder.leagueId = mDataset.get(position).getLeagueId();
        holder.matchId = mDataset.get(position).getMatchId();

        if (mDataset.get(position).getMatchId() == -1) {
            holder.local.setText(null);
            holder.visitor.setText(null);
            holder.localScore.setText(null);
            holder.visitorScore.setText(null);
            holder.leagueName.setText(mDataset.get(position).getLeagueName());

            String aq = mDataset.get(position).getFlag();
            Resources resources = context.getResources();
            final int resID = resources.getIdentifier(aq, "mipmap", context.getPackageName());
            if (resID == 0)
                holder.flag.setBackgroundResource(R.mipmap.cup);
            else
                holder.flag.setBackgroundResource(resID);

            //holder.lView.setBackgroundColor(ContextCompat.getColor(context,R.color.denemelig));
            holder.lView.setBackgroundColor(ContextCompat.getColor(context, R.color.yeniSari));
        } else {
            holder.leagueName.setText(null);
            holder.flag.setVisibility(View.GONE);
            holder.local.setText(mDataset.get(position).getLocalTeam() + "  ");
            holder.localScore.setText(mDataset.get(position).getLocalScore() + " - ");
            holder.visitorScore.setText(mDataset.get(position).getVisitorScore() + "  ");
            holder.visitor.setText(mDataset.get(position).getVisitorTeam());

            if (position%2 == 0)
                holder.lView.setBackgroundColor(ContextCompat.getColor(context,R.color.kirlibeyaz));
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public static class DataObjectHolder extends RecyclerView.ViewHolder {
        TextView leagueName, local, visitor, localScore, visitorScore;
        RelativeLayout lView;
        int leagueId, matchId;
        ImageView flag;

        public DataObjectHolder(View itemView) {
            super(itemView);
            lView = itemView.findViewById(R.id.recyclerView_result_item);
            leagueName = itemView.findViewById(R.id.leagueName);
            local = itemView.findViewById(R.id.local);
            visitor = itemView.findViewById(R.id.visitor);
            localScore = itemView.findViewById(R.id.localScore);
            visitorScore = itemView.findViewById(R.id.visitorScore);
            flag = itemView.findViewById(R.id.flag);
        }
    }
}

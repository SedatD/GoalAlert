package vavien.agency.goalalert.MatchDetail;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import vavien.agency.goalalert.R;

/**
 * Created by SD
 * on 10.05.2018.
 */

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.DataObjectHolder> {
    private ArrayList<EventsPojo> mDataset;
    private Context mContext;

    EventAdapter(ArrayList<EventsPojo> myDataset, Context context) {
        mContext = context;
        mDataset = myDataset;
    }

    @Override
    public EventAdapter.DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_row, parent, false);
        return new DataObjectHolder(view);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public void onBindViewHolder(EventAdapter.DataObjectHolder holder, int position) {
        if (position % 2 == 0) {
            holder.linearLayout_local.setBackgroundColor(mContext.getResources().getColor(R.color.matchColor));
            holder.linearLayout_visitor.setBackgroundColor(mContext.getResources().getColor(R.color.matchColor));
        }

        if (mDataset.get(position).getTeam().equals("localteam")) {
            holder.linearLayout_local.setVisibility(View.VISIBLE);
            holder.textView_minute.setText(mDataset.get(position).getMinute() + "'");
            holder.textView_player.setText(mDataset.get(position).getPlayer());
            setBg(holder.imageView, mDataset.get(position).getType());
        } else {
            holder.linearLayout_visitor.setVisibility(View.VISIBLE);
            holder.textView_minute2.setText(mDataset.get(position).getMinute() + "'");
            holder.textView_player2.setText(mDataset.get(position).getPlayer());
            setBg(holder.imageView2, mDataset.get(position).getType());
        }

    }

    private void setBg(ImageView imageView, String type) {
        switch (type) {
            case "goal":
                imageView.setBackgroundResource(R.drawable.goal);
                break;
            case "yellowcard":
                //imageView.setBackgroundColor(mContext.getResources().getColor(R.color.leagueColor));
                imageView.setBackgroundResource(R.drawable.yellowcard);
                break;
            case "redcard":
                imageView.setBackgroundResource(R.drawable.redcard);
                break;
            case "subst":
                imageView.setBackgroundResource(R.drawable.subst);
                break;
            default:
                break;
        }
    }

    class DataObjectHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout_local, linearLayout_visitor;
        TextView textView_minute, textView_player, textView_minute2, textView_player2;
        ImageView imageView, imageView2;

        DataObjectHolder(View itemView) {
            super(itemView);
            linearLayout_local = itemView.findViewById(R.id.linearLayout_local);
            linearLayout_visitor = itemView.findViewById(R.id.linearLayout_visitor);
            textView_minute = itemView.findViewById(R.id.textView_minute);
            textView_player = itemView.findViewById(R.id.textView_player);
            textView_minute2 = itemView.findViewById(R.id.textView_minute2);
            textView_player2 = itemView.findViewById(R.id.textView_player2);
            imageView = itemView.findViewById(R.id.imageView);
            imageView2 = itemView.findViewById(R.id.imageView2);
            /*imageView_kabulBekleyen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, TeklifDetayActivity.class);
                    Log.wtf("asd", "pos : " + mDataset.get(getAdapterPosition()).getId());
                    intent.putExtra("pos", mDataset.get(getAdapterPosition()).getId());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }
            });*/
        }
    }

}
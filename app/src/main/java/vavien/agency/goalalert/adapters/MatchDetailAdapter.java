package vavien.agency.goalalert.adapters;

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
import vavien.agency.goalalert.pojoClasses.MatchDetailPojo;

/**
 * Created by Sedat
 * on 3.04.2018.
 */

public class MatchDetailAdapter extends RecyclerView.Adapter<MatchDetailAdapter.DataObjectHolder> {
    private ArrayList<MatchDetailPojo> mDataset;
    private Context mContext;

    public MatchDetailAdapter(ArrayList<MatchDetailPojo> myDataset, Context context) {
        mContext = context;
        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.match_detail_row, parent, false);
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        if (position % 2 == 0)
            holder.linearLayout_row.setBackgroundColor(mContext.getResources().getColor(R.color.matchColor));
        holder.textView_stat.setText(mDataset.get(position).getStatsName());

        holder.textView_local.setText(mDataset.get(position).getLocalValue() + "");
        holder.textView_visitor.setText(mDataset.get(position).getVisitorValue() + "");

        holder.imageView_local.getLayoutParams().width = mDataset.get(position).getVisitorValue();
        holder.imageView_visitor.getLayoutParams().width = mDataset.get(position).getVisitorValue();

        holder.imageView_local.requestLayout();
        holder.imageView_visitor.requestLayout();
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public class DataObjectHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout_row;
        TextView textView_stat, textView_local, textView_visitor;
        ImageView imageView_local, imageView_visitor;

        public DataObjectHolder(View itemView) {
            super(itemView);
            linearLayout_row = itemView.findViewById(R.id.linearLayout_row);
            textView_stat = itemView.findViewById(R.id.textView_stat);
            textView_local = itemView.findViewById(R.id.textView_local);
            textView_visitor = itemView.findViewById(R.id.textView_visitor);
            imageView_local = itemView.findViewById(R.id.imageView_local);
            imageView_visitor = itemView.findViewById(R.id.imageView_visitor);
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
package vavien.agency.goalalert.MatchDetail;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import vavien.agency.goalalert.R;

/**
 * Created by SD
 * on 10.05.2018.
 */

public class KadroAdapter extends RecyclerView.Adapter<KadroAdapter.DataObjectHolder> {
    private ArrayList<KadroPojo> mDataset;
    private Context mContext;

    public KadroAdapter(ArrayList<KadroPojo> myDataset, Context context) {
        mContext = context;
        mDataset = myDataset;
    }

    @Override
    public KadroAdapter.DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.kadro_row, parent, false);
        return new DataObjectHolder(view);
    }

    @Override
    public void onBindViewHolder(KadroAdapter.DataObjectHolder holder, int position) {
        if (position % 2 == 0)
            holder.linearLayout_row.setBackgroundColor(mContext.getResources().getColor(R.color.matchColor));

        holder.textView_pos.setText("(" + mDataset.get(position).getPos() + ")");
        holder.textView_number.setText(mDataset.get(position).getNumber());
        holder.textView_name.setText(mDataset.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    class DataObjectHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout_row;
        TextView textView_pos, textView_number, textView_name;

        DataObjectHolder(View itemView) {
            super(itemView);
            linearLayout_row = itemView.findViewById(R.id.linearLayout_row);
            textView_pos = itemView.findViewById(R.id.textView_pos);
            textView_number = itemView.findViewById(R.id.textView_number);
            textView_name = itemView.findViewById(R.id.textView_name);
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
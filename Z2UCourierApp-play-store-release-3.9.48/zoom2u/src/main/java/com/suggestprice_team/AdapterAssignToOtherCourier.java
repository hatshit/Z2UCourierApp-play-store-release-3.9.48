package com.suggestprice_team;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.suggestprice_team.courier_team.MyTeamList_Model;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.R;
import java.util.List;

/**
 * Created by Arun on 23-Feb-2018.
 */

public class AdapterAssignToOtherCourier extends RecyclerView.Adapter<AdapterAssignToOtherCourier.MyViewHolder>{

    private List<MyTeamList_Model> arrayOfCarrierDrivers;
    private Context adapterViewContext;

    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(MyTeamList_Model item, MyViewHolder holder, int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView driverNameTxt, driverVehicleTxt;
        ImageView checkBoxForDriverSelection;

        public MyViewHolder(View itemView) {
            super(itemView);
            driverNameTxt = (TextView) itemView.findViewById(R.id.driverNameTxt);
            driverNameTxt.setTypeface(LoginZoomToU.NOVA_SEMIBOLD);

            checkBoxForDriverSelection = (ImageView) itemView.findViewById(R.id.checkBoxForDriverSelection);
        }

        public void bind(final MyTeamList_Model item, final OnItemClickListener listener, final MyViewHolder holder, final int position) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item, holder, position);
                }
            });

        }
    }

    public AdapterAssignToOtherCourier(Context adapterViewContext, List<MyTeamList_Model> arrayOfCarrierDrivers, OnItemClickListener listner){
        this.adapterViewContext = adapterViewContext;
        this.arrayOfCarrierDrivers = arrayOfCarrierDrivers;
        this.listener = listner;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()) .inflate(R.layout.row_reassign_driver, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final MyTeamList_Model reassignteamData = arrayOfCarrierDrivers.get(position);
        holder.bind(reassignteamData, listener, holder, position);

        holder.driverNameTxt.setText(""+reassignteamData.getName());

        if (reassignteamData.isSetFlagToSelectItem())
            holder.checkBoxForDriverSelection.setImageResource(R.drawable.assign_courier_selected);
        else
            holder.checkBoxForDriverSelection.setImageResource(R.drawable.assign_courier_unselect);

    }

    @Override
    public int getItemCount() {
        return arrayOfCarrierDrivers.size();
    }
}

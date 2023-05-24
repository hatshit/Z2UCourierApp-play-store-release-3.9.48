package com.zoom2u.slidemenu.invoice_adjustment;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zoom2u.R;
import com.zoom2u.utility.Functional_Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mahendra Dabi on 01-03-2023.
 */
public class InvoiceAdjustmentAdapter extends RecyclerView.Adapter<InvoiceAdjustmentAdapter.MyViewHolder> {

    private List<InvoiceItem> list;

    private Functional_Utility functional_utility;


    interface InvoicePagerIndex {
        void callNextPage();
    }

    public InvoiceAdjustmentAdapter() {
        this.list = new ArrayList<>();
    }

    private InvoicePagerIndex pagerIndex;

    public void setPagerIndex(InvoicePagerIndex pagerIndex) {
        this.pagerIndex = pagerIndex;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_invoice_adjustment, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (functional_utility==null)
            functional_utility=new Functional_Utility(holder.itemView.getContext());

        InvoiceItem invoiceItem = list.get(position);

        try {
            holder.tvAmount.setText("$"+invoiceItem.getAmount());
            holder.tvCategory.setText(invoiceItem.getCategory());
            holder.tvCreatedDate.setText(functional_utility.getDateServer(invoiceItem.getCreatedDateTime()));
            holder.tvDescription.setText(invoiceItem.getDescription());
            holder.button_approved.setText(invoiceItem.getStatus());

            holder.itemView.setOnClickListener(v ->
                    holder.itemView.getContext().startActivity(new Intent(holder.itemView.getContext(), InvoiceAdjustmentsDetails.class)
                            .putExtra("item",list.get(position))));

            if (getItemCount() > 5)
            if (position == getItemCount() - 2 && pagerIndex != null)
                pagerIndex.callNextPage();
        } catch (Exception e) {
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvAmount,tvCategory,tvDescription,tvCreatedDate;
        Button button_approved;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvAmount=itemView.findViewById(R.id.tv_amount);
            tvCategory=itemView.findViewById(R.id.tv_category);
            tvCreatedDate=itemView.findViewById(R.id.tv_created_date);
            tvDescription=itemView.findViewById(R.id.tv_desc);
            button_approved=itemView.findViewById(R.id.button_status);
        }
    }

    public void addInvoiceRecords(List<InvoiceItem> items) {

        try {
            if (items != null && items.size() > 0) {
                if (list != null && list.size() == 0) {
                    list.addAll(items);
                    notifyDataSetChanged();
                } else {
                    int lastCount = list.size() ;
                    list.addAll(items);
                    notifyItemRangeInserted(lastCount, list.size() - 1);
                }
            }
        } catch (Exception e) {
        }
    }

    public void clearData() {
        try {
            if (list != null) {
                list.clear();
                notifyDataSetChanged();
            }
        } catch (Exception e) {
        }
    }
}

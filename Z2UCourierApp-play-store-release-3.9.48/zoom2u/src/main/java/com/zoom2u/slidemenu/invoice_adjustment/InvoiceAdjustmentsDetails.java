package com.zoom2u.slidemenu.invoice_adjustment;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zoom2u.R;
import com.zoom2u.utility.Functional_Utility;

import java.io.Serializable;

/**
 * Created by Mahendra Dabi on 01-03-2023.
 */
public class InvoiceAdjustmentsDetails extends Activity implements View.OnClickListener {

    private ImageView backBtnHeader;

    private TextView tvAmount,tvCategory,tvDescription,tvCreatedDate,tvApprovedDate;
    private Button button_approved;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_adustments_details);

        backBtnHeader=findViewById(R.id.backBtnHeader);
        backBtnHeader.setOnClickListener(this);

        tvAmount=findViewById(R.id.tv_amount);
        tvCategory=findViewById(R.id.tv_category);
        tvCreatedDate=findViewById(R.id.tv_created_date);
        tvApprovedDate=findViewById(R.id.tv_approved_date);
        tvDescription=findViewById(R.id.tv_desc);
        button_approved=findViewById(R.id.button_approved);

        boolean hasitem = getIntent().hasExtra("item");

        if (hasitem)
        {
            InvoiceItem invoiceItem =(InvoiceItem) getIntent().getSerializableExtra("item");

            tvAmount.setText("$"+invoiceItem.getAmount());
            tvCategory.setText(invoiceItem.getCategory());

            Functional_Utility functional_utility = new Functional_Utility(this);
            tvCreatedDate.setText(functional_utility.getDateServer(invoiceItem.getCreatedDateTime()));
            tvApprovedDate.setText(functional_utility.getDateServer(invoiceItem.getApprovedDateTime()));

            tvDescription.setText(invoiceItem.getDescription());
            button_approved.setText(invoiceItem.getStatus());
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backBtnHeader:
                onBackPressed();
                break;

        }
    }
}

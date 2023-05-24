package com.zoom2u.slidemenu.invoice_adjustment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.R;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.slidemenu.offerrequesthandlr.DateTimePickerView;
import com.zoom2u.webservice.WebserviceHandler;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Created by Mahendra Dabi on 01-03-2023.
 */
public class InvoiceAdjustments extends Activity implements View.OnClickListener, InvoiceAdjustmentAdapter.InvoicePagerIndex {

    private ImageView backBtnHeader,calendar_filter,img_clear_filter;

    private RecyclerView list_invoice_adjustments;

    private DateTimePickerView dateTimePickerView;

    private ProgressDialog progressDialog;

    private LinearLayout ll_filter;

    private TextView tv_filter_date,tv_no_record;

    private SwipeRefreshLayout swipeRefreshInvoiceList;

    private int pageNumber=1;
    private int pagePerRecord=10;
    private String filterApprovedDate="";

    private int totalCounts=0;

    private InvoiceAdjustmentAdapter invoiceAdjustmentAdapter;

    private String apiResponse;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_invoice_adustments);

        backBtnHeader=findViewById(R.id.backBtnHeader);
        backBtnHeader.setOnClickListener(this);

        calendar_filter=findViewById(R.id.calendar_filter);
        calendar_filter.setOnClickListener(this);

        img_clear_filter=findViewById(R.id.img_clear_filter);
        img_clear_filter.setOnClickListener(this);

        tv_filter_date=findViewById(R.id.tv_filter_date);
        tv_filter_date.setOnClickListener(this);

        ll_filter=findViewById(R.id.ll_filter);
        ll_filter.setOnClickListener(this);

        list_invoice_adjustments=findViewById(R.id.list_invoice_adjustments);
        list_invoice_adjustments.setLayoutManager(new LinearLayoutManager(this));

        swipeRefreshInvoiceList=findViewById(R.id.swipeRefreshInvoiceList);

        tv_no_record=findViewById(R.id.tv_no_record);


        invoiceAdjustmentAdapter = new InvoiceAdjustmentAdapter();
        invoiceAdjustmentAdapter.setPagerIndex(this);
        list_invoice_adjustments.setAdapter(invoiceAdjustmentAdapter);


        progressDialog = new ProgressDialog(this);
        Custom_ProgressDialogBar.inItProgressBar(progressDialog);
        Custom_ProgressDialogBar.dismissProgressBar(progressDialog);

        getInvoiceData(filterApprovedDate,pageNumber);

        swipeRefreshInvoiceList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                img_clear_filter.performClick();
                swipeRefreshInvoiceList.setRefreshing(false);
            }
        });

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

            case R.id.img_clear_filter:
                resetFilter();
                tv_filter_date.setText("");
                ll_filter.setVisibility(View.GONE);
                getInvoiceData(filterApprovedDate,pageNumber);
                break;

            case R.id.calendar_filter:

                try {
                    if (dateTimePickerView!=null&&dateTimePickerView.getDialogDateTimePicker()!=null&&dateTimePickerView.getDialogDateTimePicker().isShowing())
                        dateTimePickerView.getDialogDateTimePicker().dismiss();
                } catch (Exception e) {
                }

                dateTimePickerView = new DateTimePickerView(this, 1);
                dateTimePickerView.setOnDateSelection(new DateTimePickerView.OnDateSelection() {
                    @Override
                    public void onDateSelected(String selectedDate) {
                        resetFilter();
                        try {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy");
                            LocalDate date = LocalDate.parse(selectedDate, formatter);
                            filterApprovedDate=date.toString();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        ll_filter.setVisibility(View.VISIBLE);
                        getInvoiceData(filterApprovedDate,pageNumber);
                    }

                    @Override
                    public void onDismiss() {

                    }
                });

                dateTimePickerView.datePickerDialog(tv_filter_date, null, -1);

                break;

        }
    }

    private void getInvoiceData(String filterApprovedDate,int pageNumber){
       new MyAsyncTasks(){
           @Override
           public void onPreExecute() {
               apiResponse=null;
               if (progressDialog != null) progressDialog.show();
           }

           @Override
           public void doInBackground() {
              apiResponse = new WebserviceHandler().getInvoiceDetails(filterApprovedDate,pageNumber);
               Log.d("invoiceResponse",apiResponse+"");

           }

           @Override
           public void onPostExecute() {
               if (progressDialog != null) progressDialog.dismiss();
               if (apiResponse!=null&&!apiResponse.isEmpty()){
                   try{
                       Gson gson=new Gson();
                       InvoiceItemsResponse invoiceItemsResponse = gson.fromJson(apiResponse, InvoiceItemsResponse.class);
                       if (invoiceItemsResponse!=null) {

                           totalCounts = invoiceItemsResponse.getTotalResultCount();
                           List<InvoiceItem> items = invoiceItemsResponse.getItems();

                           if (items!=null&&items.size()>0) {
                               tv_no_record.setVisibility(View.GONE);
                               invoiceAdjustmentAdapter.addInvoiceRecords(items);
                           }else if (pageNumber==1&&(items==null||items.size()==0)){
                               tv_no_record.setVisibility(View.VISIBLE);
                           }
                       }
                   }catch (Exception ex){
                       Toast.makeText(InvoiceAdjustments.this, "Something went Wrong, try again.", Toast.LENGTH_SHORT).show();
                   }
               }else Toast.makeText(InvoiceAdjustments.this, "Something went Wrong, try again.", Toast.LENGTH_SHORT).show();
           }
       }.execute();
    }

    @Override
    public void callNextPage() {
        if (pageNumber*pagePerRecord<totalCounts)
        getInvoiceData(filterApprovedDate,++pageNumber);
    }

    private void resetFilter(){
        if (invoiceAdjustmentAdapter!=null)
            invoiceAdjustmentAdapter.clearData();

        pageNumber=1;
        totalCounts=0;
        filterApprovedDate="";
    }


}

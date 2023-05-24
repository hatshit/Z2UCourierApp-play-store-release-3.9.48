package com.zoom2u.slidemenu.invoice_adjustment;

import java.util.List;

/**
 * Created by Mahendra Dabi on 02-03-2023.
 */
public class InvoiceItemsResponse {
    public int totalResultCount;
    public List<InvoiceItem> items;

    public int getTotalResultCount() {
        return totalResultCount;
    }

    public void setTotalResultCount(int totalResultCount) {
        this.totalResultCount = totalResultCount;
    }

    public List<InvoiceItem> getItems() {
        return items;
    }

    public void setItems(List<InvoiceItem> items) {
        this.items = items;
    }
}

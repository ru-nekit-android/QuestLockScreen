package ru.nekit.android.qls.parentControl.skulist.row;

import com.android.billingclient.api.SkuDetails;

public class SkuRowData {

    private String sku, title, price, description;

    public SkuRowData(SkuDetails details) {
        this.sku = details.getSku();
        this.title = details.getTitle();
        this.price = details.getPrice();
        this.description = details.getDescription();
    }

    public String getSku() {
        return sku;
    }

    public String getTitle() {
        return title;
    }

    public String getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }
}

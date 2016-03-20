package com.lostfind.DTO;

/**
 * Created by CHANDRASAIMOHAN on 2/27/2016.
 */
public class DataObject {


    private String imageUrl;
    private String itemDescription;
    private String itemStatus;

   public DataObject(String imageUrl,String itemDescription,String itemStatus){
       this.imageUrl = imageUrl;
       this.itemDescription = itemDescription;
       this.itemStatus  = itemStatus;

    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getItemStatus() {
        return itemStatus;
    }

    public void setItemStatus(String itemStatus) {
        this.itemStatus = itemStatus;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }
}

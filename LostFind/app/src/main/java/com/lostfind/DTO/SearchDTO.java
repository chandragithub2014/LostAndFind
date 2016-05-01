package com.lostfind.DTO;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by CHANDRASAIMOHAN on 3/21/2016.
 */
public class SearchDTO  implements Parcelable{
    private String imageURL;
    private String category;
    private String status;
    private String itemDescription;
    private String itemId;
    private String info="";
    private String location="";
    private String email = "";
public SearchDTO(){

}
    private SearchDTO(Parcel in){
        imageURL = in.readString();
        category = in.readString();
        status = in.readString();
        itemDescription = in.readString();
        itemId = in.readString();
    }
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(imageURL);
        out.writeString(category);
        out.writeString(status);
        out.writeString(itemDescription);
        out.writeString(itemId);
    }
    public static final Parcelable.Creator<SearchDTO> CREATOR = new Parcelable.Creator<SearchDTO>() {
        public SearchDTO createFromParcel(Parcel in) {
            return new SearchDTO(in);
        }

        public SearchDTO[] newArray(int size) {
            return new SearchDTO[size];
        }
    };

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return imageURL + ": " + category+ ": " +status+ ": " +itemDescription+ ": " +itemId;
    }
    public SearchDTO(String imageURL,String itemDescription,String status,String itemId,String category){
        this.imageURL = imageURL;
        this.itemDescription = itemDescription;
        this.status  = status;
        this.itemId = itemId;
        this.category = category;

    }
    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

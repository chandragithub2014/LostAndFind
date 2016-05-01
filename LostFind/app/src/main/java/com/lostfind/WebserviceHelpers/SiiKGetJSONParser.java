package com.lostfind.WebserviceHelpers;

import android.content.Context;

import com.lostfind.DTO.SearchDTO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CHANDRASAIMOHAN on 3/21/2016.
 */
public class SiiKGetJSONParser {
    private Context ctx;
    private String response;

    public List<SearchDTO> getParseResponse(Context ctx,String response){
      List<SearchDTO> parsedList = new ArrayList<SearchDTO>();
      try{
          JSONObject responseJSONObject = new JSONObject(response);
          JSONArray responseArray = responseJSONObject.getJSONArray("items");
          for(int i=0;i<responseArray.length();i++){
              JSONObject responseJSON = responseArray.getJSONObject(i);
              SearchDTO temp = new SearchDTO();
              temp.setItemId(""+responseJSON.get("id"));
              temp.setCategory((String) responseJSON.get("category"));
              temp.setImageURL(""+responseJSON.get("imageurl"));
              temp.setItemDescription((String) responseJSON.get("description"));
              temp.setStatus((String) responseJSON.get("status"));
              parsedList.add(temp);
          }
      }catch (JSONException e){
          e.printStackTrace();
      }catch (Exception e){
          e.printStackTrace();
      }
      return  parsedList;
    }

    public SearchDTO getParseResponseForItem(Context ctx,String response){
        SearchDTO temp = new SearchDTO();
        try{
            JSONObject responseJSON = new JSONObject(response);
            temp.setItemId(""+responseJSON.get("id"));
            temp.setCategory((String) responseJSON.get("category"));
            temp.setImageURL("" + responseJSON.get("imageurl"));
            temp.setItemDescription((String) responseJSON.get("description"));
            temp.setStatus((String) responseJSON.get("status"));
            temp.setInfo((String) responseJSON.get("info"));
            temp.setLocation((String) responseJSON.get("location"));
            temp.setEmail((String) responseJSON.get("email"));

        }catch (JSONException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return  temp;
    }


    /*
    {
  "id": 18,
  "email": "arunnda544@gmail.com",
  "description": "testmahesh",
  "category": "Books",
  "from_date": "2016-04-15T00:00:00.000Z",
  "to_date": "2016-03-15T00:00:00.000Z",
  "location": "KetchumIDUnitedStates",
  "imageurl": "http://52.38.114.74:8000/uploads/images/item-1460667167619.png",
  "info": "6dychvkb",
  "status": "lost",
  "created_at": "2016-04-14T20:53:34.000Z"
}
     */
}

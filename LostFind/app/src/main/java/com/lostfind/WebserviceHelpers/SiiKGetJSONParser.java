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
          JSONArray responseArray = new JSONArray(response);
          for(int i=0;i<responseArray.length();i++){
              JSONObject responseJSON = responseArray.getJSONObject(i);
              SearchDTO temp = new SearchDTO();
              temp.setItemId((String)responseJSON.get("id"));
              temp.setCategory((String) responseJSON.get("category"));
              temp.setImageURL((String) responseJSON.get("imageurl"));
              temp.setItemDescription((String) responseJSON.get("description"));
              temp.setStatus((String) responseJSON.get("type"));
              parsedList.add(temp);
          }
      }catch (JSONException e){
          e.printStackTrace();
      }catch (Exception e){
          e.printStackTrace();
      }
      return  parsedList;
    }
}

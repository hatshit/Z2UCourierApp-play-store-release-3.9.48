package com.zoom2u.utility;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.z2u.chat.ChatApplication;
import com.zoom2u.slidemenu.DhlSaveInLocal;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by geet-pc on 28/5/18.
 */

public class App_preference implements Sp_model {
    public static Sp_model INSTANCE = new App_preference();
    private final String PREF_NAME = "eot_pref";
    private final SharedPreferences sp;
    private final SharedPreferences.Editor editor;
    private final String SITECUSTOMFILED = "siteCustomFiled";



    public App_preference() {
        sp = ChatApplication.getInstance().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    public static Sp_model getSharedprefInstance() {
        return INSTANCE;
    }

    @Override
    public ArrayList<DhlSaveInLocal> getDhlBookingFiled() {
        String convert = sp.getString(SITECUSTOMFILED, "");
        Type listType = new TypeToken<List<DhlSaveInLocal>>() {
        }.getType();
        return new Gson().fromJson(convert, listType);
    }

    @Override
    public void setDhlBookingFiled(String customFiled) {
        editor.putString(SITECUSTOMFILED, customFiled);
        editor.commit();
    }

}

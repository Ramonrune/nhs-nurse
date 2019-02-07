package nhsmedic.com.tcc.nhsappmedic.institution.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import nhsmedic.com.tcc.nhsappmedic.login.LoginController;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Usuario on 31/08/2018.
 */

public class HealthInstitutionSharedPreferences {

    private static final String PREFERENCIA = "HEALTH_INSTITUTION";
    private Context context;
    public HealthInstitutionSharedPreferences(Context context){
        this.context = context;
    }

    public void setHealthInstitution(final String healthInstitutionId, String healthInstitutionName, String healthInstitutionPhoto){
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCIA, MODE_PRIVATE).edit();
        editor.putString("healthInstitutionId", healthInstitutionId);
        editor.putString("healthInstitutionName", healthInstitutionName);
        editor.putString("healthInstitutionPhoto", healthInstitutionPhoto);

        editor.apply();




    }


    public void reset(){
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCIA, MODE_PRIVATE).edit();
        editor.putString("healthInstitutionId", null);
        editor.putString("healthInstitutionName", null);
        editor.putString("healthInstitutionPhoto", null);

        editor.apply();
    }

    public String getHealthInstitutionId(){
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFERENCIA, MODE_PRIVATE);
            String healthInstitutionId = prefs.getString("healthInstitutionId", null);
            return healthInstitutionId;
        }catch(Exception e){
            return "";
        }
    }


    public String getHealthInstitutionName(){
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCIA, MODE_PRIVATE);
        String healthInstitutionName = prefs.getString("healthInstitutionName", null);
        return healthInstitutionName;
    }


    public String getHealthInstitutionPhoto(){
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCIA, MODE_PRIVATE);
        String healthInstitutionPhoto = prefs.getString("healthInstitutionPhoto", null);
        return healthInstitutionPhoto;
    }


    public boolean isHealthInstitutionSelected(){
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCIA, MODE_PRIVATE);
        String healthInstitutionId = prefs.getString("healthInstitutionId", null);
        if (healthInstitutionId != null) {
            return true;
        }

        return false;
    }
}



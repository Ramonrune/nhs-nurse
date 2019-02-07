package nhsmedic.com.tcc.nhsappmedic.login;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import nhsmedic.com.tcc.nhsappmedic.R;
import util.Criptography;
import util.Message;
import util.Uri;
import util.Validation;

public class LoginController extends AppCompatActivity {

    private LoginView loginView;
    RequestQueue requestQueue;
    private StringRequest request;

    private Validation validation = new Validation();
    private Uri uri = new Uri();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        inicializeRecurses();
        inicializeListeners();

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle(getString(R.string.alert))
                .setMessage(getString(R.string.infoAbout))
                .setIcon(R.drawable.logo)
                .show();
    }


    private void inicializeListeners() {
        loginView.getLoginButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LinkedHashMap map = new LinkedHashMap<>();
                map.put(loginView.getEmailEditText(), getResources().getString(R.string.valid_email));
                map.put(loginView.getPasswordEditText(), getResources().getString(R.string.valid_password));

                boolean sucesso = validation.validNotNull(map) && validation.validEmail(loginView.getEmailEditText(), getResources().getString(R.string.invalid_email));

                if (sucesso) {



                    final ProgressDialog dialog = ProgressDialog.show(LoginController.this, "",
                            getString(R.string.loginInsideApp), true);
                    dialog.show();

                    request = new StringRequest(Request.Method.POST, uri.UserURI(), new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            dialog.dismiss();
                            try {
                                try {
                                    response = new String(response.getBytes(), "UTF-8");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                System.out.println("Retorno " + response);
                                JSONObject jsonObject = new JSONObject(response);

                                String Token = null, UserName = null, UserType = null, code = null, userId = null;

                                for (int i = 0; i < jsonObject.length(); i++) {
                                    //JSONObject user = jsonObject.getJSONObject(i);
                                    code = jsonObject.getString("code");
                                    if(!code.equals("1")) {
                                        Token = jsonObject.getString("token");
                                        UserName = jsonObject.getString("userName");
                                        UserType = jsonObject.getString("userType");
                                        userId = jsonObject.getString("userId");
                                    }
                                }

                                //||
                                if(code.equals("0")){
                                    if (UserType.equals("4")) {

                                        LoginSharedPreferences loginSharedPreferences = new LoginSharedPreferences(LoginController.this);
                                        loginSharedPreferences.setLoginController(LoginController.this);
                                        loginSharedPreferences.setUserLogged(Token, UserName, userId, dialog);


                                    } else {
                                        Message.showDialog(LoginController.this, getResources().getString(R.string.user_not_found), getResources().getString(R.string.user_not_found_blocked_access));
                                    }
                                }
                                else{
                                    Message.showDialog(LoginController.this, getResources().getString(R.string.user_not_found), getResources().getString(R.string.user_not_found_blocked_access));
                                }




                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            HashMap<String, String> hashMap = new HashMap<String, String>();
                            hashMap.put("_login", loginView.getEmailEditText().getText().toString());
                            hashMap.put("_password", Criptography.sha256(loginView.getPasswordEditText().getText().toString()));
                            System.out.println("map" + hashMap.toString());
                            return hashMap;
                        }
                    };

                    requestQueue.add(request);

                }

            }
        });


    }

    private void inicializeRecurses() {
        loginView = new LoginView(this);
    }

}

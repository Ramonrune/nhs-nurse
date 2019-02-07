package nhsmedic.com.tcc.nhsappmedic;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AlreadyClosedException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import butterknife.BindView;
import butterknife.ButterKnife;
import nhsmedic.com.tcc.nhsappmedic.attendance.AttendanceController;
import nhsmedic.com.tcc.nhsappmedic.home.HomeController;
import nhsmedic.com.tcc.nhsappmedic.home.adapter.HomeAdapter;
import nhsmedic.com.tcc.nhsappmedic.home.model.DiagnosisModel;
import nhsmedic.com.tcc.nhsappmedic.institution.InstitutionSelectController;
import nhsmedic.com.tcc.nhsappmedic.institution.model.HealthInstitutionModel;
import nhsmedic.com.tcc.nhsappmedic.institution.util.HealthInstitutionSharedPreferences;
import nhsmedic.com.tcc.nhsappmedic.login.LoginController;
import nhsmedic.com.tcc.nhsappmedic.login.LoginSharedPreferences;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragmentController extends Fragment {

/*
    @BindView(R.id.nurseNameTextView)
    TextView nurseNameTextView;
    @BindView(R.id.nurseImageView)
    ImageView nurseImageView;
    @BindView(R.id.selectHealthInstitutionImageButton)
   ImageButton selectHealthInstitutionImageButton;

   */

    @BindView(R.id.navigation)
    BottomNavigationView navigation;

    @BindView(R.id.institutionNameTextView)
    TextView institutionNameTextView;

    @BindView(R.id.institutionImageImageView)
    ImageView institutionImageImageView;

    @BindView(R.id.logoffImageImageButton)
    ImageButton logoffImageImageButton;

    public MainFragmentController() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);


        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        LoginSharedPreferences loginSharedPreferences = new LoginSharedPreferences(getContext());


        HealthInstitutionSharedPreferences healthInstitutionSharedPreferences = new HealthInstitutionSharedPreferences(getContext());
        new CallHandler().listHealthInstitutions();


        logoffImageImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),
                        LoginController.class);
                startActivity(intent);
                LoginSharedPreferences loginSharedPreferences = new LoginSharedPreferences(getActivity());
                loginSharedPreferences.reset();


                getActivity().finish();
            }
        });


        //navigation.setSelectedItemId(R.id.navigation_home);

        // Inflate the layout for this fragment
        return view;
    }


    public void attendance() {
        AttendanceController attendanceController = new AttendanceController();
        android.support.v4.app.FragmentManager manager = getChildFragmentManager();

        manager.beginTransaction().replace(R.id.containerFrameLayout, attendanceController, attendanceController.getTag()).commit();

    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getActivity().getApplicationContext().getResources(), result);
            roundedBitmapDrawable.setCircular(true);
            bmImage.setImageDrawable(roundedBitmapDrawable);
            // bmImage.setImageBitmap(result);

        }
    }

    private List<HealthInstitutionModel> healthInstitutionModelList = new ArrayList<>();


    private class CallHandler {

        private void listHealthInstitutions() {
            if (healthInstitutionModelList.isEmpty()) {
                StringRequest request;
                LoginSharedPreferences loginSharedPreferences = new LoginSharedPreferences(getContext());

                RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                request = new StringRequest(Request.Method.GET, "https://webapp-180701221735.azurewebsites.net/webapi/user/healthinstitutionbind/" + loginSharedPreferences.getUserId() + "?status=1", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            try {
                                response = new String(response.getBytes(), "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }

                            JSONObject jsonObject = new JSONObject(response);


                            JSONArray list = jsonObject.getJSONArray("list");


                            for (int i = 0; i < list.length(); i++) {

                                JSONObject json = (JSONObject) list.get(i);


                                HealthInstitutionModel healthInstitutionModel = new HealthInstitutionModel.HealthInstitutionModelBuilder(json.getString("idHealthInstitution"), json.getString("name"))
                                        .state(json.getString("state"))
                                        .photo(json.getString("photo"))
                                        .city(json.getString("city"))
                                        .build();

                                healthInstitutionModelList.add(healthInstitutionModel);


                            }


                            if (healthInstitutionModelList.size() > 1) {
                                InstitutionSelectController institutionSelectController = new InstitutionSelectController();
                                institutionSelectController.showHealthInstitutions(healthInstitutionModelList);
                                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

                                transaction.replace(R.id.containerFrameLayout, institutionSelectController);

                                transaction.commit();

                            } else {


                                institutionNameTextView.setText(healthInstitutionModelList.get(0).getName());
                                new DownloadImageTask(institutionImageImageView)
                                        .execute("https://healthsystem.blob.core.windows.net/healthinstitution/" + healthInstitutionModelList.get(0).getPhoto());


                                HomeController homeController = new HomeController();
                                HealthInstitutionSharedPreferences healthInstitutionSharedPreferences = new HealthInstitutionSharedPreferences(getContext());

                                healthInstitutionSharedPreferences.setHealthInstitution(healthInstitutionModelList.get(0).getIdHealthInstitution(), healthInstitutionModelList.get(0).getName(), healthInstitutionModelList.get(0).getPhoto());

                                android.support.v4.app.FragmentManager manager = getChildFragmentManager();

                                manager.beginTransaction().replace(R.id.containerFrameLayout, homeController, homeController.getTag()).commit();
                            }
//                       / institutionController.showHealthInstitutions(healthInstitutionModelList);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


                        System.out.println("erroooo");
                    }
                }) {

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        LoginSharedPreferences loginSharedPreferences = new LoginSharedPreferences(getContext());
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Accept", "application/json; charset=iso-8859-1");
                        params.put("Authorization", loginSharedPreferences.getToken());
                        return params;
                    }


                };

                System.out.println("======================akalkala");
                requestQueue.add(request);
            }
        }

    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            android.support.v4.app.FragmentManager manager = getChildFragmentManager();
            HealthInstitutionSharedPreferences healthInstitutionSharedPreferences = new HealthInstitutionSharedPreferences(getContext());

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if (healthInstitutionSharedPreferences.isHealthInstitutionSelected()) {
                        HomeController homeController = new HomeController();
                        manager.beginTransaction().replace(R.id.containerFrameLayout, homeController, homeController.getTag()).commit();
                    } else {
                        InstitutionSelectController institutionSelectController = new InstitutionSelectController();
                        institutionSelectController.showHealthInstitutions(healthInstitutionModelList);
                        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

                        transaction.replace(R.id.containerFrameLayout, institutionSelectController);

                        transaction.commit();
                    }

                    //mTextMessage.setText(R.string.title_patients);
                    return true;
                case R.id.navigation_attendance:

                    if (healthInstitutionSharedPreferences.isHealthInstitutionSelected()) {
                        AttendanceController attendanceController = new AttendanceController();
                        manager.beginTransaction().replace(R.id.containerFrameLayout, attendanceController, attendanceController.getTag()).commit();
                    } else {
                        InstitutionSelectController institutionSelectController = new InstitutionSelectController();
                        institutionSelectController.showHealthInstitutions(healthInstitutionModelList);
                        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

                        transaction.replace(R.id.containerFrameLayout, institutionSelectController);

                        transaction.commit();
                    }

                    //mTextMessage.setText(R.string.title_nfcreader);
                    return true;
                case R.id.navigation_institution:
                    InstitutionSelectController institutionSelectController = new InstitutionSelectController();
                    institutionSelectController.showHealthInstitutions(healthInstitutionModelList);
                    FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

                    transaction.replace(R.id.containerFrameLayout, institutionSelectController);

                    transaction.commit();
                    return true;



            }
            return false;
        }

    };


    public void home() {
        HomeController homeController = new HomeController();
        android.support.v4.app.FragmentManager manager = getChildFragmentManager();

        manager.beginTransaction().replace(R.id.containerFrameLayout, homeController, homeController.getTag()).commit();


    }


    public void setHealthInstitution(String name, String photo) {

        institutionNameTextView.setText(name);
        new DownloadImageTask(institutionImageImageView)
                .execute("https://healthsystem.blob.core.windows.net/healthinstitution/" + photo);

    }


    public BottomNavigationView getNavigation() {
        return navigation;
    }


    @Override
    public void onResume() {
        super.onResume();

        HealthInstitutionSharedPreferences healthInstitutionSharedPreferences = new HealthInstitutionSharedPreferences(getContext());

        if (healthInstitutionSharedPreferences.isHealthInstitutionSelected()) {
            setHealthInstitution(healthInstitutionSharedPreferences.getHealthInstitutionName(), healthInstitutionSharedPreferences.getHealthInstitutionPhoto());

        }


    }
}

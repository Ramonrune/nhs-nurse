package nhsmedic.com.tcc.nhsappmedic.procedures;


import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import nhsmedic.com.tcc.nhsappmedic.MainActivity;
import nhsmedic.com.tcc.nhsappmedic.R;
import nhsmedic.com.tcc.nhsappmedic.attendance.model.NurseProcessModel;
import nhsmedic.com.tcc.nhsappmedic.home.HomeController;
import nhsmedic.com.tcc.nhsappmedic.home.model.DiagnosisModel;
import nhsmedic.com.tcc.nhsappmedic.institution.InstitutionSelectController;
import nhsmedic.com.tcc.nhsappmedic.institution.model.HealthInstitutionModel;
import nhsmedic.com.tcc.nhsappmedic.institution.util.HealthInstitutionSharedPreferences;
import nhsmedic.com.tcc.nhsappmedic.login.LoginSharedPreferences;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProcedureController extends Fragment {



    @BindView(R.id.patientImageView)
    ImageView patientImageView;
    @BindView(R.id.patientTextView)
    TextView patientTextView;
    @BindView(R.id.physicianNameTextView)
    TextView physicianNameTextView;
    @BindView(R.id.physicianImageView)
    ImageView physicianImageView;
    @BindView(R.id.diagnosisEditText)
    EditText diagnosisEditText;
    @BindView(R.id.proceduresEditText)
    EditText proceduresEditText;
    @BindView(R.id.finalizeProceduresButton)
    Button finalizeProceduresButton;

    public ProcedureController() {
        // Required empty public constructor
    }

    private CallHandler callHandler = new CallHandler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_procedure_controller, container, false);

        ButterKnife.bind(this, view);


        if(isNew) {

            new DownloadImageTask(patientImageView).execute("https://healthsystem.blob.core.windows.net/userhealth/" + diagnosisModel.getPatientPhoto());

            patientTextView.setText(diagnosisModel.getPatientName());
            diagnosisEditText.setText(diagnosisModel.getAnnotation());

            callHandler.getPhysicianData(diagnosisModel.getIdPhysician());

        }
        else{

            new DownloadImageTask(patientImageView).execute("https://healthsystem.blob.core.windows.net/userhealth/" + nurseProcessModel.getPatientPhoto());

            patientTextView.setText(nurseProcessModel.getPatientName());
            diagnosisEditText.setText(nurseProcessModel.getAnotationDiagnosis());

            proceduresEditText.setText(nurseProcessModel.getAnotationProcedure());
            new DownloadImageTask(physicianImageView).execute("https://healthsystem.blob.core.windows.net/userhealth/" + nurseProcessModel.getPhysicianPhoto());
            physicianNameTextView.setText(nurseProcessModel.getPhysicianName());

        }


        finalizeProceduresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                callHandler.endProcess();
            }
        });

        return view;
    }

    private DiagnosisModel diagnosisModel;

    private String idProcedure = "";
    private boolean isNew = true;
    public void setDiagnosisModel(DiagnosisModel diagnosisModel, String idProcedure) {
        this.diagnosisModel = diagnosisModel;
        this.idProcedure = idProcedure;

        isNew = true;
    }

    private NurseProcessModel nurseProcessModel;

    public void setNurseProcessModel(NurseProcessModel nurseProcessModel) {
        this.nurseProcessModel = nurseProcessModel;
        this.idProcedure = nurseProcessModel.getIdDiagnosisProcedure();
        isNew = false;
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
            if(isAdded()) {
                RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getActivity().getResources(), result);
                roundedBitmapDrawable.setCircular(true);
                bmImage.setImageDrawable(roundedBitmapDrawable);
                // bmImage.setImageBitmap(result);
            }
        }

    }


    private class CallHandler {

        private void getPhysicianData(String idPhysician) {
                StringRequest request;
                LoginSharedPreferences loginSharedPreferences = new LoginSharedPreferences(getContext());

                RequestQueue requestQueue = Volley.newRequestQueue(getContext());

                System.out.println("https://webapp-180701221735.azurewebsites.net/webapi/physician/userdata?id_physician=" + idPhysician);
                request = new StringRequest(Request.Method.GET, "https://webapp-180701221735.azurewebsites.net/webapi/physician/userdata?id_physician=" + idPhysician, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            try {
                                response = new String(response.getBytes(), "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }

                            System.out.println( "asasasasa " + response);

                            JSONObject jsonObject = new JSONObject(response);


                            JSONArray list = jsonObject.getJSONArray("list");


                            for (int i = 0; i < list.length(); i++) {

                                JSONObject json = (JSONObject) list.get(i);
                                new DownloadImageTask(physicianImageView).execute("https://healthsystem.blob.core.windows.net/userhealth/" + json.getString("photo"));
                                physicianNameTextView.setText(json.getString("name"));

                           /*     HealthInstitutionModel healthInstitutionModel = new HealthInstitutionModel.HealthInstitutionModelBuilder(json.getString("idHealthInstitution"), json.getString("name"))
                                        .state(json.getString("state"))
                                        .photo(json.getString("photo"))
                                        .city(json.getString("city"))
                                        .build();

                                healthInstitutionModelList.add(healthInstitutionModel);
*/

                            }



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

                requestQueue.add(request);
            }

        private void update(){


            System.out.println(idProcedure + " ----");
            final ProgressDialog dialog = ProgressDialog.show(getContext(), "",
                    getContext().getString(R.string.saving), true);
            dialog.show();

            RequestQueue requestQueue = Volley.newRequestQueue(getContext());


            StringRequest request = new StringRequest(Request.Method.POST, "https://webapp-180701221735.azurewebsites.net/webapi/nurse/updateAttendance", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    System.out.println(response);
                    dialog.dismiss();

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {


                    System.out.println(error.getMessage());
                }
            }) {


                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<String, String>();
                    params.put("Accept", "application/json; charset=iso-8859-1");
                    params.put("Authorization", new LoginSharedPreferences(getActivity()).getToken());

                    return params;
                }


                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> hashMap = new HashMap<String, String>();

                    LoginSharedPreferences loginSharedPreferences = new LoginSharedPreferences(getActivity());

                    hashMap.put("_id_diagnosis_procedure", idProcedure);
                    hashMap.put("_anotation", proceduresEditText.getText().toString());
                    hashMap.put("_status", "P"); //progress

                    return hashMap;
                }
            };
            request.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            request.setShouldCache(false);
            requestQueue.getCache().clear();
            requestQueue.add(request);

        }


        private void endProcess(){


            System.out.println(idProcedure + " ----");
            final ProgressDialog dialog = ProgressDialog.show(getContext(), "",
                    getContext().getString(R.string.saving), true);
            dialog.show();

            RequestQueue requestQueue = Volley.newRequestQueue(getContext());


            StringRequest request = new StringRequest(Request.Method.POST, "https://webapp-180701221735.azurewebsites.net/webapi/nurse/updateAttendance", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    System.out.println(response);
                    dialog.dismiss();
                    isDone = true;



                    getActivity().onBackPressed();


                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {


                    System.out.println(error.getMessage());
                }
            }) {


                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<String, String>();
                    params.put("Accept", "application/json; charset=iso-8859-1");
                    params.put("Authorization", new LoginSharedPreferences(getActivity()).getToken());

                    return params;
                }


                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> hashMap = new HashMap<String, String>();

                    LoginSharedPreferences loginSharedPreferences = new LoginSharedPreferences(getActivity());

                    hashMap.put("_id_diagnosis_procedure", idProcedure);
                    hashMap.put("_anotation", proceduresEditText.getText().toString());
                    hashMap.put("_status", "F"); //progress

                    return hashMap;
                }
            };
            request.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            request.setShouldCache(false);
            requestQueue.getCache().clear();
            requestQueue.add(request);

        }


    }
    private boolean isDone = false;

    @Override
    public void onPause() {
        super.onPause();
        if(!isDone){
            callHandler.update();

        }



    }
}

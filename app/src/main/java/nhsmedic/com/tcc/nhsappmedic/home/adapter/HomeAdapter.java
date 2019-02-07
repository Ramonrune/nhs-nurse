package nhsmedic.com.tcc.nhsappmedic.home.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

import nhsmedic.com.tcc.nhsappmedic.MainActivity;
import nhsmedic.com.tcc.nhsappmedic.R;
import nhsmedic.com.tcc.nhsappmedic.home.HomeController;
import nhsmedic.com.tcc.nhsappmedic.home.model.DiagnosisModel;
import nhsmedic.com.tcc.nhsappmedic.home.util.ItemViewHolder;
import nhsmedic.com.tcc.nhsappmedic.institution.util.HealthInstitutionSharedPreferences;
import nhsmedic.com.tcc.nhsappmedic.login.LoginController;
import nhsmedic.com.tcc.nhsappmedic.login.LoginSharedPreferences;
import nhsmedic.com.tcc.nhsappmedic.procedures.ProcedureController;
import util.Criptography;
import util.Message;


/**
 * Created by Usuario on 17/05/2018.
 */

public class HomeAdapter extends RecyclerView.Adapter<ItemViewHolder>{

    private Context context;
    private List<DiagnosisModel> diagnosisList = new ArrayList<>();

    public HomeAdapter(Context context){
        this.context = context;

    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.model_patient, viewGroup, false);
        ItemViewHolder holder = new ItemViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, final int position) {
        ItemViewHolder holder = (ItemViewHolder) itemViewHolder;
        final DiagnosisModel diagnosisModel = diagnosisList.get(position);

        holder.getUserNameTextView().setText(diagnosisModel.getPatientName());
        holder.getDateDiagnosisTextView().setText(diagnosisModel.getDateDiagnosis());


        holder.getDateDiagnosisTextView().setText(diagnosisModel.getDateDiagnosis());

        String myStrDate = diagnosisModel.getDateDiagnosis();
        try {
            holder.getDateDiagnosisTextView().setText(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(myStrDate)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.getAttendanceButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    diagnosisModel.getChannel().basicAck(diagnosisModel.getDeliveryTag(), false);
                    callHandler.addToProgressList(diagnosisModel);

                } catch (IOException e) {
                }



            }
        });

        new DownloadImageTask(holder.getUserImageImageView())
                .execute("https://healthsystem.blob.core.windows.net/userhealth/" + diagnosisModel.getPatientPhoto());





    }


    public void clear(){
        diagnosisList.clear();
        notifyDataSetChanged();
    }

    private CallHandler callHandler = new CallHandler();

    @Override
    public int getItemCount() {
        return diagnosisList.size();
    }

    public void add(DiagnosisModel diagnosisModel) {
        diagnosisList.add(diagnosisModel);
        notifyDataSetChanged();
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
            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), result);
            roundedBitmapDrawable.setCircular(true);
            bmImage.setImageDrawable(roundedBitmapDrawable);
            // bmImage.setImageBitmap(result);

        }
    }


    private class CallHandler{


        public void notifyAttendance(final DiagnosisModel diagnosisModel, final ProgressDialog progressDialog, final String uuid){
            RequestQueue requestQueue = Volley.newRequestQueue(context);

            final LoginSharedPreferences loginSharedPreferences = new LoginSharedPreferences(context.getApplicationContext());
            final HealthInstitutionSharedPreferences healthInstitutionSharedPreferences = new HealthInstitutionSharedPreferences(context.getApplicationContext());

            StringRequest request = new StringRequest(Request.Method.POST, "https://webapp-180701221735.azurewebsites.net/webapi/healthinstitution/updatewaitlist/" + healthInstitutionSharedPreferences.getHealthInstitutionId(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    progressDialog.dismiss();
                    System.out.println(response);
                    ProcedureController procedureController = new ProcedureController();
                    procedureController.setDiagnosisModel(diagnosisModel, uuid);


                    android.support.v4.app.FragmentManager manager = ((MainActivity) context).getSupportFragmentManager();


                    manager.beginTransaction().replace(R.id.container, procedureController, procedureController.getTag()).addToBackStack(null).commit();


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    System.out.println("erroooo" + error);
                }

            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Authorization", loginSharedPreferences.getToken());
                    return params;
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

        private void addToProgressList(final DiagnosisModel diagnosisModel){

            final String uuid = UUID.randomUUID().toString();
            final ProgressDialog dialog = ProgressDialog.show(context, "",
                    context.getString(R.string.loading), true);
            dialog.show();

            RequestQueue requestQueue = Volley.newRequestQueue(context);


            StringRequest request = new StringRequest(Request.Method.POST, "https://webapp-180701221735.azurewebsites.net/webapi/nurse/addAttendance", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {

                        System.out.println(response);
                        JSONObject jsonObject = new JSONObject(response);

                        String code = null;

                        for (int i = 0; i < jsonObject.length(); i++) {
                            code = jsonObject.getString("code");
                        }

                        if(code.equals("0")){
                            notifyAttendance(diagnosisModel, dialog, uuid);
                        }


                    }catch(Exception e){

                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {


                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<String, String>();
                    params.put("Accept", "application/json");
                    params.put("Authorization", new LoginSharedPreferences(context).getToken());

                    return params;
                }


                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> hashMap = new HashMap<String, String>();

                    LoginSharedPreferences loginSharedPreferences = new LoginSharedPreferences(context);

                    Date date = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String format = sdf.format(date);
                    hashMap.put("_id_diagnosis_procedure", uuid);
                    hashMap.put("_date_procedure", format);
                    hashMap.put("_anotation", "");
                    hashMap.put("_id_diagnosis", diagnosisModel.getIdDiagnosis());
                    hashMap.put("_id_nurse", loginSharedPreferences.getIdNurse());
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
    }



}
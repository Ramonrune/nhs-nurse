package nhsmedic.com.tcc.nhsappmedic.attendance;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

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

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import nhsmedic.com.tcc.nhsappmedic.R;
import nhsmedic.com.tcc.nhsappmedic.attendance.adapter.AttendanceAdapter;
import nhsmedic.com.tcc.nhsappmedic.attendance.model.NurseProcessModel;
import nhsmedic.com.tcc.nhsappmedic.institution.util.HealthInstitutionSharedPreferences;
import nhsmedic.com.tcc.nhsappmedic.login.LoginSharedPreferences;

import static android.view.View.GONE;

public class AttendanceController extends Fragment {


    @BindView(R.id.attendanceRecyclerView)
    RecyclerView attendanceRecyclerView;


    @BindView(R.id.loadingLinearLayout)
    LinearLayout loadingLinearLayout;

    @BindView(R.id.emptyLinearLayout)
    LinearLayout emptyLinearLayout;


    public AttendanceController() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_attendance_controller, container, false);

        ButterKnife.bind(this, view);

        new CallHandler().loadAttendance();
        // Inflate the layout for this fragment
        return view;
    }


    private class CallHandler {


        public void loadAttendance() {
            RequestQueue requestQueue = Volley.newRequestQueue(getContext());


            final LoginSharedPreferences loginSharedPreferences = new LoginSharedPreferences(getActivity().getApplicationContext());
            final HealthInstitutionSharedPreferences healthInstitutionSharedPreferences = new HealthInstitutionSharedPreferences(getActivity().getApplicationContext());


            StringRequest request = new StringRequest(Request.Method.GET, "https://webapp-180701221735.azurewebsites.net/webapi/nurse/listAttendance?idHealthInstitution=" + healthInstitutionSharedPreferences.getHealthInstitutionId() + "&idNurse=" + loginSharedPreferences.getIdNurse() + "&status=P", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        try {
                            response = new String(response.getBytes(), "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        System.out.println(response);
                        JSONObject jsonObject = new JSONObject(response);

                        List<NurseProcessModel> list = new ArrayList<>();

                        if(jsonObject.getString("code").equals("0")){
                            JSONArray array = jsonObject.getJSONArray("list");

                            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dddd HH:mm");
                            DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                            String formatedDate = null;


                            for(int x = 0; x < array.length(); x++){
                                JSONObject item = array.getJSONObject(x);

                                NurseProcessModel nurseProcessModel = new NurseProcessModel();
                                nurseProcessModel.setAnotationDiagnosis(item.getString("anotationDiagnosis"));
                                nurseProcessModel.setAnotationProcedure(item.getString("anotationProcedure"));
                                nurseProcessModel.setDateDiagnosis(item.getString("dateDiagnosis"));
                                nurseProcessModel.setDateProcedure(item.getString("dateProcedure"));
                                nurseProcessModel.setHealthInstitutionName(item.getString("healthInstitutionName"));
                                nurseProcessModel.setIdHealthInstitution(item.getString("idHealthInstitution"));
                                nurseProcessModel.setIdDiagnosis(item.getString("idDiagnosis"));
                                nurseProcessModel.setIdDiagnosisProcedure(item.getString("idDiagnosisProcedure"));
                                nurseProcessModel.setIdNurse(item.getString("idNurse"));
                                nurseProcessModel.setNurseName(item.getString("nurseName"));
                                nurseProcessModel.setNursePhoto(item.getString("nursePhoto"));
                                nurseProcessModel.setPatientName(item.getString("patientName"));
                                nurseProcessModel.setPatientPhoto(item.getString("patientPhoto"));
                                nurseProcessModel.setPhysicianName(item.getString("physicianName"));
                                nurseProcessModel.setPhysicianPhoto(item.getString("physicianPhoto"));
                                nurseProcessModel.setStatus(item.getString("status"));


                                list.add(nurseProcessModel);

                            }
                        }


                        if(list.size() == 0){
                            loadingLinearLayout.setVisibility(GONE);
                            attendanceRecyclerView.setVisibility(View.GONE);

                            emptyLinearLayout.setVisibility(View.VISIBLE);
                        }
                        else{
                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                            attendanceRecyclerView.setLayoutManager(layoutManager);



                            attendanceRecyclerView.setAdapter(new AttendanceAdapter(AttendanceController.this, list));
                            loadingLinearLayout.setVisibility(GONE);

                            attendanceRecyclerView.setVisibility(View.VISIBLE);
                            emptyLinearLayout.setVisibility(View.GONE);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

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
                    params.put("Accept", "application/json; charset=iso-8859-1");
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

    }


}

package nhsmedic.com.tcc.nhsappmedic.attendance.adapter;

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

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import nhsmedic.com.tcc.nhsappmedic.MainActivity;
import nhsmedic.com.tcc.nhsappmedic.R;
import nhsmedic.com.tcc.nhsappmedic.attendance.AttendanceController;
import nhsmedic.com.tcc.nhsappmedic.attendance.model.NurseProcessModel;
import nhsmedic.com.tcc.nhsappmedic.attendance.util.ItemViewHolder;
import nhsmedic.com.tcc.nhsappmedic.home.model.DiagnosisModel;
import nhsmedic.com.tcc.nhsappmedic.institution.util.HealthInstitutionSharedPreferences;
import nhsmedic.com.tcc.nhsappmedic.login.LoginSharedPreferences;
import nhsmedic.com.tcc.nhsappmedic.procedures.ProcedureController;


/**
 * Created by Usuario on 17/05/2018.
 */

public class AttendanceAdapter extends RecyclerView.Adapter<ItemViewHolder>{

    private List<NurseProcessModel> nurseProcessModelList;
    private AttendanceController attendanceController;

    public AttendanceAdapter(AttendanceController attendanceController, List<NurseProcessModel> list){
        this.nurseProcessModelList = list;
        this.attendanceController = attendanceController;

    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(attendanceController.getActivity()).inflate(R.layout.model_patient, viewGroup, false);
        ItemViewHolder holder = new ItemViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, final int position) {
        ItemViewHolder holder = (ItemViewHolder) itemViewHolder;
        final NurseProcessModel nurseProcessModel = nurseProcessModelList.get(position);

        holder.getUserNameTextView().setText(nurseProcessModel.getPatientName());
        holder.getDateDiagnosisTextView().setText(nurseProcessModel.getDateDiagnosis());


        holder.getDateDiagnosisTextView().setText(nurseProcessModel.getDateDiagnosis());

        String myStrDate = nurseProcessModel.getDateDiagnosis();
        try {
            holder.getDateDiagnosisTextView().setText(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(myStrDate)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.getContinueButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ProcedureController procedureController = new ProcedureController();
                procedureController.setNurseProcessModel(nurseProcessModel);


                android.support.v4.app.FragmentManager manager = attendanceController.getActivity().getSupportFragmentManager();


                manager.beginTransaction().replace(R.id.container, procedureController, procedureController.getTag()).addToBackStack(null).commit();


                /*try {
                    diagnosisModel.getChannel().basicAck(diagnosisModel.getDeliveryTag(), false);

                    callHandler.addToProgressList(diagnosisModel);
                } catch (IOException e) {
                    e.printStackTrace();
                }
*/

            }
        });

        new DownloadImageTask(holder.getUserImageImageView())
                .execute("https://healthsystem.blob.core.windows.net/userhealth/" + nurseProcessModel.getPatientPhoto());





    }


    public void clear(){
        nurseProcessModelList.clear();
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return nurseProcessModelList.size();
    }

    public void add(NurseProcessModel nurseProcessModel) {
        nurseProcessModelList.add(nurseProcessModel);
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
            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(attendanceController.getContext().getResources(), result);
            roundedBitmapDrawable.setCircular(true);
            bmImage.setImageDrawable(roundedBitmapDrawable);
            // bmImage.setImageBitmap(result);

        }
    }





}
package nhsmedic.com.tcc.nhsappmedic.institution.adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import nhsmedic.com.tcc.nhsappmedic.MainActivity;
import nhsmedic.com.tcc.nhsappmedic.MainFragmentController;
import nhsmedic.com.tcc.nhsappmedic.R;
import nhsmedic.com.tcc.nhsappmedic.home.HomeController;
import nhsmedic.com.tcc.nhsappmedic.home.adapter.HomeAdapter;
import nhsmedic.com.tcc.nhsappmedic.institution.model.HealthInstitutionModel;
import nhsmedic.com.tcc.nhsappmedic.institution.util.HealthInstitutionSharedPreferences;
import nhsmedic.com.tcc.nhsappmedic.institution.util.ItemViewHolder;
import util.Message;

/**
 * Created by Usuario on 17/05/2018.
 */

public class InstitutionAdapter extends RecyclerView.Adapter<ItemViewHolder>{

    private Context context;
    private List<HealthInstitutionModel> institutionList;

    public InstitutionAdapter(Context context, List<HealthInstitutionModel> institutionList){
        this.context = context;
        this.institutionList = institutionList;

    }



    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.model_institution, viewGroup, false);
        ItemViewHolder holder = new ItemViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, final int position) {
        ItemViewHolder holder = (ItemViewHolder) itemViewHolder;
        final HealthInstitutionModel institutionModel = institutionList.get(position);


        holder.getInstitutionNameTextView().setText(institutionModel.getName());

        StringBuilder addressBuilder = new StringBuilder();

        addressBuilder.append(institutionModel.getCity() + ", " + institutionModel.getState());
        holder.getAddressTextView().setText(addressBuilder.toString());
     /*   holder.getCityTextView().setText(institutionModel.getCity());
        holder.getStreetTextView().setText(institutionModel.getStreet());
        holder.getNumberTextView().setText(institutionModel.getNumber());
        holder.getNeighborhoodTextView().setText(institutionModel.getNeightborhood());
        holder.getTelephoneTextView().setText(institutionModel.getTelephone());
*/
        holder.getSelectHealthInstitutionButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HealthInstitutionSharedPreferences healthInstitutionSharedPreferences = new HealthInstitutionSharedPreferences((MainActivity) context);
                healthInstitutionSharedPreferences.setHealthInstitution(institutionModel.getIdHealthInstitution(), institutionModel.getName(), institutionModel.getPhoto());

                ((MainActivity) context).getMainFragmentController().setHealthInstitution(institutionModel.getName(), institutionModel.getPhoto());

                View view = ((MainActivity) context).getMainFragmentController().getNavigation().findViewById(R.id.navigation_home);

                view.performClick();

            }
        });

        new DownloadImageTask(holder.getInstitutionImageImageView())
                .execute("https://healthsystem.blob.core.windows.net/healthinstitution/" + institutionModel.getPhoto());


    }



    @Override
    public int getItemCount() {
        return institutionList.size();
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
}
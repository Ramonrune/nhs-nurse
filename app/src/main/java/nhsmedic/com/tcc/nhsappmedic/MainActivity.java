package nhsmedic.com.tcc.nhsappmedic;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import nhsmedic.com.tcc.nhsappmedic.attendance.AttendanceController;
import nhsmedic.com.tcc.nhsappmedic.home.HomeController;
import nhsmedic.com.tcc.nhsappmedic.institution.InstitutionSelectController;
import nhsmedic.com.tcc.nhsappmedic.institution.model.HealthInstitutionModel;
import nhsmedic.com.tcc.nhsappmedic.institution.util.HealthInstitutionSharedPreferences;
import nhsmedic.com.tcc.nhsappmedic.login.LoginController;
import nhsmedic.com.tcc.nhsappmedic.login.LoginSharedPreferences;

public class MainActivity extends AppCompatActivity {


    private MainFragmentController mainFragmentController = new MainFragmentController();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nurse_activity_main);
        ButterKnife.bind(this);


        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.container, mainFragmentController);

        transaction.commit();


    }


    public MainFragmentController getMainFragmentController() {
        return mainFragmentController;
    }

    @Override
    protected void onResume() {
        super.onResume();

        System.out.println("resumo");
    }


    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("iniciou");
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        System.out.println("restartou");
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {


            if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                getSupportFragmentManager().popBackStack();

                try {
                    Fragment fragment = getSupportFragmentManager().getFragments().get(0);


                    HealthInstitutionSharedPreferences healthInstitutionSharedPreferences = new HealthInstitutionSharedPreferences(this);

                    mainFragmentController.setHealthInstitution(healthInstitutionSharedPreferences.getHealthInstitutionName(), healthInstitutionSharedPreferences.getHealthInstitutionPhoto());


                    if (fragment instanceof HomeController) {

                        //((MainFragmentController)fragment).getNavigation().setSelectedItemId(R.id.navigation_home);
                        mainFragmentController.home();
                    }

                    if (fragment instanceof AttendanceController) {


                        System.out.println("aaqqqqqq");
                        mainFragmentController.attendance();
                        //((MainFragmentController)fragment).getNavigation().setSelectedItemId(R.id.navigation_attendance);

                    }

                }catch(Exception e){

                }

            }

            System.out.println("=======");


        } else {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    this);

            alertDialogBuilder.setTitle(getString(R.string.logoff));

            alertDialogBuilder
                    .setMessage(getString(R.string.logoffMessage))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.yesOption), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            Intent intent = new Intent(MainActivity.this,
                                    LoginController.class);
                            startActivity(intent);
                            LoginSharedPreferences loginSharedPreferences = new LoginSharedPreferences(getApplicationContext());
                            loginSharedPreferences.reset();


                            finish();

                        }
                    })
                    .setNegativeButton(getString(R.string.noOption), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // se não for precionado ele apenas termina o dialog
                            // e fecha a janelinha
                            dialog.cancel();
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();

            alertDialog.show();
        }


    }
}

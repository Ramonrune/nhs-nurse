package nhsmedic.com.tcc.nhsappmedic;

import android.content.Intent;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import nhsmedic.com.tcc.nhsappmedic.institution.util.HealthInstitutionSharedPreferences;
import nhsmedic.com.tcc.nhsappmedic.login.LoginController;
import nhsmedic.com.tcc.nhsappmedic.login.LoginSharedPreferences;

public class SplashScreen extends AppCompatActivity {


    @BindView(R.id.loadingSplashImageView)
    ImageView loadingSplashImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_splash_screen);


        ButterKnife.bind(this);



        Handler handle = new Handler();
        handle.postDelayed(new Runnable() {
            @Override
            public void run() {
                mostrarLogin();
            }
        }, 1000);
    }

    private void mostrarLogin(){
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.scale_out);

        loadingSplashImageView.startAnimation(animation);

        LoginSharedPreferences loginSharedPreferences = new LoginSharedPreferences(this);

        if(loginSharedPreferences.userLoggedIn()){
            Intent intent = new Intent(SplashScreen.this, MainActivity.class);

            HealthInstitutionSharedPreferences healthInstitutionSharedPreferences = new HealthInstitutionSharedPreferences(this);
            healthInstitutionSharedPreferences.reset();
            startActivity(intent);
            finish();
        }
        else{
            Intent intent = new Intent(SplashScreen.this, LoginController.class);
            startActivity(intent);
            finish();
        }

    }
}

package nhsmedic.com.tcc.nhsappmedic.login;

import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import nhsmedic.com.tcc.nhsappmedic.R;

/**
 * Created by Leonardo on 23/07/2018.
 */

public class LoginView {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;


    public LoginView(AppCompatActivity view){
        view.setContentView(R.layout.activity_login_controller);
        view.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        emailEditText = (EditText) view.findViewById(R.id.emailEditText);
        passwordEditText = (EditText) view.findViewById(R.id.passwordEditText);
        loginButton = (Button) view.findViewById(R.id.loginButton);



    }

    public EditText getEmailEditText() {
        return emailEditText;
    }

    public EditText getPasswordEditText() {
        return passwordEditText;
    }

    public Button getLoginButton() {
        return loginButton;
    }
}

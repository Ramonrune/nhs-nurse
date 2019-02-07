package util;

import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Leonardo on 23/07/2018.
 */

public class Validation {

    public boolean validNotNull(LinkedHashMap<EditText, String> hashEditTexts) {
        boolean success = true;
        for (Map.Entry<EditText, String> entrada : hashEditTexts.entrySet()) {
            EditText key = entrada.getKey();
            String value = key.getText().toString();
            if (value != null) {
                if (TextUtils.isEmpty(value.toString())) {
                    key.setError(entrada.getValue());
                    success = false;
                    break;
                }

            } else {
                success = false;
                break;
            }
        }

        return success;
    }

    public boolean validEmail(EditText emailEditText, String message) {
        String value = emailEditText.getText().toString();
        if (value != null) {

            if ((!TextUtils.isEmpty(value) && Patterns.EMAIL_ADDRESS.matcher(value).matches())) {
                return true;
            }

            emailEditText.setError(message);
            return false;

        } else {
            return false;
        }
    }

}

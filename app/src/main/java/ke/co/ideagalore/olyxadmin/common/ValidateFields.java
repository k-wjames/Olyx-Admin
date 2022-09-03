package ke.co.ideagalore.olyxadmin.common;

import android.app.Activity;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

public class ValidateFields {
    public boolean validateEditTextFields(Activity activity, EditText editText, String message) {
        String input = editText.getText().toString();
        if (!input.isEmpty()) {
            return true;
        } else {

            Toast.makeText(activity, message + " field cannot be empty.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public boolean validateEmailAddress(Activity activity, EditText editText) {
        String email = editText.getText().toString();
        if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return true;
        } else if (!email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(activity, "Please use a valid email address.", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            Toast.makeText(activity, "Email field cannot be empty.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}

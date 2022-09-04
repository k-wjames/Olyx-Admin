package ke.co.ideagalore.olyxadmin.common;

import android.app.Activity;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

public class ValidateFields {

    CustomDialogs customDialogs=new CustomDialogs();

    public boolean validateEditTextFields(Activity activity, EditText editText, String message) {
        String input = editText.getText().toString();
        if (!input.isEmpty()) {
            return true;
        } else {

            customDialogs.showSnackBar(activity,message + " field cannot be empty.");
            return false;
        }
    }

    public boolean validateEmailAddress(Activity activity, EditText editText) {
        String email = editText.getText().toString();
        if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return true;
        } else if (!email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            customDialogs.showSnackBar(activity,"Please use a valid email address.");
            return false;
        } else {
            customDialogs.showSnackBar(activity,"Email field cannot be empty.");
            return false;
        }
    }

}

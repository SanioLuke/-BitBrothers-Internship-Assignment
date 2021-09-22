package com.sanioluke00.bitbrothersassignment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("all")
public class SignUpPageActivity extends AppCompatActivity {

    private final String email_expn =
            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";
    DataFunctions dataFunctions = new DataFunctions();
    private TextInputLayout signup_fname, signup_lname, signup_emailid, signup_passwrd;
    private ImageButton signup_signbtn;
    private TextView signup_loginbtn;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

        signUpPageInits();

        signup_loginbtn.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), LoginPageActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        signup_signbtn.setOnClickListener(v -> {
            String fname_txt = signup_fname.getEditText().getText().toString();
            String lname_txt = signup_lname.getEditText().getText().toString();
            String email_txt = signup_emailid.getEditText().getText().toString();
            String pass_txt = signup_passwrd.getEditText().getText().toString();

            if (fname_txt.isEmpty()) {
                signup_fname.getEditText().requestFocus();
                signup_fname.getEditText().setError("Can't be empty !!");
            } else if (fname_txt.length() < 2) {
                signup_fname.getEditText().requestFocus();
                signup_fname.getEditText().setError("Please enter first name with more than 2 letters");
            } else if (lname_txt.isEmpty()) {
                signup_lname.getEditText().requestFocus();
                signup_lname.getEditText().setError("Can't be empty !!");
            } else if (lname_txt.length() < 2) {
                signup_lname.getEditText().requestFocus();
                signup_lname.getEditText().setError("Please enter last name with more than 2 letters");
            } else if (email_txt.isEmpty()) {
                signup_emailid.getEditText().requestFocus();
                signup_emailid.getEditText().setError("Can't be empty !!");
            } else if (!email_txt.matches(email_expn)) {
                signup_emailid.getEditText().requestFocus();
                signup_emailid.getEditText().setError("Please enter a valid email ID !!");
            } else if (pass_txt.isEmpty()) {
                signup_passwrd.getEditText().requestFocus();
                signup_passwrd.getEditText().setError("Can't be empty !!");
            } else if (!dataFunctions.check_net_connection(getApplicationContext())) {
                Dialog dialog = dataFunctions.createDialogBox(SignUpPageActivity.this, R.layout.no_net_dialog, false);
                TextView msg_okbtn = dialog.findViewById(R.id.msg_okbtn);
                dialog.show();
                msg_okbtn.setOnClickListener(v1 -> dialog.dismiss());
            } else {
                Dialog dialog = dataFunctions.createDialogBox(SignUpPageActivity.this, R.layout.loading_dialog, false);
                TextView loading_txt = dialog.findViewById(R.id.loading_txt);
                dialog.show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        firebaseAuth.createUserWithEmailAndPassword(email_txt, pass_txt)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {

                                        Map<Object, String> map = new HashMap<>();
                                        map.put("firstname", fname_txt);
                                        map.put("lastname", lname_txt);
                                        map.put("email_id", email_txt);

                                        db.collection("UserData")
                                                .document(task.getResult().getUser().getUid())
                                                .set(map)
                                                .addOnCompleteListener(task1 -> {

                                                    if (task1.isSuccessful()) {
                                                        loading_txt.setText("SignUp Successful...");

                                                        dataFunctions.putSharedPrefsValue(SignUpPageActivity.this, "get_user_data", "firstname", "string", fname_txt);
                                                        dataFunctions.putSharedPrefsValue(SignUpPageActivity.this, "get_user_data", "lastname", "string", lname_txt);
                                                        dataFunctions.putSharedPrefsValue(SignUpPageActivity.this, "get_user_data", "email_id", "string", email_txt);

                                                        new Handler().postDelayed(() -> {

                                                            loading_txt.setText("Going to home page.....");
                                                            signup_fname.getEditText().setText("");
                                                            signup_fname.clearFocus();
                                                            signup_fname.setErrorEnabled(false);

                                                            signup_lname.getEditText().setText("");
                                                            signup_lname.clearFocus();
                                                            signup_lname.setErrorEnabled(false);

                                                            signup_emailid.getEditText().setText("");
                                                            signup_emailid.clearFocus();
                                                            signup_emailid.setErrorEnabled(false);

                                                            signup_passwrd.getEditText().setText("");
                                                            signup_passwrd.clearFocus();
                                                            signup_passwrd.setErrorEnabled(false);

                                                            new Handler().postDelayed(() -> {
                                                                dialog.dismiss();
                                                                startActivity(new Intent(getApplicationContext(), HomePageActivity.class));
                                                            }, 1000);

                                                        }, 1000);
                                                    } else {
                                                        loading_txt.setText("SignUp Failed !!!");
                                                        new Handler().postDelayed(dialog::cancel, 1000);
                                                        dataFunctions.putSharedPrefsValue(SignUpPageActivity.this, "get_user_data", "firstname", "string", fname_txt);
                                                        dataFunctions.putSharedPrefsValue(SignUpPageActivity.this, "get_user_data", "lastname", "string", lname_txt);
                                                        dataFunctions.putSharedPrefsValue(SignUpPageActivity.this, "get_user_data", "email_id", "string", email_txt);
                                                        Toast.makeText(SignUpPageActivity.this, "SignUp failed due to : " + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                                                    }
                                                });
                                    } else {
                                        dialog.cancel();
                                        Toast.makeText(SignUpPageActivity.this, "Unable to register to account due to \n" + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }, 1000);

            }
        });
    }

    private void signUpPageInits() {
        signup_fname = findViewById(R.id.signup_fname);
        signup_lname = findViewById(R.id.signup_lname);
        signup_emailid = findViewById(R.id.signup_emailid);
        signup_passwrd = findViewById(R.id.signup_passwrd);
        signup_signbtn = findViewById(R.id.signup_signbtn);
        signup_loginbtn = findViewById(R.id.signup_loginbtn);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }
}
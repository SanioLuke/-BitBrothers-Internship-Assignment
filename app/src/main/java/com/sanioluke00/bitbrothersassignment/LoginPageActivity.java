package com.sanioluke00.bitbrothersassignment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

@SuppressWarnings("all")
public class LoginPageActivity extends AppCompatActivity {

    private final String email_expn =
            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";
    View loginpage_mainlay;
    DataFunctions dataFunctions = new DataFunctions();
    private TextInputLayout loginpage_emailid, loginpage_passwrd;
    private ImageButton loginpage_logbtn;
    private TextView loginpage_signupbtn;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        loginpageInits();

        loginpage_signupbtn.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), SignUpPageActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        loginpage_logbtn.setOnClickListener(v -> {

            String emailid_txt = loginpage_emailid.getEditText().getText().toString();
            String pass_txt = loginpage_passwrd.getEditText().getText().toString();

            if (emailid_txt.isEmpty()) {
                loginpage_emailid.getEditText().requestFocus();
                loginpage_emailid.getEditText().setError("Can't be empty !");
            } else if (!emailid_txt.matches(email_expn)) {
                loginpage_emailid.getEditText().requestFocus();
                loginpage_emailid.getEditText().setError("Please enter a valid email ID !!");
            } else if (pass_txt.isEmpty()) {
                loginpage_passwrd.getEditText().requestFocus();
                loginpage_passwrd.getEditText().setError("Can't be empty !");
            } else if (!dataFunctions.check_net_connection(getApplicationContext())) {
                Dialog dialog = dataFunctions.createDialogBox(LoginPageActivity.this, R.layout.no_net_dialog, false);
                TextView msg_okbtn = dialog.findViewById(R.id.msg_okbtn);
                dialog.show();
                msg_okbtn.setOnClickListener(v1 -> dialog.dismiss());
            } else {
                Dialog dialog = dataFunctions.createDialogBox(LoginPageActivity.this, R.layout.loading_dialog, false);
                dialog.show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        firebaseAuth.signInWithEmailAndPassword(emailid_txt, pass_txt)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {

                                        loginpage_emailid.getEditText().setText("");
                                        loginpage_emailid.clearFocus();
                                        loginpage_emailid.setErrorEnabled(false);

                                        loginpage_passwrd.getEditText().setText("");
                                        loginpage_passwrd.clearFocus();
                                        loginpage_passwrd.setErrorEnabled(false);

                                        db.collection("UserData")
                                                .document(task.getResult().getUser().getUid())
                                                .get()
                                                .addOnCompleteListener(task1 -> {
                                                    if (task1.isSuccessful()) {
                                                        Log.e("log_data", "Success");
                                                        Log.e("log_userdata", "The email ID is " + task1.getResult().getString("email_id"));
                                                        Log.e("log_userdata", "The firtname is " + task1.getResult().getString("firstname"));
                                                        Log.e("log_userdata", "The lastname is " + task1.getResult().getString("lastname"));

                                                        dataFunctions.putSharedPrefsValue(LoginPageActivity.this, "get_user_data", "firstname", "string", task1.getResult().getString("firstname"));
                                                        dataFunctions.putSharedPrefsValue(LoginPageActivity.this, "get_user_data", "lastname", "string", task1.getResult().getString("lastname"));
                                                        dataFunctions.putSharedPrefsValue(LoginPageActivity.this, "get_user_data", "email_id", "string", task1.getResult().getString("email_id"));
                                                        dialog.dismiss();
                                                        startActivity(new Intent(getApplicationContext(), HomePageActivity.class));
                                                    } else {
                                                        dialog.dismiss();
                                                        Log.e("log_data", "Failed!!" + task1.getException().getMessage());
                                                        Toast.makeText(LoginPageActivity.this, "Login Failed due to : " + task1.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                        dataFunctions.putSharedPrefsValue(LoginPageActivity.this, "get_user_data", "firstname", "string", null);
                                                        dataFunctions.putSharedPrefsValue(LoginPageActivity.this, "get_user_data", "lastname", "string", null);
                                                        dataFunctions.putSharedPrefsValue(LoginPageActivity.this, "get_user_data", "email_id", "string", null);
                                                        if (firebaseAuth.getCurrentUser() != null) {
                                                            firebaseAuth.signOut();
                                                        }
                                                    }
                                                });

                                    } else {
                                        dialog.cancel();
                                        Toast.makeText(LoginPageActivity.this, "Unable to connect to account due to \n" + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }, 1000);
            }
        });

    }

    private void loginpageInits() {
        loginpage_mainlay = findViewById(R.id.loginpage_mainlay);
        loginpage_emailid = findViewById(R.id.loginpage_emailid);
        loginpage_passwrd = findViewById(R.id.loginpage_passwrd);
        loginpage_logbtn = findViewById(R.id.loginpage_logbtn);
        loginpage_signupbtn = findViewById(R.id.loginpage_signupbtn);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}
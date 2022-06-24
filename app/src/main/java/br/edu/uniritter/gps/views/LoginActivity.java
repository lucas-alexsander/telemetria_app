package br.edu.uniritter.gps.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import br.edu.uniritter.atsd.gps.R;

public class LoginActivity extends AppCompatActivity {
    private static final  String TAG = "LoginActivity";
    private FirebaseAuth mAuth;
    //"atsd@uniritter.edu.br", "123456"
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Initialize Firebase Auth
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        Intent intentMain = new Intent(this, MainActivity.class);
        Intent intentMainAdmin = new Intent(this, MainActivityAdmin.class);
        Switch swLogin = (Switch) findViewById(R.id.swAdmin);
        EditText etEmail = (EditText) findViewById(R.id.etEmail);
        EditText etSenha = (EditText) findViewById(R.id.etSenha);

        Button btLogin = (Button) findViewById(R.id.btLogin);
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(swLogin.isChecked()){
                    //LOGIN ADMIN SCREEN
                    //TODO: VALIDAÇÃO USUARIO POSSUI ADMIN
                    login(intentMainAdmin, etEmail.getText().toString(), etSenha.getText().toString() );
                }
                else{
                    login(intentMain, etEmail.getText().toString(), etSenha.getText().toString() );
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        Intent intent = new Intent(this, MainActivity.class);
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            startActivity(intent);
        }
    }


    private void login(Intent intent, String etEmail, String etSenha){



        if(etEmail.isEmpty()){
            Toast.makeText(getApplicationContext(), "Authentication failed. Email Required",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if(etSenha.isEmpty()){
            Toast.makeText(getApplicationContext(), "Authentication failed. Senha Required",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.signInWithEmailAndPassword(etEmail, etSenha)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);


                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
    }
}
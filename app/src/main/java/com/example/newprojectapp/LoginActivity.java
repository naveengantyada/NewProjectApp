package com.example.newprojectapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newprojectapp.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private FirebaseUser currentUser;
    private EditText emaillogin,pswrdLogin;
    private TextView forgetPswrd,needAccount;
    private Button loginBtn,loginPhone;
    private FirebaseAuth mAuth;     private DatabaseReference rootRef;

    private  com.rey.material.widget.CheckBox checkBox;
    private ProgressDialog loadingbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        rootRef= FirebaseDatabase.getInstance().getReference();
        needAccount=(TextView)findViewById(R.id.need_new_acnt);
        emaillogin=(EditText)findViewById(R.id.login_email);
        pswrdLogin=(EditText)findViewById(R.id.login_pswrd);
        loginBtn=(Button)findViewById(R.id.login_btn);
        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
     //   checkBox=( com.rey.material.widget.CheckBox)findViewById(R.id.remembe_me_check);
        Paper.init(this);

        loadingbar=new ProgressDialog(this);
         loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alloUserToLogin();
            }
        });


        needAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToRegisterActivity();
            }
        });

    }


    private void alloUserToLogin() {
        String email=emaillogin.getText().toString();
        String pswrd=pswrdLogin.getText().toString();
        if(TextUtils.isEmpty(email) && TextUtils.isEmpty(pswrd)){
            Toast.makeText(this, "Please write your mailid and Password", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please write your MailId", Toast.LENGTH_SHORT).show();
        } else if(TextUtils.isEmpty(pswrd)){
            Toast.makeText(this, "Please write your Password", Toast.LENGTH_SHORT).show();
        } else if(!(email.contains("gmail.com"))) {
        //    emaillogin.setError("Not Found");
            Toast.makeText(this, "Enter your Correct Email address", Toast.LENGTH_SHORT).show();
        } else
        {
            loadingbar.setTitle("Sign in");
            loadingbar.setMessage("Please wait...");
            loadingbar.setCanceledOnTouchOutside(true);
//            if(checkBox.isChecked()){
//                Paper.book().write(Prevalent.usersPhoneKey,email);
//                Paper.book().write(Prevalent.usersPasswordKey,pswrd);
//            }
            mAuth.signInWithEmailAndPassword(email,pswrd)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                if(mAuth.getCurrentUser().isEmailVerified()){
                                    String currentUserid=mAuth.getCurrentUser().getUid();
                                    rootRef.child("Users").child(currentUserid).setValue("");
                                    sendUserToMainActivity();
                                    Toast.makeText(LoginActivity.this, "Loggedin successfully..", Toast.LENGTH_SHORT).show();
                                    loadingbar.dismiss();
                                }else{
                                    Toast.makeText(LoginActivity.this, "Please verify your mail id", Toast.LENGTH_SHORT).show();
                                    loadingbar.dismiss();
                                }

                            }else{
                                try {
                                    throw task.getException();
                                }  catch (FirebaseNetworkException e){
                                    Toast.makeText(LoginActivity.this, "Check your internet Connection", Toast.LENGTH_SHORT).show();
                                }catch (FirebaseAuthInvalidUserException e){
                                  emaillogin.setError("Not Found");
                                    Toast.makeText(LoginActivity.this, "you have not registered with this mail address", Toast.LENGTH_SHORT).show();
                                    emaillogin.requestFocus();
                                }catch (FirebaseAuthInvalidCredentialsException e){
                                    Toast.makeText(LoginActivity.this, "Enter your correct password", Toast.LENGTH_SHORT).show();
                                    pswrdLogin.setError("Wrong password");
                                 pswrdLogin.requestFocus();
                                }

                                 catch(Exception e) {
                                    e.printStackTrace();
                                }
                                loadingbar.dismiss();
                            }
                        }
                    });loadingbar.show();
        }}



    private void sendUserToMainActivity() {
        Intent mintent=new Intent(LoginActivity.this,MainActivity.class);
        mintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mintent);
        finish();
    }

    private void sendUserToRegisterActivity() {

        Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(intent);
    }
    public void reset(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = LayoutInflater.from(this).inflate(R.layout.reset,null,false);
        final EditText mail = v.findViewById(R.id.remail);
        builder.setView(v);
        builder.setCancelable(false);
        builder.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String umail = mail.getText().toString();
                if (TextUtils.isEmpty(umail)){
                    Toast.makeText(LoginActivity.this, "mail cant be empty",
                            Toast.LENGTH_SHORT).show();
                }else{
                mAuth.sendPasswordResetEmail(umail).addOnCompleteListener(LoginActivity.this,
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(LoginActivity.this, "Mail sent",
                                            Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(LoginActivity.this, "failed to send", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });}
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

}
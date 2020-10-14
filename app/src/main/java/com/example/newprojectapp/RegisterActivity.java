package com.example.newprojectapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private EditText emailReg,pswrdRed,repswrdreg;
    private Button createaccoun;  private TextView haveanAcnt;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingbar;
    private DatabaseReference rootRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        rootRef= FirebaseDatabase.getInstance().getReference();

        emailReg=(EditText)findViewById(R.id.register_email);
        pswrdRed=(EditText)findViewById(R.id.register_pswrd);
        createaccoun=(Button)findViewById(R.id.register_btn);
        haveanAcnt=(TextView)findViewById(R.id.have_an_acnt);
repswrdreg=(EditText)findViewById(R.id.register_re_pswrd);
        mAuth=FirebaseAuth.getInstance();
        loadingbar=new ProgressDialog(this);
        createaccoun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUserAccount();
            }
        });
        haveanAcnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToLoginActivity();
            }
        });
    }
    private void createUserAccount() {
        final String email=emailReg.getText().toString();
        final String pswrd=pswrdRed.getText().toString();
        String repswrd=repswrdreg.getText().toString();
        if(TextUtils.isEmpty(email)&& TextUtils.isEmpty(pswrd) && TextUtils.isEmpty(repswrd)){
            Toast.makeText(this, "Please fill the details.", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please write your MailId", Toast.LENGTH_SHORT).show();
        }else if(!(email.contains("gmail.com"))) {
            //    emaillogin.setError("Not Found");
            Toast.makeText(this, "Enter your Correct Email address", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(pswrd) && TextUtils.isEmpty(repswrd)){
            Toast.makeText(this, "Please write your Password", Toast.LENGTH_SHORT).show();
        }else if(!(pswrd.equals(repswrd))){
            Toast.makeText(this, "Password and confirm password does not match", Toast.LENGTH_SHORT).show();
        }
        else{
            loadingbar.setTitle("Creating New Account");
            loadingbar.setMessage("Please wait,While we are creating new account for you..");
            loadingbar.setCanceledOnTouchOutside(true);
            mAuth.createUserWithEmailAndPassword(email,pswrd)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
           if(task.isSuccessful()){
          sendUserToLoginActivity();
    Toast.makeText(RegisterActivity.this, "Sign Up successfull,Please verify Your email id..", Toast.LENGTH_SHORT).show();
    loadingbar.dismiss();
}else { Toast.makeText(RegisterActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
           }
                                    }
                                });

                            }else {
                                try {
                                    throw task.getException();
                                } catch(FirebaseAuthWeakPasswordException e) {
                                    pswrdRed.setError(getString(R.string.error_weak_password));
                                    Toast.makeText(RegisterActivity.this, "Password should be atleast 6 characters", Toast.LENGTH_SHORT).show();
                                   pswrdRed.requestFocus();
                                } catch(FirebaseAuthInvalidCredentialsException e) {
                                    emailReg.setError(getString(R.string.error_invalid_email));
                                    Toast.makeText(RegisterActivity.this, "Enter valid email id", Toast.LENGTH_SHORT).show();
                                    emailReg.requestFocus();
                                } catch(FirebaseAuthUserCollisionException e) {
                                    emailReg.setError(getString(R.string.error_user_exists));
                                    Toast.makeText(RegisterActivity.this, "The email address is already in use by another account,Try with another one", Toast.LENGTH_SHORT).show();
                                    emailReg.requestFocus();
                                }catch (FirebaseNetworkException e){
                                    Toast.makeText(RegisterActivity.this, "Check your internet Connection", Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                String messag=task.getException().toString();
                                 loadingbar.dismiss();
                            }
                        }
                    });loadingbar.show();
        }
    }

    private void sendUserToMainActivity() {
        Intent mintent=new Intent(RegisterActivity.this,MainActivity.class);
        mintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mintent);
        finish();
    }

    private void sendUserToLoginActivity() {
        Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(intent);
    }
}
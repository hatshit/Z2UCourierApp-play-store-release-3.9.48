package com.z2u.chat;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;

/**
 * Created by arun on 6/9/18.
 */

public class Firebase_Auth_Provider {

    Context context;
    public static String authUserPassword = "zoom2u123456";         //******** Firebase Auth password for all courier ********//

    public Firebase_Auth_Provider(Context currentActivityContext, boolean isCalledAfterLogin) {
        context = currentActivityContext;
        if (isCalledAfterLogin || LoginZoomToU.firebase_CurrentUser == null) {
            /*Firebase_Auth_Provider_AsyncTask firebaseAuthProviderAsyncTask = new Firebase_Auth_Provider_AsyncTask();
            firebaseAuthProviderAsyncTask.execute();*/
            Firebase_Auth_Provider_AsyncTask();
        }
    }

    private void Firebase_Auth_Provider_AsyncTask(){


        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                // before execution
            }

            @Override
            public void doInBackground() {
                try {
                    if (LoginZoomToU.mAuth_Firebase == null)
                        LoginZoomToU.mAuth_Firebase = FirebaseAuth.getInstance();

                    final String emailStr = LoginZoomToU.prefrenceForLogin.getString("username", null);

                    LoginZoomToU.mAuth_Firebase.signInWithEmailAndPassword(emailStr, authUserPassword)
                            .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        //Toast.makeText(context, "Logged in user is "+LoginZoomToU.mAuth_Firebase.getCurrentUser(), Toast.LENGTH_SHORT).show();
                                        LoginZoomToU.firebase_CurrentUser = LoginZoomToU.mAuth_Firebase.getCurrentUser();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        //Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show();

                                        LoginZoomToU.mAuth_Firebase.createUserWithEmailAndPassword(emailStr, authUserPassword)
                                                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                        if (task.isSuccessful()) {
                                                            // Sign in success, update UI with the signed-in user's information
                                                            // Toast.makeText(context, "Logged in user is "+LoginZoomToU.mAuth_Firebase.getCurrentUser(), Toast.LENGTH_SHORT).show();
                                                            LoginZoomToU.firebase_CurrentUser = LoginZoomToU.mAuth_Firebase.getCurrentUser();
                                                        } else {
                                                            // If sign in fails, display a message to the user.
                                                            Toast.makeText(context, "Authentication failed."+task.getException(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onPostExecute() {
                // Ui task here
            }
        }.execute();
    }



}

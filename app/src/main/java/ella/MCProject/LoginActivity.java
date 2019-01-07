package ella.MCProject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends Activity {
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private FirebaseAuth mAuth;

    private static final String TAG = "FacebookLogin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        if(isLoggedIn){

            Intent intent = new Intent(LoginActivity.this, LogoutActivity.class);

            startActivity(intent);

            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        LoginManager.getInstance().logOut();

        callbackManager= CallbackManager.Factory.create();

        loginButton= findViewById(R.id.login_button);

        loginButton.setReadPermissions("public_profile", "email","user_friends");

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.v("ONSUCCESS ", loginResult.toString());

                mAuth = FirebaseAuth.getInstance();
                handleFacebookAccessToken(loginResult.getAccessToken());
                // App code
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {

                                Log.v("RESPONSE ", response.toString());
                                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                try {
                                    String name = object.getString("name");
                                    String id = object.getString("id");
                                    User mainUser = new User(name, id);
                                    JSONObject friends = object.getJSONObject("friends");
                                    Log.v("FRIENDS ", friends.toString());
                                    JSONArray friendslist = friends.getJSONArray("data");
                                    Log.v("DATA ", friendslist.toString());
                                    for (int l=0; l < friendslist.length(); l++) {
                                        mainUser.addFriend(new User(friendslist.getJSONObject(l).getString("name"),
                                                friendslist.getJSONObject(l).getString("id")));
                                    }
                                    PrefUtils.setCurrentUser(mainUser, getApplicationContext());

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                startActivity(intent);
                                finish();
                            }

                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, name, email, gender, birthday, friends");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.v("ONCANCEL ", "Cancel");
            }

            @Override
            public void onError(FacebookException exception)  {
                Log.v("ONERROR ", exception.toString());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}

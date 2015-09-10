package com.pervasivecomputing.pervasivecomputing;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import android.content.pm.Signature;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;


public class MainActivity extends AppCompatActivity {

    RelativeLayout background;
    Button mapButton;

    private CallbackManager callbackManager;
    private TextView info;
    private LoginButton loginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);


        //FacebookStuff
        callbackManager = CallbackManager.Factory.create();

        info = (TextView) findViewById(R.id.userName);
        loginButton = (LoginButton) findViewById(R.id.connectWithFbButton);

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                info.setText("User ID: " + loginResult.getAccessToken().getUserId() + "\n"
                        + "Auth Token: " + loginResult.getAccessToken().getToken());
               // Intent mapScreen = new Intent();
               // mapScreen.setClass(getApplicationContext(), MapsActivity.class);
                //startActivity(mapScreen);
            }

            @Override
            public void onCancel() {
                info.setText("Login cancelled");
            }

            @Override
            public void onError(FacebookException error) {
                info.setText("Login failed");
            }
        });


       // getUserInfo();





        //background = (RelativeLayout) findViewById(R.id)
        mapButton = (Button) findViewById(R.id.mapbutton);

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapScreen = new Intent();
                mapScreen.setClass(getApplicationContext(), MapsActivity.class);
                startActivity(mapScreen);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }



    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }


    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }







    public void getUserInfo(){
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), getPackageManager().GET_SIGNATURES);

            for (Signature sig : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(sig.toByteArray());
                Log.d("Key_Hash:- ", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e){
            //DO SOMTGHINDSFD
        } catch (NoSuchAlgorithmException e) {
           //DOSOMTGINH
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

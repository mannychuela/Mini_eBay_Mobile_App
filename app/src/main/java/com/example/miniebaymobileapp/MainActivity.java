package com.example.miniebaymobileapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import static android.content.Context.MODE_PRIVATE;

public class MainActivity extends AppCompatActivity {
    //User name and password
    EditText uname, pwd;
    //Button object
    Button loginBtn;
    //Shared object though the application
    SharedPreferences pref;
    //Intent for calling other activity
    Intent intent;
    //Server IP address and port
    String hostAddress="192.168.0.5:8080";
    //Authentication servlet
    String servletName="sessionServlet";
    //Server default response
    String serverResponse="not";
    //Strings
    String username, password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Link activity's controls with Java variables
        setContentView(R.layout.activity_main);
        uname = (EditText)findViewById(R.id.txtName);
        pwd = (EditText)findViewById(R.id.txtPwd);
        loginBtn = (Button)findViewById(R.id.btnLogin);

        //Create local session variables
        pref = getSharedPreferences("user_details",MODE_PRIVATE);
        intent = new Intent(MainActivity.this, DetailsActivity.class);

        //Checks for session variables
        if(pref.contains("username") && pref.contains("password") && pref.contains("sessionValue")){
            //The user has been logon
            username = pref.getString("username",null);
            password = pref.getString("sessionValue",null);

            //authenticate credentials


        }
        //The user has not been authenticated
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = uname.getText().toString();
                password = pwd.getText().toString();

                //Authenticate the user via webservices
                new GetItems(MainActivity.this).execute();
            }
        });

    }


    /**
     *  This class define a thread for networks transactions
     */
    private class GetItems extends AsyncTask<Void, Void, Void> {

        // Context: every transaction in a Android application must be attached to a context
        private Activity activity;

        //Server response
        //private String serverResponse;

        private String url;

        /***
         * Special constructor: assigns the context to the thread
         *
         * @param activity: Context
         */
        //@Override
        protected GetItems(Activity activity)
        {
            //Define the servlet URL
            url = "http://" + hostAddress +"/"+ servletName;
            this.activity = activity;
        }

        /**
         *  on PreExecute method: runs after the constructor is called and before the thread runs
         */
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this, "Authenticating..." + url, Toast.LENGTH_LONG).show();
        }

        /***
         *  Main thread
         * @param arg0
         * @return
         */
        protected Void doInBackground(Void... arg0) {

            //Read GUI inputs
            String userName, passWord;
            userName = ((EditText) findViewById(R.id.txtName)).getText().toString();
            passWord = ((EditText) findViewById(R.id.txtPwd)).getText().toString();

            //Define a HttpHandler
            HttpHandler handler = new HttpHandler();

            //perform the authentication process and capture the result in serverResponse variable
            serverResponse = handler.makeServiceCallPost(url, userName, passWord);

            //Clean response
            serverResponse=serverResponse.trim();

            return null;
        }


        /***
         *  This method verify the authentication result
         *  If authenticated, it creates an jsonPerson Object and open an authenticatedActivity
         *  otherwise, it shows a error message
         * @param result
         */
        protected void onPostExecute (Void result){
            String msgToast;

            //Verify the authentication result
            // not: the user could not be authenticated
            if (!serverResponse.equals("not")) {
                //The user has been authenticated
                //Update local session variables
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("username", username);
                editor.putString("password", password);
                editor.putString("sessionValue", serverResponse);
                editor.commit();
                //Define the next activity
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);

                //call the DetailsActivity
                startActivity(intent);
            } else {
                //The user could not been authenticated, destroy session variables
                SharedPreferences.Editor editor = pref.edit();
                editor.clear();
                editor.commit();

                //Toast message
                msgToast= "Wrong user or password";
                Toast.makeText(getApplicationContext(),
                        msgToast,
                        Toast.LENGTH_LONG).show();

            }
        }
    }
}
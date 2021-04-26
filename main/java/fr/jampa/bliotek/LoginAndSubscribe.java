package fr.jampa.bliotek;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.jampa.bliotek.classes.SQLiteDataBaseHelper;
import fr.jampa.bliotek.classes.User;
import fr.jampa.bliotek.classes.UserManager;
import fr.jampa.bliotek.resources.Connectivity;
import fr.jampa.bliotek.resources.Constants;
import fr.jampa.bliotek.resources.HttpPostRequest;

public class LoginAndSubscribe extends AppCompatActivity {

    UserManager userManag;
    User user;
    SQLiteDataBaseHelper db;
    Button retryButton, loginButton, displaySubscribeButton, displayLoginFormButton, subscribeButton;
    LinearLayout noConnectionLayout, loginLayout, subscribeLayout;
    TextView login_alert, subscribe_alert;
    EditText logEmail, logPwd, subName, subEmail, subPwd;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_or_subscribe_activity);


        user = new User();
        userManag = new UserManager(this);
        loginLayout = (LinearLayout)findViewById(R.id.lyt_login);
        noConnectionLayout = (LinearLayout)findViewById(R.id.lyt_please_connect);
        subscribeLayout = (LinearLayout)findViewById(R.id.lyt_subscribe);

        login_alert = (TextView)findViewById(R.id.tv_login_alert);
        subscribe_alert = (TextView)findViewById(R.id.tv_subscribe_alert);

        loginButton = (Button)findViewById(R.id.btn_login);
        loginButton.setOnClickListener(tryToLogIn);

        displaySubscribeButton = (Button)findViewById(R.id.btn_display_subform);
        displaySubscribeButton.setOnClickListener(displaySubscribeForm);

        subscribeButton = (Button)findViewById(R.id.btn_subscribe);
        subscribeButton.setOnClickListener(tryToSubscribe);

        displayLoginFormButton = (Button)findViewById(R.id.btn_display_loginform);
        displayLoginFormButton.setOnClickListener(displayLoginForm);

        retryButton = (Button)findViewById(R.id.btn_retry_connection);
        retryButton.setOnClickListener(retryButtonClick);

        logEmail = (EditText)findViewById(R.id.et_email_adress);
        logPwd = (EditText)findViewById(R.id.et_password);
        subName = (EditText)findViewById(R.id.et_name_subscribe);
        subEmail = (EditText)findViewById(R.id.et_email_adress_subscribe);
        subPwd = (EditText)findViewById(R.id.et_password_subscribe);

        displayer();
    }

    private void displayer(){
        // Check if the user is logged in
        // Case yes : Go to mainActivity
        // Case no : Check if internet connection available and display connection form
        user = userManag.getUserInfos();
        if(user == null ){
            if(Connectivity.isConnected(getApplicationContext())==true){
                //Internet connection available, display forms
                noConnectionLayout.setVisibility(View.GONE);
                loginLayout.setVisibility(View.VISIBLE);
            }else {
                // Internet connection needed to log in application
                noConnectionLayout.setVisibility(View.VISIBLE);
                loginLayout.setVisibility(View.GONE);
            }
        }else{
            Intent goMain = new Intent(LoginAndSubscribe.this, MainActivity.class);
            startActivity(goMain);
            finish();
        }
    }
    private View.OnClickListener displaySubscribeForm = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            loginLayout.setVisibility(View.GONE);
            subscribeLayout.setVisibility(View.VISIBLE);
        }
    };
    private View.OnClickListener displayLoginForm = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            loginLayout.setVisibility(View.VISIBLE);
            subscribeLayout.setVisibility(View.GONE);
        }
    };
    private View.OnClickListener retryButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            displayer();
        }
    };
    private View.OnClickListener tryToLogIn = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            login_alert.setText("");
            if(isValidEmail(logEmail.getText().toString())){
                String[] URLString = {Constants.URL_GET_DESTINATION, "action=login&emailCo="+logEmail.getText()+"&mdpCo="+logPwd.getText(), "POST", "application/x-www-form-urlencoded"};
                new LoginRequester().execute(URLString);
            }else{
                login_alert.setText("Veuillez entrer une adresse email valide !");
            }
        }
    };
    private View.OnClickListener tryToSubscribe = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            subscribe_alert.setText("");
            if(isValidEmail(subEmail.getText().toString())) {
                Pattern p = Pattern.compile(".*?\\W.*");
                Matcher m = p.matcher(subName.getText().toString());
                boolean b = m.matches();
                if(!b){
                    if(subName.getText().toString().length() > 2){
                        Pattern pat = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,}$");
                        Matcher mat = pat.matcher(subPwd.getText().toString());
                        boolean c = mat.matches();
                        if(c){
                            String[] URLString = {Constants.URL_POST_DESTINATION, "action=subscription&email=" + subEmail.getText() + "&mdp=" + subPwd.getText() + "&pseudo=" + subName.getText(), "POST", "application/x-www-form-urlencoded"};
                            new SubscribeRequester().execute(URLString);
                        }else{
                            subscribe_alert.setText("Votre mot de passe n\'est pas suffisament fort ou contient des espaces");
                        }
                    }else{
                        subscribe_alert.setText("Votre pseudo n\'est pas suffisament long");
                    }
                }else{
                    subscribe_alert.setText("Votre pseudo contient des caractères non autorisés");
                }
            }else{
                subscribe_alert.setText("Veuillez entrer une adresse email valide !");
            }
        }
    };
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
    private class LoginRequester extends HttpPostRequest{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(!Connectivity.isConnected(getApplicationContext())) {
                Toast.makeText(getApplicationContext(), R.string.no_connection_toast, Toast.LENGTH_LONG).show();
                cancel(true);
            }else {
                pDialog = new ProgressDialog(LoginAndSubscribe.this);
                pDialog.setMessage("Veuillez patienter...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
            }
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s != ""){
                try {
                    JSONObject json = new JSONObject(s);
                    userManag.addUser(json);
                } catch (JSONException e) {
                    e.printStackTrace();
                    login_alert.setText(s);
                }finally {
                    displayer();
                    pDialog.dismiss();
                }
            }
        }
    }
    private class SubscribeRequester extends HttpPostRequest{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(!Connectivity.isConnected(getApplicationContext())) {
                Toast.makeText(getApplicationContext(), R.string.no_connection_toast, Toast.LENGTH_LONG).show();
                cancel(true);
            }else {
                pDialog = new ProgressDialog(LoginAndSubscribe.this);
                pDialog.setMessage("Veuillez patienter...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
            }
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            subscribe_alert.setText(s);
            pDialog.dismiss();
        }
    }
}

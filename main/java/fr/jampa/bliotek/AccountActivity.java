package fr.jampa.bliotek;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import fr.jampa.bliotek.classes.User;
import fr.jampa.bliotek.classes.UserManager;
import fr.jampa.bliotek.resources.Connectivity;
import fr.jampa.bliotek.resources.Constants;
import fr.jampa.bliotek.resources.HttpPostRequest;

/**
 * Display username and usermail from localDB
 * Change username on server
 * **/
public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_activity);

        UserManager UserManag = new UserManager(this);
        User user = UserManag.getUserInfos();
        int UserID = user.getUser_id();
        String APIkey = user.getUser_APIkey();

        EditText username = findViewById(R.id.account_et_username);
        TextView usermail = findViewById(R.id.account_tv_usermail);
        username.setText(user.getUser_name());
        usermail.setText(user.getUser_email());

        Button changeUsername = findViewById(R.id.account_btn_changename);
        changeUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username.getText().toString().length() < 3 ){
                    Toast.makeText(AccountActivity.this, R.string.invalid_username, Toast.LENGTH_LONG).show();
                }else{
                    String[] URLString = {Constants.URL_POST_DESTINATION, "action=changeUserName&APIkey="+APIkey+"&value="+username.getText().toString()+"&userId="+UserID, "POST", "application/x-www-form-urlencoded"};
                    new simplePostRequests().execute(URLString);
                }
            }
        });
    }

    public class simplePostRequests extends HttpPostRequest {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(!Connectivity.isConnected(getApplicationContext())) {
                Toast.makeText(getApplicationContext(), R.string.no_connection_toast, Toast.LENGTH_LONG).show();
                cancel(true);
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(getApplicationContext(), R.string.account_sent, Toast.LENGTH_LONG).show();
        }
    }
}

package fr.jampa.bliotek;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import fr.jampa.bliotek.classes.UserManager;
import fr.jampa.bliotek.resources.Connectivity;
import fr.jampa.bliotek.resources.NetworkChangeReceiver;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {


    private static final int[] MAIN_LIST_OBJECTS = {
            R.string.main_list_option1,
            R.string.main_list_option2,
            R.string.main_list_option3,
            R.string.main_list_option4,
            R.string.main_list_option5,
            R.string.main_list_option6,
            R.string.main_list_option7
    };
    private static final Class<?>[] CLASSES =
            new Class<?>[]{
                AddBook.class, BookList.class, BookList.class, BookList.class, BookList.class, AccountActivity.class, LoginAndSubscribe.class
            };

    UserManager userManag;
    ListView listViewmain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        listViewmain=(ListView)findViewById(R.id.lv_main);
        MyArrayAdapter adapter = new MyArrayAdapter(this, android.R.layout.simple_list_item_2, CLASSES);
        adapter.setTitleIds(MAIN_LIST_OBJECTS);
        listViewmain.setAdapter(adapter);
        listViewmain.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Class<?> clicked = CLASSES[position];
        String activity = "";
        //if(Connectivity.isConnected(getApplicationContext())==true) {

            // Putting different intent extras as we use several the same Class for different "Activities"
            switch (position) {
                case 0: break;
                case 1: activity = "booklist"; break;
                case 2: activity = "wishlist"; break;
                case 3: activity = "haveread"; break;
                case 4: activity = "loaned"; break;
                case 6:
                    userManag = new UserManager(getApplicationContext());
                    userManag.deleteUser();
                    break;
            }
            startActivity(new Intent(this, clicked).putExtra("activity", activity));
       /* }else{
            Toast.makeText(getApplicationContext(), R.string.no_connection_toast, Toast.LENGTH_LONG).show();
        }*/
    }
    private static class MyArrayAdapter extends ArrayAdapter<Class<?>> {

        private final Context context;
        private final Class<?>[] classes;
        private int[] TitleIds;

        MyArrayAdapter(Context context, int resource, Class<?>[] objects) {
            super(context, resource, objects);

            this.context = context;
            classes = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;

            if (convertView == null) {
                LayoutInflater inflater =
                        (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.main_list, null);
            }

            ((TextView) view.findViewById(R.id.tv_mainlist_title)).setText(TitleIds[position]);
            return view;
        }

        void setTitleIds(int[] descriptionIds) {
            this.TitleIds = descriptionIds;
        }
    }

}

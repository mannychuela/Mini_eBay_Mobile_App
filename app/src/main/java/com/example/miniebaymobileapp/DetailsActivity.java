package com.example.miniebaymobileapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

import static android.content.Context.MODE_PRIVATE;


public class DetailsActivity extends AppCompatActivity {
    SharedPreferences prf;

    private String TAG = DetailsActivity.class.getSimpleName();
    private ListView lv;

    private String hostAddress;
    private UsersAdapter adapter;
    private ArrayList<userItem> itemUserList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);

        TextView result = findViewById(R.id.resultView);
        Button btnLogOut = findViewById(R.id.btnLogOut);

        prf = getSharedPreferences("user_details", MODE_PRIVATE);
        result.setText("Hello, " + prf.getString("username", null) + " \n " + prf.getString("sessionValue", null));

        hostAddress = "192.168.0.7:8080";
        itemUserList = new ArrayList<>();
        adapter = new UsersAdapter(this, itemUserList);
        lv = findViewById(R.id.itemList);

        new GetItems(this).execute();

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = prf.edit();
                editor.clear();
                editor.apply();

                DetailsActivity.this.finishAffinity();

                Intent intent = new Intent(DetailsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private class GetItems extends AsyncTask<Void, Void, Void> {
        private Activity activity;
        private ArrayList<String> departmentList;
        protected GetItems(Activity activity) {

            this.activity = activity;

            this.departmentList = new ArrayList<>();
        }

        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(DetailsActivity.this, "Items list is downloading", Toast.LENGTH_LONG).show();
        }

        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            String productsUrl = "http://" + hostAddress + "/getProductsJSON";
            String productsJsonStr = sh.makeServiceCall(productsUrl);

            String departmentsUrl = "http://" + hostAddress + "/getDepartmentsJSON";
            String departmentsJsonStr = sh.makeServiceCall(departmentsUrl);

            Log.e(TAG, "Response from url: " + productsJsonStr);

            if (productsJsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(productsJsonStr);
                    JSONArray products = jsonObj.getJSONArray("products");

                    for (int i = 0; i < products.length(); i++) {
                        JSONObject product = products.getJSONObject(i);
                        String name = product.getString("Product Name");
                        double bid = product.getDouble("Bid");
                        String description = product.getString("Description");
                        String department = product.getString("Department");
                        String seller = product.getString("Seller");
                        String imagePath = product.getString("Image");
                        String imageURL = "http://" + hostAddress + "/cpen410/imagesjson/" + imagePath;

                        Drawable image = LoadImageFromWebOperations(imageURL);
                        itemUserList.add(new userItem(name, String.valueOf(bid), image));
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            if (departmentsJsonStr != null){
                Log.e(TAG, "Raw JSON response: " + departmentsJsonStr);
                try{
                    JSONObject departmentsObj = new JSONObject(departmentsJsonStr);
                    JSONArray departments = departmentsObj.getJSONArray("departments");
                    for (int i =0; i < departments.length(); i++){
                        JSONObject department = departments.getJSONObject(i);
                        String departmentName = department.getString("Department Name");
                        departmentList.add(departmentName);
                    }
                }catch (final JSONException e){
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            lv.setAdapter(adapter);

            Spinner dropdownList = findViewById(R.id.dropdownList);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, departmentList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dropdownList.setAdapter(adapter);
        }

        public Drawable LoadImageFromWebOperations(String url) {
            try {
                InputStream is = (InputStream) new URL(url).getContent();
                return Drawable.createFromStream(is, "src name");
            } catch (Exception e) {
                return null;
            }
        }
    }

    public class UsersAdapter extends ArrayAdapter<userItem> {

        public UsersAdapter(Context context, ArrayList<userItem> users) {
            super(context, 0, users);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            userItem user = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
            }
            TextView itemName = convertView.findViewById(R.id.itemName);
            TextView itemPrice = convertView.findViewById(R.id.itemPrice);
            ImageView itemImage = convertView.findViewById(R.id.imageView);

            itemName.setText(user.name);
            itemPrice.setText(user.price);
            itemImage.setImageDrawable(user.image);

            convertView.setTag(position);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = (Integer) view.getTag();
                    Toast.makeText(getApplicationContext(),
                            "You have selected " + itemUserList.get(position).name,
                            Toast.LENGTH_LONG).show();
                }
            });
            return convertView;
        }
    }

    public class userItem {
        public String name;
        public String price;
        public Drawable image;

        public userItem(String name, String price, Drawable image) {
            this.name = name;
            this.price = price;
            this.image = image;
        }
    }
}

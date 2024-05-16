package com.example.miniebaymobileapp;

import android.os.Bundle;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;
import android.os.AsyncTask;
import android.graphics.drawable.Drawable;
import android.widget.TextView;
import android.view.View;
import android.widget.Button;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;

public class ProductDetailsActivity extends AppCompatActivity {
    private String productId;
    private Product product;
    private ImageView productImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        productId = getIntent().getStringExtra("productId");

        // Fetch the product details using the productId
        new FetchProductDetails().execute();

        Button lgOut = findViewById(R.id.lgOut);
        lgOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductDetailsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Button back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductDetailsActivity.this, DetailsActivity.class);
                startActivity(intent);
            }
        });
    }

    private class FetchProductDetails extends AsyncTask<Void, Void, Void> {
        private String TAG = FetchProductDetails.class.getSimpleName();
        private String hostAddress = "192.168.0.5:8080";

        @Override
        protected Void doInBackground(Void... voids) {
            HttpHandler sh = new HttpHandler();

            Log.e(TAG, "Product ID: " + productId);
            // Making a request to the product details URL and getting response
            String url = "http://" + hostAddress + "/getSpecificProductJSON?productId=" + productId;
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    String id = jsonObj.getString("Product ID");
                    String productName = jsonObj.getString("Product Name");
                    String productPrice = jsonObj.getString("Bid");
                    String productDescription = jsonObj.getString("Description");
                    String productDepartment = jsonObj.getString("Department");
                    String productDueDate = jsonObj.getString("Due-Date");
                    String productSeller = jsonObj.getString("Seller");
                    String Image = jsonObj.getString("Image");
                    String imageUrl = "http://" + hostAddress + "/cpen410/imagesjson/" + Image;

                    // Create the productClass object
                    product = new Product(id, productName, productPrice, productDescription, productDepartment, productDueDate, productSeller, imageUrl);
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            // Update the UI with the product details
            ((TextView)findViewById(R.id.product_name)).setText(product.getName());
            ((TextView)findViewById(R.id.product_price)).setText("$" + product.getPrice());
            ((TextView)findViewById(R.id.product_description)).setText("Description: " + product.getDescription());
            ((TextView)findViewById(R.id.product_department)).setText("Department: " + product.getDepartment());
            ((TextView)findViewById(R.id.product_due_date)).setText("Due Date: " + product.getDueDate());
            ((TextView)findViewById(R.id.product_seller)).setText("Seller: " + product.getSeller());

            productImage = findViewById(R.id.product_image);
            new LoadImageTask().execute(product.getImageUrl());
        }
    }

    private class LoadImageTask extends AsyncTask<String, Void, Drawable> {
        protected Drawable doInBackground(String... urls) {
            return LoadImageFromWebOperations(urls[0]);
        }

        protected void onPostExecute(Drawable result) {
            productImage.setImageDrawable(result);
        }
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

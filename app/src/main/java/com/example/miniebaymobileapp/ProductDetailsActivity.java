package com.example.miniebaymobileapp;

import android.os.Bundle;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.widget.TextView;
import android.view.View;
import android.widget.Button;

public class ProductDetailsActivity extends AppCompatActivity {
    private productClass productObject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        Intent intent = getIntent();
        productClass productObject = (productClass) intent.getSerializableExtra("productObject");
        ((TextView)findViewById(R.id.product_name)).setText(productObject.getName());
        ((TextView)findViewById(R.id.product_price)).setText("$" + productObject.getPrice());
        ((TextView)findViewById(R.id.product_description)).setText("Description: " + productObject.getDescription());
        ((TextView)findViewById(R.id.product_department)).setText("Department: " + productObject.getDepartment());
        ((TextView)findViewById(R.id.product_due_date)).setText("Due Date: " + productObject.getDueDate());
        ((TextView)findViewById(R.id.product_seller)).setText("Seller: " + productObject.getSeller());

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
}
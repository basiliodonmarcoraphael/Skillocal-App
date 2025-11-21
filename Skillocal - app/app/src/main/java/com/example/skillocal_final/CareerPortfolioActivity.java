package com.example.skillocal_final;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class CareerPortfolioActivity extends AppCompatActivity {

    private ImageView btnBack;
    private Button btnAddWork, btnAddEligibility, btnAddTraining;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_career_portfolio);

        // Toolbar Back Button
        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> onBackPressed());

        // Portfolio buttons
        btnAddWork = findViewById(R.id.btn_add_work);
        btnAddEligibility = findViewById(R.id.btn_add_eligibility);
        btnAddTraining = findViewById(R.id.btn_add_training);

        // Launch Add Work Experience screen
        btnAddWork.setOnClickListener(v ->
                startActivity(new Intent(CareerPortfolioActivity.this, AddWorkExperienceActivity.class))
        );

        // Launch Add Eligibility screen
        btnAddEligibility.setOnClickListener(v ->
                startActivity(new Intent(CareerPortfolioActivity.this, AddEligibilityActivity.class))
        );

        // Launch Add Training screen
        btnAddTraining.setOnClickListener(v ->
                startActivity(new Intent(CareerPortfolioActivity.this, AddTrainingActivity.class))
        );
    }
}

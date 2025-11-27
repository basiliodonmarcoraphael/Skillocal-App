package com.example.skillocal_final;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class JobMatchingEmployerActivity extends AppCompatActivity {

    private LinearLayout layoutContainer;  // layout_employer
    private EditText searchBar;            // et_search_employer

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_matching_employer);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_employer);
        setSupportActionBar(toolbar);

        // Back button
        findViewById(R.id.icon_back_employer).setOnClickListener(v -> finish());

        // Initialize search + list container
        searchBar = findViewById(R.id.et_search_employer);
        layoutContainer = findViewById(R.id.layout_employer);

        // TEMP: Populate demo items (remove when using real data)
        addSampleItems();

        // Search listener
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // --------------------------------------------------------------------
    // 🔍 Filter list based on search text
    private void filterList(String query) {
        query = query.toLowerCase();

        for (int i = 0; i < layoutContainer.getChildCount(); i++) {
            View child = layoutContainer.getChildAt(i);

            if (child instanceof TextView) {
                TextView item = (TextView) child;

                if (item.getText().toString().toLowerCase().contains(query)) {
                    item.setVisibility(View.VISIBLE);
                } else {
                    item.setVisibility(View.GONE);
                }
            }
        }
    }

    // --------------------------------------------------------------------
    // 📝 Demo items for testing — remove and replace with real vacancies
    private void addSampleItems() {
        layoutContainer.removeAllViews();

        String[] sampleJobs = {
                "Software Developer",
                "Graphic Designer",
                "Office Clerk",
                "Accountant",
                "Field Technician",
                "HR Assistant"
        };

        for (String job : sampleJobs) {
            TextView tv = new TextView(this);
            tv.setText(job);
            tv.setTextSize(16);
            tv.setPadding(20, 20, 20, 20);
            tv.setBackgroundColor(0xFFFFFFFF);
            tv.setElevation(4);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 10, 0, 10);
            tv.setLayoutParams(params);

            layoutContainer.addView(tv);
        }
    }
}

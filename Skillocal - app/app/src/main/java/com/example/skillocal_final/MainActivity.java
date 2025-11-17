package com.example.skillocal_final;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView iconMenu, iconKebab;
    private TextView toolbarTitle, tvEstCount, tvJobCount;

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "SkillocalPrefs";
    private static final String KEY_ESTABLISHMENTS = "establishments_per_user";
    private static final String KEY_JOBS = "jobs_per_user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        iconMenu = findViewById(R.id.icon_menu);
        iconKebab = findViewById(R.id.icon_kebab);
        toolbarTitle = findViewById(R.id.toolbar_title);
        tvEstCount = findViewById(R.id.tv_est_count);
        tvJobCount = findViewById(R.id.tv_job_count);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        navigationView.setNavigationItemSelectedListener(this);

        // Open drawer when menu icon clicked
        iconMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Kebab menu logout
        iconKebab.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(MainActivity.this, v);
            popup.getMenuInflater().inflate(R.menu.kebab_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_logout) {
                    SharedPreferences prefs = getSharedPreferences("UserAccount", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("isLoggedIn", false);
                    editor.apply();

                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                }
                return false;
            });
            popup.show();
        });

        // Load live counts
        updateCounts();
    }

    private void updateCounts() {
        String userEmail = getSharedPreferences("UserSession", MODE_PRIVATE)
                .getString("email", "guest@user.com");

        int estCount = 0;
        int jobCount = 0;

        // Count establishments
        String estJson = sharedPreferences.getString(KEY_ESTABLISHMENTS, "{}");
        try {
            JSONObject json = new JSONObject(estJson);
            if (json.has(userEmail)) {
                JSONArray arr = json.getJSONArray(userEmail);
                estCount = arr.length();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Count jobs
        String jobJson = sharedPreferences.getString(KEY_JOBS, "{}");
        try {
            JSONObject json = new JSONObject(jobJson);
            if (json.has(userEmail)) {
                JSONArray arr = json.getJSONArray(userEmail);
                jobCount = arr.length();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        tvEstCount.setText("Establishments\n" + estCount);
        tvJobCount.setText("Job Postings\n" + jobCount);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            toolbarTitle.setText("Dashboard");
            updateCounts();
        } else if (id == R.id.nav_establishments) {
            startActivity(new Intent(this, EstablishmentsActivity.class));
        } else if (id == R.id.nav_employee_profile) {
            startActivity(new Intent(this, ManageEmployeeProfileActivity.class));
        } else if (id == R.id.nav_job_vacancy) {
            startActivity(new Intent(this, JobVacancyActivity.class));
        } else if (id == R.id.nav_job_matching) {
            startActivity(new Intent(this, JobMatchingActivity.class));
        }

        drawerLayout.closeDrawers();
        return true;
    }
}

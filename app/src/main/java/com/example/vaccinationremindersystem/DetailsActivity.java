package com.example.vaccinationremindersystem;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.vaccinationremindersystem.databinding.ActivityDetailsBinding;
import com.example.vaccinationremindersystem.fragment.DatePickerFragment;
import com.example.vaccinationremindersystem.model.Profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Objects;

public class DetailsActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    ActivityDetailsBinding binding;
    public Button btn;

    public ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        btn = findViewById(R.id.buttonRegister);
        progressBar = findViewById(R.id.progressBar);


        Profile profile = (Profile) getIntent().getSerializableExtra("profile");
        if (profile != null) {
            binding.fullName.setText(profile.getParentName());
            binding.email.setText(profile.getEmail());
            binding.phonenumber.setText(profile.getPhone());
            binding.location.setText(profile.getLocation());
            binding.childname.setText(profile.getChildName());
            binding.DOB.setText(profile.getChildDOB());


            btn.setOnClickListener(view -> updateUserProfile());
        } else {
            btn.setOnClickListener(view -> updateUserProfile());
        }

        binding.datePicker.setOnClickListener(view -> {
            DialogFragment fragment = new DatePickerFragment();
            fragment.show(getSupportFragmentManager(), "Date Picker");
        });

    }

    private void updateUserProfile() {

        String Fullnames = binding.fullName.getText().toString();
        String Email = binding.email.getText().toString();
        String MobileNo = binding.phonenumber.getText().toString();
        String City = binding.location.getText().toString();
        String child = binding.childname.getText().toString();
        String DOB = binding.DOB.getText().toString();


        if (Fullnames.isEmpty()) {
            Toast.makeText(DetailsActivity.this, "Enter Full names", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Email.isEmpty()) {
            Toast.makeText(DetailsActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (MobileNo.isEmpty()) {
            Toast.makeText(DetailsActivity.this, "Enter Mobile Number", Toast.LENGTH_SHORT).show();
            return;
        }
        if (City.isEmpty()) {
            Toast.makeText(DetailsActivity.this, "Enter City", Toast.LENGTH_SHORT).show();
            return;
        }
        if (child.isEmpty()) {
            Toast.makeText(DetailsActivity.this, "Enter Child's Name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (DOB.isEmpty()) {
            Toast.makeText(DetailsActivity.this, "Enter Child's DOB", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseDatabase.getInstance().getReference("Profile/" + FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(
                        new Profile(
                                Fullnames, Email, MobileNo, City, child, DOB
                        )
                ).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        binding.fullName.setText("");
                        binding.email.setText("");
                        binding.phonenumber.setText("");
                        binding.childname.setText("");
                        binding.DOB.setText("");

                        startActivity(new Intent(this, HomeActivity.class));


                    } else {
                        Toast.makeText(this, Objects.requireNonNull(task.getException()).getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String currentDate = DateFormat.getDateInstance().format(calendar.getTime());
        binding.DOB.setText(currentDate);
    }
}



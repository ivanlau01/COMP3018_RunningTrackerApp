package com.example.runningtracker.view;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.runningtracker.R;

public class UserInfo extends AppCompatActivity {

    Button saveButton;
    EditText username, height, weight;
    String mUsername, mHeight, mWeight;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        username = findViewById(R.id.username);
        height = findViewById(R.id.height);
        weight  = findViewById(R.id.weight);

        //Allows user to go RecordsInfo page and transfer their data there
        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInfo();
                Intent userInfo = new Intent(UserInfo.this, RecordsInfo.class);
                userInfo.putExtra("passUsername", mUsername);
                userInfo.putExtra("passHeight", mHeight);
                userInfo.putExtra("passWeight", mWeight);
                startActivity(userInfo);
            }
        });
    }

    //Convert users data to string format
    public void getInfo(){
        mUsername = String.valueOf(username.getText());
        mHeight = String.valueOf(height.getText());
        mWeight = String.valueOf(weight.getText());

        setUsername(mUsername);
        setHeight(mHeight);
        setWeight(mWeight);
    }

    //Setter
    public void setUsername(String mUsername){
        this.mUsername = mUsername;
    }
    public void setHeight(String mHeight){
        this.mHeight = mHeight;
    }
    public void setWeight(String mWeight){
        this.mWeight = mWeight;
    }
}
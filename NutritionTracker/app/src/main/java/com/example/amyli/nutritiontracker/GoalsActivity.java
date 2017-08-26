package com.example.amyli.nutritiontracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class GoalsActivity extends AppCompatActivity {
    String[] goals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals);
        //get goals from intent
        final Intent passedIntent = getIntent();
        goals = (String[]) passedIntent.getSerializableExtra("goals");
        //set goals
        TextView t = (TextView) findViewById(R.id.currentGoal0);
        t.setText(goals[0]);
        TextView t1 = (TextView) findViewById(R.id.currentGoal1);
        t1.setText(goals[1]);
        TextView t2 = (TextView) findViewById(R.id.currentGoal2);
        t2.setText(goals[2]);
        TextView t3 = (TextView) findViewById(R.id.currentGoal3);
        t3.setText(goals[3]);
        //return button
        Button returnHome = (Button) findViewById(R.id.homeButton);
        returnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            returnToHome();
            }
        });
    }

    public void settingsAdjust(View v){
        int goalIndex = Integer.parseInt(v.getTag().toString());
        //hide textview
        TextView t = getCurrentGoal(goalIndex);
        t.setVisibility(View.GONE);
        //show edittext
        EditText e =getNewGoal(goalIndex);
        e.setVisibility(View.VISIBLE);
        String currentgoal = goals[goalIndex];
        e.setText(currentgoal);
        //show save button
        ImageButton save = getSaveButton(goalIndex);
        save.setVisibility(View.VISIBLE);
        //hide settings button
        ImageButton settings = getSettingsButton(goalIndex);
        settings.setVisibility(View.GONE);
    }

    public void saveinput(View v){
        int inputIndex = Integer.parseInt(v.getTag().toString());
        //save text from edittext
        EditText e = getNewGoal(inputIndex);
        goals[inputIndex] = e.getText().toString();
        //hide edit text
        e.setVisibility(View.GONE);
        //show textview with new number
        TextView t = getCurrentGoal(inputIndex);
        t.setVisibility(View.VISIBLE);
        t.setText(goals[inputIndex]);
        //hide save button
        ImageButton save = getSaveButton(inputIndex);
        save.setVisibility(View.GONE);
        //show settings
        ImageButton settings = getSettingsButton(inputIndex);
        settings.setVisibility(View.VISIBLE);
        //hide keyboard
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private void returnToHome(){
        //main page intent
        //send over new goals
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("goals",goals);
        setResult(Activity.RESULT_OK,intent);
        finish();
    }

    private TextView getCurrentGoal(int i){
        switch (i){
            case 0:
                return (TextView) findViewById(R.id.currentGoal0);
            case 1:
                return (TextView) findViewById(R.id.currentGoal1);
            case 2:
                return (TextView) findViewById(R.id.currentGoal2);
            case 3:
                return (TextView) findViewById(R.id.currentGoal3);
        }
        //error. should never reach here
        return null;
    }

    private EditText getNewGoal(int i){
        switch (i){
            case 0:
                return (EditText) findViewById(R.id.newGoal0);
            case 1:
                return (EditText) findViewById(R.id.newGoal1);
            case 2:
                return (EditText) findViewById(R.id.newGoal2);
            case 3:
                return (EditText) findViewById(R.id.newGoal3);
        }
        //error. should never reach here
        return null;
    }

    private ImageButton getSettingsButton(int i){
        switch (i){
            case 0:
                return (ImageButton) findViewById(R.id.settings_image0);
            case 1:
                return (ImageButton) findViewById(R.id.settings_image1);
            case 2:
                return (ImageButton) findViewById(R.id.settings_image2);
            case 3:
                return (ImageButton) findViewById(R.id.settings_image3);
        }
        return null;
    }

    private ImageButton getSaveButton(int i){
        switch (i){
            case 0:
                return (ImageButton) findViewById(R.id.saveinput_image0);
            case 1:
                return (ImageButton) findViewById(R.id.saveinput_image1);
            case 2:
                return (ImageButton) findViewById(R.id.saveinput_image2);
            case 3:
                return (ImageButton) findViewById(R.id.saveinput_image3);
        }
        return null;
    }
}


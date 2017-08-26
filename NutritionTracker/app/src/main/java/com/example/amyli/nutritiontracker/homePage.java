package com.example.amyli.nutritiontracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.ParseException;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link homePage.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link homePage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class homePage extends Fragment {
    String[] goals= {"100","100","100","100"}; //TODO put goals in database too
    int[] currentNutrients = {0,0,0,0};

    private OnFragmentInteractionListener mListener;

    public homePage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment homePage.
     */
    public static homePage newInstance() {
        homePage fragment = new homePage();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home_page, container, false);
        currentNutrients = ((MainActivity) getActivity()).getNutrients();
        return v;
    }

    @Override
    public void onStart(){
        super.onStart();
        //assign buttons
        assignButtons();
        currentNutrients = readFromDatabase();
        updateProgress();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    /**
     * gives the buttons in the UI its functions
     */
    private void assignButtons(){
        //EDIT GOALS button
        Button buttonEditGoals = (Button) getView().findViewById(R.id.button_editGoals);
        buttonEditGoals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GoalsActivity.class);
                intent.putExtra("goals",goals);
                startActivityForResult(intent, 1);
            }
        });
        //clear database button. JUST FOR DEBUGGING
        Button clear = (Button) getView().findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NutritionIntakeDAO dbhelper = new NutritionIntakeDAO(getActivity());
                dbhelper.clearTable();
                currentNutrients = readFromDatabase();
                updateProgress();
            }
        });
    }

    /**
     * this function is called when the app returns from the goals activity
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                goals = (String[]) data.getSerializableExtra("goals");
                updateProgress();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    /**
     * This function updates the UI's progress bars when goals or current nutrients change
     * make sure global variable currentNutrients is already up-to-date
     */
    public void updateProgress(){
        //first progress bar
        ProgressBar bar1 = (ProgressBar) getView().findViewById(R.id.determinateBar_1);
        TextView text1 = (TextView) getView().findViewById(R.id.text_view_count_1);
        int progress1 = updateGoalsHelper(0);
        bar1.setProgress(progress1);
        text1.setText(Integer.toString(progress1));
        //second progress bar
        ProgressBar bar2 = (ProgressBar) getView().findViewById(R.id.determinateBar_2);
        TextView text2 = (TextView) getView().findViewById(R.id.text_view_count_2);
        int progress2 = updateGoalsHelper(1);
        bar2.setProgress(progress2);
        text2.setText(Integer.toString(progress2));
        //third progress bar
        ProgressBar bar3 = (ProgressBar) getView().findViewById(R.id.determinateBar_3);
        TextView text3 = (TextView) getView().findViewById(R.id.text_view_count_3);
        int progress3 = updateGoalsHelper(2);
        bar3.setProgress(progress3);
        text3.setText(Integer.toString(progress3));
        //fourth progress bar
        ProgressBar bar4 = (ProgressBar) getView().findViewById(R.id.determinateBar_4);
        TextView text4 = (TextView) getView().findViewById(R.id.text_view_count_4);
        int progress4 = updateGoalsHelper(3);
        bar4.setProgress(progress4);
        text4.setText(Integer.toString(progress4));
    }

    /**
     * updates the global variable "currentNutrients" with database reads
     */
    private int[] readFromDatabase() {
        int[] array= {0,0,0,0};
        NutritionIntakeDAO dbhelper = new NutritionIntakeDAO(getActivity());
        Cursor cursor = dbhelper.getAllEvents();
        if (cursor.moveToFirst()){
            do{
                String nutrientName = cursor.getString(1);
                int position = getPosition(nutrientName);
                //
                String time = cursor.getString(2);
                Date d;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                try {
                    d = sdf.parse(time);
                    Date before = getDaysAgo(-7);
                    if(d.before(before)){
                        break; //skip nutrition that was added over a week ago
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //TODO do something with datetime d

                int amount = cursor.getInt(3);
                array[position] += amount;


            }while(cursor.moveToNext());
        }
        cursor.close();
        return array;
    }

    /**
     * calculates progress of progress bar i
     *
     * @param i the position of the selected nutrient in the options array
     * @return the new integer percentage of selected nutrient. for updating progress bar
     */
    private int updateGoalsHelper(int i){
        int currentNutrient = currentNutrients[i];
        int newGoal = Integer.parseInt(goals[i]);
        float progress = (float) currentNutrient/newGoal * 100;
        return Math.round(progress);
    }

    public void addToCurrentNutrients(String s, int i){
        int p = getPosition(s);
        currentNutrients[p] += i;
    }

    private int getPosition(String s){
        if(s.equals(getString(R.string.nutrient0))){
            return 0;
        }
        else if(s.equals(getString(R.string.nutrient1))){
            return 1;
        }
        else if(s.equals(getString(R.string.nutrient2))){
            return 2;
        }
        else if(s.equals(getString(R.string.nutrient3))){
            return 3;
        }
        else{
            return -1;
        }
    }

    private Date getDaysAgo(int days){
        //int x = -10;
        Calendar cal = GregorianCalendar.getInstance();
        cal.add( Calendar.DAY_OF_YEAR, days);
        Date tenDaysAgo = cal.getTime();
        return tenDaysAgo;
    }
}

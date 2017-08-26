package com.example.amyli.nutritiontracker;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link addInfo.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link addInfo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class addInfo extends Fragment {
    int[] currentNutrients = {10,20,30,40};
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int RESULT_LOAD_IMAGE = 2;

    private OnFragmentInteractionListener mListener;

    public addInfo() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment addInfo.
     */

    public static addInfo newInstance() {
        addInfo fragment = new addInfo();
        Bundle args = new Bundle();
        fragment.setArguments(args);
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
        View v = inflater.inflate(R.layout.fragment_add_info, container, false);
        currentNutrients = ((MainActivity) getActivity()).getNutrients();
        return v;
    }

    @Override
    public void onStart(){
        super.onStart();
        //populate the spinner options
        setSpinner();
        //
        final Button add_button = getView().findViewById(R.id.button_add);
        add_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //get the input information
                Spinner s = (Spinner) getView().findViewById(R.id.nutrition_spinner);
                String n = s.getSelectedItem().toString();
                EditText e = (EditText) getView().findViewById(R.id.edittext_amount);
                String i = e.getText().toString();
                e.setText(""); //clear the editText
                addNutrient(n,i); //add to database
                //TODO show toast that info was added
                //TODO hide keyboard
            }
        });
        //
        Button camera = (Button) getView().findViewById(R.id.button_camera);
        camera.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {

                    startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                }

            }
        });
        //
        Button demo = (Button) getView().findViewById(R.id.button_upload);
        demo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Bitmap myBitmap = BitmapFactory.decodeResource(
                        getActivity().getApplicationContext().getResources(),
                        R.raw.demo);
                ImageView imageView = (ImageView) getView().findViewById(R.id.imgView);
                imageView.setImageBitmap(myBitmap);
                scanImage(myBitmap);
            }
        });
    }

    /**
     * Use this to add a update the data and to update UI of home fragment.
     * Updating the UI of another fragment is done by calling a method in the container activity
     *
     * @param nutrient this is the position of the nutrient in the string array
     * @param amount this is the amount consumed of the nutrient
     */
    //TODO to be called by OCR??
    private void addNutrient(String nutrient, String amount) {
        //adds to database
        NutritionIntakeDAO dbhelper = new NutritionIntakeDAO(getActivity());
        String currentDateTime = getCurrentDateTime();
        dbhelper.insertEvent(nutrient,
                currentDateTime,
                amount);
        //calls a function from MainActivity to update homepage UI
        MainActivity m = (MainActivity) getActivity();
        m.updateNutrients(nutrient, Integer.parseInt(amount));
        Toast.makeText(getActivity().getApplicationContext(), "Saved!", Toast.LENGTH_SHORT).show();
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

    private void setSpinner(){
        Spinner spinner = (Spinner) getView().findViewById(R.id.nutrition_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        //string array found in res/values/addOptions.xml
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.nutrients_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    private String getCurrentDateTime(){
        String timeStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime());
        return timeStamp;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            //New code starts
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

            File image = new File(Environment.getExternalStorageDirectory(), "QR_" + ".png");
   /* imagesFolder.mkdirs();

    File image = new File(imagesFolder, "QR_" + timeStamp + ".png");*/
            Uri uriSavedImage = Uri.fromFile(image);
            data.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
            //New Code ends
            //Commented old code
    /*            Bundle extras = data.getExtras();
    Bitmap imageBitmap = (Bitmap) extras.get("data");
    ImageView imageView = (ImageView) getView().findViewById(R.id.imgView);
    imageView.setImageBitmap(imageBitmap);*/
            //Commented old code
            //myBitmap = imageBitmap;

            //New Code starts
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap imageBitmap = null;
            try {
                imageBitmap = BitmapFactory.decodeStream(new FileInputStream(image));
                ImageView imageView = (ImageView) getView().findViewById(R.id.imgView);
                imageView.setImageBitmap(imageBitmap);
                scanImage(imageBitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //New Code ends
        }

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data!=null) {

            Uri selectedImage = data.getData();

            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            Bitmap bmp = null;

            try {
                bmp = getBitmapFromUri(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ImageView imageView = (ImageView) getView().findViewById(R.id.imgView);
            imageView.setImageBitmap(bmp);
            //myBitmap = bmp;

        }
    }

    private void scanImage(Bitmap imageBitmap) {
        // imageBitmap is the Bitmap image you're trying to process for text
        if(imageBitmap != null) {

            TextRecognizer textRecognizer = new TextRecognizer.Builder(getActivity()).build();
/*
            if(!textRecognizer.isOperational()) {
                // Note: The first time that an app using a Vision API is installed on a
                // device, GMS will download a native libraries to the device in order to do detection.
                // Usually this completes before the app is run for the first time.  But if that
                // download has not yet completed, then the above call will not detect any text,
                // barcodes, or faces.
                // isOperational() can be used to check if the required native libraries are currently
                // available.  The detectors will automatically become operational once the library
                // downloads complete on device.
                Log.w(LOG_TAG, "Detector dependencies are not yet available.");

                // Check for low storage.  If there is low storage, the native library will not be
                // downloaded, so detection will not become operational.
                IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
                boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

                if (hasLowStorage) {
                    Toast.makeText(this,"Low Storage", Toast.LENGTH_LONG).show();
                    Log.w(LOG_TAG, "Low Storage");
                }
            }
*/

            Frame imageFrame = new Frame.Builder()
                    .setBitmap(imageBitmap)
                    .build();

            SparseArray<TextBlock> textBlocks = textRecognizer.detect(imageFrame);

            StringBuffer ocrString = new StringBuffer();
            ocrString.append("The following were saved:");
            ocrString.append("\n");
            for (int i = 0; i < textBlocks.size(); i++) {
                TextBlock textBlock = textBlocks.get(textBlocks.keyAt(i));

                if (textBlock != null && textBlock.getValue() != null) {
                    String text = textBlock.getValue();
                    if (text.contains("Calories") && !text.contains("%") && !text.contains("from")) {
                        //ocrString.append(text.substring(9));
                        int caloriesValue = getNutrientValue(text, "Calories");
                        if(caloriesValue >= 0){
                            ocrString.append("Calories " + caloriesValue);
                            ocrString.append("\n");
                            Log.d("Calories: ", text.substring(9));
                            addNutrient("Calories",Integer.toString(caloriesValue));
                        }
                    }
                    if (text.contains("Total Fat") && !text.contains("Calories")) {
                        //ocrString.append(text.substring(10));
                        int fatValue = getNutrientValue(text, "Total Fat");
                        if(fatValue >= 0){
                            ocrString.append("Total Fat " + fatValue + "g");
                            ocrString.append("\n");
                            Log.d("Total Fat: ", text.substring(10));
                            addNutrient("Total Fat",Integer.toString(fatValue));
                        }

                    }
//                    if (text.contains("Cholesterol")) {
//                        //ocrString.append(text.substring(12));
//                        int caloriesValue = getNutrientValue(text, "Cholesterol");
//                        if(caloriesValue >= 0){
//                            ocrString.append("Cholesterol " + caloriesValue + "mg");
//                            ocrString.append("\n");
//                            Log.d("Cholesterol: ", text.substring(12));
//                        }
//                    }
                    if (text.contains("Sodium")) {
                        int sodiumValue = getNutrientValue(text, "Sodium");
                        if(sodiumValue >= 0){
                            //ocrString.append(text.substring(7));
                            ocrString.append("Sodium " + sodiumValue + "mg");
                            ocrString.append("\n");
                            Log.d("Sodium: ", text.substring(7));
                            addNutrient("Sodium",Integer.toString(sodiumValue));
                        }

                    }
//                    if (text.contains("Carbohydrate")) {
//                        ocrString.append(text.substring(19));
//                        ocrString.append("\n");
//                        Log.d("Total Carbohydrate: ", text.substring(19));
//                    }
                    if (text.contains("Protein")) {
                        //ocrString.append(text.substring(8));
                        int caloriesValue = getNutrientValue(text, "Protein");
                        if(caloriesValue >= 0){
                            ocrString.append("Protein " + caloriesValue + "g");
                            ocrString.append("\n");
                            Log.d("Protein", text.substring(8));
                            addNutrient("Protein",Integer.toString(caloriesValue));
                        }

                    }

                }
            }
            TextView t = (TextView) getView().findViewById(R.id.cameraText);
            t.setText(ocrString);
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getActivity().getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    /**
     * already assume that it contains it!
     * @param itemLine
     * @param nutrientName
     * @return
     */
    private int getNutrientValue(String itemLine, String nutrientName){
        int start = itemLine.indexOf(nutrientName);
        int length = nutrientName.length();
        int beginIdx = start + length;
//        int end = itemLine.indexOf("g", beginIdx);
//        if(end == -1){
//            //no "g" found
//
//        }
//        String valueStr = itemLine.substring(beginIdx, end);
        String valueStr = itemLine.substring(beginIdx);
        valueStr.replaceAll(" ","");
        StringBuffer sbuf = new StringBuffer();
        for(int i = 0; i < valueStr.length(); i++){
            if(Character.isDigit(valueStr.charAt(i))){
                sbuf.append(valueStr.charAt(i));
            }
        }


        //
        if(sbuf.length() == 0){
            if(valueStr.toString().contains("Og") || valueStr.toString().contains("Omg")){
                return 0;
            }
            return -1;
        }
        int value = Integer.parseInt(sbuf.toString());
        return value;
    }
}

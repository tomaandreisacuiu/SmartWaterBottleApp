package com.example.smartwaterbottle;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailsFragment newInstance(String param1, String param2) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public Button confirmBtn;
    public TextView mText;
    public TextView mText2;
    public TextView mText3;
    public EditText mEditText;
    public EditText mEditText2;
    public String getString;
    public String getString2;
    public String getString3;
    public static final String SHARED_PREF = "shared";
    public static final String SHARED_PREF2 = "shared2";
    public static final String SHARED_PREF3 = "shared3";
    public static final String TEXT = "text";
    public static final String TEXT2 = "text";
    public static final String TEXT3 = "text";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_details, container, false);

        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        confirmBtn = (Button) rootView.findViewById(R.id.editMyDetailsButton);

        mText = rootView.findViewById(R.id.ageView);
        mText2 = rootView.findViewById(R.id.weightView);
        mText3 = rootView.findViewById(R.id.intakeView);
        mEditText = rootView.findViewById(R.id.ageEditText);
        mEditText2 = rootView.findViewById(R.id.weightEditText);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getString = mEditText.getText().toString();
                getString2 = mEditText2.getText().toString();
                mText.setText("Age: " + getString + " years old");
                mText2.setText("Weight: " + getString2 + "kg");

                float f1 = Float.parseFloat(getString);
                float f2 = Float.parseFloat(getString2);


                float f3 = (f1+f2)/80;
                getString3 = String.valueOf(f3);
                mText3.setText("Suggested Water Intake: " + getString3 + "L");

                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
                SharedPreferences sharedPreferences2 = getActivity().getSharedPreferences(SHARED_PREF2, Context.MODE_PRIVATE);
                SharedPreferences sharedPreferences3 = getActivity().getSharedPreferences(SHARED_PREF3, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                SharedPreferences.Editor editor2 = sharedPreferences2.edit();
                SharedPreferences.Editor editor3 = sharedPreferences3.edit();
                editor.putString(TEXT, mText.getText().toString());
                editor2.putString(TEXT2, mText2.getText().toString());
                editor3.putString(TEXT3, mText3.getText().toString());
                editor.apply();
                editor2.apply();
                editor3.apply();
            }
        });

        update();

        return rootView;
    }

    private void update() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences sharedPreferences2 = getActivity().getSharedPreferences(SHARED_PREF2, Context.MODE_PRIVATE);
        SharedPreferences sharedPreferences3 = getActivity().getSharedPreferences(SHARED_PREF3, Context.MODE_PRIVATE);

        String text2 = sharedPreferences2.getString(TEXT2, "Weight: 80kg (default)");
        String text = sharedPreferences.getString(TEXT,  "Age: 80 years old (default)");
        String text3 = sharedPreferences3.getString(TEXT3, "Suggested Water Intake: 2.0L (default)");
        mText.setText(text);
        mText2.setText(text2);
        mText3.setText(text3);
    }

}
package com.example.g29.msbandapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


public class HealthQuestionDialog extends AppCompatDialogFragment {

    private EditText activityLevelRating;
    private EditText healthRating;
    private TextView healthQuestionError;
    private HealthQuestionDialogListener listener;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.register_question_dialog, null);

        builder.setView(view)
                .setTitle("Health Rating")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!activityLevelRating.getText().toString().isEmpty() && activityLevelRating.getText().toString() != null && !healthRating.getText().toString().isEmpty() && healthRating.getText().toString() != null){
                            listener.sendData(activityLevelRating.getText().toString(), healthRating.getText().toString());

                        }else{
                            healthQuestionError.setText("Do not leave fields blank.");
                        }
                    }
                });

        activityLevelRating = view.findViewById(R.id.activityLevelInput);
        healthRating = view.findViewById(R.id.healthLevelInput);
        healthQuestionError = view.findViewById(R.id.healthQuestionError);
        return builder.create();
    }

        @Override
        public void onAttach(Context context){
            super.onAttach(context);

            try{
                listener = (HealthQuestionDialogListener) context;
            }catch (ClassCastException e){
                throw new ClassCastException(context.toString() + "must implement HealthQuestionListener");
            }
        }

    public interface HealthQuestionDialogListener {
        void sendData(String activityLevelRating, String healthRating);
    }
}

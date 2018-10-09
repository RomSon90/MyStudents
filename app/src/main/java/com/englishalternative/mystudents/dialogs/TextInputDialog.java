package com.englishalternative.mystudents.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.englishalternative.mystudents.R;

/**
 * Created by Roman on 30.03.2017.
 */

public class TextInputDialog extends DialogFragment {

    // Enum to allow activity to request a dialog type
    public enum TextType {
        UPPERCASE, LOWERCASE, EXTRA_SPACE, NUMBERS, UPPERCASE_ALL
    }

    public static final String TEXT_ARG = "textArgument";
    public static final String ID_ARGUMENT = "viewIdArgument";
    public static final String TYPE_ARG = "typeArgument";

    String text;
    int viewId;
    TextType textType;

    EditText textInput;

    public interface TextChangedListener {
        void onTextChanged(String text, int viewId);
    }


    public static TextInputDialog getInstance(String text, View view, TextType type) {

        // Usual dialog instantiation with arguments
        TextInputDialog dialog = new TextInputDialog();
        Bundle bundle = new Bundle();
        bundle.putString(TEXT_ARG, text);
        bundle.putInt(ID_ARGUMENT, view.getId());
        bundle.putString(TYPE_ARG, type.toString());
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Get arguments from instantiation
        Bundle args = getArguments();
        text = args.getString(TEXT_ARG);
        viewId = args.getInt(ID_ARGUMENT);
        textType = TextType.valueOf(args.getString(TYPE_ARG));


        // Inflate layout, modify EditText behaviour based on TextType selected
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_text_input, null);
        textInput = (EditText) view.findViewById(R.id.text_input);
        textInput.append(text);
        switch(textType) {
            default:
            case UPPERCASE:
                textInput.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                break;
            case LOWERCASE:
                textInput.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case EXTRA_SPACE:
                textInput.setMinLines(2);
                break;
            case NUMBERS:
                textInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case UPPERCASE_ALL:
                textInput.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                break;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Use the interface to signal change in the text
                TextChangedListener activity = (TextChangedListener) getActivity();
                activity.onTextChanged(textInput.getText().toString(), viewId);
            }
        });
        // Auto show keyboard
        Dialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        return dialog;

    }
}

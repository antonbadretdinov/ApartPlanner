package com.example.apartplanner;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

public class Utils {
    public interface OnTextChangedListener {
        void onTextChanged(CharSequence charSequence, int start, int before, int count);
    }

    public static void setTextWatcher(EditText editText, OnTextChangedListener textChangedListener) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                textChangedListener.onTextChanged(charSequence, i, i1, i2);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }


    public interface OnDoneListener {
        void onDone(CharSequence charSequence);
    }

    public static void setOnDoneListener(EditText editText, OnDoneListener onDoneListener) {
        editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                onDoneListener.onDone(v.getText());
//                return true;
            }
            return false;
        });
    }


}

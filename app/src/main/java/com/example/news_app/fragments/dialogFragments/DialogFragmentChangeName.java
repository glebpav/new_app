package com.example.news_app.fragments.dialogFragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.news_app.R;
import com.example.news_app.databinding.DialogFragmentChangeNameBinding;

public class DialogFragmentChangeName extends DialogFragment {

    private DialogFragmentChangeNameBinding binding;
    private final OnBtnChangedClickedListener btnChangedClickedListener;
    private final String currentName;

    public DialogFragmentChangeName(OnBtnChangedClickedListener btnChangedClickedListener, String currentName) {
        this.btnChangedClickedListener = btnChangedClickedListener;
        this.currentName = currentName;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflater.inflate(R.layout.dialog_fragment_change_name, container, false);
        binding = DialogFragmentChangeNameBinding.inflate(inflater, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        binding.btnDismiss.setOnClickListener(btnDismissClicked);
        binding.btnApply.setOnClickListener(btnApplyClicked);
        binding.etChangeName.setText(currentName);

        return binding.getRoot();
    }

    View.OnClickListener btnDismissClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getDialog().dismiss();
        }
    };

    View.OnClickListener btnApplyClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            btnChangedClickedListener.onClicked(binding.etChangeName.getText().toString());
            /*
            mUser.setName(name);
            binding.toolbar.setTitle(mUser.getName());
            requests.changeUser(mUser);
            alertDialog.dismiss();
            Toast.makeText(getActivity(), "Имя пользователя изменено успешно", Toast.LENGTH_SHORT).show();
             */
        }
    };

    public interface OnBtnChangedClickedListener{
        void onClicked(String name);
    }
}

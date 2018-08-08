package com.sdex.id;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdex.id.util.AndroidIdGenerator;
import com.sdex.id.xml.SettingsItem;

public class IdInputDialog extends DialogFragment {

  public static final String TAG = "IdInputDialog";

  private static final String ARG_NAME = "arg_name";
  private static final String ARG_PACKAGE = "arg_package";
  private static final String ARG_OLD_ID = "arg_old_id";
  private static final String ARG_OLD_DEFAULT_ID = "arg_old_default_id";

  private OnValueInputDialogCallback callback;

  public static IdInputDialog newInstance(SettingsItem item) {
    Bundle bundle = new Bundle();
    bundle.putString(ARG_NAME, item.getAppName());
    bundle.putString(ARG_PACKAGE, item.getPackageName());
    bundle.putString(ARG_OLD_ID, item.getValue());
    bundle.putString(ARG_OLD_DEFAULT_ID, item.getDefaultValue());
    IdInputDialog dialog = new IdInputDialog();
    dialog.setArguments(bundle);
    return dialog;
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    String name = getArguments().getString(ARG_NAME);
    String packageName = getArguments().getString(ARG_PACKAGE);
    String oldId = getArguments().getString(ARG_OLD_ID, "");
    String oldDefaultId = getArguments().getString(ARG_OLD_DEFAULT_ID, "");

    final AlertDialog.Builder builder =
      new AlertDialog.Builder(getActivity());
    View view = View.inflate(getActivity(), R.layout.dialog_input_value, null);
    final ImageView appIconView = view.findViewById(R.id.app_icon);
    final TextView appNameView = view.findViewById(R.id.app_name);
    final TextInputLayout valueLayout = view.findViewById(R.id.value_layout);
    final TextInputLayout defaultValueLayout = view.findViewById(R.id.default_value_layout);
    final EditText valueView = view.findViewById(R.id.value);
    final EditText defaultValueView = view.findViewById(R.id.default_value);
    final Button generate = view.findViewById(R.id.generate);
    final CheckBox allowInvalid = view.findViewById(R.id.allow_invalid);
    final TextView packageNameView = view.findViewById(R.id.package_name);

    PackageManager packageManager = getActivity().getPackageManager();
    try {
      Drawable icon = packageManager.getApplicationIcon(packageName);
      appIconView.setImageDrawable(icon);
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }

    appNameView.setText(name);

    valueView.setText(oldId.toUpperCase());
    defaultValueView.setText(oldDefaultId.toUpperCase());

    generate.setOnClickListener(v -> {
      valueView.setText(AndroidIdGenerator.generateRandomAndroidId());
      defaultValueView.setText(AndroidIdGenerator.generateRandomAndroidId());
    });

    valueView.setOnEditorActionListener((v, actionId, event) -> {
      if (actionId == EditorInfo.IME_ACTION_DONE) {
        setValue(valueLayout, valueView,
          defaultValueLayout, defaultValueView,
          allowInvalid, packageName);
        return true;
      }
      return false;
    });
    packageNameView.setText(packageName);
    builder.setTitle(null)
      .setView(view)
      .setPositiveButton(android.R.string.ok, null)
      .setNegativeButton(android.R.string.cancel, null);
    final AlertDialog alertDialog = builder.create();
    alertDialog.setOnShowListener(dialog -> alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
      .setOnClickListener(v -> setValue(valueLayout, valueView,
        defaultValueLayout, defaultValueView,
        allowInvalid, packageName)));
    return alertDialog;
  }

  private void setValue(TextInputLayout valueLayout, EditText valueView,
                        TextInputLayout defaultValueLayout, EditText defaultValueView,
                        CheckBox allowInvalid, String packageName) {
    final String newValue = valueView.getText().toString();
    final String newDefaultValue = defaultValueView.getText().toString();

    valueLayout.setError(null);
    defaultValueLayout.setError(null);

    if (TextUtils.isEmpty(newValue)) {
      valueLayout.setError(getString(R.string.dialog_input_value_error_empty));
      return;
    }

    if (TextUtils.isEmpty(newDefaultValue)) {
      defaultValueLayout.setError(getString(R.string.dialog_input_value_error_empty));
      return;
    }

    if (allowInvalid.isChecked()) {
      if (isValidInvalid(newValue) && isValidInvalid(newDefaultValue)) {
        if (!newValue.equalsIgnoreCase(newDefaultValue)) {
          callback.onValueSet(newValue, newDefaultValue, packageName);
          dismiss();
        } else {
          defaultValueLayout.setError(getString(R.string.dialog_input_value_error_same));
        }
      } else {
        valueLayout.setError(getString(R.string.dialog_input_value_error_invalid_invalid));
        defaultValueLayout.setError(getString(R.string.dialog_input_value_error_invalid_invalid));
      }
      return;
    }

    if ((isValid(newValue) && isValid(newDefaultValue))) {
      if (!newValue.equalsIgnoreCase(newDefaultValue)) {
        callback.onValueSet(newValue, newDefaultValue, packageName);
        dismiss();
      } else {
        defaultValueLayout.setError(getString(R.string.dialog_input_value_error_same));
      }
    } else {
      valueLayout.setError(getString(R.string.dialog_input_value_error_invalid));
      defaultValueLayout.setError(getString(R.string.dialog_input_value_error_invalid));
    }
  }

  private boolean isValid(String input) {
    return input.matches("^[0-9a-fA-F]+$") && input.length() == 16;
  }

  private boolean isValidInvalid(String input) {
    return input.matches("^[a-zA-Z0-9]*$");
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    try {
      callback = (OnValueInputDialogCallback) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString()
        + " must implement OnValueInputDialogCallback");
    }
  }

  public interface OnValueInputDialogCallback {

    void onValueSet(String newId, String newDefaultId, String packageName);
  }
}

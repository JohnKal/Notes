package com.john.kalimeris.notes;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by John on 08/10/16.
 */

public class Dialogs {

    Context _context;
    String _title;
    String _message;
    String _positiveButtonText;
    String _negativeButtonText;
    DialogInterface.OnClickListener _positiveListener;
    DialogInterface.OnClickListener _negativeListener;
    int _items;
    DialogInterface.OnClickListener _singleChoiceListListener;

    public Dialogs(Context context) {

        _context = context;
    }

    public Dialogs(Context context, String title, String message, String positiveButtonText, String negativeButtonText, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener) {

        _context = context;
        _title = title;
        _message = message;
        _positiveButtonText = positiveButtonText;
        _negativeButtonText = negativeButtonText;
        _positiveListener = positiveListener;
        _negativeListener = negativeListener;
    }

    public Dialogs(Context context, String title, int items, DialogInterface.OnClickListener onClickListener) {

        _context = context;
        _title = title;
        _items = items;
        _singleChoiceListListener = onClickListener;
    }

    public AlertDialog.Builder createAlertDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(_context, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(_title);
        builder.setMessage(_message);
        builder.setPositiveButton(_positiveButtonText, _positiveListener);
        builder.setNegativeButton(_negativeButtonText, _negativeListener);
        builder.show();

        return builder;

    }

    public AlertDialog createSingleChoiceListDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(_context);
        builder.setTitle(_title)
                .setItems(_items, _singleChoiceListListener);
        return builder.create();

    }

    public void createConfirmationDateDialog(DatePickerDialog.OnDateSetListener setDateCallback, int year, int month, int day) {

        /*AlertDialog.Builder builder = new AlertDialog.Builder(_context, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(_title);
        builder.setView(R.layout.date_picker_layout);
        builder.setPositiveButton(_positiveButtonText, _positiveListener);
        builder.setNegativeButton(_negativeButtonText, _negativeListener);
        builder.show();*/

        DatePickerDialog datePickerDialog = new DatePickerDialog(_context, setDateCallback, year, month, day);

        datePickerDialog.show();

    }
}

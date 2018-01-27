package com.google.android.gms.samples.vision.barcodereader;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.gms.common.api.CommonStatusCodes;

/**
 * Created by List on 1/27/2018.
 */

public class UserDataActivity  extends Activity {
    public static final String UserID = "userID";
    public static final String StartDate = "startDate";
    public static final String TimeUnitAmount = "timeUnitAmount";
    public static final String TimeUnit = "timeUnit";

    private static final String TAG = "UserData";

    private EditText userIDview;
    private DatePicker userStartDateView;
    private NumberPicker timeUnitAmountView;
    private RadioGroup timeUnitView;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_user_data);

        userIDview = (EditText) findViewById(R.id.user_id);
        userStartDateView = (DatePicker) findViewById(R.id.start_date);
        timeUnitAmountView = (NumberPicker) findViewById(R.id.time_unit_amount);
        timeUnitView = (RadioGroup) findViewById(R.id.time_unit);

        timeUnitAmountView.setMinValue(1);
        timeUnitAmountView.setMaxValue(99);
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

    }


    public void saveData(View view) {
        Intent data = new Intent();

        String userID = userIDview.getText().toString();
        data.putExtra(UserDataActivity.UserID,
                Long.valueOf((userID == null || userID.isEmpty()) ? 0 : Long.valueOf(userID)));
        data.putExtra(UserDataActivity.StartDate, String.format("%04d-%02d-%02d",
                userStartDateView.getYear(), userStartDateView.getMonth() + 1,
                userStartDateView.getDayOfMonth()));
        data.putExtra(UserDataActivity.TimeUnitAmount, timeUnitAmountView.getValue());
        data.putExtra(UserDataActivity.TimeUnit,
                ((RadioButton) timeUnitView.findViewById(timeUnitView.getCheckedRadioButtonId()))
                        .getText().toString());

        setResult(CommonStatusCodes.SUCCESS, data);
        finish();
    }
}

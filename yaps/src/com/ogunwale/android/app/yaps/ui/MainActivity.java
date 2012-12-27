package com.ogunwale.android.app.yaps.ui;

import com.ogunwale.android.app.yaps.R;
import com.ogunwale.android.app.yaps.R.array;
import com.ogunwale.android.app.yaps.R.id;
import com.ogunwale.android.app.yaps.R.layout;
import com.ogunwale.android.app.yaps.R.menu;
import com.ogunwale.android.app.yaps.R.string;
import com.ogunwale.android.app.yaps.content.PhotosSourceEnum;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

/**
 * Main entry point for the Yaps application.
 *
 * @author ogunwale
 *
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeButtons();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    /**
     * Method initializes all the buttons on the activity.
     */
    private void initializeButtons() {
        // Transfer albums/photos button.
        final Button transfer = (Button) findViewById(R.id.transfer_albums_photos);
        transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displaySingleSelectAlertDialog(v.getContext(), R.string.select_transfer_direction, R.array.transfer_direction,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (PhotosSourceEnum.getEnum(which)) {
                                // TODO
                                case FACEBOOK:
                                    break;
                                case PICASA:
                                    break;
                                case INVALID:
                                    break;
                                }
                            }

                        });
            }
        });

        // View albums/photos button.
        final Button view = (Button) findViewById(R.id.view_albums_photos);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displaySingleSelectAlertDialog(v.getContext(), R.string.select_source, R.array.sources, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (PhotosSourceEnum.getEnum(which)) {
                        case FACEBOOK:
                            startActivity(new Intent(PhotosActivity.Extras.ACTION_SET_PHOTO_SOURCE_FACEBOOK, null, getApplicationContext(),
                                    PhotosActivity.class));
                            break;
                        case PICASA:
                            startActivity(new Intent(PhotosActivity.Extras.ACTION_SET_PHOTO_SOURCE_PICASA, null, getApplicationContext(),
                                    PhotosActivity.class));
                            break;
                        case INVALID:
                            break;
                        }
                    }

                });
            }
        });

        // Transfer status button.
        final Button status = (Button) findViewById(R.id.transfer_status);
        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
            }
        });

        // Test pages button.
        final Button test = (Button) findViewById(R.id.test_pages);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displaySingleSelectAlertDialog(v.getContext(), R.string.select_test_page, R.array.sources, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (PhotosSourceEnum.getEnum(which)) {
                        case FACEBOOK:
                            startActivity(new Intent(getApplicationContext(), FacebookTestActivity.class));
                            break;
                        case PICASA:
                            startActivity(new Intent(getApplicationContext(), PicasaTestActivity.class));
                            break;
                        case INVALID:
                            break;
                        }
                    }

                });
            }
        });

    }

    /**
     * Method constructs an alert dialog with an Okay and Cancel button. The
     * passed in listener is called when the okay button is pressed with the
     * selection item index. Both the Okay and Cancel button dismiss the dialog.
     *
     * @param context
     * @param titleRId
     * @param selectionRId
     * @param onOkay
     */
    private void displaySingleSelectAlertDialog(Context context, int titleRId, int selectionRId, final DialogInterface.OnClickListener onOkay) {

        // final int[] selection = new int[] { -1 };

        new AlertDialog.Builder(context).setTitle(titleRId).setSingleChoiceItems(selectionRId, -1, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                onOkay.onClick(dialog, which);
                dialog.dismiss();
                // selection[0] = which;
            }
            // }).setPositiveButton(R.string.okay, new
            // DialogInterface.OnClickListener() {
            //
            // @Override
            // public void onClick(DialogInterface dialog, int which) {
            // onOkay.onClick(dialog, selection[0]);
            // dialog.dismiss();
            // }
            // }).setNegativeButton(R.string.cancel, new
            // DialogInterface.OnClickListener() {
            //
            // @Override
            // public void onClick(DialogInterface dialog, int which) {
            // dialog.dismiss();
            // }
        }).show();

    }
}

package com.ogunwale.android.app.yaps;

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

    private void initializeButtons() {

        // Transfer albums/photos button.
        final Button transfer = (Button) findViewById(R.id.transfer_albums_photos);
        transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
            }
        });

        // View albums/photos button.
        final Button view = (Button) findViewById(R.id.view_albums_photos);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
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
                SingleSelectAlertDialog dialog = new SingleSelectAlertDialog(view.getContext());
                dialog.show(R.string.select_test_page, R.array.sources, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                        case 0:
                            startActivity(new Intent(getApplicationContext(), PicasaTestActivity.class));
                            break;
                        case 1:
                            startActivity(new Intent(getApplicationContext(), FacebookTestActivity.class));
                            break;
                        }
                    }

                });
            }
        });

    }

    /**
     * Convenience class for creating a single select alert dialog.
     *
     * @author ogunwale
     *
     */
    public static class SingleSelectAlertDialog extends AlertDialog.Builder {

        private int mWhich = -1;

        public SingleSelectAlertDialog(Context context) {
            super(context);
        }

        public void show(int tId, int rId, final DialogInterface.OnClickListener onOkay) {
            setMessage(tId);

            setSingleChoiceItems(rId, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mWhich = which;
                }
            });

            setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onOkay.onClick(dialog, mWhich);
                    dialog.dismiss();
                }
            });

            setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            show();
        }
    }

}

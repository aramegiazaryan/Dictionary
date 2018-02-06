package com.example.aram.dictionary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public String KEY_SUM = "KEY_SUM";
    private DBHelper dbHelper;
    private Cursor c;
    private Resources res;
    private int rlID;
    private int sumID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);
        res = getResources();

        for (char i = 'A'; i <= 'Z'; i++) {
            rlID = res.getIdentifier("rl" + i, "id", this.getPackageName());
            ((RelativeLayout) findViewById(rlID)).setOnClickListener(this);
        }

        getSumLetter();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //  if (data != null && requestCode == 15) {
        getSumLetter();
        //  }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add) {

            Intent intent = new Intent(this, AddActivity.class);
            startActivityForResult(intent, 15);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        for (char i = 'A'; i <= 'Z'; i++) {
            if (v.getId() == res.getIdentifier("rl" + i, "id", this.getPackageName())) {
                String k = String.valueOf(i);
                c = dbHelper.query(k);
                if (c != null) {
                    if (c.moveToFirst()) {
                        Intent intent = new Intent(this, TranslationActivity.class);
                        intent.putExtra("letter", k);
                        startActivityForResult(intent, 20);
                    }
                }
            }
        }
    }


    public void getSumLetter() {
        int num = 0;
        for (char i = 'A'; i <= 'Z'; i++) {
            String k = String.valueOf(i);
            c = dbHelper.query(k);
            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        num++;
                    } while (c.moveToNext());
                }
                sumID = res.getIdentifier("sum" + i, "id", this.getPackageName());
                if (num != 0) {
                    ((TextView) findViewById(sumID)).setText(""+num);
                    num = 0;
                }else {
                    ((TextView) findViewById(sumID)).setText(" ");
                }
            }

        }

    }


}

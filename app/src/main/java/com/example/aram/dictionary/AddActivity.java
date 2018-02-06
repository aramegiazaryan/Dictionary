package com.example.aram.dictionary;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.annotation.UiThread;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public class AddActivity extends AppCompatActivity implements View.OnClickListener {
    private Button buttonSave;
    private Button buttonCancel;
    private EditText word;
    private EditText translation;
    private DBHelper dbHelper;
    private Calendar calendar;
    private Intent intent;
    private ImageView buttonRecord;
    private ImageView buttonPlay;
    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;
    public String mFileName = null;
    private Boolean secondClickRecord = false;
    private Boolean secondClickStop = false;
    private Boolean secondClickPlay = false;
    public static final int RequestPermissionCode = 1;
    private char letterChar;
    public      String folderName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        buttonSave = (Button) findViewById(R.id.btn_save);
        buttonCancel = (Button) findViewById(R.id.btn_cancel);
        buttonRecord = (ImageView) findViewById(R.id.iv_record);
        buttonPlay = (ImageView) findViewById(R.id.iv_play);
        word = (EditText) findViewById(R.id.et_word);
        translation = (EditText) findViewById(R.id.et_translation);
        buttonSave.setOnClickListener(this);
        buttonCancel.setOnClickListener(this);
        buttonRecord.setOnClickListener(this);
        buttonPlay.setOnClickListener(this);
        dbHelper = new DBHelper(this);
        calendar = Calendar.getInstance();
        intent = getIntent();
        File folderRecord = new File(Environment.getExternalStorageDirectory().toString()+"/DictionaryRecord");
        folderRecord.mkdirs();
        folderName=folderRecord.toString();
        if (intent.getStringExtra("et_word") != null) {
            word.setText(intent.getStringExtra("et_word"));
            translation.setText(intent.getStringExtra("et_translation"));
            String filename=folderName+"/"+intent.getStringExtra("et_word")+".3gp";
            File fileWord= new File(filename);
            if(fileWord.isFile()){
                buttonRecord.setBackground(getResources().getDrawable(R.drawable.delete64x64));
                buttonPlay.setVisibility(View.VISIBLE);
                mFileName=filename;
                secondClickStop=true;
            }

        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save: {
                if (!word.getText().toString().isEmpty()) {
                    String dbWord = word.getText().toString();
                    String dbLetter = dbWord.substring(0, 1).toUpperCase();
                     letterChar = dbLetter.charAt(0);
                    if (letterChar >= 'A' && letterChar <= 'Z') {
                        String dbTranslation = translation.getText().toString();
                        String dbData = "" + calendar.get(Calendar.DATE) + "." + "" + (calendar.get(Calendar.MONTH) + 1) + "." + "" + "" + calendar.get(Calendar.YEAR);

                        if (intent.getStringExtra("et_word") != null) {
                            int num = dbHelper.update(intent.getStringExtra("et_word"), dbData, dbLetter, dbWord, dbTranslation);
                            if (num == 1) {
                                intent = new Intent();
                                intent.putExtra("et_letter_update", dbLetter);
                                intent.putExtra("et_word_update", word.getText().toString());
                                intent.putExtra("et_translation_update", translation.getText().toString());
                                Toast.makeText(this, "Update", Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK, intent);
                                // finish();
                                // break;

                            } else {
                                Toast.makeText(this, "No update", Toast.LENGTH_SHORT).show();
                            }

                        } else

                        {
                            dbHelper.insert(dbData, dbLetter, dbWord, dbTranslation);
                            Toast.makeText(this, "Save", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK, null);
                            // finish();
                            //  break;
                        }
                        finish();
                        break;
                    }
                    Toast.makeText(this, "Please enter the correct word", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.btn_cancel: {
                finish();
                break;
            }
            case R.id.iv_record: {
                if (!word.getText().toString().isEmpty()) {
                    if (!secondClickStop) {
                        if (!secondClickRecord) {
                            mFileName = folderName+"/" + word.getText().toString() + ".3gp";
                            buttonRecord.setBackground(getResources().getDrawable(R.drawable.stop64x64));
                            startRecording();
                            secondClickRecord = true;
                        } else {
                            stopRecording();
                            buttonRecord.setBackground(getResources().getDrawable(R.drawable.delete64x64));
                            buttonPlay.setVisibility(View.VISIBLE);
                            secondClickRecord = false;
                            secondClickStop=true;
                        }

                    } else {
                        buttonRecord.setBackground(getResources().getDrawable(R.drawable.record64x64));
                        buttonPlay.setVisibility(View.GONE);
                        secondClickStop=false;
                        File file=new File(mFileName);
                        file.delete();
                    }
                }
                break;

            }
            case R.id.iv_play: {
                buttonPlay.setBackground(getResources().getDrawable(R.drawable.stop64x64));
                startPlaying(mFileName);
                break;
            }
        }
    }

    private void startRecording() {
        if (checkPermission()) {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setOutputFile(mFileName);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mRecorder.setAudioChannels(2);

            try {
                mRecorder.prepare();
            } catch (IOException e) {
                Toast.makeText(AddActivity.this, "prepare() failed", Toast.LENGTH_SHORT).show();
            }

            mRecorder.start();
        } else {
            requestPermission();
        }

    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    public void startPlaying(String playName) {
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                buttonPlay.setBackground(getResources().getDrawable(R.drawable.play64x64));
            }

        });
        try {
            mPlayer.setDataSource(playName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Toast.makeText(AddActivity.this, "prepare() failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(AddActivity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(AddActivity.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(AddActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }



    @Override
    public void onPause() {
        super.onPause();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

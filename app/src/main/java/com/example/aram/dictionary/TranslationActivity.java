package com.example.aram.dictionary;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TranslationActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener,ClickItemCallBack{
    private ListView lvWord;
    private CustomAdapter customAdapter;
    private List<ListWord> items;
    private String letter;
    private DBHelper dbHelper;
    private Intent intent;
    private int positionEdit;
    private AddActivity addActivity;
    private MediaPlayer mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translation);
        lvWord = (ListView) findViewById(R.id.lv_word);
        items = new ArrayList();
        dbHelper = new DBHelper(this);
        lvWord.setOnItemLongClickListener(this);
        intent = getIntent();
        letter = intent.getStringExtra("letter");
        upload();
        addActivity=new AddActivity();




    }


    @Override
    public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, final long id) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final String dialogItems[] = {"edit", "delete"};
        builder.setItems(dialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0: {
                        positionEdit=position;
                        intent = new Intent(getApplicationContext(), AddActivity.class);
                        intent.putExtra("et_word", items.get(position).getWord());
                        intent.putExtra("et_translation", items.get(position).getTranslation());
                        startActivityForResult(intent,18);
                        break;
                    }
                    case 1: {
                        String name1= items.get(position).getWord();
                        String name= Environment.getExternalStorageDirectory().toString()+"/DictionaryRecord"+"/" + name1 + ".3gp";
                        File fileRecord=new File(name);
                        if(fileRecord.isFile()) {
                            fileRecord.delete();
                        }
                        deleteDialog(position);
                        break;
                    }
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data!=null&& requestCode==18){
            String letterUpdate=data.getStringExtra("et_letter_update");
            if (letter.equals(letterUpdate)) {
                items.get(positionEdit).setWord(data.getStringExtra("et_word_update"));
                items.get(positionEdit).setTranslation(data.getStringExtra("et_translation_update"));
            }else
            {
                items.remove(positionEdit);
                if(items.isEmpty()) {
                    finish();

                }
            }
            customAdapter.notifyDataSetChanged();
        }

    }

    public void upload (){
        Cursor c = dbHelper.query(letter);
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    int indexWord = c.getColumnIndex("word");
                    int indexTranslation = c.getColumnIndex("translation");
                    ListWord listWord = new ListWord(c.getString(indexWord), c.getString(indexTranslation));
                    items.add(listWord);

                } while (c.moveToNext());
            }
            c.close();

        }

        customAdapter = new CustomAdapter(this, items);
        lvWord.setAdapter(customAdapter);

    }

    public void deleteDialog(final int position) {

        final AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Are you sure want to remove this word?")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                         dialog.dismiss();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = items.get(position).getWord();
                     int num=dbHelper.delete(text);
                        if(num==1) {
                            items.remove(position);
                            customAdapter.notifyDataSetChanged();
                          //  dbHelper.query(letter);
                            Toast.makeText(getApplicationContext(), "delete", Toast.LENGTH_SHORT).show();
                            if(items.isEmpty()){
                                finish();
                            }
                        }else
                        {
                            Toast.makeText(getApplicationContext(), "No delete", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        AlertDialog alert1 = builder1.create();
        alert1.show();
    }
    

    @Override
    public void clickInItem(int position,View view) {
        String name1=items.get(position).getWord().toString();
        String name= Environment.getExternalStorageDirectory().toString()+"/DictionaryRecord"+"/" + name1 + ".3gp";
        File fileRecord=new File(name);
        if(view!=null&&fileRecord.isFile()){
            final ImageView image =(ImageView) view.findViewById(R.id.iv_play_translation);
            image.setBackground(getResources().getDrawable(R.drawable.stop32x32));

            mPlayer = new MediaPlayer();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    image.setBackground(getResources().getDrawable(R.drawable.play32x32));
                }

            });
            try {
                mPlayer.setDataSource(name);
                mPlayer.prepare();
                mPlayer.start();
            } catch (IOException e) {
                Toast.makeText(TranslationActivity.this, "prepare() failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

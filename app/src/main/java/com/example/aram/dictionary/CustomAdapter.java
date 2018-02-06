package com.example.aram.dictionary;

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

public class CustomAdapter extends BaseAdapter  {
    private final Context context;
    private ImageView playTranslation;
    private LayoutInflater layoutInflater;
    List<ListWord> items;
    private ClickItemCallBack clickInIremCallBack;

    public CustomAdapter(Context context, List<ListWord> items){
        this.items=items;
        layoutInflater=LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public int getCount() {
        if(items.size()!=0){
            return items.size();
        }
        return 0;
    }

    @Override
    public ListWord getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if(convertView==null){
            convertView=layoutInflater.inflate(R.layout.row_item, parent, false);
        }
        TextView textWord= (TextView) convertView.findViewById(R.id.tv_word);
        TextView textTranslation= (TextView) convertView.findViewById(R.id.tv_translation);
        ImageView playTranslation=(ImageView)convertView.findViewById(R.id.iv_play_translation);
        ListWord current=getItem(position);
        textWord.setText(current.getWord());
        textTranslation.setText(current.getTranslation());
        String name1= current.getWord();
        String name= Environment.getExternalStorageDirectory().toString()+"/DictionaryRecord"+"/" + name1 + ".3gp";
        clickInIremCallBack = (ClickItemCallBack) context;
        final View finalConvertView = convertView;
        playTranslation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clickInIremCallBack.clickInItem(position,finalConvertView);
            }
        });
        File fileRecord=new File(name);
        if(fileRecord.isFile()){
            playTranslation.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

/*    @Override
public void onClick(View v) {
    switch (v.getId()){

        case R.id.iv_play_translation:{
            playTranslation.setBackground(layoutInflater.getContext().getDrawable(R.drawable.stop32x32));
            AddActivity add=new AddActivity();
            // add.mFileName;
        }
    }
}*/
}

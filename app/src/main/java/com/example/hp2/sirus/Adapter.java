package com.example.hp2.sirus;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.vstechlab.easyfonts.EasyFonts;

import java.util.ArrayList;



public class Adapter extends ArrayAdapter<Message> {

    @NonNull Context context; int resource; @NonNull ArrayList<Message> objects;

    public Adapter(@NonNull Context context, int resource, @NonNull ArrayList<Message> objects) {
        super(context, resource, objects);
        this.context=context;
        this.objects=objects;
        this.resource=resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v= LayoutInflater.from(context).inflate(resource,parent,false);
        TextView t=v.findViewById(R.id.txt123);
        t.setTypeface(EasyFonts.caviarDreams(context));
        if(objects.get(position).owner==0){
            t.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
        }else{
            t.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        }
        t.setText(objects.get(position).getText()+"\n");
        return v;
    }
}

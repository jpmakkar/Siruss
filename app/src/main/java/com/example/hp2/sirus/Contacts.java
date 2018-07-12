package com.example.hp2.sirus;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.widget.Toast.LENGTH_SHORT;


public class Contacts extends AppCompatActivity {

    ListView listView;
    ContentResolver resolver;
    ArrayAdapter<String> adapter;
    ArrayList<String> arrayList, arrayList1;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        if (ActivityCompat.checkSelfPermission(Contacts.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Contacts.this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    1);
            return;
        }

        listView = (ListView) findViewById(R.id.listview);
        arrayList = new ArrayList<>();
        resolver = getContentResolver();
        editText = (EditText) findViewById(R.id.search);
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor c = resolver.query(uri, null, null, null, null);

        while (c.moveToNext()) {
            StringBuffer o = new StringBuffer();
            o.append(c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)) + "\n");
            o.append(c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
            if (!arrayList.contains(o.toString()))
                arrayList.add(o.toString());
        }


        arrayList1 = new ArrayList<String>();
        arrayList1.addAll(arrayList);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() >= 0) {
                    arrayList.clear();
                    for (String s : arrayList1) {
                        if (s.toLowerCase().contains(charSequence.toString()) || s.contains(charSequence.toString())) {
                            arrayList.add(s);
                        }
                    }
                }
                Collections.sort(arrayList);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        Collections.sort(arrayList);
        adapter = new ArrayAdapter<String>(this, R.layout.contact, R.id.textcontact, arrayList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final String num = arrayList.get(i).substring(arrayList.get(i).indexOf("\n") + 1);
                new SweetAlertDialog(Contacts.this).setContentText(arrayList.get(i).toString()).setTitleText("Details").setConfirmText("Call").setCancelText("Sms").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + num));
                        if (ActivityCompat.checkSelfPermission(Contacts.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(Contacts.this,
                                    new String[]{Manifest.permission.CALL_PHONE},
                                    1);
                            return;
                        }
                        startActivity(intent);
                    }
                }).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        if (ActivityCompat.checkSelfPermission(Contacts.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(Contacts.this,
                                    new String[]{Manifest.permission.SEND_SMS},
                                    1);
                            return ;
                        }

                        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                        sendIntent.putExtra("sms_body", "");
                        sendIntent.setData(Uri.parse("smsto:"+num));

                        startActivity(sendIntent);
                    }
                }).show();
            }

        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(Contacts.this, "Permission granted to call your Contacts", LENGTH_SHORT).show();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(Contacts.this, "Permission denied to call your Contacts", LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}

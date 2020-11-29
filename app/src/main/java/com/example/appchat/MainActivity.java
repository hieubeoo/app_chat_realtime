package com.example.appchat;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {
    ListView listViewUser, listViewNoiDung;
    TextView txtNoiDung;
    EditText edtInput;
    ImageButton imgButtonAddUser, imgButtonSend;

    ArrayList<String> arrayUser;
    ArrayAdapter adapterUser;
    private Socket mSocket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AnhXa();
        try {
            mSocket = IO.socket("http://192.168.1.3:3000/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        mSocket.connect();

        //nhan ket qua cac user da dang ky
        mSocket.on("server-send-user", onRetriveUser);

        //nhan ket qua user dang ky thanh cong hay that bai
        mSocket.on("server-send-result",onRetriveResult);

        arrayUser = new ArrayList<>();
        adapterUser = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayUser);

        listViewUser.setAdapter(adapterUser);
        imgButtonAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtInput.getText().toString().trim().length() > 0){
                    mSocket.emit("client-register-user", edtInput.getText().toString());
                }
            }
        });
    }
    private Emitter.Listener onRetriveUser = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject object = (JSONObject) args[0];
                    arrayUser.clear();
                    try {
                        JSONArray jsonArray = object.getJSONArray("danhsach");
                        for(int i = 0; i < jsonArray.length(); i++){
                            String userName = jsonArray.getString(i);
                            arrayUser.add(userName);
                        }
                        adapterUser.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    };
    private Emitter.Listener onRetriveResult = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject object = (JSONObject) args[0];
                    try {
                        Boolean ketQua = object.getBoolean("ketqua");
                        if(ketQua){
                            Toast.makeText(MainActivity.this, "Tài khoản đã tồn tại", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
    public void AnhXa(){
        listViewUser        = findViewById(R.id.listViewUser);
        listViewNoiDung     = findViewById(R.id.listViewNoiDung);
        edtInput            = findViewById(R.id.editTextInput);
        imgButtonAddUser    = findViewById(R.id.imageButtonAdd);
        imgButtonSend       = findViewById(R.id.imageButtonSend);
    }
}
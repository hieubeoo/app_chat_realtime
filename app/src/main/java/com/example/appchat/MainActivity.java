package com.example.appchat;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {
    ListView listViewUser, listViewNoiDung;
    TextView txtNoiDung;
    EditText edtInput;
    ImageButton imgButtonAddUser, imgButtonSend;
    private Socket mSocket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnhXa();
        setContentView(R.layout.activity_main);
        try {
            mSocket = IO.socket("http://192.168.1.3:3000/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        mSocket.connect();
        mSocket.on("server-send-data",onRetriveData);
        mSocket.emit("client-send-data","Lap trinh Android");
    }
    private Emitter.Listener onRetriveData = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject object = (JSONObject) args[0];
                    try {
                        String ten = object.getString("noidung");
                        Toast.makeText(MainActivity.this, ten, Toast.LENGTH_SHORT).show();
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
package com.sni.checker;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private EditText editHost;
    private TextView txtResult;
    private OkHttpClient client;

    @Override
    protected void Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editHost = findViewById(R.id.editHost);
        Button btnCheck = findViewById(R.id.btnCheck);
        txtResult = findViewById(R.id.txtResult);

        client = new OkHttpClient();

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String host = editHost.getText().toString().trim();
                if (!host.isEmpty()) {
                    checkTLS(host);
                } else {
                    txtResult.setText("Por favor, ingresa un dominio válido.");
                }
            }
        });
    }

    private void checkTLS(String host) {
        txtResult.setText("Conectando con el host...");
        String url = "https://" + host;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtResult.setText("Error de red / SNI bloqueado:\n" + e.getMessage());
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String protocol = response.handshake() != null ? 
                        response.handshake().tlsVersion().name() : "Desconocido";
                final String cipher = response.handshake() != null ? 
                        response.handshake().cipherSuite().name() : "Desconocido";
                final int code = response.code();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtResult.setText("¡Conexión Exitosa!\n\n" +
                                "Código de Estado: " + code + "\n" +
                                "Versión de TLS: " + protocol + "\n" +
                                "Cifrado: " + cipher);
                    }
                });
            }
        });
    }
}

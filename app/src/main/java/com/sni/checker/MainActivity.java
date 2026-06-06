package com.sni.checker;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Handshake;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private EditText editHost;
    private TextView txtResult;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editHost = findViewById(R.id.editHost);
        Button btnCheck = findViewById(R.id.btnCheck);
        txtResult = findViewById(R.id.txtResult);

        client = new OkHttpClient();

        btnCheck.setOnClickListener(v -> {

            String host = editHost.getText().toString().trim();

            if (!host.isEmpty()) {
                checkTLS(host);
            } else {
                txtResult.setText("Ingresa un dominio válido");
            }
        });
    }

    private void checkTLS(String host) {

        txtResult.setText("Conectando...");

        String url = "https://" + host;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

                runOnUiThread(() ->
                        txtResult.setText(
                                "Error de conexión:\n" + e.getMessage()
                        )
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                Handshake handshake = response.handshake();

                String tlsVersion = "Desconocido";
                String cipher = "Desconocido";

                if (handshake != null) {
                    tlsVersion = handshake.tlsVersion().javaName();
                    cipher = handshake.cipherSuite().javaName();
                }

                int code = response.code();

                String finalTlsVersion = tlsVersion;
                String finalCipher = cipher;

                runOnUiThread(() ->

                        txtResult.setText(
                                "Conexión exitosa\n\n" +
                                "Código: " + code + "\n" +
                                "TLS: " + finalTlsVersion + "\n" +
                                "Cipher: " + finalCipher
                        )
                );

                response.close();
            }
        });
    }
}                });
            }
        });
    }
}
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

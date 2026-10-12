package com.example.vangustapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EditarPerfil extends AppCompatActivity {

    private ImageView imgEditarPerfil;
    private CardView cardFotoPerfil;
    private TextView tvAlterarFoto;
    private TextInputEditText txNomePerfil, txEmailPerfil;
    private Button btnSalvarAlteracoes;

    private ActivityResultLauncher<String> selecionarImagemLauncher;
    private byte[] imagemBytes = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_perfil_layout);

        imgEditarPerfil = findViewById(R.id.imgEditarPerfil);
        cardFotoPerfil = findViewById(R.id.FotoPerfil);
        tvAlterarFoto = findViewById(R.id.tvAlterarFoto);

        txNomePerfil = findViewById(R.id.txEditarNome);
        txEmailPerfil = findViewById(R.id.txEditarEmail);
        btnSalvarAlteracoes = findViewById(R.id.btnSalvarPerfil);

        carregarDadosAtuais();

        selecionarImagemLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        imgEditarPerfil.setImageURI(uri);

                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                            imagemBytes = redimensionarEConverterParaBytes(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Toast.makeText(this, "Foto selecionada com sucesso!", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        View.OnClickListener abrirGaleriaListener = v -> selecionarImagemLauncher.launch("image/*");

        if (cardFotoPerfil != null) {
            cardFotoPerfil.setOnClickListener(abrirGaleriaListener);
        }
        if (tvAlterarFoto != null) {
            tvAlterarFoto.setOnClickListener(abrirGaleriaListener);
        }

        btnSalvarAlteracoes.setOnClickListener(v -> {
            String novoNome = txNomePerfil.getText().toString().trim();
            String novoEmail = txEmailPerfil.getText().toString().trim();

            if (novoNome.isEmpty() || novoEmail.isEmpty()) {
                Toast.makeText(this, "Preencha o nome e o e-mail", Toast.LENGTH_SHORT).show();
            } else {
                salvarPerfilNaApi(novoNome, novoEmail, imagemBytes);
            }
        });

        ImageView btnVoltarEditarPerfil = findViewById(R.id.btnVoltarEditarPerfil);
        if (btnVoltarEditarPerfil != null) {
            btnVoltarEditarPerfil.setOnClickListener(v -> finish());
        }

        // Dentro do onCreate de EditarPerfil.java:
        Button btnIrParaAlterarSenha = findViewById(R.id.btnIrParaAlterarSenha);
        if (btnIrParaAlterarSenha != null) {
            btnIrParaAlterarSenha.setOnClickListener(v -> {
                Intent intent = new Intent(EditarPerfil.this, AlterarSenhaActivity.class);
                startActivity(intent);
            });
        }
    }

    private void carregarDadosAtuais() {
        SharedPreferences prefs = getSharedPreferences("VangustPrefs", MODE_PRIVATE);

        String nomeSalvo = prefs.getString("nome_usuario", "");
        String emailSalvo = prefs.getString("email_usuario", "");

        if (txNomePerfil != null) txNomePerfil.setText(nomeSalvo);
        if (txEmailPerfil != null) txEmailPerfil.setText(emailSalvo);

        // Carrega a foto em Base64 armazenada no SharedPreferences e converte para Bitmap
        String fotoBase64 = prefs.getString("foto_perfil", "");
        if (!fotoBase64.isEmpty()) {
            try {
                byte[] decodedString = Base64.decode(fotoBase64, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imgEditarPerfil.setImageBitmap(decodedByte);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private byte[] redimensionarEConverterParaBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        return stream.toByteArray();
    }

    private void salvarPerfilNaApi(String nome, String email, byte[] bytesFoto) {
        SharedPreferences prefs = getSharedPreferences("VangustPrefs", MODE_PRIVATE);
        int idUsuario = prefs.getInt("id_cliente", 1);

        String url = "http://10.0.2.2/api_hamburgueria/atualizar_perfil_cliente.php";

        // Converte os bytes da foto nova para Base64 (se houver)
        String imagemBase64 = "";
        if (bytesFoto != null && bytesFoto.length > 0) {
            imagemBase64 = Base64.encodeToString(bytesFoto, Base64.DEFAULT);
        }

        final String finalImagemBase64 = imagemBase64;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("sucesso")) {
                            Toast.makeText(this, "Perfil atualizado com sucesso!", Toast.LENGTH_SHORT).show();

                            JSONObject userObj = jsonObject.getJSONObject("usuario");
                            prefs.edit()
                                    .putString("nome_usuario", userObj.getString("nome"))
                                    .putString("email_usuario", userObj.getString("email"))
                                    .putString("foto_perfil", userObj.optString("foto", ""))
                                    .apply();

                            finish();
                        } else {
                            Toast.makeText(this, jsonObject.optString("erro", "Erro ao atualizar"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Erro ao processar resposta do servidor", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Erro de conexão com o servidor", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_usuario", String.valueOf(idUsuario));
                params.put("nome", nome);
                params.put("email", email);
                if (!finalImagemBase64.isEmpty()) {
                    params.put("foto_base64", finalImagemBase64);
                }
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }
}
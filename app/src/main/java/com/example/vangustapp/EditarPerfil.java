package com.example.vangustapp;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.textfield.TextInputEditText;

public class EditarPerfil extends AppCompatActivity {

    private ImageView imgEditarPerfil;
    private CardView cardFotoPerfil;
    private TextView tvAlterarFoto;
    private TextInputEditText txNomePerfil, txEmailPerfil;
    private Button btnSalvarAlteracoes;

    // Launcher para abrir a galeria e selecionar a imagem
    private ActivityResultLauncher<String> selecionarImagemLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_perfil_layout);



        // Ligar aos IDs exatos do seu layout XML
        imgEditarPerfil = findViewById(R.id.imgEditarPerfil);
        cardFotoPerfil = findViewById(R.id.FotoPerfil);
        tvAlterarFoto = findViewById(R.id.tvAlterarFoto);

        txNomePerfil = findViewById(R.id.txEditarNome);
        txEmailPerfil = findViewById(R.id.txEditarEmail);
        btnSalvarAlteracoes = findViewById(R.id.btnSalvarPerfil);

        // 1. Carregar dados atuais (Nome, Email e Foto guardados)
        carregarDadosAtuais();

        // 2. Configurar o seletor de imagens da galeria
        selecionarImagemLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        // Exibe a imagem selecionada no ImageView
                        imgEditarPerfil.setImageURI(uri);

                        // Guarda o caminho (URI) da imagem no SharedPreferences
                        SharedPreferences prefs = getSharedPreferences("VangustPrefs", MODE_PRIVATE);
                        prefs.edit().putString("foto_perfil", uri.toString()).apply();

                        Toast.makeText(this, "Foto de perfil atualizada!", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // 3. Ação para abrir a galeria ao tocar no Cartão da foto ou no texto
        View.OnClickListener abrirGaleriaListener = v -> selecionarImagemLauncher.launch("image/*");

        if (cardFotoPerfil != null) {
            cardFotoPerfil.setOnClickListener(abrirGaleriaListener);
        }
        if (tvAlterarFoto != null) {
            tvAlterarFoto.setOnClickListener(abrirGaleriaListener);
        }

        // 4. Ação do botão de salvar alterações de texto (nome)
        btnSalvarAlteracoes.setOnClickListener(v -> {
            String novoNome = txNomePerfil.getText().toString().trim();

            if (!novoNome.isEmpty()) {
                SharedPreferences prefs = getSharedPreferences("VangustPrefs", MODE_PRIVATE);
                prefs.edit().putString("nome_usuario", novoNome).apply();

                Toast.makeText(this, "Perfil atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "O nome não pode estar vazio", Toast.LENGTH_SHORT).show();
            }
        });

        // Ligar o botão de voltar
        ImageView btnVoltarEditarPerfil = findViewById(R.id.btnVoltarEditarPerfil);
        if (btnVoltarEditarPerfil != null) {
            btnVoltarEditarPerfil.setOnClickListener(v -> finish());
        }
    }

    private void carregarDadosAtuais() {
        SharedPreferences prefs = getSharedPreferences("VangustPrefs", MODE_PRIVATE);

        // Carregar Nome e Email
        String nomeSalvo = prefs.getString("nome_usuario", "");
        String emailSalvo = prefs.getString("email_usuario", "");

        if(txNomePerfil != null) txNomePerfil.setText(nomeSalvo);
        if(txEmailPerfil != null) txEmailPerfil.setText(emailSalvo);

        // Carregar a foto guardada anteriormente no SharedPreferences (se existir)
        String fotoUriStr = prefs.getString("foto_perfil", "");
        if (!fotoUriStr.isEmpty()) {
            imgEditarPerfil.setImageURI(Uri.parse(fotoUriStr));
        }
    }
}
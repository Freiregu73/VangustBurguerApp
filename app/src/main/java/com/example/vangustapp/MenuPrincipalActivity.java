package com.example.vangustapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MenuPrincipalActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    DrawerLayout drawerLayout;
    View headerView;
    ImageView btnMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.menu_principal_layout);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        drawerLayout = findViewById(R.id.drawer_layout);
        headerView = findViewById(R.id.header_principal);
        btnMenu = headerView.findViewById(R.id.btnMenu);
        bottomNavigationView = findViewById(R.id.bottomNavegation);

        atualizarPerfilHeader();
        verificarEEnviarTokenFCM();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new InicioFragment())
                    .commit();
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.item_1) {
                selectedFragment = new InicioFragment();
            } else if (itemId == R.id.item_2) {
                selectedFragment = new CardapioFragment();
            } else if (itemId == R.id.item_3) {
                selectedFragment = new FavoritoFragment();
            } else if (itemId == R.id.item_4) {
                selectedFragment = new PedidosFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.mPerfil) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new PerfilFragment())
                        .commit();
            } else if (id == R.id.mHistorico) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new PedidosFragment())
                        .commit();
            } else if (id == R.id.mEndereco) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new EnderecoFragment())
                        .commit();
            } else if (id == R.id.mFavoritos) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new FavoritoFragment())
                        .commit();
            } else if (id == R.id.mCupom) {
                startActivity(new android.content.Intent(MenuPrincipalActivity.this, CupomActivity.class));
            }

            drawerLayout.closeDrawer(GravityCompat.END);
            return true;
        });

        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.END));

        if (getIntent().getBooleanExtra("abrir_carrinho", false)) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new CarrinhoFragment())
                    .commit();
        }
    }

    private void verificarEEnviarTokenFCM() {
        SharedPreferences preferences = getSharedPreferences("VangustPrefs", MODE_PRIVATE);
        String emailUsuario = preferences.getString("email_usuario", "");

        if (emailUsuario.isEmpty()) return;

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String tokenFCM = task.getResult();
                enviarTokenParaServidor(emailUsuario, tokenFCM);
            }
        });
    }

    private void enviarTokenParaServidor(String email, String token) {
        String url = "https://vangustburguer.com.br/api_hamburgueria/salvar_token.php";

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        // Token guardado com sucesso no servidor
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Erro de ligação ignorado para não afetar a experiência do utilizador
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("fcm_token", token);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void atualizarPerfilHeader() {
        SharedPreferences preferences = getSharedPreferences("VangustPrefs", MODE_PRIVATE);
        String nomeCompleto = preferences.getString("nome_usuario", "Utilizador");
        String primeiroNome = nomeCompleto.split(" ")[0];
        String fotoBase64 = preferences.getString("foto_perfil", "");

        TextView tvNomePrincipal = headerView.findViewById(R.id.tvNomePrincipal);
        if (tvNomePrincipal != null) {
            tvNomePrincipal.setText(primeiroNome);
        }

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerDrawer = navigationView.getHeaderView(0);
        if (headerDrawer != null) {
            TextView tvNomeDrawer = headerDrawer.findViewById(R.id.tvNomeDrawer);
            if (tvNomeDrawer != null) {
                tvNomeDrawer.setText(primeiroNome.toUpperCase());
            }

            ImageView imgHeaderPerfil = headerDrawer.findViewById(R.id.imgHeaderPerfil);
            if (imgHeaderPerfil != null && !fotoBase64.isEmpty()) {
                try {
                    byte[] decodedString = Base64.decode(fotoBase64, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    imgHeaderPerfil.setImageBitmap(decodedByte);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        atualizarPerfilHeader();
    }
}
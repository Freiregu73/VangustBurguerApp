package com.example.vangustapp;

import android.content.SharedPreferences;
import android.os.Bundle;
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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;


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

        // 1. Inicializar os componentes principais logo no início
        drawerLayout = findViewById(R.id.drawer_layout);
        headerView = findViewById(R.id.header_principal);
        btnMenu = headerView.findViewById(R.id.btnMenu);
        bottomNavigationView = findViewById(R.id.bottomNavegation);

        // 2. Atualizar o nome do usuário nos cabeçalhos ao abrir a activity
        atualizarNomeUsuario();

        // 3. Carregar fragmento inicial ao abrir o app
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new InicioFragment())
                    .commit();
        }

        // 4. Configurar o clique no menu inferior
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

            // Efetivar a troca
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });

        // 5. Configurar os cliques no Menu Lateral (NavigationView)
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

            // Fecha o menu lateral após o clique
            drawerLayout.closeDrawer(GravityCompat.END);
            return true;
        });

        btnMenu.setOnClickListener(v -> {
            // Abre o menu lateral
            drawerLayout.openDrawer(GravityCompat.END);
        });

        // Verificar se deve abrir o carrinho diretamente
        if (getIntent().getBooleanExtra("abrir_carrinho", false)) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new CarrinhoFragment())
                    .commit();
        }
    }

    // Método para buscar o nome salvo no SharedPreferences e atualizar os cabeçalhos
    private void atualizarNomeUsuario() {
        SharedPreferences preferences = getSharedPreferences("VangustPrefs", MODE_PRIVATE);
        String nomeCompleto = preferences.getString("nome_usuario", "Usuário");
        String primeiroNome = nomeCompleto.split(" ")[0];

        // Atualizar no cabeçalho principal da barra superior (header_principal.xml)
        TextView tvNomePrincipal = headerView.findViewById(R.id.tvNomePrincipal);
        if (tvNomePrincipal != null) {
            tvNomePrincipal.setText(primeiroNome);
        }

        // Atualizar no menu lateral (header_drawer.xml)
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerDrawer = navigationView.getHeaderView(0);
        if (headerDrawer != null) {
            TextView tvNomeDrawer = headerDrawer.findViewById(R.id.tvNomeDrawer);
            if (tvNomeDrawer != null) {
                tvNomeDrawer.setText(primeiroNome.toUpperCase());
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Atualiza o nome caso o utilizador mude algo na tela de Editar Perfil e volte
        atualizarNomeUsuario();
    }
}
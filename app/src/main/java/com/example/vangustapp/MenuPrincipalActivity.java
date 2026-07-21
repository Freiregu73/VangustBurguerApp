package com.example.vangustapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;


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
        bottomNavigationView = findViewById(R.id.bottomNavegation);

        // 1. Carregar fragmento inicial ao abrir o app
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new InicioFragment()) // Certifique-se de ter essa classe
                    .commit();
        }

        // 2. Configurar o clique no menu
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

            // 3. Efetivar a troca
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });

        drawerLayout = findViewById(R.id.drawer_layout);
        headerView = findViewById(R.id.header_principal);
        btnMenu = headerView.findViewById(R.id.btnMenu);

        btnMenu.setOnClickListener(v -> {
            // Abre o menu lateral
            drawerLayout.openDrawer(GravityCompat.START);
        });

    }
}
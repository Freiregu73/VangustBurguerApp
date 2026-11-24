package com.example.vangustapp;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String titulo = "Atualização no Pedido";
        String corpo = "O status do seu pedido foi atualizado.";

        // Lê os dados enviados pelo PHP através do bloco 'data' (funciona com app aberta ou fechada)
        if (remoteMessage.getData().size() > 0) {
            Map<String, String> data = remoteMessage.getData();
            if (data.containsKey("title")) {
                titulo = data.get("title");
            }
            if (data.containsKey("body")) {
                corpo = data.get("body");
            }
        }

        // Dispara a notificação visualmente através do NotificationHelper
        NotificationHelper.mostrarNotificacao(getApplicationContext(), titulo, corpo);
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        // O Firebase gerou um token novo: envia ao servidor para as notificações continuarem chegando
        String email = getSharedPreferences("VangustPrefs", MODE_PRIVATE).getString("email_usuario", "");
        FcmTokenSender.enviar(getApplicationContext(), email, token);
    }
}
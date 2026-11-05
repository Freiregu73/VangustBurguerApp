package com.example.vangustapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class NotificationHelper {

    private static final String CHANNEL_ID = "status_pedido_channel";
    private static final String CHANNEL_NAME = "Estado do Pedido";
    private static final String CHANNEL_DESC = "Notificações sobre a alteração do estado do seu pedido";

    public static void mostrarNotificacao(Context context, String titulo, String mensagem) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Criar o canal de notificação para Android 8.0 ou superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(CHANNEL_DESC);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Intent para abrir o app ao tocar na notificação (ex: abrir a MainActivity ou ir direto para os Pedidos)
        Intent intent = new Intent(context, MenuPrincipalActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE);

        // Construir a notificação
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_vangust_burguer) // Substitua pelo ícone do seu app se necessário
                .setContentTitle(titulo)
                .setContentText(mensagem)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        // Disparar a notificação
        if (notificationManager != null) {
            // Usamos o System.currentTimeMillis() como ID para que cada notificação seja única
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }
}
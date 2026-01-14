package com.example.fogfix;

import org.geysermc.geyser.api.connection.GeyserConnection;
import org.geysermc.geyser.api.extension.Extension;
import org.geysermc.geyser.api.event.lifecycle.GeyserPostInitializeEvent;
import org.geysermc.geyser.api.GeyserApi;
import org.geysermc.geyser.session.GeyserSession; // セッションを直接インポート

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class NetherFogFix implements Extension {

    private static final String BEDROCK_FOG_HELL = "minecraft:fog_hell";

    @org.geysermc.event.subscribe.Subscribe
    public void onPostInit(GeyserPostInitializeEvent event) {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                for (GeyserConnection connection : GeyserApi.api().onlineConnections()) {
                    // GeyserConnection を GeyserSession にキャスト（変換）して詳細情報を取得します
                    if (connection instanceof GeyserSession) {
                        GeyserSession session = (GeyserSession) connection;

                        // GeyserExtrasの ExtrasPlayer.java と同じ判定方法です
                        if (session.getDimensionType().isNetherLike()) {
                            // 霧を削除してバイオーム固有の霧を復活させる
                            session.camera().removeFog(BEDROCK_FOG_HELL);
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }, 1, 1, TimeUnit.SECONDS);
    }
}

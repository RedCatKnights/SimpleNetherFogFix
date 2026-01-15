package com.example.fogfix;

import org.geysermc.geyser.api.GeyserApi;
import org.geysermc.geyser.api.connection.GeyserConnection;
import org.geysermc.geyser.api.event.lifecycle.GeyserPostInitializeEvent;
import org.geysermc.geyser.api.event.lifecycle.GeyserShutdownEvent;
import org.geysermc.geyser.api.extension.Extension;
import org.geysermc.geyser.session.GeyserSession;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NetherFogFix implements Extension {

    // 定数として霧のIDを定義（タイポ防止）
    private static final String BEDROCK_FOG_ID = "minecraft:fog_hell";

    // 設定値を保持する変数
    private int checkInterval = 30;
    private int loginDelay = 1000;
    private int dimensionDelay = 1000;

    // 非同期処理用のエグゼクーター
    private ScheduledExecutorService executor;

    /**
     * 拡張機能の初期化処理
     */
    @org.geysermc.event.subscribe.Subscribe
    public void onPostInit(GeyserPostInitializeEvent event) {
        // 1. 設定の読み込みを独立したメソッドで実行
        loadConfiguration();

        // 2. スレッドプールの初期化（1つの専用スレッドで全処理を完結）
        this.executor = Executors.newSingleThreadScheduledExecutor();

        // 3. 定期チェックタスクの登録
        if (checkInterval > 0) {
            this.executor.scheduleAtFixedRate(this::checkAllPlayers, 5, checkInterval, TimeUnit.SECONDS);
        }

        this.logger().info(String.format("NetherFogFix Started! [Interval: %ds, Login: %dms, Dim: %dms]",
                checkInterval, loginDelay, dimensionDelay));
    }

    /**
     * 接続イベントの処理
     */
    @org.geysermc.event.subscribe.Subscribe
    public void onConnection(org.geysermc.geyser.api.event.connection.ConnectionEvent event) {
        GeyserConnection connection = event.connection();

        // ログイン時の遅延実行（0より大きい場合）
        if (loginDelay > 0) {
            scheduleFogRemoval(connection, loginDelay);
        }

        // 次元移動時の遅延実行（設定が0より大きく、かつログイン遅延と異なる場合のみ追加予約）
        if (dimensionDelay > 0 && dimensionDelay != loginDelay) {
            scheduleFogRemoval(connection, dimensionDelay);
        }
    }

    /**
     * 全プレイヤーをスキャンして霧を削除する（定期実行用）
     */
    private void checkAllPlayers() {
        try {
            for (GeyserConnection connection : GeyserApi.api().onlineConnections()) {
                executeRemoveFog(connection);
            }
        } catch (Exception e) {
            this.logger().error("Error occurred during periodic fog check", e);
        }
    }

    /**
     * 指定された遅延後に霧削除を予約する
     */
    private void scheduleFogRemoval(GeyserConnection connection, int delayMs) {
        this.executor.schedule(() -> executeRemoveFog(connection), delayMs, TimeUnit.MILLISECONDS);
    }

    /**
     * 霧削除の共通ロジック
     * 安全性を高めるため、セッションの状態を厳密にチェックします
     */
    private void executeRemoveFog(GeyserConnection connection) {
        // GeyserSessionでない場合は無視
        if (!(connection instanceof GeyserSession)) return;

        GeyserSession session = (GeyserSession) connection;

        try {
            // セッションが有効かつネザー環境であるか確認
            // session.getDimensionType() のヌルチェックも兼ねた安全な呼び出し
            if (session.getDimensionType() != null && session.getDimensionType().isNetherLike()) {
                session.camera().removeFog(BEDROCK_FOG_ID);
            }
        } catch (Exception ignored) {
            // プレイヤーが実行直前にログアウトした場合などの例外を安全に無視
        }
    }

    /**
     * 拡張機能の終了処理
     */
    @org.geysermc.event.subscribe.Subscribe
    public void onShutdown(GeyserShutdownEvent event) {
        if (this.executor != null) {
            this.executor.shutdown();
            try {
                // 進行中のタスクが終了するのを最大1秒待機
                if (!this.executor.awaitTermination(1, TimeUnit.SECONDS)) {
                    this.executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                this.executor.shutdownNow();
            }
            this.logger().info("NetherFogFix safely stopped.");
        }
    }

    /**
     * 設定ファイルの生成と読み込み
     * 分離することでメインロジックの視認性を向上
     */
    private void loadConfiguration() {
        try {
            File dataFolder = this.dataFolder().toFile();
            if (!dataFolder.exists()) dataFolder.mkdirs();

            File configFile = new File(dataFolder, "config.yml");
            // リソースからデフォルトファイルをコピー
            if (!configFile.exists()) {
                try (InputStream in = getClass().getClassLoader().getResourceAsStream("config.yml")) {
                    if (in != null) Files.copy(in, configFile.toPath());
                }
            }

            // 設定の解析
            for (String line : Files.readAllLines(configFile.toPath())) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#") || !line.contains(":")) continue;

                String[] parts = line.split(":", 2);
                String key = parts[0].trim();
                String value = parts[1].trim();

                try {
                    switch (key) {
                        case "check-interval-seconds":
                            this.checkInterval = Integer.parseInt(value);
                            break;
                        case "login-delay-milliseconds":
                            this.loginDelay = Integer.parseInt(value);
                            break;
                        case "dimension-change-delay-milliseconds":
                            this.dimensionDelay = Integer.parseInt(value);
                            break;
                    }
                } catch (NumberFormatException e) {
                    this.logger().warning("Invalid number format for '" + key + "': " + value);
                }
            }
        } catch (Exception e) {
            this.logger().error("Could not load configuration, using internal defaults.", e);
        }
    }
}

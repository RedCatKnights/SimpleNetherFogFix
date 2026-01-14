plugins {
    java
}

group = "com.example"
version = "1.0.0"

repositories {
    mavenCentral()
    maven { url = uri("https://repo.opencollab.dev/main/") }
    maven { url = uri("https://repo.viaversion.com/main/") }
}

dependencies {
    // APIではなく、GeyserExtrasが実際に参照している core 2.9.1 を指定します
    compileOnly("org.geysermc.geyser:core:2.9.1-SNAPSHOT")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
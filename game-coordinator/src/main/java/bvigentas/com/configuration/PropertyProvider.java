package bvigentas.com.configuration;

import java.io.IOException;
import java.util.Properties;

public class PropertyProvider {

    public static PropertyProvider INSTANCE;

    private static Properties properties;

    public PropertyProvider() {
        properties = new Properties();
        try {
            properties.load(PropertyProvider.class.getClassLoader().getResourceAsStream("config.properties"));
        } catch (IOException e) {
            throw new RuntimeException("Propertie file not found");
        }

    }

    public static String getUser() {
        return properties.getProperty("steam.user");
    }

    public static String getPassword() {
        return properties.getProperty("steam.passworld");
    }

    public static Integer getCsgoAppId() {
        return Integer.parseInt(properties.getProperty("steam.csgo.appid"));
    }

    public static Long getClientTimeout() {
        return Long.parseLong(properties.getProperty("game.coordinator.client.timeout"));
    }

    public static synchronized PropertyProvider getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PropertyProvider();
        }

        return INSTANCE;
    }

}

package com.example.beadando.forex;

import com.oanda.v20.account.AccountID;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Az Oanda hozzáférési adatait az oanda.properties fájlból olvassa be.
 * Először a program indítási mappájában keresi (a beadando.jar mellett),
 * ha ott nincs, akkor a classpath-on (src/main/resources).
 * A token így nem kerül bele a forráskódba.
 */
@Component
public class OandaConfig {

    private static final String FILE_NAME = "oanda.properties";

    private String url = "https://api-fxpractice.oanda.com";
    private String accountId;
    private String token;

    public OandaConfig() {
        Properties props = new Properties();
        try {
            Path external = Path.of(FILE_NAME);
            if (Files.exists(external)) {
                try (InputStream in = new FileInputStream(external.toFile())) {
                    props.load(in);
                }
            } else {
                try (InputStream in = OandaConfig.class.getClassLoader().getResourceAsStream(FILE_NAME)) {
                    if (in != null) {
                        props.load(in);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Az " + FILE_NAME + " beolvasása nem sikerült: " + e.getMessage());
        }

        url = props.getProperty("oanda.url", url).trim();
        accountId = props.getProperty("oanda.accountId", "").trim();
        token = props.getProperty("oanda.token", "").trim();
    }

    public boolean isConfigured() {
        return !accountId.isEmpty() && !token.isEmpty();
    }

    public String getUrl() {
        return url;
    }

    public AccountID getAccountId() {
        return new AccountID(accountId);
    }

    public String getToken() {
        return token;
    }
}

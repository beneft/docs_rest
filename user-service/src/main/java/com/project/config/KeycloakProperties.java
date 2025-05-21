package com.project.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakProperties {

    private String serverUrl;      // http://localhost:8080   (без завершающего / )
    private String realm;          // documentsUsersRealm
    private String clientId;       // documents-flow-client
    private String clientSecret;   // ****
    private String adminUser;      // admin
    private String adminPassword;  // admin

    /** Полный URL для запроса токена grant_type=password */
    public String tokenUrl() {
        return serverUrl + "/realms/" + realm + "/protocol/openid-connect/token";
    }
}

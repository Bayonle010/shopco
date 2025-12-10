package com.shopco.user.component;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.superadmin")
public class SuperAdminProperties {
    private String email;
    private String password;
}

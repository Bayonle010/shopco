package com.shopco.mail;

import lombok.Getter;

@Getter
public enum EmailTemplateName {
    VERIFY_ACCOUNT("activate_account");
    
    private String name;

    EmailTemplateName(String name) {
        this.name = name;
    }
}


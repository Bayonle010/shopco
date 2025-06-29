package com.shopco.mail;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MailTokenRepository extends JpaRepository<MailToken, UUID> {

}

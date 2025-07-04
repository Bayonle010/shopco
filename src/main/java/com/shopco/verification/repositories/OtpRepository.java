package com.shopco.verification.repositories;

import com.shopco.verification.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface OtpRepository extends JpaRepository<Otp, UUID> {
    Otp findByToken(String token);

}

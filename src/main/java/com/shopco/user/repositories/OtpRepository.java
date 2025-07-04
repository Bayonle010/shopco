package com.shopco.user.repositories;

import com.shopco.user.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OtpRepository extends JpaRepository<Otp, UUID> {

}

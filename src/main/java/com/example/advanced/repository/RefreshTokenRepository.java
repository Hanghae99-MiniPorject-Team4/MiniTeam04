package com.example.advanced.repository;

import com.example.advanced.domain.Member;
import com.example.advanced.domain.RefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
  Optional<RefreshToken> findByMember(Member member);
}

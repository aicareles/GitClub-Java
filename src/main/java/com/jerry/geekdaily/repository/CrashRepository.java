package com.jerry.geekdaily.repository;

import com.jerry.geekdaily.domain.Crash;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrashRepository extends JpaRepository<Crash,Integer> {
}

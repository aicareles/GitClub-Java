package com.jerry.geekdaily.repository;

import com.jerry.geekdaily.domain.Crash;
import com.jerry.geekdaily.domain.StatusInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppStatusInfoRepository extends JpaRepository<StatusInfo,Integer> {
}

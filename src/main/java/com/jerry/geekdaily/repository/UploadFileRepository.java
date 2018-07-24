package com.jerry.geekdaily.repository;

import com.jerry.geekdaily.domain.UploadFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface UploadFileRepository extends JpaRepository<UploadFile,Integer> {
//    @Transactional(readOnly = true)
//    UploadFile getUploadFilesById(@Param("id")int id);

    UploadFile findByFileName(@Param("fileName")String fileName);

    @Transactional()
    void deleteByFileName(@Param("fileName")String fileName);
}

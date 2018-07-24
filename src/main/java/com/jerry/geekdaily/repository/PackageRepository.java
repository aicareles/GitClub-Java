package com.jerry.geekdaily.repository;

import com.jerry.geekdaily.domain.Package;
import com.jerry.geekdaily.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PackageRepository extends JpaRepository<Package,Integer> {
//    @Transactional(readOnly = true)
//    UploadFile getUploadFilesById(@Param("id")int id);

    @Query("select u from Package u where u.package_name = :package_name")
    Package findByPackage_name(@Param("package_name") String package_name);
}

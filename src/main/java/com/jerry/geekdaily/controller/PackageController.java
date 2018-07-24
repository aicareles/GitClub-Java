package com.jerry.geekdaily.controller;

import com.jerry.geekdaily.base.Result;
import com.jerry.geekdaily.base.ResultUtils;
import com.jerry.geekdaily.domain.Package;
import com.jerry.geekdaily.domain.User;
import com.jerry.geekdaily.repository.PackageRepository;
import com.jerry.geekdaily.repository.UserRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "PackageController", description = "安装包相关接口")
@RestController
public class PackageController {

    private final static Logger logger = LoggerFactory.getLogger(PackageController.class);

    @Autowired
    private PackageRepository packageRepository;

    //开关接口  不用管  测试用
    @PostMapping("/getPackageStatus")
    public Result<Package> getPackageStatus(@RequestParam("package_name")String package_name){
        Package p = packageRepository.findByPackage_name(package_name);
        if(!StringUtils.isEmpty(p)){
            return ResultUtils.ok(p);
        }
        return ResultUtils.error("未找到相关包信息!");
    }

}

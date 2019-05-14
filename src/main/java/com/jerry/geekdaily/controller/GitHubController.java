package com.jerry.geekdaily.controller;


import com.jerry.geekdaily.base.Result;
import com.jerry.geekdaily.base.ResultUtils;
import com.jerry.geekdaily.config.Constans;
import com.jerry.geekdaily.util.HttpUtils;
import com.jerry.geekdaily.util.MarkdownUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "GitHubController", description = "GitHub相关接口")
@RestController
public class GitHubController {

    @ApiOperation(value = "获取GitHub仓库的详细信息", notes = "获取GitHub仓库的详细信息接口")
    @PostMapping("/getGitHubDetail")
    public Result<GHRepository> getGitHubDetail(@RequestParam String link) {
        String repositoryName = MarkdownUtils.getRepositoryName(link);//如：Alex-Jerry/Android-BLE
        StringBuffer url = new StringBuffer()
                .append(Constans.GitHub.BASE_GITHUB_URL)
                .append(Constans.GitHub.REPOS)
                .append(repositoryName);
        return ResultUtils.ok(HttpUtils.sendGet(String.valueOf(url),""));
    }

}

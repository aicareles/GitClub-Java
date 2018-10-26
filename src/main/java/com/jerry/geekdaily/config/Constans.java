package com.jerry.geekdaily.config;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Constans {

    @Retention(RetentionPolicy.SOURCE)
    public @interface RedisKey {
        String ARTICLE_TOTAL_VIEWS = "article_total_views";//当天文章总阅读数
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface WeChat {
        String WECHAT_APP_ID = "wx5cd48edea47a1f48";//小程序app_id
        String WECHAT_SECRET = "f287022f6c86d348ef6db4ad5e93709b";//小程序secret
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface GitHub {
        String BASE_GITHUB_URL = "https://api.github.com";
        String REPOS = "/repos/";//仓库
    }
}

package com.jerry.geekdaily.util;

import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;

public class GitHubUtils {

    public static String getRepository(String link){
        GitHub gitHub = null;
        try {
            gitHub = GitHub.connectAnonymously();
            String repositoryName = MarkdownUtils.getRepositoryName(link);
            System.out.println(gitHub.getRepository(repositoryName).getReadme().getContent());
            return gitHub.getRepository(repositoryName).getReadme().getContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

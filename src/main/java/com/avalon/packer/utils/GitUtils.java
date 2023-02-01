package com.avalon.packer.utils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * 注意：使用时必须设置先设置资源lu ji
 */
@Data
@Slf4j
public class GitUtils {
    private String repoPath;
    private String gitUser;
    private String gitUserPwd;
    private String localPath;
    private String branchName = "master";

    public GitUtils(String gitUser, String gitUserPwd,String repoPath,String localPath) {
        this.gitUser = gitUser;
        this.gitUserPwd = gitUserPwd;
        this.localPath = localPath;
        this.repoPath = repoPath;
    }
    public GitUtils(String gitUser, String gitUserPwd,String repoPath,String localPath,String branchName) {
        this.gitUser = gitUser;
        this.gitUserPwd = gitUserPwd;
        this.localPath = localPath;
        this.repoPath = repoPath;
        this.branchName = branchName;
    }

    public  Git openRpo(){
        Git git = null;
        try {
            Repository repository = new FileRepositoryBuilder()
                    .setGitDir(Paths.get(this.localPath, ".git").toFile())
                    .build();
            git = new Git(repository);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return git;
    }

    private CredentialsProvider createCredential() {
        return new UsernamePasswordCredentialsProvider(this.gitUser, this.gitUserPwd);
    }

    public boolean pull() {
        boolean pullFlag = true;
        try (Git git = openRpo()) {
           git.pull()
                   .setCredentialsProvider(createCredential())
                   .setRemoteBranchName(this.branchName)
                   .call();
        } catch (Exception e) {
            e.printStackTrace();
            pullFlag = false;
        }
        return pullFlag;
    }

    public boolean cloneRemoteRepo() {
        boolean pullFlag = true;
        try {
            File localFile = new File(this.getLocalPath());
            Git.cloneRepository()
                    .setURI(this.repoPath)
                    .setDirectory(localFile)
                    .setCredentialsProvider(createCredential())
                    .setCloneSubmodules(true)
                    .setBranch(this.branchName)
                    .call();
        } catch (Exception e) {
            e.printStackTrace();
            pullFlag = false;
        }
        return pullFlag;
    }

    public static void main(String[] args) throws IOException {
        GitUtils gitUtils = new GitUtils("",
                "",
                "",
                "");
        FileUtils.deleteDir(gitUtils.getLocalPath());
        gitUtils.cloneRemoteRepo();
    }


}

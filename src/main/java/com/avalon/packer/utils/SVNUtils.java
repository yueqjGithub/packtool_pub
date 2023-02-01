package com.avalon.packer.utils;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.wc.*;
import org.tmatesoft.svn.core.wc.admin.SVNAdminClient;
import org.tmatesoft.svn.core.wc.admin.SVNSyncInfo;
import org.tmatesoft.svn.core.wc2.ISvnObjectReceiver;
import org.tmatesoft.svn.core.wc2.SvnList;
import org.tmatesoft.svn.core.wc2.SvnOperationFactory;
import org.tmatesoft.svn.core.wc2.SvnTarget;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SVNUtils {
    private SVNClientManager ourClientManager;
    private SVNURL repositoryOptUrl;
    private String userName;
    private String passwd;
    public SVNUtils(String userName, String passwd){
        this.userName=userName;
        this.passwd=passwd;
    }
    private void setUpSVNClient(String userName,String passwd){
        SVNRepositoryFactoryImpl.setup();
        ISVNOptions options = SVNWCUtil.createDefaultOptions(true);
        ourClientManager = SVNClientManager.newInstance(
                (DefaultSVNOptions) options, userName, passwd);

    }
    public boolean checkOutModel(String svnUrl,String dirPath, Boolean deep){
        setUpSVNClient(userName,passwd);
        File outDir=new File(dirPath);
        SVNUpdateClient updateClient = ourClientManager.getUpdateClient();
        updateClient.setIgnoreExternals(false);
        try {
            log.info("svn参数正常，执行拉取");
            repositoryOptUrl=SVNURL.parseURIEncoded(svnUrl);
            long l = updateClient.doCheckout(repositoryOptUrl, outDir, SVNRevision.HEAD, SVNRevision.HEAD, deep, true);
            log.info(String.valueOf(l));
            if(l>0){
                return true;
            }
        } catch (SVNException e) {
            // TODO Auto-generated catch block
            log.error(e.getMessage());
            log.error("执行svn拉取时进入catch");
            e.printStackTrace();
        }
        return false;
    }
    public boolean updateModel(String svnUrl,String dirPath){
        setUpSVNClient(userName,passwd);
        File outDir=new File(dirPath);
        //outDir.mkdirs();//创建目录
        SVNUpdateClient updateClient=ourClientManager.getUpdateClient();
        updateClient.setIgnoreExternals(false);
        try {
            repositoryOptUrl=SVNURL.parseURIEncoded(svnUrl);
            long l = updateClient.doUpdate(outDir,SVNRevision.HEAD,SVNDepth.INFINITY,true,true);
            if(l>0){
                return true;
            }
        } catch (SVNException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }
    /**
     * 上传模型
     * @param dirPath
     */
    public void uploadMoel(String dirPath,String modelName){
        setUpSVNClient(userName,passwd);
        File impDir = new File(dirPath);
        SVNCommitClient commitClient = ourClientManager.getCommitClient();
        commitClient.setIgnoreExternals(false);
        try {
            repositoryOptUrl=SVNURL.parseURIEncoded(RepositoryInfo.buffUrl+modelName);
            commitClient.doImport(impDir,
                    repositoryOptUrl, "import operation!", null, true, true,
                    SVNDepth.INFINITY);
        } catch (SVNException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 下载模型
     */
    public void downloadModel(String downloadModelName,String dirPath){
        setUpSVNClient(userName,passwd);
        File outDir=new File(dirPath+"/"+downloadModelName);
        //outDir.mkdirs();//创建目录
        SVNUpdateClient updateClient=ourClientManager.getUpdateClient();
        updateClient.setIgnoreExternals(false);

        try {
            repositoryOptUrl=SVNURL.parseURIEncoded(RepositoryInfo.storeUrl+downloadModelName);
            updateClient.doExport(repositoryOptUrl, outDir, SVNRevision.HEAD, SVNRevision.HEAD, "downloadModel",true,true);
        } catch (SVNException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    /**
     * 删除模型
     */
    public void deleteModel(String deleteModelName){
        setUpSVNClient(userName,passwd);
        SVNCommitClient commitClient=ourClientManager.getCommitClient();
        commitClient.setIgnoreExternals(false);

        try {
            repositoryOptUrl=SVNURL.parseURIEncoded(RepositoryInfo.storeUrl+deleteModelName);
            SVNURL deleteUrls[]=new SVNURL[1];
            deleteUrls[0]=repositoryOptUrl;
            commitClient.doDelete(deleteUrls, "delete model");
        } catch (SVNException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * 移动模型
     */
    public void moveModel(String modelName){
        setUpSVNClient(userName,passwd);
        SVNCopyClient copyClient=ourClientManager.getCopyClient();
        copyClient.setIgnoreExternals(false);

        try {
            repositoryOptUrl=SVNURL.parseURIEncoded(RepositoryInfo.buffUrl+modelName);
            SVNURL destUrl=SVNURL.parseURIEncoded(RepositoryInfo.storeUrl+modelName);
            SVNCopySource[] copySources = new SVNCopySource[1];
            copySources[0] = new SVNCopySource(SVNRevision.HEAD, SVNRevision.HEAD, repositoryOptUrl);

            copyClient.doCopy(copySources, destUrl, true, false, false, "move", null);
        } catch (SVNException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * 获取SVN目录
     */
    static public List<String> getSvnPathList (String svnPath, String NAME, String PASSWORD) {
        ArrayList<String> versions = new ArrayList<String>();
        SVNURL svnurl = null;
        try {
            svnurl = SVNURL.parseURIEncoded(svnPath);
        } catch (SVNException e) {
            e.printStackTrace();
        }

        SVNRevision revision = SVNRevision.HEAD;
        SvnOperationFactory operationFactory = new SvnOperationFactory();
        operationFactory.setAuthenticationManager(new BasicAuthenticationManager(NAME, PASSWORD));
        SvnList list = operationFactory.createList();

        list.setDepth(SVNDepth.IMMEDIATES);
        list.setRevision(revision);
        list.addTarget(SvnTarget.fromURL(svnurl, revision));
        list.setReceiver(new ISvnObjectReceiver<SVNDirEntry>() {
            public void receive(SvnTarget target, SVNDirEntry object) throws SVNException {
                String name = object.getRelativePath();
                if(name!=null && !name.isEmpty()){
                    versions.add(name);
                }
            }
        });
        try {
            list.run();
        } catch (SVNException ex) {
            ex.printStackTrace();
        }

        return versions;
    }



    public static void main(String[] args) throws SVNException {
        SVNUtils modelOption=new SVNUtils("xiaobin.wang", "Ewxb@827");
        //modelOption.checkOutModel("","E://test-svn");
        //modelOption.updateModel("","E://test-svn");
        //modelOption.testModel("");
    }

    static class RepositoryInfo {
        public static String storeUrl="https://svn.avalongames.com/svn/Avalon/AvalonDevHub/SuperSDK/trunk/server/AvalonSuperSDK/";
        public static String buffUrl="http://10.13.30.22/svn/SVNRepository/UnChecked/";
        public static String sysInfoUrl="http://10.13.30.22/svn/SVNRepository/Log/";
    }
}

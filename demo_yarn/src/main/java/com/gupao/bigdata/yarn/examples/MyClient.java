package com.gupao.bigdata.yarn.examples;

import org.apache.commons.cli.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.util.ClassUtil;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.hadoop.yarn.api.ApplicationConstants.Environment;
import org.apache.hadoop.yarn.api.protocolrecords.GetNewApplicationResponse;
import org.apache.hadoop.yarn.api.records.*;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.client.api.YarnClientApplication;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.apache.hadoop.yarn.util.Records;

import java.io.IOException;
import java.util.*;

public class MyClient {
    private static final Log LOG = LogFactory.getLog(MyClient.class);

    // 配置文件
    private Configuration conf;

    private YarnClient yarnClient;

    // 作业名
    private String appName = "HelloYarn";

    // AppMaster优先级
    private int amPriority = 0;

    // AppMaster使用的队列
    private String amQueue = "default";

    // AppMaster需要使用的内存
    private int amMemory = 10;

    // AppMaster需要使用的vCores
    private int amVCores = 1;

    // AppMaster Jar路径
    private String appMasterJarPath;

    // Container优先级
    private int requestPriority = 0;

    // 运行HelloYarn所需要的内存
    private int containerMemory = 10;

    // 运行HelloYarn所需要的vCores
    private int containerVirtualCores = 1;

    // 需要启动多少个Container运行HelloYarn
    private int numContainers = 1;

    // 命令选项
    private Options opts;

    /**
     * 构造函数
     */
    public MyClient() throws Exception {
        yarnClient = YarnClient.createYarnClient();
        this.conf = new YarnConfiguration();
        yarnClient.init(conf);
        appMasterJarPath = ClassUtil.findContainingJar(MyClient.class);
    }

    /**
     * 程序主逻辑入口
     */
    public boolean run() throws IOException, YarnException {

        LOG.info("Running Client");
        yarnClient.start();

        // 新建一个YarnClientApplication
        YarnClientApplication app = yarnClient.createApplication();
        GetNewApplicationResponse appResponse = app.getNewApplicationResponse();

        int maxMem = appResponse.getMaximumResourceCapability().getMemory();
        LOG.info("Max mem capabililty of resources in this cluster " + maxMem);

        // 检查当前申请运行的Application的内存资源不可超过最大值
        if (amMemory > maxMem) {
            LOG.info("AM memory specified above max threshold of cluster. Using max value."
                    + ", specified=" + amMemory
                    + ", max=" + maxMem);
            amMemory = maxMem;
        }

        // 检查当前申请运行的Application的vCores不可超过最大值
        int maxVCores = appResponse.getMaximumResourceCapability().getVirtualCores();
        LOG.info("Max virtual cores capabililty of resources in this cluster " + maxVCores);

        if (amVCores > maxVCores) {
            LOG.info("AM virtual cores specified above max threshold of cluster. "
                    + "Using max value." + ", specified=" + amVCores
                    + ", max=" + maxVCores);
            amVCores = maxVCores;
        }

        // 设置Application名字
        ApplicationSubmissionContext appContext = app.getApplicationSubmissionContext();
        ApplicationId appId = appContext.getApplicationId();

        appContext.setApplicationName(appName);

        // 设置需要申请多少资源启动ApplicationMaster
        Resource capability = Records.newRecord(Resource.class);
        capability.setMemory(amMemory);
        capability.setVirtualCores(amVCores);
        appContext.setResource(capability);

        // 设置ApplicationMaster的优先级
        Priority pri = Records.newRecord(Priority.class);
        pri.setPriority(amPriority);
        appContext.setPriority(pri);

        // 设置队列
        appContext.setQueue(amQueue);

        // 设置一个ContainerLaunchContext来描述容器如何启动ApplicationMaster
        appContext.setAMContainerSpec(getAMContainerSpec(appId.getId()));

        LOG.info("Submitting application to ASM");

        // 使用yarnClient提交运行
        yarnClient.submitApplication(appContext);

        // 监控运行结果
        return monitorApplication(appId);
    }

    private ContainerLaunchContext getAMContainerSpec(int appId) throws IOException, YarnException {
        // 创建一个ContainerLaunchContext
        ContainerLaunchContext amContainer = Records.newRecord(ContainerLaunchContext.class);

        // 拷贝客户端的Jar到HDFS
        FileSystem fs = FileSystem.get(conf);
        Map<String, LocalResource> localResources = new HashMap<String, LocalResource>();
        LOG.info("Copy App Master jar from local filesystem and add to local environment");
        addToLocalResources(fs, appMasterJarPath, Constants.AM_JAR_NAME, appId,
                localResources, null);
        // 为ApplicationMaster的Container设置运行资源(JAR路径)
        amContainer.setLocalResources(localResources);

        // 配置环境变量
        LOG.info("Set the environment for the application master");
        amContainer.setEnvironment(getAMEnvironment(localResources, fs));

        // 设置启动参数
        Vector<CharSequence> vargs = new Vector<CharSequence>(30);
        LOG.info("Setting up app master command");
        vargs.add(Environment.JAVA_HOME.$$() + "/bin/java");
        vargs.add("-Xmx" + amMemory + "m");
        vargs.add("com.wikibooks.hadoop.yarn.examples.MyApplicationMaster");
        vargs.add("--container_memory " + String.valueOf(containerMemory));
        vargs.add("--container_vcores " + String.valueOf(containerVirtualCores));
        vargs.add("--num_containers " + String.valueOf(numContainers));
        vargs.add("--priority " + String.valueOf(requestPriority));
        vargs.add("1>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/AppMaster.stdout");
        vargs.add("2>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/AppMaster.stderr");

        StringBuilder command = new StringBuilder();
        for (CharSequence str : vargs) {
            command.append(str).append(" ");
        }

        LOG.info("Completed setting up app master command " + command.toString());
        List<String> commands = new ArrayList<String>();
        commands.add(command.toString());
        amContainer.setCommands(commands);

        return amContainer;
    }

    /**
     * 把本地文件拷贝到HDFS上
     * */
    private void addToLocalResources(FileSystem fs, String fileSrcPath,
                                     String fileDstPath, int appId, Map<String, LocalResource> localResources,
                                     String resources) throws IOException {
        String suffix = appName + "/" + appId + "/" + fileDstPath;
        Path dst =
                new Path(fs.getHomeDirectory(), suffix);
        if (fileSrcPath == null) {
            FSDataOutputStream ostream = null;
            try {
                ostream = FileSystem
                        .create(fs, dst, new FsPermission((short) 0710));
                ostream.writeUTF(resources);
            } finally {
                IOUtils.closeQuietly(ostream);
            }
        } else {
            fs.copyFromLocalFile(new Path(fileSrcPath), dst);
        }
        FileStatus scFileStatus = fs.getFileStatus(dst);
        LocalResource scRsrc =
                LocalResource.newInstance(
                        ConverterUtils.getYarnUrlFromURI(dst.toUri()),
                        LocalResourceType.FILE, LocalResourceVisibility.APPLICATION,
                        scFileStatus.getLen(), scFileStatus.getModificationTime());
        localResources.put(fileDstPath, scRsrc);
    }

    private Map<String, String> getAMEnvironment(Map<String, LocalResource> localResources
            , FileSystem fs) throws IOException {
        Map<String, String> env = new HashMap<String, String>();

        LocalResource appJarResource = localResources.get(Constants.AM_JAR_NAME);
        Path hdfsAppJarPath = new Path(fs.getHomeDirectory(), appJarResource.getResource().getFile());
        FileStatus hdfsAppJarStatus = fs.getFileStatus(hdfsAppJarPath);
        long hdfsAppJarLength = hdfsAppJarStatus.getLen();
        long hdfsAppJarTimestamp = hdfsAppJarStatus.getModificationTime();

        env.put(Constants.AM_JAR_PATH, hdfsAppJarPath.toString());
        env.put(Constants.AM_JAR_TIMESTAMP, Long.toString(hdfsAppJarTimestamp));
        env.put(Constants.AM_JAR_LENGTH, Long.toString(hdfsAppJarLength));

        StringBuilder classPathEnv = new StringBuilder(Environment.CLASSPATH.$$())
                .append(ApplicationConstants.CLASS_PATH_SEPARATOR).append("./*");
        for (String c : conf.getStrings(
                YarnConfiguration.YARN_APPLICATION_CLASSPATH,
                YarnConfiguration.DEFAULT_YARN_CROSS_PLATFORM_APPLICATION_CLASSPATH)) {
            classPathEnv.append(ApplicationConstants.CLASS_PATH_SEPARATOR);
            classPathEnv.append(c.trim());
        }
        env.put("CLASSPATH", classPathEnv.toString());

        return env;
    }

    /**
     * 监控作业运行情况
     */
    private boolean monitorApplication(ApplicationId appId)
            throws YarnException, IOException {

        while (true) {
            // 每隔1秒检查一下
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                LOG.error("Thread sleep in monitoring loop interrupted");
            }

            // 通过ApplicationReport对象获得运行情况
            ApplicationReport report = yarnClient.getApplicationReport(appId);
            YarnApplicationState state = report.getYarnApplicationState();
            FinalApplicationStatus dsStatus = report.getFinalApplicationStatus();
            if (YarnApplicationState.FINISHED == state) {
                if (FinalApplicationStatus.SUCCEEDED == dsStatus) {
                    LOG.info("Application has completed successfully. "
                            + " Breaking monitoring loop : ApplicationId:" + appId.getId());
                    return true;
                } else {
                    LOG.info("Application did finished unsuccessfully."
                            + " YarnState=" + state.toString() + ", DSFinalStatus=" + dsStatus.toString()
                            + ". Breaking monitoring loop : ApplicationId:" + appId.getId());
                    return false;
                }
            } else if (YarnApplicationState.KILLED == state
                    || YarnApplicationState.FAILED == state) {
                LOG.info("Application did not finish."
                        + " YarnState=" + state.toString() + ", DSFinalStatus=" + dsStatus.toString()
                        + ". Breaking monitoring loop : ApplicationId:" + appId.getId());
                return false;
            }

        }
    }

    /**
     * 入口
     */
    public static void main(String[] args) {
        boolean result = false;
        try {
            MyClient client = new MyClient();
            LOG.info("Initializing Client");
            result = client.run();
        } catch (Throwable t) {
            LOG.fatal("Error running CLient", t);
            System.exit(1);
        }
        if (result) {
            LOG.info("Application completed successfully");
            System.exit(0);
        }
        LOG.error("Application failed to complete successfully");
        System.exit(2);
    }
}

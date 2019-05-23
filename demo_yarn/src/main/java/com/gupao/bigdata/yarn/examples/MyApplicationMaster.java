package com.gupao.bigdata.yarn.examples;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.ExitUtil;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateResponse;
import org.apache.hadoop.yarn.api.records.*;
import org.apache.hadoop.yarn.client.api.AMRMClient;
import org.apache.hadoop.yarn.client.api.AMRMClient.ContainerRequest;
import org.apache.hadoop.yarn.client.api.NMClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.apache.hadoop.yarn.util.Records;
import org.apache.log4j.LogManager;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MyApplicationMaster {
    private static final Log LOG = LogFactory.getLog(MyApplicationMaster.class);

    protected ApplicationAttemptId appAttemptID;

    private int numTotalContainers = 1;

    private int containerMemory = 10;

    private int containerVirtualCores = 1;

    private int requestPriority;

    private String appJarPath = "";

    private long appJarTimestamp = 0;

    private long appJarPathLen = 0;

    private Configuration conf;

    public MyApplicationMaster() {
        conf = new YarnConfiguration();
    }

    /**
     * 解析命令行参数
     */
    public boolean init(String[] args) throws Exception {
        Options opts = new Options();
        opts.addOption("app_attempt_id", true,
                "App Attempt ID. Not to be used unless for testing purposes");
        opts.addOption("shell_env", true,
                "Environment for shell script. Specified as env_key=env_val pairs");
        opts.addOption("container_memory", true,
                "Amount of memory in MB to be requested to run the shell command");
        opts.addOption("container_vcores", true,
                "Amount of virtual cores to be requested to run the shell command");
        opts.addOption("num_containers", true,
                "No. of containers on which the shell command needs to be executed");
        opts.addOption("priority", true, "Application Priority. Default 0");
        opts.addOption("help", false, "Print usage");

        CommandLine cliParser = new GnuParser().parse(opts, args);

        Map<String, String> envs = System.getenv();

        if (!envs.containsKey(ApplicationConstants.Environment.CONTAINER_ID.name())) {
            if (cliParser.hasOption("app_attempt_id")) {
                String appIdStr = cliParser.getOptionValue("app_attempt_id", "");
                appAttemptID = ConverterUtils.toApplicationAttemptId(appIdStr);
            } else {
                throw new IllegalArgumentException(
                        "Application Attempt Id not set in the environment");
            }
        } else {
            ContainerId containerId = ConverterUtils.toContainerId(envs
                    .get(ApplicationConstants.Environment.CONTAINER_ID.name()));
            appAttemptID = containerId.getApplicationAttemptId();
        }

        if (!envs.containsKey(ApplicationConstants.APP_SUBMIT_TIME_ENV)) {
            throw new RuntimeException(ApplicationConstants.APP_SUBMIT_TIME_ENV + " not set in the environment");
        }
        if (!envs.containsKey(ApplicationConstants.Environment.NM_HOST.name())) {
            throw new RuntimeException(ApplicationConstants.Environment.NM_HOST.name() + " not set in the environment");
        }
        if (!envs.containsKey(ApplicationConstants.Environment.NM_HTTP_PORT.name())) {
            throw new RuntimeException(ApplicationConstants.Environment.NM_HTTP_PORT + " not set in the environment");
        }
        if (!envs.containsKey(ApplicationConstants.Environment.NM_PORT.name())) {
            throw new RuntimeException(ApplicationConstants.Environment.NM_PORT.name() + " not set in the environment");
        }

        if (envs.containsKey(Constants.AM_JAR_PATH)) {
            appJarPath = envs.get(Constants.AM_JAR_PATH);

            if (envs.containsKey(Constants.AM_JAR_TIMESTAMP)) {
                appJarTimestamp = Long.valueOf(envs.get(Constants.AM_JAR_TIMESTAMP));
            }
            if (envs.containsKey(Constants.AM_JAR_LENGTH)) {
                appJarPathLen = Long.valueOf(envs.get(Constants.AM_JAR_LENGTH));
            }

            if (!appJarPath.isEmpty() && (appJarTimestamp <= 0 || appJarPathLen <= 0)) {
                LOG.error("Illegal values in env for shell script path" + ", path="
                        + appJarPath + ", len=" + appJarPathLen + ", timestamp=" + appJarTimestamp);
                throw new IllegalArgumentException(
                        "Illegal values in env for shell script path");
            }
        }

        LOG.info("Application master for app" + ", appId="
                + appAttemptID.getApplicationId().getId() + ", clusterTimestamp="
                + appAttemptID.getApplicationId().getClusterTimestamp()
                + ", attemptId=" + appAttemptID.getAttemptId());

        containerMemory = Integer.parseInt(cliParser.getOptionValue("container_memory", "10"));
        containerVirtualCores = Integer.parseInt(cliParser.getOptionValue("container_vcores", "1"));
        numTotalContainers = Integer.parseInt(cliParser.getOptionValue("num_containers", "1"));
        if (numTotalContainers == 0) {
            throw new IllegalArgumentException("Cannot run MyAppliCationMaster with no containers");
        }
        requestPriority = Integer.parseInt(cliParser.getOptionValue("priority", "0"));

        return true;
    }

    /**
     * 主方法运行ApplicationMaster
     *
     * @throws org.apache.hadoop.yarn.exceptions.YarnException
     * @throws java.io.IOException
     */
    @SuppressWarnings({"unchecked"})
    public void run() throws Exception {
        LOG.info("Running MyApplicationMaster");

        // 初始化客户端
        AMRMClient<ContainerRequest> amRMClient = AMRMClient.createAMRMClient();
        amRMClient.init(conf);
        amRMClient.start();

        // 向ResourceManager注册
        amRMClient.registerApplicationMaster("", 0, "");

        // 为运行HelloYarn配置内存和虚拟处理器资源
        Resource capability = Records.newRecord(Resource.class);
        capability.setMemory(containerMemory);
        capability.setVirtualCores(containerVirtualCores);

        // 设置优先级, 这里的优先级是指内部任务的优先级
        Priority priority = Records.newRecord(Priority.class);
        priority.setPriority(requestPriority);

        // 向ResourceManager发起ContainerRequest请求
        for (int i = 0; i < numTotalContainers; ++i) {
            ContainerRequest containerAsk = new ContainerRequest(capability, null, null, priority);
            amRMClient.addContainerRequest(containerAsk);
        }

        // 初始化客户端与NodeManager通信
        NMClient nmClient = NMClient.createNMClient();
        nmClient.init(conf);
        nmClient.start();

        Map<String, String> containerEnv = new HashMap<String, String>();
        containerEnv.put("CLASSPATH", "./*");

        // 为Container添加运行所需要的Jar路径
        LocalResource appMasterJar = createAppMasterJar();

        // 轮询等待分配资源
        int allocatedContainers = 0;
        int completedContainers = 0;
        while (allocatedContainers < numTotalContainers) {
            AllocateResponse response = amRMClient.allocate(0);
            // 如果分配到了资源则创建ContainerLaunchContext对象并向NodeManager提交申请
            for (Container container : response.getAllocatedContainers()) {
                allocatedContainers++;

                ContainerLaunchContext appContainer = createContainerLaunchContext(appMasterJar, containerEnv);
                LOG.info("Launching container " + allocatedContainers);

                nmClient.startContainer(container, appContainer);
            }
            // 先判断一遍是否完成
            for (ContainerStatus status : response.getCompletedContainersStatuses()) {
                ++completedContainers;
                LOG.info("ContainerID:" + status.getContainerId() + ", state:" + status.getState().name());
            }
            Thread.sleep(100);
        }

        // 等待没有完成的Container
        while (completedContainers < numTotalContainers) {
            AllocateResponse response = amRMClient.allocate(completedContainers / numTotalContainers);
            for (ContainerStatus status : response.getCompletedContainersStatuses()) {
                ++completedContainers;
                LOG.info("ContainerID:" + status.getContainerId() + ", state:" + status.getState().name());
            }
            Thread.sleep(100);
        }

        LOG.info("Completed containers:" + completedContainers);

        // 向ResourceManager注销
        amRMClient.unregisterApplicationMaster(
                FinalApplicationStatus.SUCCEEDED, "", "");
        LOG.info("Finished MyApplicationMaster");
    }

    private LocalResource createAppMasterJar() throws IOException {
        LocalResource appMasterJar = Records.newRecord(LocalResource.class);
        if (!appJarPath.isEmpty()) {
            appMasterJar.setType(LocalResourceType.FILE);
            Path jarPath = new Path(appJarPath);
            jarPath = FileSystem.get(conf).makeQualified(jarPath);
            appMasterJar.setResource(ConverterUtils.getYarnUrlFromPath(jarPath));
            appMasterJar.setTimestamp(appJarTimestamp);
            appMasterJar.setSize(appJarPathLen);
            appMasterJar.setVisibility(LocalResourceVisibility.PUBLIC);
        }
        return appMasterJar;
    }

    /**
     * 创建ContainerLaunchContext描述如何启动Container
     */
    private ContainerLaunchContext createContainerLaunchContext(LocalResource appMasterJar,
                                                                Map<String, String> containerEnv) {
        ContainerLaunchContext appContainer =
                Records.newRecord(ContainerLaunchContext.class);
        appContainer.setLocalResources(
                Collections.singletonMap(Constants.AM_JAR_NAME, appMasterJar));
        appContainer.setEnvironment(containerEnv);
        appContainer.setCommands(
                Collections.singletonList(
                        "$JAVA_HOME/bin/java" +
                                " -Xmx256M" +
                                " com.wikibooks.hadoop.yarn.examples.HelloYarn" +
                                " 1>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stdout" +
                                " 2>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stderr"
                )
        );


        return appContainer;
    }

    public static void main(String[] args) throws Exception {
        try {
            MyApplicationMaster appMaster = new MyApplicationMaster();
            LOG.info("Initializing MyApplicationMaster");
            boolean doRun = appMaster.init(args);
            if (!doRun) {
                System.exit(0);
            }
            appMaster.run();
        } catch (Throwable t) {
            LOG.fatal("Error running MyApplicationMaster", t);
            LogManager.shutdown();
            ExitUtil.terminate(1, t);
        }
    }
}

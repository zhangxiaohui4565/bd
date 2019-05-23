#1、Not enough live tablet servers to create a table with the requested replication factor 3. 2 tablet servers are alive.

    Sets the number of replicas that each tablet will have. If not specified, it uses the
      * server-side default which is usually 3 unless changed by an administrator.


#2、org.apache.kudu.client.NonRecoverableException: RPC can not complete before timeout: KuduRpc(method=CreateTable, tablet=Kudu Master, attempt=38, DeadlineTracker(timeout=30000, elapsed=28601),

     session.setTimeoutMillis(60000);
     new KuduClient.KuduClientBuilder("hadoop6").defaultAdminOperationTimeoutMs(600000).build();

#3、org.apache.kudu.client.NonRecoverableException: illegal replication factor 2 (replication factor must be odd)


#4、客户端版本要和服务端版本保持一致(非常重要)






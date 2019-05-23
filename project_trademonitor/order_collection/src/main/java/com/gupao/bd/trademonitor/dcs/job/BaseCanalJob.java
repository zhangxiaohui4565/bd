package com.gupao.bd.trademonitor.dcs.job;


import com.gupao.bd.trademonitor.dcs.dao.CanalClient;
import com.gupao.bd.trademonitor.dcs.model.CanalRecord;

/**
 * 功能：通用的canal数据收集器
 */
public abstract class BaseCanalJob implements Runnable {

    protected JobConf jobConf;

    public BaseCanalJob(JobConf canalConf) {
        this.jobConf = canalConf;
    }

    /**
     * 默认将消息打印在console上，子类根据需要重新改写对消息的处理方式
     */
    protected void process(String jsonStr) {
        System.out.println("=============== message ===============");
        System.out.println(jsonStr);
    }

    @Override
    public void run() {

        //1、创建CanalClient
        CanalClient canalClient = new CanalClient(jobConf);

        //2、订阅表
        canalClient.subscribe(jobConf.getCanalTableFilter());

        try {
            //连续maxPollSize次拉取不到数据，则关闭程序
            for (int emptySize = 0, maxPollSize = Integer.MAX_VALUE; emptySize < maxPollSize; ) {

                //3、通过轮询的方式获取数据(默认一次获取100条记录)
                CanalRecord record = canalClient.poll();
                try {
                    if (record.isEmpty()) {
                        System.out.println("no message:" + (++emptySize));
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        emptySize = 0;

                        //4、处理数据
                        record.getRowEntries().forEach(e -> {
                            process(e.toJsonString());
                        });
                    }
                    //5.1、处理完成，提交确认
                    canalClient.ack(record.getBatchId());
                } catch (Exception e) {
                    //5.2、处理失败，回滚数据
                    canalClient.rollback(record.getBatchId());
                }
            }
        } finally {
            //6、关闭CanalClient
            canalClient.close();
        }
    }
}
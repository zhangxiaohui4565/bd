-- job启动过程

    StreamingContext.start()

       scheduler.start()

          jobGenerator.start()

              eventLoop : 处理消息
              timer：定时发送消息 （batchDuration）


              generateJobs

              jobScheduler.submitJobSet


              JobHandler.run()

              job.run()

                fun() = foreachFunc

                         RDD.take

                            sc.runJob
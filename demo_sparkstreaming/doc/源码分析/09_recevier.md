===  receiver启动的过程

  scheduler.start()

    receiverTracker.start()

       launchReceivers()

          endpoint.send(StartAllReceivers(receivers))

              ReceiverTrackerEndpoint.receive

              ReceiverTrackerEndpoint.startReceiver

                通过Spark Job的方式启动receiver

                 - startReceiverFunc
                 - receiverRDD

                 val supervisor = new ReceiverSupervisorImpl(
                                receiver, SparkEnv.get, serializableHadoopConf.value, checkpointDirOption)
                 supervisor.start()

                     ReceiverSupervisorImpl.onStart : 启动BlockGenerator
                     startReceiver :
                     receiver.onStart() -> SocketReceiver.onStart() -> receive

===  receiver接收数据的过程


  接收和存储

      Receiver：可以自定义

         SocketReceiver.onStart -> receive:启动一个线程（持续接收数据）

            Receiver.store(区别单条消息和批量消息）

             supervisor.push(区别单条消息和批量消息)

               如果单条记录，调用BlockGenerator积攒

               如果批量记录，直接调用ReceivedBlockHandler.storeBlock

                 BlockManager

                 WAL

  元数据汇报

     ReceiverSupervisorImpl.pushAndReportBlock

        trackerEndpoint.askWithRetry

           ReceiverTrackerEndpoint.receiveAndReply(处理AddBlock消息）

              ReceiverTracker.addBlock

                 ReceivedBlockTracker.addBlock -> writeToLog (目录：checkpoint/receivedBlockMetadata）







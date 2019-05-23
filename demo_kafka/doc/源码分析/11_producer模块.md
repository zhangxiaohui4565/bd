KafkaProducer()

-> KafkaThread(Sender)

-> RecordAccumulator.append()

    -> ProducerBatch

      -> MemoryRecordsBuilder

        -> MemoryRecords

    -> sender

        -> KafkaClient (ClientRequest)

#!/usr/bin/env bash

kafka-consumer-groups.sh --bootstrap-server dev-bg-01:9092 --list --new-consumer

./kafka-consumer-groups.sh --bootstrap-server BI-HD17:9092 --list --new-consumer

./kafka-simple-consumer-shell.sh --topic __consumer_offsets --partition 29 --broker-list BI-HD17:9092 --formatter "kafka.coordinator.GroupMetadataManager\$OffsetsMessageFormatter"

kafka-console-consumer.sh  --bootstrap-server dev-bg-01:9092 --topic __consumer_offsets --formatter "kafka.coordinator.GroupMetadataManager\$OffsetsMessageFormatter" --from-beginning
./kafka-console-consumer.sh  --bootstrap-server BI-HD17:9092 --topic __consumer_offsets --formatter "kafka.coordinator.GroupMetadataManager\$OffsetsMessageFormatter" --from-beginning

[console-consumer-67049,db_nono_b,1]::[OffsetMetadata[129403199,NO_METADATA],CommitTime 1528125244287,ExpirationTime 1528211644287]

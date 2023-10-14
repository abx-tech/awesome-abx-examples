#!/bin/bash
set -x
awslocal s3 mb s3://chat-app-s3
KINESIS_STREAM_SHARDS=5
KINESIS_STREAM_NAME=multiverse-exploration-kinesis-stream
awslocal kinesis create-stream --shard-count ${KINESIS_STREAM_SHARDS} \
  --stream-name ${KINESIS_STREAM_NAME}

set +x
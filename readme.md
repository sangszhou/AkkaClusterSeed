## What's is project about
This project demonstrates how to use akka cluster sharding and akka remoting. It also use akka distributed data to store
cluster information but that part only exists in configuration

```
   cluster {
     sharding.state-store-mode = ddata
   }

   akka.extensions=["akka.cluster.metrics.ClusterMetricsExtension", "akka.cluster.ddata.DistributedData"]

```

Before akka 2.4 where akka distributed data was introduced, we need external persistent layer to store cluster information.
when leader is down, slave node selected as new leader and it restore leader's memory by reading those cluster information.

When we first try akka cluster sharding last year, akka's newest version is 2.3.6. At the time, we use
akka-persistence-mongo as persistent layer, it works well but it also introduced third-party dependency and `error prone`.

## How to run
import project into intellij idea and click `run root` in `main.Boot`
will need jdk1.8

## Configuration
ClusterTool.enable set to true
ClusterTool.shardingConf.enable set to true to enable cluster sharding demo
ClusterTool.shardingConf.enable set to true to enable akka remoting demo

single node cannot make a cluster, you can add multi-instances to `seed-notes` by specifing host & port. Each instance
should set netty.tcp hostname and port to the correct value.

## How to verify akka cluster sharding works
Start at least two nodes A and B. Suppose A is the first node configured in seed-nodes and started in the first place so
it represent the leader, then start node B.

1. When shardingConf is enabled, one producer will generate random messages and send them to shardingProbe, you can see
shardingProbe at Node B receive message from Producer at node A and vice verse. [This is easy to verify]

2. Shutdown Node A and Node B is the only node left in cluster, so it became the leader. all new message's are sent
to Node B, including those nmessage should be sent to Node A before A is shutdown. Restart A and A become slave but A
wouldn't receive any messages from producer at both Node A and Node B, because those shardingProbe are all at Node B.
[This is hard to verify, because we cannot control the message sent by producer at wrong time. An easy way to deal with
this is introducing spray where we sent message at runtime through REST API]

## How to verify akka remoting works
todo

## Todo
1. Add sbt package plugin to build fat jar
2. Add spray to simplify the verification #2
3. Finish the persistent part, write to db when ping-pong messages are not shown in pair

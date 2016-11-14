#!/usr/bin/env bash
java -cp target/u1-1.0-SNAPSHOT-jar-with-dependencies.jar jade.Boot \
  -host localhost \
  -port 6000 \
  -agents 'dfsubscriber:org.deadlock.id2209.agents.DFSubscriberAgent;curator:org.deadlock.id2209.agents.CuratorAgent;guide:org.deadlock.id2209.agents.GuideAgent;profiler:org.deadlock.id2209.agents.ProfilerAgent'
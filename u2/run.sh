#!/usr/bin/env bash
java -cp $(dirname "$0")/../target/u1-1.0-SNAPSHOT-jar-with-dependencies.jar jade.Boot \
  -host localhost \
  -port 6000 \
  -agents 'curator1:org.deadlock.id2209.u2.agents.CuratorAgent;curator2:org.deadlock.id2209.u2.agents.CuratorAgent;lowprofile:org.deadlock.id2209.u2.agents.ProfilerAgent(lowbidder);highprofile:org.deadlock.id2209.u2.agents.ProfilerAgent(highbidder)'
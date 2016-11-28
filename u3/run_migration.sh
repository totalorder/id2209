#!/usr/bin/env bash
java -cp $(dirname "$0")/../target/u1-1.0-SNAPSHOT-jar-with-dependencies.jar jade.Boot \
  -host localhost \
  -port 6000 \
  -agents "boostrapper:org.deadlock.id2209.u3.agents.ControllerAgent"
#!/usr/bin/env bash
java -cp $(dirname "$0")/../target/u1-1.0-SNAPSHOT-jar-with-dependencies.jar jade.Boot \
  -container -host localhost -port 6000 -agents 'dflookup:org.deadlock.id2209.u1.agents.DFLookupAgent'
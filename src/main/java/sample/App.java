/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package sample;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import scala.io.StdIn;

// let's combine a bunch of concepts
public class App {

  public static void main(String[] args) {
    ActorSystem system = ActorSystem.create();

    ActorRef db = system.actorOf(DbSupervisor.props(), "db");

    system.actorOf(WebServer.props(db, "localhost", 8080), "webserver");

  }
}

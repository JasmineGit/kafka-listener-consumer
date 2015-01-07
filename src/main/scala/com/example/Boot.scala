package com.example


import akka.actor.{Props, ExtendedActorSystem, ActorSystem}
import akka.io.IO
import akka.serialization.JavaSerializer
import com.example.kafkaconsumer.{KafkaCustomEncoder, ConsumerActor}
import com.sclasen.akka.kafka.{AkkaConsumer, AkkaConsumerProps}
import com.typesafe.config.ConfigFactory
import kafka.serializer.StringDecoder
import kafka.utils.VerifiableProperties
import spray.can.Http
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

object Boot extends App {

  // we need an ActorSystem to host our application in
//  implicit val system = ActorSystem("on-spray-can")
//  JavaSerializer.currentSystem.value = system.asInstanceOf[ExtendedActorSystem]
//  // create and start our service actor
//  val service = system.actorOf(Props[MyServiceActor], "demo-service")
//
  val system2 = ActorSystem("ResponderSystem", ConfigFactory.load("remote.conf"))
  val serializer = new JavaSerializer(system2.asInstanceOf[ExtendedActorSystem])
//
//  implicit val timeout = Timeout(15.seconds)
//  // start a new HTTP server on port 8080 with our service actor as the handler
//  IO(Http) ? Http.Bind(service, interface = "localhost", port = 9080)



  val actor = system2.actorOf(Props(new ConsumerActor()), "testremote")

  val consumerProps = AkkaConsumerProps.forSystem(system2, "localhost:2181", "test-future", "mailed", 1, new StringDecoder(), new KafkaCustomEncoder(new VerifiableProperties()), actor)

  //val consumerProps = AkkaConsumerProps.forSystem(system2, "localhost:2181", "test-future", "mailed", 1, new StringDecoder(), new StringDecoder(), actor)
  val consumer = new AkkaConsumer(consumerProps)
  consumer.start()
}

package com.example.kafkaconsumer

import akka.actor.{ActorPath, ExtendedActorSystem, Actor}
import akka.serialization.JavaSerializer
import com.example.{RequestCapsule, Boot}
import com.sclasen.akka.kafka.StreamFSM

/**
 * Created by cloudera on 12/22/14.
 */
case class ConsumerActor() extends Actor{

  lazy val serializer = new JavaSerializer(Boot.system2.asInstanceOf[ExtendedActorSystem])

  override def receive: Receive = {

    case message: Array[Byte] => {
      try {
        println("received request as Byte Array")
        val reqCapsule = serializer.fromBinary(message).asInstanceOf[RequestCapsule]
        //implicit val ctx = context
        val actrPath = reqCapsule.actorPath
        println("######### ###### ##### ACTOR PATH :"+actrPath)
        Boot.system2.actorSelection(actrPath).forward("This is what I want to do !!! Finally did it ..."+reqCapsule.custId)
        //ActorRefConverter.convertToRequest(message)
      }
      catch {
        case e: Exception =>
          e.printStackTrace

      }
      sender ! StreamFSM.Processed
    }
    case reqCapsule: RequestCapsule => {
      try {
        println("received request as Request Capsule")
        //val reqCapsule = serializer.fromBinary(message).asInstanceOf[RequestCapsule]
        //implicit val ctx = context
        val actrPath = reqCapsule.actorPath
        println("######### ###### ##### ACTOR PATH :"+actrPath)
        Boot.system2.actorSelection(actrPath).forward("This is what I want to do !!! Finally did it ..."+reqCapsule.custId)
        //ActorRefConverter.convertToRequest(message)
      }
      catch {
        case e: Exception =>
          e.printStackTrace

      }
      sender ! StreamFSM.Processed

    }
    case mssg: String => {
      try {
        println("Received response : " + mssg)
        try {
          println("received request as String")
//          val reqCapsule = serializer.fromBinary(serializer.toBinary(mssg)).asInstanceOf[RequestCapsule]
//          //implicit val ctx = context
//          val actrPath = serializer.fromBinary(reqCapsule.actorPath).asInstanceOf[ActorPath]
          context.actorSelection("akka.tcp://remotesys@127.0.0.1:2553/user/creationActor1").forward("This is what I want to do !!! Finally did it ..."+mssg)
          //ActorRefConverter.convertToRequest(message)
        }
        catch {
          case e: Exception =>
            e.printStackTrace

        }
      }
      catch {
        case e: Exception =>
          e.printStackTrace

      }
      sender ! StreamFSM.Processed
    }
    case mss => {

      try {
        println("Received response type : " + mss.getClass.getName())
      }
      catch {
        case e: Exception =>
          e.printStackTrace

      }
      sender ! StreamFSM.Processed

    }
  }
}

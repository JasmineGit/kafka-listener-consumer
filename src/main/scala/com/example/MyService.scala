package com.example

import java.util.Properties

import akka.actor._
import kafka.producer.{KeyedMessage, ProducerConfig, Producer}
import spray.routing._
import spray.http._
import MediaTypes._

import scala.concurrent.Promise


case class CustomResponder(promise: Promise[String]) extends Actor{

  override def receive = {
    case mssg : String => {
      promise.success(mssg)
      //context.system.stop(self)
    }
  }
}

case class RequestCapsule(custId: Int, actorPath: String) extends Serializable

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class MyServiceActor extends Actor with HttpService {


  //lazy val Boot.serializer = new JavaBoot.serializer(Boot.system.asInstanceOf[ExtendedActorSystem])

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute)

  val myRoute =
    path("") {
            get {
              respondWithMediaType(`text/html`) { // XML is marshalled to `text/xml` by default, so we simply override here
                complete {
                  <html>
                    <body>
                      <h1>Testing routes hello to <i>spray-routing</i> on <i>Jetty</i>!</h1>
                    </body>
                  </html>
                }
              }
            }
    } ~
  path("index"){
      getFromResource("html/index.html")
  }~
  path("sample"/ IntNumber){ id =>
    {req =>
      implicit def executionContext = actorRefFactory.dispatcher
      val promise = Promise[String]()
      val cResponder = Boot.system2.actorOf(Props(new CustomResponder(promise)), "customResponder"+System.currentTimeMillis())
//      pushAfterSomeTime(id, cResponder.path)
      req.complete(promise.future)
      //completeTheRequest(id, cResponder)

      //pushAfterSomeTime(id, cResponder.path)
    }
  }


//  def pushAfterSomeTime(custId: Int, actorPath: ActorPath): Unit = {
//    //Thread.sleep(5000)
//    SimpleKafkaProducer.send(new RequestCapsule(custId, Boot.serializer.toBinary(actorPath)))
//  }


//  def completeTheRequest(cId:Int, cResponder:ActorRef): Unit ={
//    val byteArrReq = Boot.serializer.toBinary(new RequestCapsule(cId,Boot.serializer.toBinary(cResponder.path)))
//
//    val reqCapsule = Boot.serializer.fromBinary(byteArrReq).asInstanceOf[RequestCapsule]
//    //implicit val ctx = context
//    val actrPath = Boot.serializer.fromBinary(reqCapsule.actorPath).asInstanceOf[ActorPath]
//    Boot.system.actorSelection(actrPath).forward("This is what I want to do !!! Finally did it ..."+reqCapsule.custId)
//  }


//  def returnRoute(req: RequestContext): Unit ={
////    println("Req:"+req.request)
//    val byteArrReq = Boot.serializer.toBinary(req) //Helper.searialize(req)
////    println(req.responder.path.address.toString)
////    val byteArrPath = Boot.serializer.toBinary(req.responder.path)
//
////    println("bytes:\n"+byteArr)
//    // Thread.sleep(5000)
//    val r = Boot.serializer.fromBinary(byteArrReq).asInstanceOf[RequestContext] //Helper.deSerialize(byteArr
//    complete("Finally made It !!!!").apply(r.withResponder(Boot.system.actorOf(Props[CustomResponder], "custom-responder")))
//
//
//  }

}


object SimpleKafkaProducer {

  val effectiveConfig = {
    val c = new Properties
    c.put("metadata.broker.list", "localhost:9092")
    c.put("serializer.class", "com.example.kafkaconsumer.KafkaCustomEncoder")
    c
  }

  lazy val producer = new Producer[Array[Byte], RequestCapsule](new ProducerConfig(effectiveConfig))

  def kafkaMsg(message: RequestCapsule): KeyedMessage[Array[Byte], RequestCapsule] = {
    new KeyedMessage("test-future", message)
  }
  def send(req: RequestCapsule): Unit = {
    producer.send(kafkaMsg(req))
  }
}


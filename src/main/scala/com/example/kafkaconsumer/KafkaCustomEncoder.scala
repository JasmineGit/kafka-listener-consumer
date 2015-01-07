package com.example.kafkaconsumer

import com.example.{Boot, RequestCapsule}
import kafka.serializer.{Encoder, Decoder}



class KafkaCustomEncoder(props: kafka.utils.VerifiableProperties) extends Encoder[RequestCapsule] with Decoder[RequestCapsule] {

  override def toBytes(t: RequestCapsule): Array[Byte] = {
    Boot.serializer.toBinary(t)
  }

  override def fromBytes(bytes: Array[Byte]): RequestCapsule = {
    Boot.serializer.fromBinary(bytes).asInstanceOf[RequestCapsule]
  }
}

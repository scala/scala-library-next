/*
 * Scala (https://www.scala-lang.org)
 *
 * Copyright EPFL and Lightbend, Inc.
 *
 * Licensed under Apache License 2.0
 * (http://www.apache.org/licenses/LICENSE-2.0).
 *
 * See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.
 */

package scala.collection.next

import org.junit.Assert._
import org.junit.Test
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
//import scala.concurrent.ExecutionContext._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import scala.concurrent.Promise

final class FutureExtensionsTest{

    import FutureExtensions._

    @Test
    def futureSuccessTapEachTest(): Unit = {

        implicit val ec = ExecutionContext.parasitic
        val input: Future[List[Option[Int]]] = Future(List(Some(1), None, Some(2), Some(3), Some(4)))
        val expected: Future[List[Int]] = Future.successful(List(3, 4, 17))
        //val expected: List[Int] = List(3, 4, 17)
        
        var num = 0 

        def tapAndMutate(t: Future[List[Option[Int]]]) ={
            input.map(
                _.flatten
                .tapEach(a => {num = num + a})
                .filter(_ > 2)
            )
            .tapEach(a => {num = num + a.sum})
            .map(_  :+ num)
        }

        val awaited = Await.result(tapAndMutate(input), 5.second)
        val awaited2 = Await.result(expected, 5.second)

       /* val p = Promise[String]
    val q = Promise[String]
    val res = Promise[String]
    val s = "hi"
    p.future.onComplete(t => res.complete(t))
    q.future.onComplete(t => res.complete(t))   // previously, uncompleted promise held reference to promise completed with value
    assertNotReachable(s, q) {
      p.complete(Try(s))
    }
    */

        assertEquals(awaited2, awaited)
    }

    /*
    @Test 
    def tryFailureTapEachTest(): Unit = {
        val failInt: Try[Int] = Failure[Int](new RuntimeException("run time exception"))
        var num = 3 
        failInt.tapEach(a => {num = 5})
        assertEquals(3, num)
    }

    
    @Test
    def tryFailingSideEffectTapEachTest() : Unit = {
         val succInt: Try[Int] = Success[Int](5)
         val e = new RuntimeException("run time exception")
         val newSucc: Try[Int] = succInt
            .tapEach(_ => throw e)
            .map(_ + 1)

        assertEquals(Failure(e), newSucc)
    }*/
}
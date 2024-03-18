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

package scala.util.next

import org.junit.Assert._
import org.junit.Test
import scala.util.{Try, Success, Failure}


final class TryExtensionsTest{

    import TryExtensions._

    @Test
    def trySuccessTapEachTest(): Unit = {

        val succInt: Try[Int] = Success[Int](5)
        var num = 3 

        def tapAndMutate(t: Try[Int]) ={
            t.tapEach(a => {num = 5} ).map(_ + num)
        }

        assertEquals(Success(10), tapAndMutate(succInt))
    }

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
    }
}
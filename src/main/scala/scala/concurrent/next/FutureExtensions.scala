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

package scala.concurrent.next

import scala.concurrent.{Future, ExecutionContext}
import scala.util.next.TryExtensions._

object FutureExtensions{
    implicit class FutureExt[T](ft: Future[T]){

        /** Applies a side-effecting function to the encapsulated value in this Future
          * 
          * Calling `tapEach` on `Failure` will not execute `f`
          * 
          * If the side-effecting function fails, the exception will be 
          * propagated as a `Failure` and the current value of 
          * `Success` will be discarded. For example the following will result
          * in a `RuntimeException` and will not reach `map`
          * 
          * {{{
          *   val f : Future[Int] = Future.successful(5)
          * 
          *   f
          *     .tapEach(throw new RuntimeException("runtime exception"))
          *     .map(_ + 1)
          * }}}
          * 
          * @param f a function to apply to each element in this Future
          * @tparam U the return type of f
          * @return The same Future as this
        */
        def tapEach[U](f: T => U)(implicit executor: ExecutionContext): Future[T] = ft.transform(_ tapEach f)
    }
}
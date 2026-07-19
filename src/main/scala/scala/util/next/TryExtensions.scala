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

import scala.util.{Try, Failure, Success}
import scala.util.control.NonFatal

object TryExtensions{

    implicit class TryExtensions[T](t: Try[T]) { 

        /** Applies a side-effecting function to the encapsulated value in this Try
          * 
          * Calling `tapEach` on `Failure` will not execute `f`
          * 
          * If the side-effecting function fails, the exception will be 
          * propagated as a `Failure` and the current value of 
          * `Success` will be discarded. For example the following will result
          * in a `RuntimeException` and will not reach `map`
          * 
          * {{{
          *   val t : Try[Int] = Success(5)
          * 
          *   t.tapEach(throw new RuntimeException("runtime exception")).map(_ + 1)
          * }}}
          * 
          * @param f a function to apply to each element in this Future
          * @tparam U the return type of f
          * @return The same Try as this
        */
        def tapEach[U](f: T => U) = t match{
            case _ : Failure[T] => t.asInstanceOf[Try[T]]
            case _ : Success[T] => try {
                f(t.get)
                t
            } catch{
                case NonFatal(e) => Failure[T](e)
            }
        } 
    }
}
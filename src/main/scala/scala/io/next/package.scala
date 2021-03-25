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

package scala.io

package object next {
  implicit class NextStdInExtensions(si: StdIn) {
    /** Reads and applying a function on an entire line of the default input .
     *
     *  @return the Byte that was read
     *  @throws java.io.EOFException if the end of the
     *  input stream has been reached.
     */
    def readWith[A](f: String => A): A = {
      val s = si.readLine()
      if (s == null)
        throw new java.io.EOFException("Console has reached end of input")
      else
        f(s)
    }
  }
}

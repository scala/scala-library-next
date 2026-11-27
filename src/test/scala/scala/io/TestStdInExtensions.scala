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

import next._

import org.junit.Assert._
import org.junit.Test

import java.io.ByteArrayInputStream
import java.io.InputStream

class TestStdInExtensions {
  @Test
  def readArray(): Unit = {
    val in = new ByteArrayInputStream("1 2 3 4".getBytes)
    Console.withIn(in) {
      assertArrayEquals(
        StdIn.readWith(input => input.split(" ").map(_.toInt)),
        Array(1, 2, 3, 4))
    }
  }

  @Test
  def readClass(): Unit = {
    case class Person(name: String, age: Int)
    val in = new ByteArrayInputStream("John 34".getBytes)
    Console.withIn(in) {
      assertEquals(
        StdIn.readWith(input => {
          val Array(name, age) = input.split(" ")
          Person(name, age.toInt)
        }),
        Person("John", 34))
    }
  }
}

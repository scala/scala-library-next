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

package scala.next

import org.junit.Assert._
import org.junit.Test

final class TestStringOpsExtensions {
  @Test
  def splitAsListEmptyStringNoPreserveEmpty(): Unit = {
    val str = ""

    assertTrue(str.splitAsList(',').isEmpty)
  }

  @Test
  def splitAsListEmptyStringPreserveEmpty(): Unit = {
    val str = ""
    val expected = List("")

    assertEquals(expected, str.splitAsList(separator = ',', preserveEmptySubStrings = true))
  }

  @Test
  def splitAsListBlankStrings(): Unit = {
    val strings = List(
      " ",
      "   ",
      "\t",
      "\t\t\t",
      "\n",
      "\n\n\n",
      " \t \t \n"
    )

    strings.foreach { str =>
      val expected = List(str)
      assertEquals(expected, str.splitAsList(','))
    }
  }

  @Test
  def splitAsListDelimiterNotFound(): Unit = {
    val str = "Hello World"
    val expected = List(str)

    assertEquals(expected, str.splitAsList(','))
  }

  @Test
  def splitAsListSingleDelimiter(): Unit = {
    val str = "Hello,World"
    val expected = List("Hello", "World")

    assertEquals(expected, str.splitAsList(','))
  }

  @Test
  def splitAsListMultipleDelimiters(): Unit = {
    val str = "Hello,World,Good,Bye,World"
    val expected = List("Hello", "World", "Good", "Bye", "World")

    assertEquals(expected, str.splitAsList(','))
  }

  @Test
  def splitAsListEmptySubStrings(): Unit = {
    val str = "Hello,,World,"
    val expected = List("Hello", "World")

    assertEquals(expected, str.splitAsList(','))
  }

  @Test
  def splitAsListDelimiterAtTheEnd(): Unit = {
    val str = "Hello,World,"
    val expected = List("Hello", "World")

    assertEquals(expected, str.splitAsList(','))
  }

  @Test
  def splitAsListDelimiterAtTheBeginning(): Unit = {
    val str = ",Hello,World"
    val expected = List("Hello", "World")

    assertEquals(expected, str.splitAsList(','))
  }

  @Test
  def splitAsListPreserveEmptySubStrings(): Unit = {
    val str = ",Hello,,World,"
    val expected = List("", "Hello", "", "World", "")

    assertEquals(expected, str.splitAsList(separator = ',', preserveEmptySubStrings = true))
  }
}

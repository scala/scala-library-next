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
import scala.collection.IterableOnceOps
import scala.collection.generic.IsIterableOnce
import scala.collection.immutable.{SortedMap, SortedSet}

final class TestIterableOnceExtensions {
  import TestIterableOnceExtensions._

  // groupMapReduce --------------------------------------------
  @Test
  def iteratorGroupMapReduce(): Unit = {
    def occurrences[A](coll: IterableOnce[A]): Map[A, Int] =
      coll.iterator.groupMapReduce(identity)(_ => 1)(_ + _)

    val xs = Seq('a', 'b', 'b', 'c', 'a', 'a', 'a', 'b')
    val expected = Map('a' -> 4, 'b' -> 3, 'c' -> 1)
    assertEquals(expected, occurrences(xs))
  }

  @Test
  def iterableOnceOpsGroupMapReduce(): Unit = {
    def occurrences[A, CC[_], C](coll: IterableOnceOps[A, CC, C]): Map[A, Int] =
      coll.groupMapReduce(identity)(_ => 1)(_ + _)

    val xs = Seq('a', 'b', 'b', 'c', 'a', 'a', 'a', 'b')
    val expected = Map('a' -> 4, 'b' -> 3, 'c' -> 1)
    assertEquals(expected, occurrences(xs))
  }

  @Test
  def anyLikeIterableOnceGroupMapReduce(): Unit = {
    def occurrences[Repr](coll: Repr)(implicit it: IsIterableOnce[Repr]): Map[it.A, Int] =
      it(coll).iterator.groupMapReduce(identity)(_ => 1)(_ + _)

    val xs = "abbcaaab"
    val expected = Map('a' -> 4, 'b' -> 3, 'c' -> 1)
    assertEquals(expected, occurrences(xs))
  }

  @Test
  def customIterableOnceOpsGroupMapReduce(): Unit = {
    def occurrences(coll: LowerCaseString): Map[Char, Int] =
      coll.groupMapReduce(identity)(_ => 1)(_ + _)

    val xs = LowerCaseString("abBcAaAb")
    val expected = Map('a' -> 4, 'b' -> 3, 'c' -> 1)
    assertEquals(expected, occurrences(xs))
  }
  // -----------------------------------------------------------

  // groupMapTo ------------------------------------------------
  @Test
  def anyCollectionGroupMapToFull(): Unit = {
    def getUniqueUsersByCountrySorted(data: List[Record]): List[(String, List[String])] =
      data
        .groupMapTo(_.country)(_.user)
        .collectValuesAs(SortedSet)
        .collectResultsAs(SortedMap)
        .result
        .view
        .mapValues(_.toList)
        .toList

    val data = List(
      Record(user = "Luis", country = "Colombia"),
      Record(user = "Seth", country = "USA"),
      Record(user = "April", country =  "USA"),
      Record(user = "Julien", country = "Suisse"),
      Record(user = "Rob", country =  "USA"),
      Record(user = "Seth", country = "USA")
    )

    val expected = List(
      "Colombia" -> List("Luis"),
      "Suisse" -> List("Julien"),
      "USA" -> List("April", "Rob", "Seth")
    )

    assertEquals(expected, getUniqueUsersByCountrySorted(data))
  }

  @Test
  def anyCollectionGroupByToFull(): Unit = {
    def getUniqueWordsByFirstLetterSorted(data: List[String]): List[(Char, List[String])] =
      data
        .groupByTo(_.head)
        .collectValuesAs(SortedSet)
        .collectResultsAs(SortedMap)
        .result
        .view
        .mapValues(_.toList)
        .toList

    val data = List(
      "Autumn",
      "Banana",
      "April",
      "Wilson",
      "Apple",
      "Apple",
      "Winter",
      "Banana"
    )

    val expected = List(
      'A' -> List("Apple", "April", "Autumn"),
      'B' -> List("Banana"),
      'W' -> List("Wilson", "Winter")
    )

    assertEquals(expected, getUniqueWordsByFirstLetterSorted(data))
  }

  @Test
  def anyCollectionGroupByToReduceFull(): Unit = {
    def getAllWordsByFirstLetterSorted(data: List[String]): List[(Char, String)] =
      data
        .groupByTo(_.head)
        .collectResultsAs(SortedMap)
        .reduce(_ ++ " " ++ _)
        .toList

    val data = List(
      "Autumn",
      "Banana",
      "April",
      "Wilson",
      "Apple",
      "Apple",
      "Winter",
      "Banana"
    )

    val expected = List(
      'A' -> "Autumn April Apple Apple",
      'B' -> "Banana Banana",
      'W' -> "Wilson Winter"
    )

    assertEquals(expected, getAllWordsByFirstLetterSorted(data))
  }
  // -----------------------------------------------------------
}

object TestIterableOnceExtensions {
  final case class LowerCaseString(source: String) extends IterableOnce[Char] with IterableOnceOps[Char, Iterable, String] {
    override def iterator: Iterator[Char] = source.iterator.map(_.toLower)

    override def scanLeft[B](z: B)(op: (B, Char) => B): Iterable[B] = ???
    override def filter(p: Char => Boolean): String = ???
    override def filterNot(pred: Char => Boolean): String = ???
    override def take(n: Int): String = ???
    override def takeWhile(p: Char => Boolean): String = ???
    override def drop(n: Int): String = ???
    override def dropWhile(p: Char => Boolean): String = ???
    override def slice(from: Int, until: Int): String = ???
    override def map[B](f: Char => B): Iterable[B] = ???
    override def flatMap[B](f: Char => IterableOnce[B]): Iterable[B] = ???
    override def flatten[B](implicit asIterable: Char => IterableOnce[B]): Iterable[B] = ???
    override def collect[B](pf: PartialFunction[Char,B]): Iterable[B] = ???
    override def zipWithIndex: Iterable[(Char, Int)] = ???
    override def span(p: Char => Boolean): (String, String) = ???
    override def tapEach[U](f: Char => U): String = ???
  }

  final case class Record(user: String, country: String)
}

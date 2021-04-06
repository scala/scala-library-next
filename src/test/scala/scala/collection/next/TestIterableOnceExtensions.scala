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
import scala.collection.immutable.{ArraySeq, BitSet, SortedMap, SortedSet}

final class TestIterableOnceExtensions {
  import TestIterableOnceExtensions._

  // groupMapReduce --------------------------------------------
  @Test
  def iteratorGroupMapReduce(): Unit = {
    def occurrences[A](data: IterableOnce[A]): Map[A, Int] =
      data.iterator.groupMapReduce(identity)(_ => 1)(_ + _)

    val data = Seq('a', 'b', 'b', 'c', 'a', 'a', 'a', 'b')
    val expected = Map('a' -> 4, 'b' -> 3, 'c' -> 1)

    assertEquals(expected, occurrences(data))
  }

  @Test
  def iterableOnceOpsGroupMapReduce(): Unit = {
    def occurrences[A, CC[_], C](data: IterableOnceOps[A, CC, C]): Map[A, Int] =
      data.groupMapReduce(identity)(_ => 1)(_ + _)

    val data = Seq('a', 'b', 'b', 'c', 'a', 'a', 'a', 'b')
    val expected = Map('a' -> 4, 'b' -> 3, 'c' -> 1)

    assertEquals(expected, occurrences(data))
  }

  @Test
  def anyLikeIterableOnceGroupMapReduce(): Unit = {
    def occurrences[Repr](data: Repr)(implicit it: IsIterableOnce[Repr]): Map[it.A, Int] =
      it(data).iterator.groupMapReduce(identity)(_ => 1)(_ + _)

    val data = "abbcaaab"
    val expected = Map('a' -> 4, 'b' -> 3, 'c' -> 1)

    assertEquals(expected, occurrences(data))
  }

  @Test
  def customIterableOnceOpsGroupMapReduce(): Unit = {
    def occurrences(data: LowerCaseString): Map[Char, Int] =
      data.groupMapReduce(identity)(_ => 1)(_ + _)

    val data = LowerCaseString("abBcAaAb")
    val expected = Map('a' -> 4, 'b' -> 3, 'c' -> 1)

    assertEquals(expected, occurrences(data))
  }
  // -----------------------------------------------------------

  // GroupMapGenGen --------------------------------------------
  @Test
  def anyCollectionGroupMapGenResultAs(): Unit = {
    def getUniqueUsersByCountrySorted(data: List[Record]): List[(String, List[String])] =
      data
        .groupMapGenGen(_.country)(_.user)
        .collectValuesAs(SortedSet)
        .resultAs(SortedMap)
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
  def anyCollectionGroupMapGenGenReduce(): Unit = {
    def getAllWordsByFirstLetterSorted(data: List[String]): List[(Char, String)] =
      data
        .groupByGenGen(_.head)
        .reduceValuesAs(SortedMap)(_ ++ " " ++ _)
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

  @Test
  def iterableOnceOpsGroupByGenSpecificFactory(): Unit = {
    def bitsByEven(data: BitSet): Map[Boolean, BitSet] =
      data.groupByGen(x => (x % 2) == 0).result

    val data = BitSet(1, 2, 3, 4, 5)
    val expected = Map(
      true -> BitSet(2, 4),
      false -> BitSet(1, 3, 5)
    )

    assertEquals(expected, bitsByEven(data))
  }

  @Test
  def iterableOnceOpsGroupMapGenIterableFactory(): Unit = {
    def bitsByEvenAsChars(data: BitSet): Map[Boolean, Set[Char]] =
      data.groupMapGen(x => (x % 2) == 0)(_.toChar).result

    val data = BitSet(100, 101, 102, 103, 104, 105)
    val expected = Map(
      true -> Set('d', 'f', 'h'),
      false -> Set('e', 'g', 'i')
    )

    assertEquals(expected, bitsByEvenAsChars(data))
  }

  @Test
  def iteratorGroupBy(): Unit = {
    def getUniqueWordsByFirstLetter(data: IterableOnce[String]): List[(Char, Set[String])] =
      data
        .iterator
        .groupBy(_.head)
        .view
        .mapValues(_.toSet)
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
      'A' -> Set("Apple", "April", "Autumn"),
      'B' -> Set("Banana"),
      'W' -> Set("Wilson", "Winter")
    )

    assertEquals(expected, getUniqueWordsByFirstLetter(data))
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

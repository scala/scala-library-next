/*
 * Scala (https://www.scala-lang.org)
 *
 * Copyright Skylight IPV Ltd.
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
import scala.collection.IterableOps
import scala.collection.generic.IsIterable

final class TestIterableOpsExtensions {
  import TestIterableOpsExtensions.LowerCaseString

  @Test
  def iterableOpsGroupFlatMap(): Unit = {
    def groupedStrings[A, CC[_], C](coll: IterableOps[A, CC, C]): Map[A, CC[A]] =
      coll.groupFlatMap(identity)(Seq(_))

    val xs = Seq('a', 'b', 'c', 'b', 'c', 'c')
    val expected = Map('a' -> Seq('a'), 'b' -> Seq('b', 'b'), 'c' -> Seq('c', 'c', 'c'))
    assertEquals(expected, groupedStrings(xs))
  }

  @Test
  def anyLikeIterableGroupFlatMap(): Unit = {
    def groupedStrings[Repr](coll: Repr)(implicit it: IsIterable[Repr]): Map[it.A, Iterable[it.A]] =
      it(coll).groupFlatMap(identity)(Seq(_))

    val xs = "abbcaaab"
    val expected = Map('a' -> Seq('a', 'a', 'a', 'a'), 'b' -> Seq('b', 'b', 'b'), 'c' -> Seq('c'))
    assertEquals(expected, groupedStrings(xs))
  }

  @Test
  def customIterableOnceOpsGroupMapReduce(): Unit = {
    def groupedStrings(coll: LowerCaseString): Map[Char, Iterable[Char]] =
      coll.groupFlatMap(identity)(Seq(_))

    val xs = LowerCaseString("abBcAaAb")
    val expected = Map('a' -> Seq('a', 'a', 'a', 'a'), 'b' -> Seq('b', 'b', 'b'), 'c' -> Seq('c'))
    assertEquals(expected, groupedStrings(xs))
  }
}

object TestIterableOpsExtensions {
  final case class LowerCaseString(source: String) extends Iterable[Char] {
    override def iterator: Iterator[Char] = source.iterator.map(_.toLower)
  }
}

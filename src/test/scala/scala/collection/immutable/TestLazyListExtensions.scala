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

package scala.collection.immutable

import org.junit.Assert._
import org.junit.Test

import next._

class TestLazyListExtensions {

  // This method will *not* terminate for non-cyclic infinite-sized collections.
  // (It's kind of nasty to have tests whose failure mode is to hang, but I don't
  // see an obvious alternative that doesn't involve copying code from LazyList.
  // Perhaps this could be improved at the time this all gets merged into stdlib.)
  def assertConstantMemory[T](xs: LazyList[T]): Unit =
    // `force` does cycle detection, so if this terminates, the collection is
    // either finite or a cycle
    xs.force

  @Test
  def cycleEmpty1(): Unit = {
    val xs = LazyList.empty  // realized
    val cyc = xs.cycle
    assertTrue(cyc.isEmpty)
    assertTrue(cyc.size == 0)
    assertEquals(Nil, cyc.toList)
  }
  @Test
  def cycleEmpty2(): Unit = {
    val xs = LazyList.empty #::: LazyList.empty  // not realized
    assertEquals(-1, xs.knownSize)  // double-check it's not realized
    val cyc = xs.cycle
    assertTrue(cyc.isEmpty)
    assertTrue(cyc.size == 0)
    assertEquals(Nil, cyc.toList)
  }
  @Test
  def cycleNonEmpty(): Unit = {
    val xs = LazyList(1, 2, 3)
    val cyc = xs.cycle
    assertFalse(cyc.isEmpty)
    assertConstantMemory(cyc)
    assertEquals(LazyList(1, 2, 3, 1, 2, 3, 1, 2), cyc.take(8))
  }
  @Test
  def cycleToString(): Unit = {
    assertEquals("LazyList()",
      LazyList.empty.cycle.toString)
    assertEquals("LazyList(<not computed>)",
      LazyList(1, 2, 3).cycle.toString)
    // note cycle detection here!
    assertEquals("LazyList(1, 2, 3, <cycle>)",
      LazyList(1, 2, 3).cycle.force.toString)
  }
  @Test
  def cycleRepeats(): Unit = {
    val xs = LazyList(1, 2, 3)
    val cyc = xs.cycle
    assertFalse(cyc.isEmpty)
    assertEquals(LazyList(1, 2, 3, 1, 2, 3, 1, 2), cyc.take(8))
  }
  @Test
  def cycleConstantMemory1(): Unit = {
    val xs = LazyList(1, 2, 3)
    val cyc = xs.cycle
    assertTrue(cyc.tail eq cyc.tail.tail.tail.tail)
    assertTrue(cyc.tail.tail eq cyc.drop(4).tail)
    assertTrue(cyc.tail eq cyc.drop(3).tail)
  }
  @Test
  def cycleConstantMemory2(): Unit = {
    var counter = 0
    def count(): Int = { counter += 1; counter }
    val xs = count() #:: count() #:: count() #:: LazyList.empty
    val cyc = xs.cycle
    assertEquals(0, counter)
    assertEquals(10, cyc.take(10).size)
    assertEquals(3, counter)
  }
  @Test
  def cycleConstantMemory3(): Unit = {
    val xs = LazyList(1, 2, 3)
    val cyc = xs.cycle
    assertConstantMemory(cyc)
    assertConstantMemory(cyc.tail)
    assertConstantMemory(cyc.tail.tail)
    assertConstantMemory(cyc.tail.tail.tail)
    assertConstantMemory(cyc.tail.tail.tail.tail)
    assertConstantMemory(cyc.drop(1))
    assertConstantMemory(cyc.drop(10))
  }
  @Test
  def cycleUnbounded(): Unit = {
    val xs = LazyList.from(1)
    val cyc = xs.cycle
    assertEquals(LazyList(1, 2, 3), cyc.take(3))
  }
  @Test
  def cycleSecondCallIsSafeButNotIdempotent(): Unit = {
    val xs = LazyList(1, 2, 3)
    // this is safe to do
    val twice = xs.cycle.cycle
    // and the contents are as expected
    assertEquals(LazyList(1, 2, 3, 1, 2, 3, 1, 2), twice.take(8))
    // but the result is not a cycle. it might be nice if it were, but oh well.
    // testing the existing behavior.
    assertFalse(twice.tail eq twice.tail.tail.tail.tail)
  }
}

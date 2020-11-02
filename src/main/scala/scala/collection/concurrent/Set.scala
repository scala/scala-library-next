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

package scala
package collection
package concurrent

/**
 * A template trait for mutable sets that allow concurrent access.
 *
 * $concurrentsetinfo
 *
 * @tparam A the value type of the set
 * @define Coll `concurrent.Set`
 * @define coll concurrent set
 * @define concurrentsetinfo
 *              This is a base trait for all Scala concurrent set implementations. It
 *              provides all of the methods a `Set` does, with the difference that all the
 *              changes are atomic.
 * @note The concurrent set do not accept `null` for values.
 */
trait Set[A] extends scala.collection.mutable.Set[A]

object Set extends IterableFactory.Delegate[Set](SetFromMap(TrieMap)) {
  def fromMap(factory: MapFactory[Map]): IterableFactory[Set] = SetFromMap(factory)

  def fromMap[A](map: Map[A, Unit]): Set[A] = SetFromMap(map)
}

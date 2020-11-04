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
package mutable

import scala.annotation.unused

package object next {
  implicit final class NextSCMSetCompanionExtensions(@unused private val self: Set.type)
      extends AnyVal {
    def fromMap(factory: MapFactory[Map]): IterableFactory[Set] = SetFromMap(factory)

    def fromMap[A](map: Map[A, Unit]): Set[A] = SetFromMap(map)
  }

  implicit final class NextSCMSortedSetCompanionExtensions(@unused private val self: SortedSet.type)
      extends AnyVal {
    def fromMap(factory: SortedMapFactory[SortedMap]): SortedIterableFactory[SortedSet] =
      SortedSetFromMap(factory)

    def fromMap[A](map: SortedMap[A, Unit]): SortedSet[A] = SortedSetFromMap(map)
  }
}

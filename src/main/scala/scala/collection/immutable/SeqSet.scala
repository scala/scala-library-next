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
package immutable

/**
 * A generic trait for ordered immutable sets. Concrete classes have to provide
 * functionality for the abstract methods in `SeqSet`.
 *
 * Note that when checking for equality [[SeqSet]] does not take into account
 * ordering.
 *
 * @tparam A the type of the values contained in this linked set.
 * @define coll immutable seq set
 * @define Coll `immutable.SeqSet`
 */
trait SeqSet[A]
    extends Set[A]
    with collection.SeqSet[A]
    with SetOps[A, SeqSet, SeqSet[A]]
    with IterableFactoryDefaults[A, SeqSet] {
  override def iterableFactory: IterableFactory[SeqSet] = SeqSet
}

object SeqSet extends IterableFactory.Delegate[SeqSet](SeqSetFromMap(SeqMap)) {
  def fromMap(factory: MapFactory[SeqMap]): IterableFactory[SeqSet] = SeqSetFromMap(factory)

  def fromMap[A](map: SeqMap[A, Unit]): SeqSet[A] = SeqSetFromMap(map)
}

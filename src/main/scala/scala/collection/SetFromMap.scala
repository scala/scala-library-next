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

import scala.collection.generic.DefaultSerializable

@SerialVersionUID(3L)
private class SetFromMap[A](protected[collection] val underlying: Map[A, Unit])
    extends AbstractSet[A]
    with SetFromMapOps.Unknown[A, Map, Map[A, Unit], SetFromMap, SetFromMap[A]]
    with SetFromMapOps.Unsorted[A, Map, SetFromMap]
    with SetFromMapOps.DynamicClassName
    with IterableFactoryDefaults[A, SetFromMap]
    with DefaultSerializable {
  protected[this] def fromMap[B](m: Map[B, Unit]): SetFromMap[B] = new SetFromMap(m)

  override def iterableFactory: IterableFactory[SetFromMap] =
    new SetFromMap.WrapperFactory(underlying.mapFactory)
}

private object SetFromMap extends SetFromMapMetaFactory[Map, Set] {
  def apply(factory: MapFactory[Map]): IterableFactory[Set] = new WrapperFactory(factory)

  def apply[A](map: Map[A, Unit]): Set[A] = new SetFromMap(map)

  @SerialVersionUID(3L)
  private class WrapperFactory(mf: MapFactory[Map]) extends SetFromMapFactory[Map, SetFromMap](mf) {
    protected[this] def fromMap[A](map: Map[A, Unit]): SetFromMap[A] =
      new SetFromMap(map)
  }

}

@SerialVersionUID(3L)
private class SeqSetFromMap[A](protected[collection] val underlying: SeqMap[A, Unit])
    extends AbstractSet[A]
    with SetFromMapOps.Unknown[A, SeqMap, SeqMap[A, Unit], SeqSetFromMap, SeqSetFromMap[A]]
    with SetFromMapOps.Unsorted[A, SeqMap, SeqSetFromMap]
    with SetFromMapOps.DynamicClassName
    with SeqSet[A]
    with IterableFactoryDefaults[A, SeqSetFromMap]
    with DefaultSerializable {
  protected[this] def fromMap[B](m: SeqMap[B, Unit]): SeqSetFromMap[B] = new SeqSetFromMap(m)

  override def iterableFactory: IterableFactory[SeqSetFromMap] =
    new SeqSetFromMap.WrapperFactory(underlying.mapFactory)
}

private object SeqSetFromMap extends SetFromMapMetaFactory[SeqMap, SeqSet] {
  def apply(factory: MapFactory[SeqMap]): IterableFactory[SeqSet] = new WrapperFactory(factory)

  def apply[A](map: SeqMap[A, Unit]): SeqSet[A] = new SeqSetFromMap(map)

  @SerialVersionUID(3L)
  private class WrapperFactory(mf: MapFactory[SeqMap])
      extends SetFromMapFactory[SeqMap, SeqSetFromMap](mf) {
    protected[this] def fromMap[A](map: SeqMap[A, Unit]): SeqSetFromMap[A] =
      new SeqSetFromMap(map)
  }

}

@SerialVersionUID(3L)
private class SortedSetFromMap[A](protected[collection] val underlying: SortedMap[A, Unit])(implicit
    val ordering: Ordering[A]
) extends AbstractSet[A]
    with SetFromMapOps.Unknown[A, Map, SortedMap[A, Unit], Set, SortedSetFromMap[A]]
    with SetFromMapOps.Sorted[A, SortedMap, Set, SortedSetFromMap]
    with SetFromMapOps.DynamicClassName
    with SortedSet[A]
    with SortedSetOps[A, SortedSetFromMap, SortedSetFromMap[A]]
    with IterableFactoryDefaults[A, Set]
    with SortedSetFactoryDefaults[A, SortedSetFromMap, Set]
    with DefaultSerializable {
  protected[this] def fromMap[B](m: Map[B, Unit]): SetFromMap[B] = new SetFromMap(m)

  protected[this] def fromSortedMap[B: Ordering](m: SortedMap[B, Unit]): SortedSetFromMap[B] =
    new SortedSetFromMap(m)

  override def iterableFactory: IterableFactory[Set] = SetFromMap(underlying.mapFactory)

  override def sortedIterableFactory: SortedIterableFactory[SortedSetFromMap] =
    new SortedSetFromMap.WrapperFactory(underlying.sortedMapFactory)
}

private object SortedSetFromMap extends SortedSetFromMapMetaFactory[SortedMap, SortedSet] {
  def apply(factory: SortedMapFactory[SortedMap]): SortedIterableFactory[SortedSet] =
    new WrapperFactory(factory)

  def apply[A](map: SortedMap[A, Unit]): SortedSet[A] = new SortedSetFromMap(map)(map.ordering)

  @SerialVersionUID(3L)
  private final class WrapperFactory(mf: SortedMapFactory[SortedMap])
      extends SortedSetFromMapFactory[SortedMap, SortedSetFromMap](mf) {
    protected[this] def fromMap[A](map: SortedMap[A, Unit]): SortedSetFromMap[A] =
      new SortedSetFromMap(map)(map.ordering)
  }

}

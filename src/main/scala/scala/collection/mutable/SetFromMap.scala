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

import scala.collection.SetFromMapOps.WrappedMap
import scala.collection.generic.DefaultSerializable

// Implementation note: The `concurrent.Set` implementation
//   inherits from this, so we have to be careful about
//   making changes that do more than forward to the
//   underlying `Map`. If we have a method implementation
//   that is not atomic, we MUST override that method in
//   `concurrent.SetFromMap`.
private[collection] trait SetFromMapOps[
    A,
    +MM[K, V] <: MapOps[K, V, MM, _],
    +M <: MapOps[A, Unit, MM, M],
    +CC[_],
    +C <: SetFromMapOps[A, MM, M, CC, C],
] extends SetOps[A, CC, C]
    with collection.SetFromMapOps[A, MM, M, CC, C] {
  def clear(): Unit = underlying.clear()

  def addOne(elem: A): this.type = { underlying.update(elem, ()); this }

  override def add(elem: A): Boolean = underlying.put(elem, ()).isEmpty

  override def addAll(xs: IterableOnce[A]): this.type =
    xs match {
      case coll: WrappedMap[A] =>
        underlying.addAll(coll.underlying)
        this
      case coll => super.addAll(coll)
    }

  def subtractOne(elem: A): this.type = { underlying.subtractOne(elem); this }

  override def remove(elem: A): Boolean = underlying.remove(elem).isDefined

  override def subtractAll(xs: IterableOnce[A]): this.type = { underlying.subtractAll(xs); this }

  // We need to define this explicitly because there's a multiple inheritance diamond
  override def knownSize: Int = super[SetFromMapOps].knownSize
}

@SerialVersionUID(3L)
private[collection] class SetFromMap[A](protected[collection] val underlying: Map[A, Unit])
    extends AbstractSet[A]
    with SetFromMapOps[A, Map, Map[A, Unit], SetFromMap, SetFromMap[A]]
    with SetFromMapOps.Unsorted[A, Map, SetFromMap]
    with SetFromMapOps.DynamicClassName
    with IterableFactoryDefaults[A, SetFromMap]
    with DefaultSerializable {
  protected[this] def fromMap[B](m: Map[B, Unit]): SetFromMap[B] = new SetFromMap(m)

  override def iterableFactory: IterableFactory[SetFromMap] = SetFromMap(underlying.mapFactory)
}

private[collection] object SetFromMap extends SetFromMapMetaFactory[Map, Set] {
  def apply(factory: MapFactory[Map]): IterableFactory[SetFromMap] = new WrapperFactory(factory)

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
    with SetFromMapOps[A, SeqMap, SeqMap[A, Unit], SeqSetFromMap, SeqSetFromMap[A]]
    with SetFromMapOps.Unsorted[A, SeqMap, SeqSetFromMap]
    with SetFromMapOps.DynamicClassName
    with SeqSet[A]
    with IterableFactoryDefaults[A, SeqSetFromMap]
    with DefaultSerializable {
  protected[this] def fromMap[B](m: SeqMap[B, Unit]): SeqSetFromMap[B] = new SeqSetFromMap(m)

  override def iterableFactory: IterableFactory[SeqSetFromMap] = SeqSetFromMap(
    underlying.mapFactory
  )
}

private object SeqSetFromMap extends SetFromMapMetaFactory[SeqMap, SeqSet] {
  def apply(factory: MapFactory[SeqMap]): IterableFactory[SeqSetFromMap] = new WrapperFactory(
    factory
  )

  def apply[A](map: SeqMap[A, Unit]): mutable.SeqSet[A] = new SeqSetFromMap(map)

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
    with SetFromMapOps[A, Map, SortedMap[A, Unit], Set, SortedSetFromMap[A]]
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

  override def iterableFactory: IterableFactory[SetFromMap] = SetFromMap(underlying.mapFactory)

  override def sortedIterableFactory: SortedIterableFactory[SortedSetFromMap] =
    new SortedSetFromMap.WrapperFactory(underlying.sortedMapFactory)
}

private object SortedSetFromMap extends SortedSetFromMapMetaFactory[SortedMap, SortedSet] {
  def apply(factory: SortedMapFactory[SortedMap]): SortedIterableFactory[SortedSet] =
    new WrapperFactory(factory)

  def apply[A](map: SortedMap[A, Unit]): mutable.SortedSet[A] = new SortedSetFromMap(map)(
    map.ordering
  )

  @SerialVersionUID(3L)
  private final class WrapperFactory(mf: SortedMapFactory[SortedMap])
      extends SortedSetFromMapFactory[SortedMap, SortedSetFromMap](mf) {
    protected[this] def fromMap[A](map: SortedMap[A, Unit]): SortedSetFromMap[A] =
      new SortedSetFromMap(map)(map.ordering)
  }

}

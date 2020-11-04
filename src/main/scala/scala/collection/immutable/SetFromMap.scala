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

import scala.collection.generic.DefaultSerializable

private[collection] trait SetFromMapOps[
    A,
    +MM[K, +V] <: MapOps[K, V, MM, _],
    +M <: MapOps[A, Unit, MM, M],
    +CC[_],
    +C <: SetFromMapOps[A, MM, M, CC, C],
] extends SetOps[A, CC, C]
    with collection.SetFromMapOps[A, MM, M, CC, C] {
  def excl(elem: A): C = fromSpecificMap(underlying removed elem)

  override def removedAll(that: IterableOnce[A]): C = fromSpecificMap(underlying removedAll that)
}

private[collection] object SetFromMapOps {

  trait Unsorted[
      A,
      +MM[K, +V] <: MapOps[K, V, MM, MM[K, V]],
      +CC[X] <: Unsorted[X, MM, CC],
  ] extends SetFromMapOps[A, MM, MM[A, Unit], CC, CC[A]]
      with collection.SetFromMapOps.Unsorted[A, MM, CC] {
    def incl(elem: A): CC[A] = fromMap(underlying.updated(elem, ()))
  }

}

@SerialVersionUID(3L)
private[collection] class SetFromMap[A](protected[collection] val underlying: Map[A, Unit])
    extends AbstractSet[A]
    with SetFromMapOps.Unsorted[A, Map, SetFromMap]
    with collection.SetFromMapOps.DynamicClassName
    with IterableFactoryDefaults[A, SetFromMap]
    with DefaultSerializable {
  protected[this] def fromMap[B](m: Map[B, Unit]): SetFromMap[B] = new SetFromMap(m)

  override def iterableFactory: IterableFactory[SetFromMap] =
    new SetFromMap.WrapperFactory(underlying.mapFactory)
}

private[collection] object SetFromMap extends SetFromMapMetaFactory[Map, Set] {
  def apply(factory: MapFactory[Map]): IterableFactory[Set] = new DynamicFactory(factory)

  def apply[A](map: Map[A, Unit]): Set[A] = map match {
    case map: SeqMap[A, Unit]    => SeqSetFromMap(map)
    case map: SortedMap[A, Unit] => SortedSetFromMap(map)
    case map                     => apply(map)
  }

  @SerialVersionUID(3L)
  private class WrapperFactory(mf: MapFactory[Map]) extends SetFromMapFactory[Map, SetFromMap](mf) {
    protected[this] def fromMap[A](map: Map[A, Unit]): SetFromMap[A] = new SetFromMap(map)
  }

  @SerialVersionUID(3L)
  private class DynamicFactory(mf: MapFactory[Map]) extends SetFromMapFactory[Map, Set](mf) {
    protected[this] def fromMap[A](map: Map[A, Unit]): Set[A] = SetFromMap(map)
  }

}

@SerialVersionUID(3L)
private[collection] class SeqSetFromMap[A](protected[collection] val underlying: SeqMap[A, Unit])
    extends AbstractSet[A]
    with SetFromMapOps.Unsorted[A, SeqMap, SeqSetFromMap]
    with collection.SetFromMapOps.DynamicClassName
    with SeqSet[A]
    with IterableFactoryDefaults[A, SeqSetFromMap]
    with DefaultSerializable {
  protected[this] def fromMap[B](m: SeqMap[B, Unit]): SeqSetFromMap[B] =
    new SeqSetFromMap(m)

  override def iterableFactory: IterableFactory[SeqSetFromMap] =
    new SeqSetFromMap.WrapperFactory(underlying.mapFactory)
}

private[collection] object SeqSetFromMap extends SetFromMapMetaFactory[SeqMap, SeqSet] {
  def apply(factory: MapFactory[SeqMap]): IterableFactory[SeqSet] = new WrapperFactory(factory)

  def apply[A](map: SeqMap[A, Unit]): SeqSet[A] = new SeqSetFromMap(map)

  @SerialVersionUID(3L)
  private class WrapperFactory(mf: MapFactory[SeqMap])
      extends SetFromMapFactory[SeqMap, SeqSetFromMap](mf) {
    protected[this] def fromMap[A](map: SeqMap[A, Unit]): SeqSetFromMap[A] = new SeqSetFromMap(map)
  }

}

@SerialVersionUID(3L)
private[collection] class SortedSetFromMap[A](
    protected[collection] val underlying: SortedMap[A, Unit]
)(implicit val ordering: Ordering[A])
    extends AbstractSet[A]
    with SetFromMapOps[A, Map, SortedMap[A, Unit], Set, SortedSetFromMap[A]]
    with collection.SetFromMapOps.Sorted[A, SortedMap, Set, SortedSetFromMap]
    with collection.SetFromMapOps.DynamicClassName
    with SortedSet[A]
    with SortedSetOps[A, SortedSetFromMap, SortedSetFromMap[A]]
    with IterableFactoryDefaults[A, Set]
    with SortedSetFactoryDefaults[A, SortedSetFromMap, Set]
    with DefaultSerializable {

  import SortedSetFromMap.ssfm

  protected[this] def fromMap[B](m: Map[B, Unit]): Set[B] = new SetFromMap(m)

  protected[this] def fromSortedMap[B: Ordering](m: SortedMap[B, Unit]): SortedSetFromMap[B] =
    new SortedSetFromMap(m)

  override def iterableFactory: IterableFactory[Set] = SetFromMap(underlying.mapFactory)

  override def sortedIterableFactory: SortedIterableFactory[SortedSetFromMap] =
    new SortedSetFromMap.WrapperFactory(underlying.sortedMapFactory)

  override def incl(elem: A): SortedSetFromMap[A] = ssfm(underlying.updated(elem, ()))
}

private[collection] object SortedSetFromMap
    extends SortedSetFromMapMetaFactory[SortedMap, SortedSet] {
  @inline private def ssfm[A: Ordering](map: SortedMap[A, Unit]): SortedSetFromMap[A] =
    new SortedSetFromMap(map)

  def apply(factory: SortedMapFactory[SortedMap]): SortedIterableFactory[SortedSet] =
    new WrapperFactory(factory)

  def apply[A](map: SortedMap[A, Unit]): SortedSet[A] = ssfm(map)(map.ordering)

  @SerialVersionUID(3L)
  private final class WrapperFactory(mf: SortedMapFactory[SortedMap])
      extends SortedSetFromMapFactory[SortedMap, SortedSetFromMap](mf) {
    protected[this] def fromMap[A](map: SortedMap[A, Unit]): SortedSetFromMap[A] =
      ssfm(map)(map.ordering)
  }

}

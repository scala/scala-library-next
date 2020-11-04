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

import scala.collection.generic.DefaultSerializable
import scala.collection.{mutable => m}

@SerialVersionUID(3L)
private[collection] class SetFromMap[A](override protected[collection] val underlying: Map[A, Unit])
    extends mutable.SetFromMap[A](underlying)
    with Set[A]
    with mutable.SetFromMapOps[A, m.Map, m.Map[A, Unit], m.SetFromMap, m.SetFromMap[A]]
    with IterableFactoryDefaults[A, m.SetFromMap]
    with DefaultSerializable {
  override protected[this] def fromMap[B](m: mutable.Map[B, Unit]): mutable.SetFromMap[B] =
    SetFromMap.fromMutableMap(m)

  override def iterableFactory: IterableFactory[m.SetFromMap] =
    new SetFromMap.MutableFactory(underlying.mapFactory)
}

private[collection] object SetFromMap extends SetFromMapMetaFactory[Map, Set] {
  def apply(factory: MapFactory[Map]): IterableFactory[Set] = new WrapperFactory(factory)

  def apply[A](map: Map[A, Unit]): Set[A] = new SetFromMap(map)

  @inline private def fromMutableMap[A](map: m.Map[A, Unit]): m.SetFromMap[A] =
    map match {
      case conc: Map[A, Unit] => new SetFromMap(conc)
      case mut                => new m.SetFromMap(mut)
    }

  @SerialVersionUID(3L)
  private class WrapperFactory(mf: MapFactory[Map]) extends SetFromMapFactory[Map, SetFromMap](mf) {
    protected[this] def fromMap[A](map: Map[A, Unit]): SetFromMap[A] = new SetFromMap(map)
  }

  @SerialVersionUID(3L)
  private class MutableFactory(mf: MapFactory[m.Map])
      extends SetFromMapFactory[m.Map, m.SetFromMap](mf) {
    protected[this] def fromMap[A](map: m.Map[A, Unit]): m.SetFromMap[A] = fromMutableMap(map)
  }

}

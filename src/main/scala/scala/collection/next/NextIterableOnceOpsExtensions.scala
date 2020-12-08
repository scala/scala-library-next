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

package scala.collection
package next

import scala.language.implicitConversions

private[next] final class NextIterableOnceOpsExtensions[A, CC[_], C](
  private val col: IterableOnceOps[A, CC, C]
) extends AnyVal {
  import NextIterableOnceOpsExtensions.GroupMap

  /**
   * Partitions this IterableOnce into a map according to a discriminator function `key`. All the values that
   * have the same discriminator are then transformed by the `value` function and then reduced into a
   * single value with the `reduce` function.
   *
   * {{{
   *   def occurrences[A](as: IterableOnce[A]): Map[A, Int] =
   *     as.iterator.groupMapReduce(identity)(_ => 1)(_ + _)
   * }}}
   *
   * @note This will force the evaluation of the Iterator.
   */
  def groupMapReduce[K, B](key: A => K)(f: A => B)(reduce: (B, B) => B): immutable.Map[K, B] =
    groupMapTo(key)(f).reduce(reduce)

  def groupByTo[K](key: A => K): GroupMap[A, K, A, immutable.Iterable, immutable.Map] =
    groupMapTo(key)(identity)

  def groupMapTo[K, V](key: A => K)(f: A => V): GroupMap[A, K, V, immutable.Iterable, immutable.Map] =
    new GroupMap(col, key, f, immutable.Iterable, immutable.Map)
}

object NextIterableOnceOpsExtensions {
  final case class GroupMap[A, K, V, CC[_], MC[_, _]](
    col: IterableOnceOps[A, AnyConstr, _],
    key: A => K,
    f: A => V,
    colFactory: Factory[V, CC[V]],
    mapFactory: CustomMapFactory[MC, K]
  ) {
    def collectValuesAs[CC1[_]](factory: Factory[V, CC1[V]]): GroupMap[A, K, V, CC1, MC] =
      this.copy(colFactory = factory)

    def collectResultsAs[MC1[_, _]](factory: CustomMapFactory[MC1, K]): GroupMap[A, K, V, CC, MC1] =
      this.copy(mapFactory = factory)

    final def result: MC[K, CC[V]] = {
      val m = mutable.Map.empty[K, mutable.Builder[V, CC[V]]]
      col.foreach { elem =>
        val k = key(elem)
        val v = f(elem)
        m.get(k) match {
          case Some(builder) => builder.addOne(v)
          case None          => m += (k -> colFactory.newBuilder.addOne(v))
        }
      }
      mapFactory.from(m.view.mapValues(_.result()))
    }

    final def reduce(reduce: (V, V) => V): MC[K, V] = {
      val m = mutable.Map.empty[K, V]
      col.foreach { elem =>
        m.updateWith(key = key(elem)) {
          case Some(b) => Some(reduce(b, f(elem)))
          case None    => Some(f(elem))
        }
      }
      mapFactory.from(m)
    }
  }

  sealed trait CustomMapFactory[MC[_, _], K] {
    def from[V](col: IterableOnce[(K, V)]): MC[K, V]
  }

  object CustomMapFactory {
    implicit def fromMapFactory[MC[_, _], K](mf: MapFactory[MC]): CustomMapFactory[MC, K] =
      new CustomMapFactory[MC, K] {
        override def from[V](col: IterableOnce[(K, V)]): MC[K, V] =
          mf.from(col)
      }

    implicit def fromSortedMapFactory[MC[_, _], K : Ordering](smf: SortedMapFactory[MC]): CustomMapFactory[MC, K] =
      new CustomMapFactory[MC, K] {
        override def from[V](col: IterableOnce[(K, V)]): MC[K, V] =
          smf.from(col)
      }
  }
}

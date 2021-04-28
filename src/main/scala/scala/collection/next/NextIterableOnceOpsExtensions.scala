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

private[next] final class NextIterableOnceOpsExtensions[A, CC[_], C](
  private val col: IterableOnceOps[A, CC, C]
) extends AnyVal {
  import NextIterableOnceOpsExtensions.{GroupMapToView, GroupMapView}

  def groupBy[K](key: A => K)(implicit groupsFactory: Factory[A, C]): immutable.Map[K, C] =
    viewGroupByTo(key).toMap

  def viewGroupByTo[K](key: A => K)(implicit groupsFactory: Factory[A, C]): GroupMapToView[A, K, A, C] =
    viewGroupBy(key).collectGroupsTo(groupsFactory)

  def viewGroupBy[K](key: A => K): GroupMapView[A, K, A] =
    viewGroupMap(key)(identity)

  def groupMap[K, V](key: A => K)(f: A => V)(implicit groupsFactory: Factory[V, CC[V]]): immutable.Map[K, CC[V]] =
    viewGroupMapTo(key)(f).toMap

  def viewGroupMapTo[K, V](key: A => K)(f: A => V)(implicit groupsFactory: Factory[V, CC[V]]): GroupMapToView[A, K, V, CC[V]] =
    viewGroupMap(key)(f).collectGroupsTo(groupsFactory)

  def viewGroupMap[K, V](key: A => K)(f: A => V): GroupMapView[A, K, V] =
    new GroupMapView(col, key, f)

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
  def groupMapReduce[K, V](key: A => K)(f: A => V)(reduce: (V, V) => V): immutable.Map[K, V] =
    viewGroupMap(key)(f).reduceValuesTo(immutable.Map)(reduce)
}

private[next] object NextIterableOnceOpsExtensions {
  final class GroupMapView[A, K, V] private[NextIterableOnceOpsExtensions](
    col: IterableOnceOps[A, AnyConstr, _],
    key: A => K,
    f: A => V
  ) {
    def reduceValuesTo[MC](resultFactory: Factory[(K, V), MC])(reduce: (V, V) => V): MC = {
      val m = mutable.Map.empty[K, V]
      col.foreach { elem =>
        m.updateWith(key = key(elem)) {
          case Some(b) => Some(reduce(b, f(elem)))
          case None    => Some(f(elem))
        }
      }
      resultFactory.fromSpecific(m)
    }

    def collectGroupsTo[C](groupsFactory: Factory[V, C]): GroupMapToView[A, K, V, C] =
      new GroupMapToView(col, key, f, groupsFactory)
  }

  final class GroupMapToView[A, K, V, C] private[NextIterableOnceOpsExtensions](
    col: IterableOnceOps[A, AnyConstr, _],
    key: A => K,
    f: A => V,
    groupsFactory: Factory[V, C]
  ) {
    def toMap: immutable.Map[K, C] =
      to(immutable.Map)

    def to[MC](resultFactory: Factory[(K, C), MC]): MC = {
      val m = mutable.Map.empty[K, mutable.Builder[V, C]]
      col.foreach { elem =>
        val k = key(elem)
        val v = f(elem)
        m.get(k) match {
          case Some(builder) => builder.addOne(v)
          case None          => m.update(key = k, value = groupsFactory.newBuilder.addOne(v))
        }
      }
      resultFactory.fromSpecific(m.view.mapValues(_.result()))
    }
  }
}

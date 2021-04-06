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
  import NextIterableOnceOpsExtensions.{GroupMapGen, GroupMapGenGen}

  def groupBy[K](key: A => K)(implicit valuesFactory: Factory[A, C]): immutable.Map[K, C] =
    groupByGen(key).result

  def groupByGen[K](key: A => K)(implicit valuesFactory: Factory[A, C]): GroupMapGen[A, K, A, C] =
    groupByGenGen(key).collectValuesAs(valuesFactory)

  def groupByGenGen[K](key: A => K): GroupMapGenGen[A, K, A] =
    groupMapGenGen(key)(identity)

  def groupMap[K, V](key: A => K)(f: A => V)(implicit valuesFactory: Factory[V, CC[V]]): immutable.Map[K, CC[V]] =
    groupMapGen(key)(f).result

  def groupMapGen[K, V](key: A => K)(f: A => V)(implicit valuesFactory: Factory[V, CC[V]]): GroupMapGen[A, K, V, CC[V]] =
    groupMapGenGen(key)(f).collectValuesAs(valuesFactory)

  def groupMapGenGen[K, V](key: A => K)(f: A => V): GroupMapGenGen[A, K, V] =
    new GroupMapGenGen(col, key, f)

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
    groupMapGenGen(key)(f).reduceValues(reduce)
}

private[next] object NextIterableOnceOpsExtensions {
  final class GroupMapGenGen[A, K, V] private[NextIterableOnceOpsExtensions](
    col: IterableOnceOps[A, AnyConstr, _],
    key: A => K,
    f: A => V
  ) {
    def reduceValues(reduce: (V, V) => V): immutable.Map[K, V] =
      reduceValuesAs(immutable.Map)(reduce)

    def reduceValuesAs[MC](resultFactory: Factory[(K, V), MC])(reduce: (V, V) => V): MC = {
      val m = mutable.Map.empty[K, V]
      col.foreach { elem =>
        m.updateWith(key = key(elem)) {
          case Some(b) => Some(reduce(b, f(elem)))
          case None    => Some(f(elem))
        }
      }
      resultFactory.fromSpecific(m)
    }

    def collectValuesAs[C](valuesFactory: Factory[V, C]): GroupMapGen[A, K, V, C] =
      new GroupMapGen(col, key, f, valuesFactory)
  }

  final class GroupMapGen[A, K, V, C] private[NextIterableOnceOpsExtensions](
    col: IterableOnceOps[A, AnyConstr, _],
    key: A => K,
    f: A => V,
    valuesFactory: Factory[V, C]
  ) {
    def result: immutable.Map[K, C] =
      resultAs(immutable.Map)

    def resultAs[MC](resultFactory: Factory[(K, C), MC]): MC = {
      val m = mutable.Map.empty[K, mutable.Builder[V, C]]
      col.foreach { elem =>
        val k = key(elem)
        val v = f(elem)
        m.get(k) match {
          case Some(builder) => builder.addOne(v)
          case None          => m.update(key = k, value = valuesFactory.newBuilder.addOne(v))
        }
      }
      resultFactory.fromSpecific(m.view.mapValues(_.result()))
    }
  }
}

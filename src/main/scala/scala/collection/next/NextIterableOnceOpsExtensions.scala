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
  def groupMapReduce[K, B](key: A => K)(f: A => B)(reduce: (B, B) => B): immutable.Map[K, B] = {
    val m = mutable.Map.empty[K, B]
    col.foreach { elem =>
      m.updateWith(key = key(elem)) {
        case Some(b) => Some(reduce(b, f(elem)))
        case None    => Some(f(elem))
      }
    }
    m.to(immutable.Map)
  }
}

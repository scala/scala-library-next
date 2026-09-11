/*
 * Scala (https://www.scala-lang.org)
 *
 * Copyright Skylight IPV Ltd.
 *
 * Licensed under Apache License 2.0
 * (http://www.apache.org/licenses/LICENSE-2.0).
 *
 * See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.
 */
package scala.collection
package next

private[next] final class NextIterableOpsOps[A, CC[_], C](
  private val coll: IterableOps[A, CC, C]
) extends AnyVal {
  /**
   * Partitions this $coll into a map of ${coll}s according to a discriminator function `key`.
   * Each element in a group is transformed into a collection of type `B` using the `value` function.
   *
   * It is equivalent to `groupBy(key).mapValues(_.flatMap(f))`, but more efficient.
   *
   * {{{
   *   case class User(name: String, age: Int, pets: Seq[String])
   *
   *   def petsByAge(users: Seq[User]): Map[Int, Seq[String]] =
   *     users.groupFlatMap(_.age)(_.pets)
   * }}}
   *
   * $willForceEvaluation
   *
   * @param key the discriminator function
   * @param f the element transformation function
   * @tparam K the type of keys returned by the discriminator function
   * @tparam B the type of values returned by the transformation function
   */
  def groupFlatMap[K, B](key: A => K)(f: A => IterableOnce[B]): immutable.Map[K, CC[B]] = {
    val m = mutable.Map.empty[K, mutable.Builder[B, CC[B]]]
    coll.foreach { elem =>
      val k = key(elem)
      val b = m.getOrElseUpdate(k, coll.iterableFactory.newBuilder[B])
      b ++= f(elem)
    }
    var result = immutable.Map.empty[K, CC[B]]
    m.foreach { case (k, b) =>
      result = result + ((k, b.result()))
    }
    result
  }
}

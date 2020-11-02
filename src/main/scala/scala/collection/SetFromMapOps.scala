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

import scala.annotation.implicitNotFound
import scala.collection.SetFromMapOps.WrappedMap
import scala.reflect.ClassTag

trait SetFromMapOps[
    A,
    +MM[K, V] <: MapOps[K, V, MM, _],
    +M <: MapOps[A, Unit, MM, M],
    +CC[_],
    +C <: SetFromMapOps[A, MM, M, CC, C],
] extends SetOps[A, CC, C]
    with WrappedMap[A]
    with Serializable {
  protected[collection] val underlying: M

  protected[this] def fromMap[B](m: MM[B, Unit]): CC[B]

  protected[this] def fromSpecificMap(m: M): C

  // methods for adapting functions for a set to functions for a map
  @inline protected[this] def adapted[B](f: A => B): ((A, Unit)) => B = { case (elem, _) =>
    f(elem)
  }

  @inline protected[this] def adaptedTuple[B](f: A => B): ((A, Unit)) => (B, Unit) = {
    case (elem, _) => f(elem) -> ()
  }

  @inline protected[this] def adaptedTuplePF[B](
      pf: PartialFunction[A, B]
  ): PartialFunction[(A, Unit), (B, Unit)] =
    adapted(pf.lift).andThen(_.map(_ -> ())).unlift

  def contains(elem: A): Boolean = underlying contains elem

  def iterator: Iterator[A] = underlying.keysIterator

  override def isEmpty: Boolean = underlying.isEmpty

  override def size: Int = underlying.size

  override def knownSize: Int = underlying.knownSize

  override def head: A = underlying.head._1

  override def headOption: Option[A] = underlying.headOption.map(_._1)

  override def last: A = underlying.last._1

  override def lastOption: Option[A] = underlying.lastOption.map(_._1)

  override def collect[B](pf: PartialFunction[A, B]): CC[B] = fromMap(
    underlying collect adaptedTuplePF(pf)
  )

  override def collectFirst[B](pf: PartialFunction[A, B]): Option[B] =
    underlying collectFirst adapted(pf.lift).unlift

  override def copyToArray[B >: A](xs: Array[B], start: Int): Int =
    underlying.keySet.copyToArray(xs, start)

  override def copyToArray[B >: A](xs: Array[B], start: Int, len: Int): Int =
    underlying.keySet.copyToArray(xs, start, len)

  override def count(p: A => Boolean): Int = underlying.keySet count p

  override def drop(n: Int): C = fromSpecificMap(underlying drop n)

  override def dropRight(n: Int): C = fromSpecificMap(underlying dropRight n)

  override def dropWhile(p: A => Boolean): C = fromSpecificMap(underlying dropWhile adapted(p))

  override def exists(p: A => Boolean): Boolean = underlying exists adapted(p)

  override def filter(pred: A => Boolean): C = fromSpecificMap(underlying filter adapted(pred))

  override def filterNot(pred: A => Boolean): C = fromSpecificMap(
    underlying filterNot adapted(pred)
  )

  override def find(p: A => Boolean): Option[A] = underlying.keySet find p

  override def flatMap[B](f: A => IterableOnce[B]): CC[B] = fromMap {
    underlying flatMap { case (elem, _) =>
      f(elem) match {
        case that: WrappedMap[B] => that.underlying
        case that                => that.iterator map { _ -> () }
      }
    }
  }

  override def flatten[B](implicit asIterable: A => IterableOnce[B]): CC[B] = flatMap(asIterable)

  override def foldLeft[B](z: B)(op: (B, A) => B): B = underlying.keySet.foldLeft(z)(op)

  override def foldRight[B](z: B)(op: (A, B) => B): B = underlying.keySet.foldRight(z)(op)

  override def forall(p: A => Boolean): Boolean = underlying.keySet forall p

  override def foreach[U](f: A => U): Unit = underlying.keySet foreach f

  override def groupMapReduce[K, B](key: A => K)(f: A => B)(
      reduce: (B, B) => B
  ): immutable.Map[K, B] =
    underlying.keySet.groupMapReduce(key)(f)(reduce)

  @deprecated(
    "Check .knownSize instead of .hasDefiniteSize for more actionable information (see scaladoc for details)",
    "2.13.0",
  )
  override def hasDefiniteSize: Boolean = underlying.hasDefiniteSize

  override def map[B](f: A => B): CC[B] = fromMap(underlying map adaptedTuple(f))

  override def max[B >: A: Ordering]: A = underlying.keySet.max[B]

  override def maxBy[B: Ordering](f: A => B): A = underlying.keySet maxBy f

  override def min[B >: A: Ordering]: A = underlying.keySet.min[B]

  override def minBy[B: Ordering](f: A => B): A = underlying.keySet minBy f

  override def partition(p: A => Boolean): (C, C) = {
    val (a, b) = underlying partition adapted(p)
    (fromSpecificMap(a), fromSpecificMap(b))
  }

  override def partitionMap[A1, A2](f: A => Either[A1, A2]): (CC[A1], CC[A2]) = {
    val (a, b) = underlying partitionMap adapted(f)
    (iterableFactory.from(a), iterableFactory.from(b))
  }

  override def reduceLeft[B >: A](op: (B, A) => B): B = underlying.keySet reduceLeft op

  override def reduceLeftOption[B >: A](op: (B, A) => B): Option[B] =
    underlying.keySet reduceLeftOption op

  override def reduceRight[B >: A](op: (A, B) => B): B = underlying.keySet reduceRight op

  override def reduceRightOption[B >: A](op: (A, B) => B): Option[B] =
    underlying.keySet reduceRightOption op

  override def scanLeft[B](z: B)(op: (B, A) => B): CC[B] =
    iterableFactory.from(underlying.scanLeft(z)((acc, t) => op(acc, t._1)))

  override def scanRight[B](z: B)(op: (A, B) => B): CC[B] =
    iterableFactory.from(underlying.scanRight(z)((t, acc) => op(t._1, acc)))

  override def slice(from: Int, until: Int): C = fromSpecificMap(underlying.slice(from, until))

  override def span(p: A => Boolean): (C, C) = {
    val (a, b) = underlying span adapted(p)
    (fromSpecificMap(a), fromSpecificMap(b))
  }

  override def splitAt(n: Int): (C, C) = {
    val (a, b) = underlying splitAt n
    (fromSpecificMap(a), fromSpecificMap(b))
  }

  override def stepper[S <: Stepper[_]](implicit shape: StepperShape[A, S]): S =
    underlying.keyStepper

  override def take(n: Int): C = fromSpecificMap(underlying take n)

  override def takeRight(n: Int): C = fromSpecificMap(underlying takeRight n)

  override def takeWhile(p: A => Boolean): C = fromSpecificMap(underlying takeWhile adapted(p))

  override def tapEach[U](f: A => U): C = fromSpecificMap(underlying tapEach adapted(f))

  override def toArray[B >: A: ClassTag]: Array[B] = underlying.keySet.toArray[B]

  override def view: View[A] = underlying.keySet.view
}

object SetFromMapOps {

  // top type to make pattern matching easier
  sealed trait WrappedMap[A] extends IterableOnce[A] {
    protected[collection] val underlying: IterableOnce[(A, Unit)]
  }

  trait DynamicClassName { self: Iterable[_] =>
    protected[collection] val underlying: Iterable[_]

    override protected[this] def className: String = s"SetFrom_${underlying.collectionClassName}"
  }

  // unknown whether mutable or immutable
  trait Unknown[
      A,
      +MM[K, V] <: MapOps[K, V, MM, _],
      +M <: MapOps[A, Unit, MM, M],
      +CC[_],
      +C <: Unknown[A, MM, M, CC, C],
  ] extends SetFromMapOps[A, MM, M, CC, C] {
    def diff(that: Set[A]): C =
      toIterable
        .foldLeft(newSpecificBuilder)((b, elem) => if (that contains elem) b else b += elem)
        .result()
  }

  trait Unsorted[A, +MM[K, V] <: MapOps[K, V, MM, MM[K, V]], +CC[X] <: Unsorted[X, MM, CC]]
      extends SetFromMapOps[A, MM, MM[A, Unit], CC, CC[A]] {
    protected[this] final def fromSpecificMap(m: MM[A, Unit]): CC[A] = fromMap(m)

    override def concat(that: IterableOnce[A]): CC[A] = fromMap {
      that match {
        case coll: WrappedMap[A] => underlying concat coll.underlying
        case coll                => underlying concat coll.iterator.map((_, ()))
      }
    }
  }

  trait Sorted[
      A,
      +MM[K, V] <: Map[K, V] with SortedMapOps[K, V, MM, MM[K, V]],
      +UnsortedCC[X] <: Set[X],
      +CC[X] <: Sorted[X, MM, UnsortedCC, CC] with SortedSet[X] with SortedSetOps[X, CC, CC[X]],
  ] extends SetFromMapOps[A, Map, MM[A, Unit], UnsortedCC, CC[A]]
      with SortedSetOps[A, CC, CC[A]] {
    override protected[collection] val underlying: MM[A, Unit]

    @inline protected[this] final implicit def implicitOrd: Ordering[A] = ordering

    protected[this] def fromSortedMap[B: Ordering](m: MM[B, Unit]): CC[B]

    protected[this] final def fromSpecificMap(m: MM[A, Unit]): CC[A] = fromSortedMap(m)

    def iteratorFrom(start: A): Iterator[A] = underlying.keysIteratorFrom(start)

    def rangeImpl(from: Option[A], until: Option[A]): CC[A] =
      fromSortedMap(underlying.rangeImpl(from, until))

    override def rangeTo(to: A): CC[A] = fromSortedMap(underlying rangeTo to)

    override def head: A = underlying.firstKey

    override def firstKey: A = underlying.firstKey

    override def headOption: Option[A] = underlying.headOption.map(_._1)

    override def last: A = underlying.lastKey

    override def lastKey: A = underlying.lastKey

    override def lastOption: Option[A] = underlying.lastOption.map(_._1)

    override def collect[B](pf: PartialFunction[A, B])(implicit
        @implicitNotFound(SortedSetOps.ordMsg) ev: Ordering[B]
    ): CC[B] =
      fromSortedMap(underlying collect adaptedTuplePF(pf))

    override def concat(that: IterableOnce[A]): CC[A] = fromSortedMap {
      that match {
        case coll: WrappedMap[A] => underlying concat coll.underlying
        case coll                => underlying concat coll.iterator.map((_, ()))
      }
    }

    override def flatMap[B](
        f: A => IterableOnce[B]
    )(implicit @implicitNotFound(SortedSetOps.ordMsg) ev: Ordering[B]): CC[B] =
      fromSortedMap {
        underlying flatMap { case (elem, _) =>
          f(elem) match {
            case that: WrappedMap[B] => that.underlying
            case that                => that.iterator map { _ -> () }
          }
        }
      }

    override def map[B](f: A => B)(implicit
        @implicitNotFound(SortedSetOps.ordMsg) ev: Ordering[B]
    ): CC[B] =
      fromSortedMap(underlying map adaptedTuple(f))

    override def max[B >: A: Ordering]: A = super[SortedSetOps].max[B]

    override def maxBefore(key: A): Option[A] = underlying maxBefore key map { _._1 }

    override def min[B >: A: Ordering]: A = super[SortedSetOps].min

    override def minAfter(key: A): Option[A] = underlying minAfter key map { _._1 }
  }

}

abstract class SetFromMapFactory[+MM[K, V] <: Map[K, V], +CC[A] <: WrappedMap[A]](
    mf: MapFactory[MM]
) extends IterableFactory[CC]
    with Serializable {
  protected[this] def fromMap[A](map: MM[A, Unit]): CC[A]

  def from[A](source: IterableOnce[A]): CC[A] =
    source match {
      case coll: WrappedMap[A] => fromMap(mf from coll.underlying)
      case coll                => newBuilder[A].addAll(coll).result()
    }

  def empty[A]: CC[A] = fromMap(mf.empty)

  def newBuilder[A]: mutable.Builder[A, CC[A]] = new WrappedBuilder(mf.newBuilder)

  private final class WrappedBuilder[A](b: mutable.Builder[(A, Unit), MM[A, Unit]])
      extends mutable.Builder[A, CC[A]] {
    def clear(): Unit = b.clear()

    def result(): CC[A] = fromMap(b.result())

    def addOne(elem: A): this.type = { b.addOne((elem, ())); this }

    override def addAll(xs: IterableOnce[A]): this.type =
      xs match {
        case coll: WrappedMap[A] =>
          b.addAll(coll.underlying)
          this
        case coll => super.addAll(coll)
      }

    override def sizeHint(size: Int): Unit = b.sizeHint(size)
  }

}

abstract class SortedSetFromMapFactory[+MM[K, V] <: SortedMap[K, V], +CC[A] <: WrappedMap[A]](
    mf: SortedMapFactory[MM]
) extends SortedIterableFactory[CC]
    with Serializable {
  protected[this] def fromMap[A: Ordering](map: MM[A, Unit]): CC[A]

  def from[A: Ordering](it: IterableOnce[A]): CC[A] =
    it match {
      case coll: WrappedMap[A] => fromMap(mf from coll.underlying)
      case coll                => newBuilder[A].addAll(coll).result()
    }

  def empty[A: Ordering]: CC[A] = fromMap(mf.empty)

  def newBuilder[A: Ordering]: mutable.Builder[A, CC[A]] = new WrapperBuilder[A](mf.newBuilder)

  private final class WrapperBuilder[A: Ordering](b: mutable.Builder[(A, Unit), MM[A, Unit]])
      extends mutable.Builder[A, CC[A]] {
    def clear(): Unit = b.clear()

    def result(): CC[A] = fromMap(b.result())

    def addOne(elem: A): this.type = { b.addOne((elem, ())); this }

    override def addAll(xs: IterableOnce[A]): this.type =
      xs match {
        case coll: WrappedMap[A] =>
          b.addAll(coll.underlying)
          this
        case coll => super.addAll(coll)
      }

    override def sizeHint(size: Int): Unit = b.sizeHint(size)
  }

}

sealed abstract class SetFromMapMetaFactoryBase[MM[K, V] <: Map[K, V], +CC[A] <: Set[A]] {
  def apply[A](map: MM[A, Unit]): CC[A]
}

abstract class SetFromMapMetaFactory[MM[K, V] <: Map[K, V], +CC[A] <: Set[A]]
    extends SetFromMapMetaFactoryBase[MM, CC] {
  def apply(factory: MapFactory[MM]): IterableFactory[CC]
}

abstract class SortedSetFromMapMetaFactory[MM[K, V] <: SortedMap[K, V], +CC[A] <: SortedSet[A]]
    extends SetFromMapMetaFactoryBase[MM, CC] {
  def apply(factory: SortedMapFactory[MM]): SortedIterableFactory[CC]
}

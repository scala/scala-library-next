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

package scala.collection.immutable

package object next {

  implicit class NextLazyListExtensions[T](private val ll: LazyList[T]) extends AnyVal {
    /**
     * When called on a finite `LazyList`, returns a circular structure
     * that endlessly repeats the elements in the input.
     * The result is a true cycle occupying only constant memory.
     *
     * Does not force the input list (not even its empty-or-not status).
     *
     * Safe to call on unbounded input, but in that case the result is not a cycle
     * (not even if the input was).
     *
     * Note that some `LazyList` methods preserve cyclicality and others do not.
     * So for example the `tail` of a cycle is still a cycle, but `map` and `filter`
     * on a cycle do not return cycles.
     */
    def cycle: LazyList[T] =
      // case 1: the input is already known to be empty
      // (the test can be changed to ll.knownIsEmpty when this code moves to stdlib)
      if (ll.knownSize == 0) LazyList.empty
      // we don't want to force the input's empty-or-not status until we must.
      // `LazyList.empty #:::` accomplishes that delay
      else LazyList.empty #::: {
        // case 2: the input is later discovered to be empty
        if (ll.isEmpty) LazyList.empty
        else {
          // case 3: non-empty
          lazy val result: LazyList[T] = ll #::: result
          result
        }
      }
  }

}

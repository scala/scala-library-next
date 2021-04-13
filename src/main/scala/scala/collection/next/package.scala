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

import scala.language.implicitConversions

package object next {
  implicit final def scalaNextSyntaxForIterableOnceOps[A, CC[_], C](
    col: IterableOnceOps[A, CC, C]
  ): NextIterableOnceOpsExtensions[A, CC, C] =
    new NextIterableOnceOpsExtensions(col)

  implicit final class OptionOpsExtensions[A](val v: Option[A]) extends AnyVal {
    /** Apply the side-effecting function `f` to the option's value
     *  if it is nonempty. Otherwise, do nothing.
     *
     *  @param  f  a function to apply to the option's value
     *  @return the option
     */
    def tapEach[B](f: A => B): Option[A] = { v.foreach(f); v }
  }
}

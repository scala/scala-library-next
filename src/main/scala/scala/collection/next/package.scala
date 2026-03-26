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

  implicit final def scalaNextSyntaxForIterableOps[A, CC[_], C](
    coll: IterableOps[A, CC, C]
  ): NextIterableOpsOps[A, CC, C] =
    new NextIterableOpsOps(coll)
}

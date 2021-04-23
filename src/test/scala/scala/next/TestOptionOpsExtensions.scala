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

package scala.next

final class TestOptionOpsExtensions {
  // Compile checks the return type, no need to run as test.
  def tapEachReturnType(): Option[Int] = {
    // Don't return the Option directly, so the compiler is not able to coerce a specific implicit to be used.
    val opt = Option(5).tapEach(identity)
    opt.map(identity)
  }
}

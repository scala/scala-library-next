
package scala

object next {
  implicit class NextArray[A <: AnyRef](private val as: Array[A]) extends AnyVal {
    def sameElements[B <: AnyRef](bs: Array[B]): Boolean =
      Array.equals(as.asInstanceOf[Array[AnyRef]], bs.asInstanceOf[Array[AnyRef]])
  }
  implicit class NextArrayCompanion(private val ary: Array.type) /*extends AnyVal*/ {
    def equals[A <: AnyRef, B <: AnyRef](as: Array[A], bs: Array[B]): Boolean =
      ary.equals(as.asInstanceOf[Array[AnyRef]], bs.asInstanceOf[Array[AnyRef]])
  }
}

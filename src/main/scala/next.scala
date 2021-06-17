
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

  implicit class NextRichByte(private val b: Byte) extends AnyVal {
    def toBinaryString: String = java.lang.Integer.toBinaryString(java.lang.Byte.toUnsignedInt(b))
    def toHexString: String    = java.lang.Integer.toHexString(java.lang.Byte.toUnsignedInt(b))
    def toOctalString: String  = java.lang.Integer.toOctalString(java.lang.Byte.toUnsignedInt(b))
  }
  implicit class NextRichShort(private val s: Short) extends AnyVal {
    def toBinaryString: String = java.lang.Integer.toBinaryString(java.lang.Short.toUnsignedInt(s))
    def toHexString: String    = java.lang.Integer.toHexString(java.lang.Short.toUnsignedInt(s))
    def toOctalString: String  = java.lang.Integer.toOctalString(java.lang.Short.toUnsignedInt(s))
  }
}

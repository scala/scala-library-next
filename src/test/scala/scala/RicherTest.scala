
// test package because we are testing imports
package scala.test

import org.junit.{Assert, Test}

class RicherTest {
  import scala.next._
  import RicherTest._

  private def assertEqualTo(expected: String)(actual: String) = Assert.assertEquals(expected, actual)

  @Test def `Byte expansions should be byte-sized`(): Unit = {
    val sixteen = 16.toByte
    assertEqualTo(x"1_0000")(sixteen.toBinaryString)
    assertEqualTo("10")(sixteen.toHexString)
    assertEqualTo("20")(sixteen.toOctalString)
    val max = 0x7F.toByte
    assertEqualTo(x"111_1111")(max.toBinaryString)
    assertEqualTo("7f")(max.toHexString)
    assertEqualTo("177")(max.toOctalString)
    val extended = 0x80.toByte
    assertEqualTo(x"1000_0000")(extended.toBinaryString)
    assertEqualTo("80")(extended.toHexString)
    assertEqualTo("200")(extended.toOctalString)
    val neg = -1.toByte
    assertEqualTo(x"1111_1111")(neg.toBinaryString)
    assertEqualTo("ff")(neg.toHexString)
    assertEqualTo("377")(neg.toOctalString)
  }
  @Test def `Short expansions should be short-sized`(): Unit = {
    val sixteen = 16.toShort
    assertEqualTo(x"1_0000")(sixteen.toBinaryString)
    assertEqualTo("10")(sixteen.toHexString)
    assertEqualTo("20")(sixteen.toOctalString)
    val max = 0x7FFF.toShort
    assertEqualTo(x"111_1111_1111_1111")(max.toBinaryString)
    assertEqualTo("7fff")(max.toHexString)
    assertEqualTo("77777")(max.toOctalString)
    val extended = 0x8000.toShort
    assertEqualTo(x"1000_0000_0000_0000")(extended.toBinaryString)
    assertEqualTo("8000")(extended.toHexString)
    assertEqualTo(x"10_0000")(extended.toOctalString)
    val neg = -1.toShort
    assertEqualTo("1" * 16)(neg.toBinaryString)
    assertEqualTo("ffff")(neg.toHexString)
    assertEqualTo(x"17_7777")(neg.toOctalString)
  }
  // same as short, but uses int conversion because unsigned
  @Test def `Char expansions should be char-sized`(): Unit = {
    val sixteen = 16.toChar
    assertEqualTo(x"1_0000")(sixteen.toBinaryString)
    assertEqualTo("10")(sixteen.toHexString)
    assertEqualTo("20")(sixteen.toOctalString)
    val max = 0x7FFF.toChar
    assertEqualTo(x"111_1111_1111_1111")(max.toBinaryString)
    assertEqualTo("7fff")(max.toHexString)
    assertEqualTo("77777")(max.toOctalString)
    val extended = 0x8000.toChar
    assertEqualTo(x"1000_0000_0000_0000")(extended.toBinaryString)
    assertEqualTo("8000")(extended.toHexString)
    assertEqualTo(x"10_0000")(extended.toOctalString)
    val neg = -1.toChar
    assertEqualTo("1" * 16)(neg.toBinaryString)
    assertEqualTo("ffff")(neg.toHexString)
    assertEqualTo(x"17_7777")(neg.toOctalString)
  }
}

object RicherTest {
  implicit class stripper(private val sc: StringContext) extends AnyVal {
    def x(args: Any*) = StringContext.standardInterpolator(_.replace("_", ""), args, sc.parts)
  }
}


import org.junit.{Assert, Test}, Assert.{assertTrue}

class ArrayTest {
  import scala.next._

  @Test def `compare arbitrary arrays`(): Unit = {
    val xs = Array(List(1, 2, 3))
    val ys = Array(Vector(1, 2, 3))
    assertTrue(Array.equals(xs, ys))
    assertTrue(xs.sameElements(ys))
  }
}

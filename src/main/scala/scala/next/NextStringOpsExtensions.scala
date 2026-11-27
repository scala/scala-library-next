package scala
package next

private[next] final class NextStringOpsExtensions(
  private val str: String
) extends AnyVal {
  /** Split this string around the separator character; as a [[List]]
   *
   *  If this string is blank,
   *  returns an empty list.
   *
   *  If this string is not a blank string,
   *  returns a list containing the substrings terminated by the start of the string,
   *  the end of the string or the separator character.
   *
   *  By default, this method discards empty substrings.
   *
   * @param separator the character used as the delimiter.
   * @param preserveEmptySubStrings set as `true` to preserve empty substrings.
   * @return a [[List]] of substrings.
   */
  def splitAsList(separator: Char, preserveEmptySubStrings: Boolean = false): List[String] = {
    @annotation.tailrec
    def loop(currentIdx: Int, acc: List[String]): List[String] = {
      def addToAcc(substring: String): List[String] =
        if (substring.isEmpty && !preserveEmptySubStrings)
          acc
        else
          substring :: acc

      val newIndex = str.lastIndexOf(separator, currentIdx - 1)
      if (newIndex == -1)
        addToAcc(substring = str.substring(0, currentIdx))
      else
        loop(
          currentIdx = newIndex,
          acc = addToAcc(substring = str.substring(newIndex + 1, currentIdx))
        )
    }

    loop(
      currentIdx = str.length,
      acc = List.empty
    )
  }
}

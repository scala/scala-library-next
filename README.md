# scala-library-next

This repo holds code that will be added to a future version of
the Scala standard library.

We will publish this as a library, to make the additions usable
immediately. But publishing hasn't happened yet.  (The repo only just
started, in October 2020.)

## Why?

Why make a library for this?  Because:

* Scala 2.13 is expected to be final major release of Scala 2.
* We cannot make additions to the standard library in the Scala 2.13.x
  series, because of our [binary compatibility
  constraints](https://docs.scala-lang.org/overviews/core/binary-compatibility-of-scala-releases.html).
* To help users transition from Scala 2 to 3, Scala 2.13 and 3.0 will
  share the same standard library.

Therefore, additions to the library cannot happen until Scala 3.1 at the earliest.

But why publish the additions separately in the meantime?  Because:

* Users may find the additions useful now.
* Having users start using the additions now will help shake out
  problems early.
* Contributors will be more motivated to work on improvements if users
  can use them now.

## What can be added?

Anything merged here _will_ become part of the next Scala standard library.

Therefore, we will not merge anything here unless the Scala 2 and 3
teams agree on the addition.  The bar for accepting additions remains
very high.

Is it okay to open an issue and/or pull request regardless? Yes,
definitely. Let's discuss your idea. Just be aware that the bar is
high and contributions may be rejected unless there is a high degree
of consensus and confidence that it really belongs in the standard
library.

It's not required, but you may wish to bring your idea up on
[contributors.scala-lang.org](https://contributors.scala-lang.org)
first to gauge reaction.

There are may be technical constraints on what can be added, since
this a separate codebase from the actual standard library.  So for
example if you want to add a new method to an existing class, it must
be added as an extension method. We are still discussing details on
[issue #4](https://github.com/scala/scala-library-next/issues/4).

## What if my contribution is rejected?

If your contribution is collections-themed, it could find a home at [scala-collection-contrib](https://github.com/scala/scala-collection-contrib), which has a much more liberal merge policy.

You might also consider publishing your code yourself in a separate library, of course.

## History

The discussions that led to this repo being created are here:

* https://github.com/scala/scala-dev/issues/661
* https://github.com/scala/scala-collection-compat/issues/329
* https://github.com/scala/scala-dev/issues/449

The name was discussed here:

* https://github.com/scala/scala-library-next/issues/1

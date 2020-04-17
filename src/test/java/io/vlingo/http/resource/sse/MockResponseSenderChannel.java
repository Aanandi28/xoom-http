// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.http.resource.sse;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import io.vlingo.actors.testkit.AccessSafely;
import io.vlingo.http.Response;
import io.vlingo.http.ResponseParser;
import io.vlingo.wire.channel.RequestResponseContext;
import io.vlingo.wire.channel.ResponseSenderChannel;
import io.vlingo.wire.message.ConsumerByteBuffer;

public class MockResponseSenderChannel implements ResponseSenderChannel {
  public AtomicInteger abandonCount = new AtomicInteger(0);
  public AtomicReference<Response> eventsResponse = new AtomicReference<>();
  public AtomicInteger respondWithCount = new AtomicInteger(0);
  public AtomicReference<Response> response = new AtomicReference<>();
  private AccessSafely abandonSafely = AccessSafely.afterCompleting(0);
  private AccessSafely respondWithSafely = AccessSafely.afterCompleting(0);

  private boolean receivedStatus;

  public MockResponseSenderChannel() {
    respondWithSafely = expectRespondWith(0);
    receivedStatus = false;
  }

  @Override
  public void abandon(final RequestResponseContext<?> context) {
    final int count = abandonCount.incrementAndGet();
    abandonSafely.writeUsing("count", count);
  }

  @Override
  public void explicitClose(final RequestResponseContext<?> requestResponseContext, final boolean option) {
    // unused in tests
  }

  @Override
  public void respondWith(final RequestResponseContext<?> context, final ConsumerByteBuffer buffer) {
    respondWith(context, buffer, false);
  }

  @Override
  public void respondWith(final RequestResponseContext<?> context, final ConsumerByteBuffer buffer, final boolean closeFollowing) {
    final ResponseParser parser = receivedStatus ?
            ResponseParser.parserForBodyOnly(buffer.asByteBuffer()) :
            ResponseParser.parserFor(buffer.asByteBuffer());

    if (!receivedStatus) {
      response.set(parser.fullResponse());
    } else {
      respondWithSafely.writeUsing("events", parser.fullResponse());
    }
    receivedStatus = true;
  }

  /**
   * Answer with an AccessSafely which
   *  writes the abandon call count using "count" every time abandon(...) is called, and
   *  reads the abandon call count using "count".
   * @param n Number of times abandon must be called before readFrom will return.
   * @return
   */
  public AccessSafely expectAbandon(int n) {
    abandonSafely = AccessSafely.afterCompleting(n)
        .writingWith("count", (x) -> {})
        .readingWith("count", () -> abandonCount.get());
    return abandonSafely;
  }

  /**
   * Answer with an AccessSafely which
   *  writes the respondWith call count using "count" every time respondWith(...) is called, and
   *  reads the respondWith call count using "count".
   * @param n Number of times respondWith must be called before readFrom will return.
   * @return
   */
  public AccessSafely expectRespondWith(int n) {
    respondWithSafely = AccessSafely.afterCompleting(n)
        .writingWith("events", (Response response) -> { respondWithCount.incrementAndGet(); eventsResponse.set(response); } )
        .readingWith("count", () -> respondWithCount.get())
        .readingWith("eventsResponse", () -> eventsResponse.get());
    return respondWithSafely;
  }

}

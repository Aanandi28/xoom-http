// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.http;

/**
 * An HTTP response body that provides a multi-chunk format. You may create
 * one chunk in each instance, or multiple chunks.
 */
public class ChunkedBody implements Body {

  /** My content. */
  private final StringBuilder content;

  /**
   * Answer self after appending the {@code body} as the chunk.
   * @param body the Body with content to append as the chunk
   * @return ChunkedBody
   */
  public ChunkedBody appendChunk(final Body body) {
    return appendChunk(body.content());
  }

  /**
   * Answer self after appending the {@code chunk}.
   * @param chunk the String content to append as the chunk
   * @return ChunkedBody
   */
  public ChunkedBody appendChunk(final String chunk) {
    content
      .append(Integer.toHexString(chunk.length()))
      .append("\r\n")
      .append(chunk)
      .append("\r\n");

    return this;
  }

  /**
   * Answer a new {@code Body} as a {@code PlainBody} with my content.
   * @return Body
   */
  public Body asPlainBody() {
    return new PlainBody(content());
  }

  /**
   * Answer my content as a {@code String}.
   * @return String
   */
  @Override
  public String content() {
    return toString();
  }

  /**
   * @see io.vlingo.http.Body#isComplex()
   */
  @Override
  public boolean isComplex() {
    return true;
  }

  /**
   * Answer self after appending the end chunk, which is a length of 0.
   * @return ChunkedBody
   */
  public ChunkedBody end() {
    content.append(0).append("\r\n");

    return this;
  }

  /**
   * @see io.vlingo.http.Body#hasContent()
   */
  @Override
  public boolean hasContent() {
    return content.length() > 0;
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return content.toString();
  }

  /**
   * Construct my default state.
   */
  ChunkedBody() {
    this.content = new StringBuilder();
  }
}
// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.http.media;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class MediaTypeDescriptor {

  static final String PARAMETER_SEPARATOR = ";";
  static final String MIME_SUBTYPE_SEPARATOR = "/";
  static final String PARAMETER_ASSIGNMENT = "=";

  protected final String mimeType;
  protected final String mimeSubType;
  public final Map<String, String> mediaTypeParameters;

  public MediaTypeDescriptor(String mimeType, String mimeSubType, Map<String, String> parameters) {
    this.mimeType = mimeType;
    this.mimeSubType = mimeSubType;
    this.mediaTypeParameters = new HashMap<>(parameters);
  }

  public MediaTypeDescriptor(String mimeType, String mimeSubType) {
    this.mimeType = mimeType;
    this.mimeSubType = mimeSubType;
    this.mediaTypeParameters = new HashMap<>();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(mimeType)
        .append(MIME_SUBTYPE_SEPARATOR)
        .append(mimeSubType);

    for (String parameterName : mediaTypeParameters.keySet()) {
      sb.append(PARAMETER_SEPARATOR)
          .append(parameterName)
          .append(PARAMETER_ASSIGNMENT)
          .append(mediaTypeParameters.get(parameterName));
    }
    return sb.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    MediaTypeDescriptor that = (MediaTypeDescriptor) o;
    return Objects.equals(mimeType, that.mimeType) &&
        Objects.equals(mimeSubType, that.mimeSubType) &&
        Objects.equals(mediaTypeParameters, that.mediaTypeParameters);
  }

  @Override
  public int hashCode() {
    return Objects.hash(mimeType, mimeSubType, mediaTypeParameters);
  }

  public static class Builder<T> {
    protected String mimeType;
    protected String mimeSubType;
    protected Map<String, String> mediaTypeParameters;
    protected final Supplier<T> supplier;

    @FunctionalInterface
    public interface Supplier<U> {
      U supply(final String mimeType, final String mimeSubType, final Map<String, String> parameters);
    }

    public Builder(Supplier<T> supplier) {
      this.supplier = supplier;
      mediaTypeParameters = new HashMap<>();
      mimeType = "";
      mimeSubType = "";
    }

    Builder<T> withMimeType(final String mimeType) {
      this.mimeType = mimeType;
      return this;
    }

    Builder<T> withMimeSubType(final String mimeSubType) {
      this.mimeSubType = mimeSubType;
      return this;
    }

    Builder<T> withParameter(final String paramName, final String paramValue) {
      mediaTypeParameters.put(paramName, paramValue);
      return this;
    }

    T build() {
      return this.supplier.supply(mimeType, mimeSubType, mediaTypeParameters);
    }
  }
}

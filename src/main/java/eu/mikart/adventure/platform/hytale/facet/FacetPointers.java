/*
 * This file is part of adventure-platform-hytale, licensed under the MIT License.
 *
 * Copyright (c) 2026 ArikSquad
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package eu.mikart.adventure.platform.hytale.facet;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.pointer.Pointer;
import org.jetbrains.annotations.ApiStatus;

/**
 * Pointers for facet-specific data.
 *
 * @since 1.1.0
 */
@ApiStatus.Internal
public final class FacetPointers {
  private FacetPointers() {
  }

  private static final String NAMESPACE = "adventure_platform";
  public static final Pointer<String> SERVER = Pointer.pointer(String.class, Key.key(NAMESPACE, "server"));
  public static final Pointer<Key> WORLD = Pointer.pointer(Key.class, Key.key(NAMESPACE, "world"));
  public static final Pointer<Type> TYPE = Pointer.pointer(Type.class, Key.key(NAMESPACE, "type"));

  /**
   * Types of audience that may receive special handling.
   *
   * @since 1.1.0
   */
  public enum Type {
    PLAYER,
    CONSOLE,
    OTHER
  }
}

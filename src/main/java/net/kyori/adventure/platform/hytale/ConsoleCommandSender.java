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
package net.kyori.adventure.platform.hytale;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import java.util.UUID;
import net.kyori.adventure.text.serializer.ansi.ANSIComponentSerializer;
import org.jspecify.annotations.NonNull;

/**
 * Console command sender implementation for Hytale.
 *
 * @since 1.0.0
 */
public class ConsoleCommandSender implements CommandSender {

  private static final ANSIComponentSerializer ANSI_SERIALIZER = ANSIComponentSerializer.ansi();
  private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

  @Override
  public String getDisplayName() {
    return "Adventure";
  }

  @Override
  public UUID getUuid() {
    return new UUID(0, 0);
  }

  @Override
  public boolean hasPermission(final @NonNull String perm) {
    return true;
  }

  @Override
  public boolean hasPermission(final @NonNull String perm, final boolean b) {
    return true;
  }

  @Override
  public void sendMessage(final @NonNull Message message) {
    LOGGER.atInfo().log(ANSI_SERIALIZER.serialize(HytaleComponentSerializer.get().deserialize(message)));
  }

}

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
package eu.mikart.adventure.platform.hytale;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.hypixel.hytale.codec.EmptyExtraInfo;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import net.kyori.adventure.platform.facet.FacetComponentFlattener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.flattener.FlattenerListener;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import org.jetbrains.annotations.NotNull;

/**
 * A serializer for Hytale's JSON text format.
 *
 * @since 1.0.0
 */
@SuppressWarnings("UnstableApiUsage")
public final class HytaleComponentSerializer implements ComponentSerializer<Component, Component, Message> {
  private static final Gson GSON = new GsonBuilder().serializeNulls().create();
  private static final HytaleComponentSerializer INSTANCE = new HytaleComponentSerializer();
  private static final ComponentFlattener FLATTENER = FacetComponentFlattener.get(HytaleServer.get(), Collections.singletonList(new HytaleFacet.Translator()));

  private HytaleComponentSerializer() {
  }

  /**
   * Gets the singleton instance of Hytale component serializer.
   *
   * @return a component serializer
   * @since 1.0.0
   */
  public static @NotNull HytaleComponentSerializer get() {
    return INSTANCE;
  }

  /**
   * Deserializes a Hytale JSON string into a component.
   *
   * @param input an input
   * @return a component
   * @since 1.0.0
   */
  public @NotNull Component deserialize(final @NotNull String input) {
    final JsonObject json = GSON.fromJson(input, JsonObject.class);
    return this.deserializeElement(json, Style.empty());
  }

  /**
   * Deserializes a Hytale message into a component.
   *
   * @param message a message
   * @return a component
   * @since 1.0.0
   */
  @Override
  public @NotNull Component deserialize(final @NotNull Message message) {
    return this.deserialize(Message.CODEC.encode(message, EmptyExtraInfo.EMPTY).toString());
  }

  private @NotNull Component deserializeElement(final @NotNull JsonObject json, final @NotNull Style parentStyle) {
    final Style.Builder styleBuilder = Style.style();

    if (json.has("Bold") && !json.get("Bold").isJsonNull()) {
      styleBuilder.decoration(TextDecoration.BOLD, json.get("Bold").getAsBoolean() ? TextDecoration.State.TRUE : TextDecoration.State.FALSE);
    }
    if (json.has("Italic") && !json.get("Italic").isJsonNull()) {
      styleBuilder.decoration(TextDecoration.ITALIC, json.get("Italic").getAsBoolean() ? TextDecoration.State.TRUE : TextDecoration.State.FALSE);
    }
    if (json.has("Underline") && !json.get("Underline").isJsonNull()) {
      styleBuilder.decoration(TextDecoration.UNDERLINED, json.get("Underline").getAsBoolean() ? TextDecoration.State.TRUE : TextDecoration.State.FALSE);
    }

    if (json.has("Color") && !json.get("Color").isJsonNull()) {
      final TextColor color = TextColor.fromHexString(json.get("Color").getAsString());
      if (color != null) {
        styleBuilder.color(color);
      }
    }

    if (json.has("Link") && !json.get("Link").isJsonNull()) {
      styleBuilder.clickEvent(ClickEvent.openUrl(json.get("Link").getAsString()));
    }

    final Style style = parentStyle.merge(styleBuilder.build());

    if (json.has("Children")) {
      final JsonArray children = json.getAsJsonArray("Children");
      final TextComponent.Builder builder = Component.text().style(style);

      for (final JsonElement child : children) {
        builder.append(this.deserializeElement(child.getAsJsonObject(), style));
      }

      return builder.build();
    } else {
      final String text = json.has("RawText") && !json.get("RawText").isJsonNull()
          ? json.get("RawText").getAsString()
          : "";
      return Component.text(text).style(style);
    }
  }

  @Override
  public @NotNull Message serialize(final @NotNull Component component) {
    final SerializerListener listener = new SerializerListener();
    FLATTENER.flatten(component, listener);
    return Message.parse(GSON.toJson(listener.result()));
  }

  private static final class SerializerListener implements FlattenerListener {
    private final Deque<StyleContext> styleStack = new ArrayDeque<>();
    private final JsonArray rootChildren = new JsonArray();
    private JsonArray currentChildren = this.rootChildren;

    @Override
    public void pushStyle(final @NotNull Style style) {
      final JsonArray newChildren = new JsonArray();
      this.styleStack.push(new StyleContext(style, this.currentChildren, newChildren));
      this.currentChildren = newChildren;
    }

    @Override
    public void component(final @NotNull String text) {
      if (text.isEmpty()) {
        return;
      }

      final JsonObject textNode = new JsonObject();
      textNode.addProperty("RawText", text);

      Style accumulatedStyle = Style.empty();
      for (final StyleContext ctx : this.styleStack) {
        accumulatedStyle = accumulatedStyle.merge(ctx.style, Style.Merge.Strategy.IF_ABSENT_ON_TARGET);
      }

      addStyleProperties(textNode, accumulatedStyle);
      this.currentChildren.add(textNode);
    }

    @Override
    public void popStyle(final @NotNull Style style) {
      final StyleContext ctx = this.styleStack.pop();
      this.currentChildren = ctx.parentChildren;

      if (!ctx.children.isEmpty()) {
        for (int i = 0; i < ctx.children.size(); i++) {
          this.currentChildren.add(ctx.children.get(i));
        }
      }
    }

    public @NotNull JsonElement result() {
      if (this.rootChildren.isEmpty()) {
        final JsonObject empty = new JsonObject();
        empty.addProperty("RawText", "");
        addStyleProperties(empty, Style.empty());
        return empty;
      } else if (this.rootChildren.size() == 1) {
        return this.rootChildren.get(0);
      } else {
        final JsonObject root = new JsonObject();
        root.add("Children", this.rootChildren);
        addBaseStyleProperties(root, Style.empty());
        return root;
      }
    }

    private static final class StyleContext {
      final Style style;
      final JsonArray parentChildren;
      final JsonArray children;

      StyleContext(final Style style, final JsonArray parentChildren, final JsonArray children) {
        this.style = style;
        this.parentChildren = parentChildren;
        this.children = children;
      }
    }
  }

  private static void addStyleProperties(final @NotNull JsonObject json, final @NotNull Style style) {
    addBaseStyleProperties(json, style);

    final TextColor color = style.color();
    if (color != null) {
      json.addProperty("Color", color.asHexString());
    } else {
      json.add("Color", JsonNull.INSTANCE);
    }

    final ClickEvent<?> clickEvent = style.clickEvent();
    if (clickEvent != null && clickEvent.action() == ClickEvent.Action.OPEN_URL) {
      json.addProperty("Link", ((ClickEvent.Payload.Text) clickEvent.payload()).value());
    } else {
      json.add("Link", JsonNull.INSTANCE);
    }
  }

  private static void addBaseStyleProperties(final @NotNull JsonObject json, final @NotNull Style style) {
    json.add("Bold", decorationToJson(style.decoration(TextDecoration.BOLD)));
    json.add("Italic", decorationToJson(style.decoration(TextDecoration.ITALIC)));
    json.add("Monospace", JsonNull.INSTANCE); // Adventure doesn't have monospace
    json.add("Underline", decorationToJson(style.decoration(TextDecoration.UNDERLINED)));
  }

  private static @NotNull JsonElement decorationToJson(final @NotNull TextDecoration.State state) {
    switch (state) {
      case TRUE:
        return new JsonPrimitive(true);
      case FALSE:
        return new JsonPrimitive(false);
      case NOT_SET:
      default:
        return JsonNull.INSTANCE;
    }
  }
}

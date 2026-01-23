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

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.i18n.I18nModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.EventTitleUtil;
import java.util.UUID;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.permission.PermissionChecker;
import net.kyori.adventure.platform.facet.Facet;
import net.kyori.adventure.platform.facet.FacetBase;
import net.kyori.adventure.platform.facet.FacetComponentFlattener;
import net.kyori.adventure.platform.facet.FacetPointers;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.util.TriState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
class HytaleFacet<V extends CommandSender> extends FacetBase<V> {
  static final ComponentFlattener FLATTENER = FacetComponentFlattener.get(HytaleServer.get(), null);
  static final HytaleComponentSerializer HYTALE = HytaleComponentSerializer.get();

  protected HytaleFacet(final @Nullable Class<? extends V> viewerClass) {
    super(viewerClass);
  }

  private static PlayerRef toPlayerRef(final Player player) {
    final Ref<EntityStore> ref = player.getReference();
    if (ref == null) {
      throw new IllegalStateException("Player " + player.getDisplayName() + " has no reference");
    }
    final Store<EntityStore> store = ref.getStore();
    return store.getComponent(ref, PlayerRef.getComponentType());
  }

  static class ChatConsole extends HytaleFacet<CommandSender> implements Facet.Chat<CommandSender, String> {
    protected ChatConsole() {
      super(CommandSender.class);
    }

    @Override
    public boolean isApplicable(final @NotNull CommandSender viewer) {
      return super.isApplicable(viewer) && !(viewer instanceof Player);
    }

    @Override
    public String createMessage(final @NotNull CommandSender viewer, final @NotNull Component message) {
      return HYTALE.serialize(message);
    }

    @Override
    public void sendMessage(final @NotNull CommandSender viewer, final @NotNull Identity source, final @NotNull String message, final @NotNull Object type) {
      viewer.sendMessage(com.hypixel.hytale.server.core.Message.parse(message));
    }
  }

  static class Message extends HytaleFacet<Player> implements Facet.Message<Player, String> {
    protected Message() {
      super(Player.class);
    }

    @Override
    public String createMessage(final @NotNull Player viewer, final @NotNull Component message) {
      return HYTALE.serialize(message);
    }
  }

  static class ChatPlayer extends Message implements Facet.Chat<Player, String> {
    @Override
    public void sendMessage(final @NotNull Player viewer, final @NotNull Identity source, final @NotNull String message, final @NotNull Object type) {
      viewer.sendMessage(com.hypixel.hytale.server.core.Message.parse(message));
    }
  }

  static class Title extends Message implements Facet.Title<Player, String, Title.TitleData, Title.TitleData> {
    @Override
    public @NotNull TitleData createTitleCollection() {
      return new TitleData();
    }

    @Override
    public void contributeTitle(final @NotNull TitleData coll, final @NotNull String title) {
      coll.title = title;
    }

    @Override
    public void contributeSubtitle(final @NotNull TitleData coll, final @NotNull String subtitle) {
      coll.subtitle = subtitle;
    }

    @Override
    public void contributeTimes(final @NotNull TitleData coll, final int inTicks, final int stayTicks, final int outTicks) {
      if (inTicks > -1) coll.fadeIn = inTicks / 50f;
      if (stayTicks > -1) coll.stay = stayTicks / 50f;
      if (outTicks > -1) coll.fadeOut = outTicks / 50f;
    }

    @Nullable
    @Override
    public TitleData completeTitle(final @NotNull TitleData coll) {
      return coll;
    }

    @Override
    public void showTitle(final @NotNull Player viewer, final @NotNull TitleData title) {
      EventTitleUtil.showEventTitleToPlayer(HytaleFacet.toPlayerRef(viewer), com.hypixel.hytale.server.core.Message.parse(title.title), com.hypixel.hytale.server.core.Message.parse(title.subtitle), title.isMajor, title.icon, title.stay, title.fadeIn, title.fadeOut);
    }

    @Override
    public void clearTitle(final @NotNull Player viewer) {
      EventTitleUtil.hideEventTitleFromPlayer(HytaleFacet.toPlayerRef(viewer), 0);
    }

    @Override
    public void resetTitle(final @NotNull Player viewer) {
      EventTitleUtil.hideEventTitleFromPlayer(HytaleFacet.toPlayerRef(viewer), 0);
    }

    static class TitleData {
      String title = "";
      String subtitle = "";
      String icon = null;
      boolean isMajor = false;
      float stay = 4.0f;
      float fadeIn = 1.0f;
      float fadeOut = 1.0f;
    }
  }

  static final class CommandSenderPointers extends HytaleFacet<CommandSender> implements Facet.Pointers<CommandSender> {
    CommandSenderPointers() {
      super(CommandSender.class);
    }

    @Override
    public void contributePointers(final CommandSender viewer, final net.kyori.adventure.pointer.Pointers.Builder builder) {
      builder.withDynamic(Identity.NAME, viewer::getDisplayName);
      builder.withStatic(PermissionChecker.POINTER, perm -> viewer.hasPermission(perm) ? TriState.TRUE : TriState.FALSE);
      if (!(viewer instanceof Player)) {
        final UUID uuid = viewer.getUuid();
        builder.withStatic(Identity.UUID, uuid);
        final FacetPointers.Type type = uuid.getLeastSignificantBits() == 0 && uuid.getMostSignificantBits() == 0 ? FacetPointers.Type.CONSOLE : FacetPointers.Type.OTHER;
        builder.withStatic(FacetPointers.TYPE, type);
      }
    }
  }

  static final class PlayerPointers extends HytaleFacet<Player> implements Facet.Pointers<Player> {
    PlayerPointers() {
      super(Player.class);
    }

    @Override
    public void contributePointers(final Player viewer, final net.kyori.adventure.pointer.Pointers.Builder builder) {
      final Ref<EntityStore> ref = viewer.getReference();
      if (ref == null) {
        throw new IllegalStateException("Player " + viewer.getDisplayName() + " has no reference");
      }

      final Store<EntityStore> store = ref.getStore();
      final UUIDComponent uuid = store.getComponent(ref, UUIDComponent.getComponentType());
      if (uuid == null) {
        throw new IllegalStateException("Player " + viewer.getDisplayName() + " has no UUID component");
      }

      builder.withDynamic(Identity.UUID, uuid::getUuid);
      builder.withDynamic(Identity.NAME, () -> String.valueOf(viewer.getDisplayName()));
      builder.withStatic(FacetPointers.TYPE, FacetPointers.Type.PLAYER);
      builder.withStatic(PermissionChecker.POINTER, perm -> viewer.hasPermission(perm) ? TriState.TRUE : TriState.FALSE);
    }
  }

  static class Translator extends FacetBase<HytaleServer> implements FacetComponentFlattener.Translator<HytaleServer> {
    Translator() {
      super(HytaleServer.class);
    }

    @Override
    public @NotNull String valueOrDefault(final @NotNull HytaleServer game, final @NotNull String key) {
      final String value = I18nModule.get().getMessage(I18nModule.DEFAULT_LANGUAGE, key);
      return value != null ? value : key;
    }
  }
}

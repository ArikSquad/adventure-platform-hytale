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

import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.platform.facet.FacetAudienceProvider;
import net.kyori.adventure.platform.facet.Knob;
import net.kyori.adventure.pointer.Pointered;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.renderer.ComponentRenderer;
import net.kyori.adventure.translation.GlobalTranslator;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import static java.util.Objects.requireNonNull;

@SuppressWarnings("UnstableApiUsage")
final class HytaleAudiencesImpl extends FacetAudienceProvider<CommandSender, HytaleAudience> implements HytaleAudiences {
  static {
    Knob.OUT = System.out::println;
    Knob.ERR = (message, error) -> {
      System.err.println(message);
      if (error != null) {
        error.printStackTrace(System.err);
      }
    };
  }

  private static final Map<String, HytaleAudiences> INSTANCES = Collections.synchronizedMap(new HashMap<>(4));

  static @NotNull HytaleAudiences instanceFor(final @NotNull JavaPlugin plugin) {
    return builder(plugin).build();
  }

  static @NotNull Builder builder(final @NotNull JavaPlugin plugin) {
    return new Builder(plugin);
  }

  @Override
  public @NotNull Audience filter(final @NotNull Predicate<CommandSender> filter) {
    return super.filter(filter);
  }

  @Override
  public @NotNull Audience player(final @NonNull PlayerRef player) {
    return this.player(player.getUuid());
  }

  @Override
  public @NotNull Audience sender(final @NonNull CommandSender sender) {
    if (sender instanceof Player) {
      return this.player(sender.getUuid());
    } else if (sender.getUuid().getLeastSignificantBits() == 0 && sender.getUuid().getMostSignificantBits() == 0) {
      return this.console();
    }
    return this.createAudience(Collections.singletonList(sender));
  }

  private final JavaPlugin plugin;

  HytaleAudiencesImpl(final JavaPlugin plugin, final @NotNull ComponentRenderer<Pointered> componentRenderer) {
    super(componentRenderer);
    this.plugin = requireNonNull(plugin, "plugin");

    final CommandSender console = new ConsoleCommandSender();
    this.addViewer(console);

    Universe.get().getWorlds().forEach((string, world) -> {
      for (final PlayerRef playerRef : world.getPlayerRefs()) {
        final Holder<EntityStore> holder = playerRef.getHolder();
        if (holder == null) {
          throw new IllegalArgumentException("holder cannot be null");
        }
        final Player player = holder.getComponent(Player.getComponentType());
        if (player == null) {
          throw new IllegalArgumentException("player cannot be null");
        }
        this.addViewer(playerRef);
      }
    });

    plugin.getEventRegistry().registerGlobal(PlayerReadyEvent.class, event -> {
      final Ref<EntityStore> ref = event.getPlayerRef();
      final Store<EntityStore> store = ref.getStore();
      final PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
      if (playerRef == null) {
        throw new IllegalArgumentException("player cannot be null");
      }
      this.addViewer(playerRef);
    });
  }

  @Override
  protected @NotNull HytaleAudience createAudience(final @NotNull Collection<CommandSender> viewers) {
    return new HytaleAudience(this, viewers);
  }

  @Override
  public @NotNull ComponentFlattener flattener() {
    return HytaleFacet.FLATTENER;
  }

  @Override
  public void close() {
    HytaleAudiencesImpl.INSTANCES.remove(this.plugin.getName());
    super.close();
  }

  static final class Builder implements HytaleAudiences.Builder {
    private final @NotNull JavaPlugin plugin;
    private ComponentRenderer<Pointered> componentRenderer;

    Builder(final @NotNull JavaPlugin plugin) {
      this.plugin = requireNonNull(plugin, "plugin");
      this.componentRenderer(ptr -> ptr.getOrDefault(Identity.LOCALE, DEFAULT_LOCALE), GlobalTranslator.renderer());
    }

    @Override
    public @NotNull Builder componentRenderer(final @NotNull ComponentRenderer<Pointered> componentRenderer) {
      this.componentRenderer = requireNonNull(componentRenderer, "component renderer");
      return this;
    }

    @Override
    public HytaleAudiences.@NotNull Builder partition(final @NotNull Function<Pointered, ?> partitionFunction) {
      requireNonNull(partitionFunction, "partitionFunction");
      return this;
    }

    @Override
    public @NotNull HytaleAudiences build() {
      return INSTANCES.computeIfAbsent(this.plugin.getName(), name -> new HytaleAudiencesImpl(this.plugin, this.componentRenderer));
    }
  }
}

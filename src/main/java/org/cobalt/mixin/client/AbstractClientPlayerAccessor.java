package org.cobalt.mixin.client;

import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractClientPlayer.class)
public interface AbstractClientPlayerAccessor {

  /**
   * Returns the backing {@link PlayerInfo} instance from {@link AbstractClientPlayer}.
   *
   * @return the current player info for this client player
   */
  @Accessor("playerInfo")
  PlayerInfo getClientPlayerInfo();

}

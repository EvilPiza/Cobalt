package org.cobalt.mixin.client;

import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractClientPlayer.class)
public interface AbstractClientPlayerAccessor {

  /**
   * Returns the client-side player info.
   *
   * @return the {@link net.minecraft.client.multiplayer.PlayerInfo} associated with this player
   */
  @Accessor("playerInfo")
  PlayerInfo getClientPlayerInfo();

}

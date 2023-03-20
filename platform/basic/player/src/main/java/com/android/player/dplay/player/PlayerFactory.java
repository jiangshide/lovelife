package com.android.player.dplay.player;

public abstract class PlayerFactory<P extends AbstractPlayer> {
    public abstract P createPlayer();
}

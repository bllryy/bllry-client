package me.lily.bllry.utils.mixins;

public interface IChatHudLine {
    boolean sydney$isClientMessage();

    void sydney$setClientMessage(boolean clientMessage);

    String sydney$getClientIdentifier();

    void sydney$setClientIdentifier(String clientIdentifier);
}
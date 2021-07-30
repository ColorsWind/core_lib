package io.github.divios.core_lib.misc;

import com.cryptomorin.xseries.messages.Titles;
import com.google.common.base.Preconditions;
import io.github.divios.core_lib.Core_lib;
import io.github.divios.core_lib.Schedulers;
import org.bukkit.Bukkit;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ChatPrompt implements Listener {

    private static final Map<Player, Prompt> prompts = new HashMap<>();
    private static boolean load = false;

    private static void init(Plugin plugin) {
        if (!load) {
            new ChatPrompt(plugin);
            load = true;
        }
    }

    public static ChatPromptBuilder builder() {
        return new ChatPromptBuilder();
    }

    /**
     * Prompts a player with callbacks for player response and cancelling
     * @param player The player to prompt
     * @param plugin The prompt to send to the player
     * @param onResponse The callback for when the player responds
     * @param onCancel The callback for when the prompt is cancelled
     */
    @Deprecated
    public static void prompt(
            Plugin plugin,
            Player player,
            Consumer<String> onResponse,
            Consumer<CancelReason> onCancel,
            String title,
            String subTitle

    ) {
        prompt(player, onResponse, onCancel, title, subTitle);
    }

    /**
     * Prompts a player with callbacks for player response and cancelling
     * @param player The player to prompt
     * @param onResponse The callback for when the player responds
     * @param onCancel The callback for when the prompt is cancelled
     */

    @Deprecated
    public static void prompt(
            Player player,
            Consumer<String> onResponse,
            Consumer<CancelReason> onCancel,
            String title,
            String subTitle

    ) {
        init(Core_lib.getPlugin());
        Prompt removed = prompts.remove(player);
        if (removed != null) {
            removed.cancel(CancelReason.PROMPT_OVERRIDDEN);
        }
        //prompts.put(player, new Prompt(onResponse, onCancel));
        player.closeInventory();
        Titles.sendTitle(player, FormatUtils.color(title), FormatUtils.color(subTitle));

        Conversation conv = new ConversationFactory(Core_lib.getPlugin())
                .withEscapeSequence("cancel")
                .withFirstPrompt(new org.bukkit.conversations.Prompt() {

                    @NotNull
                    @Override
                    public String getPromptText(@NotNull ConversationContext conversationContext) {
                        return FormatUtils.color(Msg.PREFIX + "&7Input chat. Type &fcancel &7to exit");
                    }

                    @Override
                    public boolean blocksForInput(@NotNull ConversationContext conversationContext) {
                        return true;
                    }

                    @Nullable
                    @Override
                    public org.bukkit.conversations.Prompt acceptInput(@NotNull ConversationContext conversationContext, @Nullable String s) {
                        Schedulers.sync().runLater(() -> onResponse.accept(s), 1);
                        return null;
                    }
                })
                .addConversationAbandonedListener(e -> onCancel.accept(CancelReason.PLAYER_LEFT))
                .withTimeout(50)
                .buildConversation(player);

        conv.setLocalEchoEnabled(false);
        conv.begin();

    }


    private ChatPrompt(Plugin plugin) {
        //Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private static class Prompt {

        private final Consumer<String> onResponse;
        private final Consumer<CancelReason> onCancel;

        public Prompt(Consumer<String> onResponse, Consumer<CancelReason> onCancel) {
            this.onResponse = onResponse;
            this.onCancel = onCancel;
        }

        public void respond(String response) {
            onResponse.accept(response);
        }

        public void cancel(CancelReason reason) {
            onCancel.accept(reason);
        }

    }

    public enum CancelReason {
        /**
         * Passed when the player was given another prompt. This prompt is removed and cancelled.
         */
        PROMPT_OVERRIDDEN,
        /**
         * Passed when the prompt was cancelled because the player typed 'cancel'.
         */
        PLAYER_CANCELLED,
        /**
         * Passed when the prompt was cancelled because the player left the server.
         */
        PLAYER_LEFT
    }

    public static final class ChatPromptBuilder {

        private Player player;
        private Consumer<String> onResponse;
        private Consumer<CancelReason> onCancel;
        private String title;
        private String subTitle;

        public ChatPromptBuilder withPlayer(Player p) {
            this.player = p;
            return this;
        }

        public ChatPromptBuilder withResponse(Consumer<String> response) {
            this.onResponse = response;
            return this;
        }

        public ChatPromptBuilder withCancel(Consumer<CancelReason> onCancel) {
            this.onCancel = onCancel;
            return this;
        }

        public ChatPromptBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public ChatPromptBuilder withSubtitle(String subtitle) {
            this.subTitle = subtitle;
            return this;
        }

        public void prompt() {

            Preconditions.checkNotNull(player, "player is null");
            if (onResponse == null) onResponse = (s) -> {};
            if (onCancel == null) onCancel = (r) -> {};
            if (title == null) title = "";
            if (subTitle == null) subTitle = "";

            ChatPrompt.prompt(player, onResponse, onCancel, title, subTitle);
        }


    }
}
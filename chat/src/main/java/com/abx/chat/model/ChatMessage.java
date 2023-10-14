package com.abx.chat.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableChatMessage.class)
@JsonDeserialize(as = ImmutableChatMessage.class)
public interface ChatMessage {

    String getUserId();
    String getChatThreadId();
    String getContent();
    String getTimestamp();
}

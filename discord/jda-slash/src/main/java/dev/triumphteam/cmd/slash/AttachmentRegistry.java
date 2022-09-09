package dev.triumphteam.cmd.slash;

import com.google.common.collect.MapMaker;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

class AttachmentRegistry {

    private final Map<String, Message.Attachment> attachments = new MapMaker().weakValues().makeMap();

    @Nullable
    public Message.Attachment getAttachment(@NotNull final String id) {
        return attachments.get(id);
    }

    public void addAttachment(@NotNull final String id, @NotNull final Message.Attachment attachment) {
        attachments.put(id, attachment);
    }
}

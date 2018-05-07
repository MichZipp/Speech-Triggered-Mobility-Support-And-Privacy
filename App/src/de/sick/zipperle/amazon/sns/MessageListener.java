package de.sick.zipperle.amazon.sns;

import de.sick.zipperle.amazon.sns.datastructure.GCMMessage;

/**
 * Created by michael on 20.12.17.
 */

public interface MessageListener {
    public void newMessage(final GCMMessage message);
}

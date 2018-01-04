package de.pho.descent.web.quest;

import de.pho.descent.web.campaign.*;

/**
 *
 * @author pho
 */
public class QuestValidationException extends Exception {

    private static final long serialVersionUID = 1L;

    public QuestValidationException() {

    }

    public QuestValidationException(String message) {
        super(message);
    }

}

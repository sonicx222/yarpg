package de.pho.descent.shared.model.item;

/**
 *
 * @author pho
 */
public enum Tier {
    ZERO("0"),
    ONE("1"),
    TWO("2"),
    THREE("3");
    
    private final String text;

    private Tier(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}

package de.pho.descent.shared.model.token;

/**
 *
 * @author pho
 */
public enum TokenType {
    OBJECTIVE_BLACK("objective_black"),
    OBJECTIVE_BLUE("objective_blue"),
    OBJECTIVE_GREEN("objective_green"),
    OBJECTIVE_RED("objective_red"),
    OBJECTIVE_WHITE("objective_white"),
    SEARCH("search"),
    SEARCH_SPECIAL("search_special"),
    VILLAGER_FEMALE("villager_female"),
    VILLAGER_MALE("villager_male");

    private final String imageName;

    private TokenType(String imageName) {
        this.imageName = imageName;
    }

    public String getImageName() {
        return imageName;
    }
}

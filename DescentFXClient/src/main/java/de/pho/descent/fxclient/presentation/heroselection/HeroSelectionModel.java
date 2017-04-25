package de.pho.descent.fxclient.presentation.heroselection;

import de.pho.descent.shared.model.hero.HeroTemplate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.annotation.PostConstruct;

/**
 *
 * @author pho
 */
public class HeroSelectionModel {

    private final ObservableList<HeroTemplate> heroes = FXCollections.observableArrayList();

    @PostConstruct
    public void init() {

    }

    public ObservableList<HeroTemplate> getHeroes() {
        return heroes;
    }
    
}

package de.pho.descent.fxclient.presentation.heroselection;

import de.pho.descent.shared.dto.WsHeroSelection;
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
    private final ObservableList<WsHeroSelection> heroSelections = FXCollections.observableArrayList();
    private WsHeroSelection currentSelection;
    private HeroTemplate selectedTemplate;

    @PostConstruct
    public void init() {

    }

    public ObservableList<HeroTemplate> getHeroes() {
        return heroes;
    }

    public ObservableList<WsHeroSelection> getHeroSelections() {
        return heroSelections;
    }

    public WsHeroSelection getCurrentSelection() {
        return currentSelection;
    }

    public void setCurrentSelection(WsHeroSelection currentSelection) {
        this.currentSelection = currentSelection;
    }

    public HeroTemplate getSelectedTemplate() {
        return selectedTemplate;
    }

    public void setSelectedTemplate(HeroTemplate selectedTemplate) {
        this.selectedTemplate = selectedTemplate;
    }

}

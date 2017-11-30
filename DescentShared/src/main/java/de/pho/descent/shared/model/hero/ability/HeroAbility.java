package de.pho.descent.shared.model.hero.ability;

import de.pho.descent.shared.model.hero.GameHero;

/**
 *
 * @author pho
 */
@FunctionalInterface
public interface HeroAbility {
    public void perform(GameHero hero);
}

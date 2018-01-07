package de.pho.descent.fxclient.presentation.game.hero;

import com.airhacks.afterburner.views.FXMLView;

/**
 *
 * @author pho
 */
public class HeroGameView extends FXMLView {

    public HeroGamePresenter getRealPresenter() {
        return (HeroGamePresenter) super.getPresenter();
    }

}

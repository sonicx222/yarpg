package de.pho.descent.fxclient.presentation.game.prolog;

import com.airhacks.afterburner.views.FXMLView;

/**
 *
 * @author pho
 */
public class PrologView extends FXMLView {
    
    public PrologPresenter getRealPresenter() {
        return (PrologPresenter) super.getPresenter();
    }
    
}

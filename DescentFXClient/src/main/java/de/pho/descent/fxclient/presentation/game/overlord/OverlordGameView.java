package de.pho.descent.fxclient.presentation.game.overlord;

import de.pho.descent.fxclient.presentation.game.hero.*;
import com.airhacks.afterburner.views.FXMLView;

/**
 *
 * @author pho
 */
public class OverlordGameView extends FXMLView {
    
    public OverlordGamePresenter getRealPresenter() {
        return (OverlordGamePresenter) super.getPresenter();
    }
    
}

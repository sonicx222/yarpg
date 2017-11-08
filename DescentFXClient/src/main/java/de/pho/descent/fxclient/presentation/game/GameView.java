/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pho.descent.fxclient.presentation.game;

import com.airhacks.afterburner.views.FXMLView;

/**
 *
 * @author pho
 */
public class GameView extends FXMLView {
    
    public GamePresenter getRealPresenter() {
        return (GamePresenter) super.getPresenter();
    }
    
}

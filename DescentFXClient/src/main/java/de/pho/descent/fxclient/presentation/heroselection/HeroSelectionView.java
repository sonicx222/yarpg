/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pho.descent.fxclient.presentation.heroselection;

import com.airhacks.afterburner.views.FXMLView;

/**
 *
 * @author pho
 */
public class HeroSelectionView extends FXMLView {
    
    public HeroSelectionPresenter getRealPresenter() {
        return (HeroSelectionPresenter) super.getPresenter();
    }
    
}

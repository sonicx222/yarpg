/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pho.descent.web.session;

import de.pho.descent.web.model.Campaign;
import de.pho.descent.web.model.Encounter;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author pho
 */
@Stateless
public class GameSessionController {
    
    @PersistenceContext
    private EntityManager em;

    public Campaign createNewCampaign() {
        return new Campaign();
    }
    
    public Campaign createTestCampaign() {
        Campaign c = new Campaign();
        
        
        
        return new Campaign();
    }
    
    public Encounter getCurrentEncounter() {
        return new Encounter();
    }

    public Encounter getEncounter(int id) {
        return new Encounter();
    }
}

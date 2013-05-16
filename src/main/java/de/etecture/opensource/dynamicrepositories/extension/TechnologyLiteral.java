/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.etecture.opensource.dynamicrepositories.extension;

import de.etecture.opensource.dynamicrepositories.spi.Technology;
import javax.enterprise.util.AnnotationLiteral;

// must be subclassed, due to select method

@SuppressWarnings(value = "AnnotationAsSuperInterface") // must be subclassed, due to select method
class TechnologyLiteral extends AnnotationLiteral<Technology> implements Technology {
    static final long serialVersionUID = 0;
    private final String technologyName;

    public TechnologyLiteral(String technologyName) {
        this.technologyName = technologyName;
    }

    @Override
    public String value() {
        return technologyName;
    }
}

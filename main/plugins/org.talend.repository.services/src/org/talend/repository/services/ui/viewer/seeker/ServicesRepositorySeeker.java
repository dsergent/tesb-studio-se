// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.repository.services.ui.viewer.seeker;

import java.util.List;

import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.repository.seeker.AbstractRepoViewSeeker;
import org.talend.repository.services.utils.ESBRepositoryNodeType;

/**
 * DOC ggu class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 55206 2011-02-15 17:32:14Z mhirt $
 * 
 */
public class ServicesRepositorySeeker extends AbstractRepoViewSeeker {

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.repository.seeker.AbstractRepoViewSeeker#getValidationTypes()
     */
    @Override
    protected List<ERepositoryObjectType> getValidationTypes() {
        List<ERepositoryObjectType> validationTypes = super.getValidationTypes();
        validationTypes.add(ESBRepositoryNodeType.SERVICES);
        return validationTypes;
    }
}

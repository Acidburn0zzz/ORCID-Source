/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.persistence.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;

import org.orcid.persistence.dao.IdentityProviderDao;
import org.orcid.persistence.jpa.entities.IdentityProviderEntity;

/**
 * 
 * @author Will Simpson
 * 
 */
public class IdentityProviderDaoImpl extends GenericDaoImpl<IdentityProviderEntity, Long> implements IdentityProviderDao {

    public IdentityProviderDaoImpl() {
        super(IdentityProviderEntity.class);
    }

    @Override
    public IdentityProviderEntity findByProviderid(String providerid) {
        TypedQuery<IdentityProviderEntity> query = entityManager.createQuery("from IdentityProviderEntity where providerid = :providerid", IdentityProviderEntity.class);
        query.setParameter("providerid", providerid);
        List<IdentityProviderEntity> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

}

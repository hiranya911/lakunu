package org.lakunu.web.service;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AbstractDomainService {

    protected final DAOFactory daoFactory;

    protected AbstractDomainService(DAOFactory daoFactory) {
        checkNotNull(daoFactory, "DAOFactory is required");
        this.daoFactory = daoFactory;
    }
}

// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.repository.services.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.image.IImage;
import org.talend.core.model.properties.Information;
import org.talend.core.model.properties.InformationLevel;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryContentHandler;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.repository.utils.XmiResourceManager;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.IRepositoryNode.EProperties;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.services.model.services.ServiceConnection;
import org.talend.repository.services.model.services.ServiceItem;
import org.talend.repository.services.model.services.ServiceOperation;
import org.talend.repository.services.model.services.ServicePort;
import org.talend.repository.services.model.services.ServicesFactory;
import org.talend.repository.services.model.services.ServicesPackage;
import org.talend.repository.services.model.services.util.EServiceCoreImage;

/**
 * DOC hwang class global comment. Detailled comment
 */
public class ESBRepositoryContentHandler implements IRepositoryContentHandler {

    private XmiResourceManager xmiResourceManager = new XmiResourceManager();

    private final IProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();

    public Item createNewItem(ERepositoryObjectType type) {
        Item item = ServicesFactory.eINSTANCE.createServiceItem();
        return item;
    }

    public Resource create(IProject project, Item item, int classifierID, IPath path) throws PersistenceException {
        Resource itemResource = null;
        ERepositoryObjectType type;
        switch (classifierID) {
        case ServicesPackage.SERVICE_ITEM:
            if (item != null && item instanceof ServiceItem) {
                type = ESBRepositoryNodeType.SERVICES;
                itemResource = create(project, (ServiceItem) item, path, type);
                return itemResource;
            }
        default:
            return null;
        }
    }

    private Resource create(IProject project, ServiceItem item, IPath path, ERepositoryObjectType type)
            throws PersistenceException {
        Resource itemResource = xmiResourceManager.createItemResource(project, item, path, type, false);
        itemResource.getContents().add(item.getConnection());

        return itemResource;
    }

    public Resource save(Item item) throws PersistenceException {
        Resource itemResource = null;
        EClass eClass = item.eClass();
        if (eClass.eContainer() == ServicesPackage.eINSTANCE) {
            switch (eClass.getClassifierID()) {
            case ServicesPackage.SERVICE_ITEM:
                itemResource = save((ServiceItem) item);
                checkService((ServiceItem) item);
                return itemResource;
            default:
                return null;
            }
        }
        return null;
    }

    private Resource save(ServiceItem item) {
        Resource itemResource = xmiResourceManager.getItemResource(item);
        itemResource.getContents().clear();
        itemResource.getContents().add(item.getConnection());
        for (ServicePort port : ((ServiceConnection) item.getConnection()).getServicePort()) {
            itemResource.getContents().add(port);
            for (ServiceOperation operation : port.getServiceOperation()) {
                itemResource.getContents().add(operation);
            }
        }
        return itemResource;
    }

    public IImage getIcon(ERepositoryObjectType type) {
        if (type == ESBRepositoryNodeType.SERVICES) {
            return EServiceCoreImage.SERVICE_ICON;
        } else if (type == ERepositoryObjectType.SERVICESPORT) {
            return EServiceCoreImage.PORT_ICON;
        } else if (type == ERepositoryObjectType.SERVICESOPERATION) {
            return EServiceCoreImage.OPERATION_ICON;
        }
        return null;
    }

    public boolean isRepObjType(ERepositoryObjectType type) {
        boolean isESBType = false;
        if (type == ESBRepositoryNodeType.SERVICES) {
            isESBType = true;
        }
        return isESBType;
    }

    public ERepositoryObjectType getRepositoryObjectType(Item item) {
        if (item == null) {
            return ESBRepositoryNodeType.SERVICES;
        }
        EClass eClass = item.eClass();
        if (eClass.eContainer() == ServicesPackage.eINSTANCE) {
            switch (eClass.getClassifierID()) {
            case ServicesPackage.SERVICE_ITEM:
                return ESBRepositoryNodeType.SERVICES;
            default:
                return null;
            }
        }
        return null;
    }

    public RepositoryNode createRepositoryNode(RepositoryNode node) {
        RepositoryNode serviceNode = new RepositoryNode(null, node, ENodeType.SYSTEM_FOLDER);
        serviceNode.setProperties(EProperties.LABEL, ESBRepositoryNodeType.SERVICES);
        serviceNode.setProperties(EProperties.CONTENT_TYPE, ESBRepositoryNodeType.SERVICES);
        return serviceNode;
    }

    public boolean isProcess(Item item) {
        // TODO Auto-generated method stub
        return false;
    }

    public ERepositoryObjectType getProcessType() {
        // TODO Auto-generated method stub
        return null;
    }

    public ERepositoryObjectType getCodeType() {
        // TODO Auto-generated method stub
        return null;
    }

    public void addNode(ERepositoryObjectType type, RepositoryNode recBinNode, IRepositoryViewObject repositoryObject,
            RepositoryNode node) {
        if (type == ESBRepositoryNodeType.SERVICES) {
            ServiceConnection serviceConnection = (ServiceConnection) ((ServiceItem) repositoryObject.getProperty().getItem())
                    .getConnection();
            List<ServicePort> listPort = serviceConnection.getServicePort();
            for (ServicePort port : listPort) {
                PortRepositoryObject portRepositoryObject = new PortRepositoryObject(repositoryObject, port);
                RepositoryNode portNode = new RepositoryNode(portRepositoryObject, node, ENodeType.REPOSITORY_ELEMENT); //$NON-NLS-1$
                portNode.setProperties(EProperties.LABEL, port.getName());
                portNode.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.SERVICESPORT);
                node.getChildren().add(portNode);
                //
                List<ServiceOperation> listOperation = port.getServiceOperation();
                for (ServiceOperation operation : listOperation) {
                    OperationRepositoryObject operationRepositoryObject = new OperationRepositoryObject(repositoryObject,
                            operation);
                    RepositoryNode operationNode = new RepositoryNode(operationRepositoryObject, portNode,
                            ENodeType.REPOSITORY_ELEMENT); //$NON-NLS-1$
                    operationNode.setProperties(EProperties.LABEL, operation.getLabel());
                    operationNode.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.SERVICESOPERATION);
                    portNode.getChildren().add(operationNode);
                }
            }
        }
    }

    public void addContents(Collection<EObject> collection, Resource resource) {
        if (collection != null && collection.size() > 0) {
            for (EObject object : collection) {
                if (object instanceof ServiceConnection) {
                    ServiceConnection serviceConnection = (ServiceConnection) object;
                    List<ServicePort> listPort = serviceConnection.getServicePort();
                    for (ServicePort port : listPort) {
                        resource.getContents().add(port);
                        List<ServiceOperation> listOperation = port.getServiceOperation();
                        for (ServiceOperation operation : listOperation) {
                            resource.getContents().add(operation);
                        }
                    }
                }
            }
        }
    }

    private void computePropertyMaxInformationLevel(Property property) {
        EList<Information> informations = property.getInformations();
        InformationLevel maxLevel = null;
        for (Information information : informations) {
            int value = information.getLevel().getValue();
            if (maxLevel == null || value > maxLevel.getValue()) {
                maxLevel = information.getLevel();
            }
        }
        property.setMaxInformationLevel(maxLevel);
    }

    private void checkService(ServiceItem serviceItem) {
        if (serviceItem == null || serviceItem.getProperty() == null)
            return;
        Property property = serviceItem.getProperty();
        EList<Information> informations = property.getInformations();
        if (!WSDLUtils.isValidService(serviceItem)) {
            Information info = PropertiesFactory.eINSTANCE.createInformation();
            info.setLevel(InformationLevel.WARN_LITERAL);
            info.setText("Invalid item");
            informations.add(info);
        } else {
            Iterator<Information> iter = informations.iterator();
            while (iter.hasNext()) {
                Information info = iter.next();
                if (info != null && (info.getLevel() == InformationLevel.WARN_LITERAL)) {
                    iter.remove();
                }
            }
        }
        computePropertyMaxInformationLevel(property);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.repository.IRepositoryContentHandler#getIcon(org.talend.core.model.properties.Item)
     */
    public IImage getIcon(Item item) {
        // TODO Auto-generated method stub
        return null;
    }
}

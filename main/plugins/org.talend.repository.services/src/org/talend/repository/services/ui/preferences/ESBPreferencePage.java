package org.talend.repository.services.ui.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.talend.repository.services.ServicesPlugin;
import org.talend.repository.services.Messages;

public class ESBPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {
	
	public ESBPreferencePage(){
		super(GRID);
		setPreferenceStore(ServicesPlugin.getDefault().getPreferenceStore());
		setDescription(Messages.ESBPreferencePage_description);
	}

	public void init(IWorkbench workbench) {
		
	}

	@Override
	protected void createFieldEditors() {
	}


}

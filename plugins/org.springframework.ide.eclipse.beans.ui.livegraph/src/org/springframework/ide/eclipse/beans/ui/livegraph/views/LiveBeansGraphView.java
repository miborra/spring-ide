/*******************************************************************************
 *  Copyright (c) 2012 VMware, Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *      VMware, Inc. - initial API and implementation
 *******************************************************************************/
package org.springframework.ide.eclipse.beans.ui.livegraph.views;

import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.BaseSelectionListenerAction;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.CompositeLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.DirectedGraphLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.HorizontalShift;
import org.springframework.ide.eclipse.beans.ui.livegraph.actions.ConnectToApplicationAction;
import org.springframework.ide.eclipse.beans.ui.livegraph.actions.OpenBeanClassAction;
import org.springframework.ide.eclipse.beans.ui.livegraph.actions.OpenBeanDefinitionAction;
import org.springframework.ide.eclipse.beans.ui.livegraph.actions.RefreshApplicationAction;
import org.springframework.ide.eclipse.beans.ui.livegraph.model.LiveBeansModel;
import org.springframework.ide.eclipse.beans.ui.livegraph.model.LiveBeansModelCollection;

/**
 * A simple view to host our graph
 * 
 * @author Leo Dos Santos
 */
public class LiveBeansGraphView extends ViewPart {

	private class LoadModelAction extends Action {

		private final LiveBeansModel model;

		public LoadModelAction(LiveBeansModel model) {
			super(model.getApplicationName(), Action.AS_RADIO_BUTTON);
			this.model = model;
		}

		@Override
		public boolean isChecked() {
			return model.equals(viewer.getInput());
		}

		@Override
		public void run() {
			viewer.setInput(model);
		}

	}

	public static final String VIEW_ID = "org.springframework.ide.eclipse.beans.ui.livegraph.views.LiveBeansGraphView";

	private GraphViewer viewer;

	private BaseSelectionListenerAction openBeanClassAction;

	private BaseSelectionListenerAction openBeanDefAction;

	private Action connectApplicationAction;

	@Override
	public void createPartControl(Composite parent) {
		viewer = new GraphViewer(parent, SWT.NONE);
		viewer.setContentProvider(new LiveBeansGraphContentProvider());
		viewer.setLabelProvider(new LiveBeansGraphLabelProvider());
		viewer.setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);
		viewer.setLayoutAlgorithm(new CompositeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING,
				new LayoutAlgorithm[] { new DirectedGraphLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING),
						new HorizontalShift(LayoutStyles.NO_LAYOUT_NODE_RESIZING) }));
		viewer.applyLayout();
		getSite().setSelectionProvider(viewer);

		makeActions();
		hookPullDownMenu();
		hookContextMenu();

		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				if (openBeanDefAction != null && openBeanDefAction.isEnabled()) {
					openBeanDefAction.run();
				}
			}
		});
	}

	@Override
	public void dispose() {
		if (viewer != null) {
			viewer.removeSelectionChangedListener(openBeanClassAction);
			viewer.removeSelectionChangedListener(openBeanDefAction);
		}
		super.dispose();
	}

	private void fillContextMenu(IMenuManager menuManager) {
		menuManager.add(new Separator());
		menuManager.add(openBeanClassAction);
		menuManager.add(openBeanDefAction);
	}

	private void fillPullDownMenu(IMenuManager menuManager) {
		menuManager.add(connectApplicationAction);
		Set<LiveBeansModel> collection = LiveBeansModelCollection.getInstance().getCollection();
		if (collection.size() > 0) {
			menuManager.add(new Separator());
		}
		for (LiveBeansModel model : collection) {
			menuManager.add(new LoadModelAction(model));
		}
	}

	public LiveBeansModel getInput() {
		if (viewer != null) {
			Object obj = viewer.getInput();
			if (obj instanceof LiveBeansModel) {
				return (LiveBeansModel) obj;
			}
		}
		return null;
	}

	private void hookContextMenu() {
		MenuManager menuManager = new MenuManager();
		menuManager.setRemoveAllWhenShown(true);
		fillContextMenu(menuManager);

		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				fillContextMenu(manager);
			}
		});

		Menu menu = menuManager.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuManager, viewer);

	}

	private void hookPullDownMenu() {
		IActionBars bars = getViewSite().getActionBars();
		IMenuManager menuManager = bars.getMenuManager();
		menuManager.setRemoveAllWhenShown(true);
		fillPullDownMenu(bars.getMenuManager());

		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				fillPullDownMenu(manager);
			}
		});
	}

	private void makeActions() {
		openBeanClassAction = new OpenBeanClassAction();
		openBeanClassAction.setEnabled(false);
		viewer.addSelectionChangedListener(openBeanClassAction);

		openBeanDefAction = new OpenBeanDefinitionAction();
		openBeanDefAction.setEnabled(false);
		viewer.addSelectionChangedListener(openBeanDefAction);

		connectApplicationAction = new ConnectToApplicationAction(this);

		IActionBars bars = getViewSite().getActionBars();
		IToolBarManager toolbar = bars.getToolBarManager();
		toolbar.add(new RefreshApplicationAction(this));
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	public void setInput(LiveBeansModel model) {
		if (viewer != null) {
			viewer.setInput(model);
		}
	}

}
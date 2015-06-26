/*******************************************************************************
 * Copyright (c) 2015 Pivotal, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Pivotal, Inc. - initial API and implementation
 *******************************************************************************/
package org.springframework.ide.eclipse.boot.dash.views.sections;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.springframework.ide.eclipse.boot.dash.model.BootDashElement;
import org.springframework.ide.eclipse.boot.dash.model.requestmappings.RequestMapping;
import org.springsource.ide.eclipse.commons.livexp.core.LiveExpression;

import org.springframework.ide.eclipse.boot.properties.editor.util.FuzzyMatcher;

public class RequestMappingContentProposalProvider implements IContentProposalProvider {

	private static final IContentProposal[] NO_PROPOSALS = {};

	private LiveExpression<BootDashElement> input;

	public RequestMappingContentProposalProvider(LiveExpression<BootDashElement> input) {
		this.input = input;
	}

	@Override
	public IContentProposal[] getProposals(String contents, int position) {
		BootDashElement bde = input.getValue();
		if (bde!=null) {
			List<RequestMapping> rms = bde.getLiveRequestMappings();
			if (rms!=null && !rms.isEmpty()) {
				ArrayList<IContentProposal> proposals = new ArrayList<IContentProposal>(rms.size());
				for (RequestMapping rm : rms) {
					String path = rm.getPath();
					if (FuzzyMatcher.matchScore(contents, path)!=0) {
						proposals.add(simpleProposal(path));
					}
				}
			}
		}
		return NO_PROPOSALS;
	}

	private IContentProposal simpleProposal(final String path) {
		return new IContentProposal() {

			@Override
			public String getLabel() {
				return path;
			}

			@Override
			public String getDescription() {
				return path;
			}

			@Override
			public int getCursorPosition() {
				return path.length();
			}

			@Override
			public String getContent() {
				return path;
			}
		};
	}

}

/**
 *   Copyright (c) 2017 Marco Merli <yohji@marcomerli.net>
 *   This file is part of XPFP.
 *
 *   XPFP is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   XPFP is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with XPFP.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.marcomerli.xpfp.gui.comp;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Philip Isenhour
 * @author Marco Merli
 * @since 1.0
 */
public class FormPanel extends JPanel {

	private static final long serialVersionUID = - 4777808857168558526L;
	private static final double DEFAULT_WEIGHT = 1.0;

	private final GridBagConstraints cLast;
	private final GridBagConstraints cMiddle;

	public FormPanel() {

		super(new GridBagLayout());

		cLast = new GridBagConstraints();
		cLast.fill = GridBagConstraints.HORIZONTAL;
		cLast.anchor = GridBagConstraints.NORTHWEST;
		cLast.insets = new Insets(5, 5, 5, 5);
		cLast.gridwidth = GridBagConstraints.REMAINDER;

		cMiddle = (GridBagConstraints) cLast.clone();
		cMiddle.gridwidth = 1;
	}

	public JLabel addLabel(String label)
	{
		return addLabel(label, DEFAULT_WEIGHT);
	}

	public JLabel addLabel(String label, double weight)
	{
		JLabel jl = new JLabel(label);
		addMiddle(jl, weight);

		return jl;
	}

	public void addSpace(Number space)
	{
		addLabel(StringUtils.repeat(' ', space.intValue()));
	}

	public void addLast(Component field)
	{
		addLast(field, DEFAULT_WEIGHT);
	}

	public void addLast(Component field, double weight)
	{
		cLast.weightx = weight;

		GridBagLayout gbl = (GridBagLayout) getLayout();
		gbl.setConstraints(field, cLast);

		add(field);
	}

	public void addMiddle(Component field)
	{
		addMiddle(field, DEFAULT_WEIGHT);
	}

	public void addMiddle(Component field, double weight)
	{
		cMiddle.weightx = weight;

		GridBagLayout gbl = (GridBagLayout) getLayout();
		gbl.setConstraints(field, cMiddle);

		add(field);
	}
}

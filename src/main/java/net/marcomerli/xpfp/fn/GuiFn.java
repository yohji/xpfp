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

package net.marcomerli.xpfp.fn;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.net.URL;

import javax.swing.JOptionPane;

import org.apache.commons.lang3.exception.ExceptionUtils;

import net.marcomerli.xpfp.gui.MainWindow;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class GuiFn {

	public static void infoDialog(String message, Component component)
	{
		JOptionPane.showMessageDialog(component, message,
			MainWindow.TITLE_COMPACT + " :: Information", JOptionPane.INFORMATION_MESSAGE);
	}

	public static void warnDialog(String message, Component component)
	{
		JOptionPane.showMessageDialog(component, message,
			MainWindow.TITLE_COMPACT + " :: Warning", JOptionPane.WARNING_MESSAGE);
	}

	public static void warnDialog(final Throwable e, final Component component)
	{
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run()
			{
				Throwable err = ExceptionUtils.getRootCause(e);
				if (err == null)
					err = e;

				JOptionPane.showMessageDialog(component, err.getMessage(),
					MainWindow.TITLE_COMPACT + " :: Warning", JOptionPane.WARNING_MESSAGE);
			}
		});
	}

	public static void errorDialog(final Throwable e, final Component component)
	{
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run()
			{
				Throwable err = ExceptionUtils.getRootCause(e);
				if (err == null)
					err = e;

				JOptionPane.showMessageDialog(component, err.getMessage(),
					MainWindow.TITLE_COMPACT + " :: Error", JOptionPane.ERROR_MESSAGE);
			}
		});
	}

	public static void fatalDialog(Throwable e, Component component)
	{
		errorDialog(e, component);
	}

	public static int selectDialog(String message, Component component)
	{
		return JOptionPane.showConfirmDialog(component, message,
			MainWindow.TITLE_COMPACT + " :: Select", JOptionPane.YES_NO_OPTION);
	}

	public static boolean isBrowserSupported()
	{
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE))
			return true;

		return false;
	}

	public static void openBrowser(URL url) throws Exception
	{
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE))
			desktop.browse(url.toURI());
	}

	private GuiFn() {}
}

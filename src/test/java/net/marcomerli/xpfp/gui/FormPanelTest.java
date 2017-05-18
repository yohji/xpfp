package net.marcomerli.xpfp.gui;

import javax.swing.JFrame;

import net.marcomerli.xpfp.gui.Components.FormPanel;
import net.marcomerli.xpfp.gui.Components.NumberInput;

public class FormPanelTest {

	public static void main(String[] args)
	{
		JFrame win = new JFrame();
		win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		win.setContentPane(new Content());
		win.pack();

		win.setLocationByPlatform(true);
		win.setResizable(false);
		win.setVisible(true);
	}

	@SuppressWarnings("serial")
	public static class Content extends FormPanel {

		public Content() {

			addLabel("First");
			addMiddle(new NumberInput(3));
			addLabel("Second");
			addLast(new NumberInput(4));

			addLabel("Third");
			addMiddle(new NumberInput(3));
			addLabel("Fourth");
			addLast(new NumberInput(4));
		}
	}
}

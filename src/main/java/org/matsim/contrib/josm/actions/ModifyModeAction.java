package org.matsim.contrib.josm.actions;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.command.ChangePropertyCommand;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.command.SequenceCommand;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.gui.ExtendedDialog;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.PleaseWaitRunnable;
import org.openstreetmap.josm.io.OsmTransferException;
import org.openstreetmap.josm.tools.Shortcut;
import org.xml.sax.SAXException;

@SuppressWarnings("serial")
public class ModifyModeAction extends JosmAction {

	static final int MODIFY = 1;
	static final int CANCEL = 2;
	static boolean remove = false;

	public ModifyModeAction() {
		super(tr("Modify Mode"), "pastetags.png", tr("Modify mode of links"), Shortcut.registerShortcut(
				"tools:modifymode", tr("Tool: {0}", tr("Modify Mode")), KeyEvent.VK_M, Shortcut.ALT_CTRL), true);
	}

	/**
	 * Called when the action is executed.
	 *
	 * This method opens up a dialog where modes can be appended to or deleted from
	 * the selected links that don't have to be homogeneous.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		/**
		 * 1) fetch selection information 2) check selection information for consistency
		 * 3) Open up dialog 4) change selection information
		 * 
		 */
		// 1) Fetch selection information
		DataSet ds = getLayerManager().getEditDataSet();
		if (ds == null) {
			return;
		}
		Collection<OsmPrimitive> selection = ds.getSelected();
		List<Way> selectedWays = OsmPrimitive.getFilteredList(selection, Way.class);

		// 2) Check selection information for consistency
		// TODO

		// 3) Open up dialog
		String[] output = openDialog();

		if (output == null || output[0].isEmpty()) {
			return;
		}

		// 4) Change selection information on basis of dialog entry
		PleaseWaitRunnable task = new PleaseWaitRunnable("MATSim LinkReference") {

			@Override
			protected void cancel() {
			}

			@Override
			protected void finish() {
			}

			@Override
			protected void realRun() throws SAXException, IOException, OsmTransferException {
				Collection<Command> commandList = new ArrayList<>();
				// Add selected output to selection link modes
				if (output[1].equals("add")) {
					for (Way w : selectedWays) {
						String value = w.getKeys().get("matsim:modes");
						if (!value.contains(output[0])) {
							commandList.add(new ChangePropertyCommand(w, "matsim:modes", value + ";" + output[0]));
						}
					}

				}
				// Remove selected output from selection link modes
				else {
					for (Way w : selectedWays) {
						String value = w.getKeys().get("matsim:modes");
						commandList.add(new ChangePropertyCommand(w, "matsim:modes", value.replace(output[0], "")));
					}
				}

				// 5) Cleanup
				// Show Notification box: "MODE added to X Nodes/Links changed out of Y
				// selected"
				MainApplication.undoRedo.add(new SequenceCommand(tr("Advanced Split"), commandList));
			}

		};
		MainApplication.worker.execute(task);
	}

	private String[] openDialog() {
		JPanel panel = new JPanel(new GridBagLayout());
		JLabel label;
		JComboBox<String> cbox1;
		JComboBox<String> cbox2;
		GridBagConstraints c = new GridBagConstraints();

		String[] modes = { "", "car", "walk", "bike", "pt", "bus", "ferry", "rail", "train", "tram", "transfer-walk",
				"funicular" };
		String[] dec = { "add", "remove" };

		label = new JLabel("Modify modes of MATSim Links");
		c.weightx = 0.8;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(0, 0, 10, 0);
		panel.add(label, c);

		label = new JLabel("Mode to add:");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 1;
		panel.add(label, c);

		cbox1 = new JComboBox<String>(modes);
		cbox1.setEditable(true);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.gridx = 1;
		c.gridy = 1;
		panel.add(cbox1, c);

		label = new JLabel("add/remove :");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 2;
		panel.add(label, c);

		cbox2 = new JComboBox<String>(dec);
		cbox2.setEditable(false);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.gridx = 1;
		c.gridy = 2;
		panel.add(cbox2, c);

		int answer = new ModifyModeDialog(panel, "Modify Modes", null).getValue();

		if (answer == 2) {
			return null;
		}

		String[] back = { (String) cbox1.getSelectedItem(), (String) cbox2.getSelectedItem() };

		return back;

	}

	class ModifyModeDialog extends ExtendedDialog {
		ModifyModeDialog(Component content, String title, String... ButtonText) {
			super(Main.parent, title, (new String[] { tr("Add/Remove"), tr("Cancel") }), true);

			contentInsets = new Insets(10, 5, 0, 5);
			setButtonIcons("ok", "cancel");
			setContent(content);
			setDefaultButton(1);

			Dimension d = getSize();
			if (d.width < 350) {
				d.width = 350;
				setSize(d);
			}

			super.showDialog();
		}
	}

}

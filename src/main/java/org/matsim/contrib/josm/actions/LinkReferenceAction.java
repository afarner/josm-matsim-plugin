package org.matsim.contrib.josm.actions;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import org.matsim.contrib.josm.gui.LinkReferenceDialog;
import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.PleaseWaitRunnable;
import org.openstreetmap.josm.io.OsmTransferException;
import org.openstreetmap.josm.tools.Shortcut;
import org.xml.sax.SAXException;

/**
 * The ImportAction that handles population imports.
 *
 * @author Nico
 */
@SuppressWarnings("serial")
public class LinkReferenceAction extends JosmAction {

	public LinkReferenceAction() {
		super(tr("Clean up Link References"), "wayflip.png", tr("Clean up Link References"),
				Shortcut.registerShortcut("menu:matsimLinkRef", tr("Menu: {0}", tr("MATSim Link Reference")),
						KeyEvent.VK_R, Shortcut.ALT_CTRL),
				true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		LinkReferenceDialog dialog = new LinkReferenceDialog();
		JOptionPane pane = new JOptionPane(dialog, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);

		JDialog dlg = pane.createDialog(Main.parent, tr("Link References"));
		dlg.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dlg.setMinimumSize(new Dimension(300, 400));
		dlg.setVisible(true);
		if (pane.getValue() != null) {
			if (((Integer) pane.getValue()) == JOptionPane.OK_OPTION) {
				if (dialog.getpopulationFile() != null) {

					final java.io.File population = dialog.getpopulationFile();
					final java.io.File chain = dialog.getchainFile();

					PleaseWaitRunnable task = new PleaseWaitRunnable("MATSim LinkReference") {
						@Override
						protected void cancel() {
							// not really important i guess
						}

						@Override
						protected void finish() {
							// close files etc?
						}

						@Override
						protected void realRun() throws SAXException, IOException, OsmTransferException {
							// Read Chain File and save into List
							List<String> chains = new ArrayList<>();
							String line;
							try (BufferedReader cr = new BufferedReader(new FileReader(chain))) {
								while ((line = cr.readLine()) != null) {
									chains.add(line);
								}
								cr.close();
							}

							// Create new population file, copy line from old file, replace with chain and
							// save line into new file
							File newpop = new File(population.getAbsolutePath().replace(".xml", "_ref.xml"));
							BufferedWriter bw = new BufferedWriter(new FileWriter(newpop));

							String qm = "( |<)";
							
							try (BufferedReader br = new BufferedReader(new FileReader(population))) {
								while ((line = br.readLine()) != null) {
									if (line.contains("route")) {
										for (int i = 0; i < chains.size(); i++) {
											String temp = chains.get(i).split(" ")[0];
											line = line.replaceAll(temp+qm,chains.get(i));
										}
									}
									bw.write(line + "\n");
								}
								br.close();
								bw.close();
							}
						}

					};
					// WaitRunnableFinished
					MainApplication.worker.execute(task);
				}

			}
		}
		dlg.dispose();
	}

}
